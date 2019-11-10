package ssw.mj.impl;

import ssw.mj.Errors;
import ssw.mj.Parser;
import ssw.mj.Scanner;
import ssw.mj.Token.Kind;

import java.util.EnumSet;

import static ssw.mj.Errors.Message.*;
import static ssw.mj.Token.Kind.*;

public final class ParserImpl extends Parser {

    private static final EnumSet<Kind> firstTSStatement = EnumSet.of
            (ident, if_, while_, break_, compare_, return_, read, print, lbrace, semicolon);
    private static final EnumSet<Kind> firstTSAssignOp = EnumSet.of
            (assign, plusas, minusas, timesas, slashas, remas);
    private static final EnumSet<Kind> firstTSExpr = EnumSet.of
            (minus, ident, number, charConst, new_, lpar);
    private static final EnumSet<Kind> firstTSRelOp = EnumSet.of
            (eql, neq, gtr, geq, lss, leq);


    public ParserImpl(Scanner scanner) {
        super(scanner);
    }

    @Override
    public void parse() {
        scan();
        Program();
        check(eof);
    }

    private void check(Kind expected) {
        if (sym == expected) {
            scan();
        } else {
            error(Errors.Message.TOKEN_EXPECTED, expected);
        }
    }

    private void scan() {
        t = la;
        la = scanner.next();
        sym = la.kind;
    }

    private void Program() {
        check(program);
        check(ident);
		while (true) {
			if (sym == class_) {
				ClassDecl();
			} else if (sym == ident) {
				VarDecl();
			} else if (sym == final_) {
				ConstDecl();
			} else {
				break;
			}
		}
		check(lbrace);
        while (true) {
            if (sym == void_ || sym == ident) {
                MethodDecl();
            } else {
                break;
            }
        }
        check(rbrace);
    }

    private void ClassDecl() {
        scan();
        check(ident);
        check(lbrace);
        while (true) {
            if (sym == ident) {
                VarDecl();
            } else {
                break;
            }
        }
        check(rbrace);
    }

    private void VarDecl() {
        Type();
        check(ident);
        while (true) {
            if (sym == comma) {
            	scan();
                check(ident);
            } else break;
        }
        check(semicolon);
    }

    private void ConstDecl() {
        scan();
        Type();
        check(ident);
        check(assign);
        if (sym == number || sym == charConst) {
            scan();
        } else {
            error(CONST_DECL);
        }
        check(semicolon);
    }

    private void Type() {
        check(ident);
        if (sym == lbrack) {
            scan();
            check(rbrack);
        }
    }

    private void MethodDecl() {
        if (sym == void_) {
            scan();
        } else if (sym == ident) {
            Type();
        } else {
            error(METH_DECL);
        }
        check(ident);
        check(lpar);
        if (sym == ident) {
            FormPars();
        }
        check(rpar);
        while (sym == ident) {
            VarDecl();
        }
        Block();
    }

    private void FormPars() {
        Type();
        check(ident);
        while (sym == comma) {
            scan();
            Type();
            check(ident);
        }
    }

    private void Block() {
        check(lbrace);
        while (firstTSStatement.contains(sym))
            Statement();
        check(rbrace);
    }

    private void Statement() {
		switch (sym) {
			case ident:
				Designator();
				if (firstTSAssignOp.contains(sym)) {
					Assignop();
					Expr();
				} else if (sym == lpar) {
					ActPars();
				} else if (sym == pplus) {
					scan();
				} else if (sym == mminus) {
					scan();
				} else {
					error(DESIGN_FOLLOW);
				}
				check(semicolon);
				break;
			case if_:
				scan();
				check(lpar);
				Condition();
				check(rpar);
				Statement();
				if (sym == else_) {
					scan();
					Statement();
				}
				break;
			case while_:
				scan();
				check(lpar);
				Condition();
				check(rpar);
				Statement();
				break;
			case break_:
				scan();
				check(semicolon);
				break;
			case compare_:
				scan();
				check(lpar);
				Expr();
				check(comma);
				Expr();
				check(rpar);
				Block();
				Block();
				Block();
				break;
			case return_:
				scan();
				if (firstTSExpr.contains(sym))
					Expr();
				check(semicolon);
				break;
			case read:
				scan();
				check(lpar);
				Designator();
				check(rpar);
				check(semicolon);
				break;
			case print:
				scan();
				check(lpar);
				Expr();
				if (sym == comma) {
					scan();
					check(number);
				}
				check(rpar);
				check(semicolon);
				break;
			case lbrace:
				Block();
				break;
			case semicolon:
				scan();
				break;
			default:
				error(INVALID_STAT);
				break;
		}
    }

	private void Assignop() {
        if (firstTSAssignOp.contains(sym))
            scan();
        else
            error(ASSIGN_OP);
    }

    private void ActPars() {
        check(lpar);
        if (firstTSExpr.contains(sym)) {
            Expr();
            while (sym == comma) {
                scan();
                Expr();
            }
        }
        check(rpar);
    }

    private void Condition() {
        CondTerm();
        while (sym == or) {
            scan();
            CondTerm();
        }
    }

    private void CondTerm() {
        CondFact();
        while (sym == and) {
            scan();
            CondFact();
        }
    }

    private void CondFact() {
        Expr();
        Relop();
        Expr();
    }

    private void Relop() {
        if (firstTSRelOp.contains(sym))
            scan();
        else
            error(REL_OP);
    }

    private void Expr() {
        if (sym == minus)
            scan();
        Term();
        while (sym == plus || sym == minus) {
            Addop();
            Term();
        }
    }

    private void Term() {
        Factor();
        while (sym == times || sym == slash || sym == rem) {
            Mulop();
            Factor();
        }
    }

    private void Factor() {
        switch (sym) {
            case ident:
                Designator();
                if (sym == lpar)
                    ActPars();
                break;
            case number:
            case charConst:
                scan();
                break;
            case new_:
                scan();
                check(ident);
                if (sym == lbrack) {
                    scan();
                    Expr();
                    check(rbrack);
                }
                break;
            case lpar:
                scan();
                Expr();
                check(rpar);
                break;
            default:
                error(INVALID_FACT);
                break;
        }
    }

	private void Designator() {
		check(ident);
		while (sym == period || sym == lbrack) {
			if (sym == period) {
				scan();
				check(ident);
			} else {
				scan();
				Expr();
				check(rbrack);
			}
		}
	}

	private void Addop() {
		if (sym == plus || sym == minus) {
			scan();
		} else {
			error(ADD_OP);
		}
	}

	private void Mulop() {
		if (sym == times || sym == slash || sym == rem) {
			scan();
		} else {
			error(MUL_OP);
		}
	}
}
