package ssw.mj.impl;

import ssw.mj.Errors;
import ssw.mj.Parser;
import ssw.mj.Scanner;
import ssw.mj.Token.Kind;
import ssw.mj.symtab.Obj;
import ssw.mj.symtab.Struct;
import ssw.mj.symtab.Tab;

import java.util.EnumSet;

import static ssw.mj.Errors.Message.*;
import static ssw.mj.Token.Kind.*;
import static ssw.mj.symtab.Tab.noType;

public final class ParserImpl extends Parser {

    //private static final EnumSet<Kind> firstTSStatement = EnumSet.of
    //        (ident, if_, while_, break_, compare_, return_, read, print, lbrace, semicolon);
    private static final EnumSet<Kind> firstTSAssignOp = EnumSet.of
            (assign, plusas, minusas, timesas, slashas, remas);
    private static final EnumSet<Kind> firstTSExpr = EnumSet.of
            (minus, ident, number, charConst, new_, lpar);
    private static final EnumSet<Kind> firstTSRelOp = EnumSet.of
            (eql, neq, gtr, geq, lss, leq);

    private static final EnumSet<Kind> catchDecl = EnumSet.of(eof, final_, class_, lbrace, semicolon);
    private static final EnumSet<Kind> catchStat = EnumSet.of(eof, if_, while_, break_, compare_, read, print, semicolon);


    private static final int MIN_ERROR_DIST = 3;

    private int errorDist;

    public ParserImpl(Scanner scanner) {
        super(scanner);
        this.errorDist = MIN_ERROR_DIST;
    }

    @Override
    public void parse() {
        scan();
        Program();
        check(eof);
    }


    // Errors are no longer handled in panic mode so we override :
    @Override
    public void error(Errors.Message msg, Object... msgParams) {
        if (errorDist >= MIN_ERROR_DIST) {
            scanner.errors.error(la.line, la.col, msg, msgParams);
        }
        errorDist = 0;
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
        errorDist++;
    }

    private void Program() {
        check(program);
        check(ident);
        Obj program = tab.insert(Obj.Kind.Prog, t.str, noType);

        tab.openScope();

        while (sym != lbrace && sym != eof) {
            if (sym == class_) {
                ClassDecl();
            } else if (sym == ident) {
                VarDecl();
            } else if (sym == final_) {
                ConstDecl();
            } else {
                recoverDecl();
            }
        }

        if (tab.curScope.nVars() > MAX_GLOBALS) {
            error(TOO_MANY_GLOBALS);
        }

        check(lbrace);
        while (true) {
            if (sym != eof && sym != rbrace) {
                MethodDecl();
            } else {
                break;
            }
        }
        check(rbrace);
        program.locals = tab.curScope.locals();
        tab.closeScope();
    }

    private void recoverDecl() {
        error(INVALID_DECL);
        do {
            scan();
        } while (!catchDecl.contains(sym));
        errorDist = 0;
    }

    private void ClassDecl() {
        scan();
        check(ident);
        Obj clas = tab.insert(Obj.Kind.Type, t.str, new StructImpl(Struct.Kind.Class));
        tab.openScope();
        check(lbrace);
        while (sym == ident) {
            VarDecl();
        }

        if (tab.curScope.nVars() > MAX_FIELDS) {
            error(TOO_MANY_FIELDS);
        }
        clas.type.fields = tab.curScope.locals();
        check(rbrace);
        tab.closeScope();
    }

    private void VarDecl() {
        StructImpl type = Type();
        check(ident);
        tab.insert(Obj.Kind.Var, t.str, type);
        while (true) {
            if (sym == comma) {
                scan();
                check(ident);
                tab.insert(Obj.Kind.Var, t.str, type);
            } else break;
        }
        check(semicolon);
    }

    private void ConstDecl() {
        scan();
        StructImpl type = Type();
        check(ident);
        Obj constObj = tab.insert(Obj.Kind.Con, t.str, type);
        check(assign);
        if (sym == number) {
            if (type.kind != Struct.Kind.Int) error(CONST_TYPE);
            scan();
            constObj.val = t.val;
        } else if (sym == charConst) {
            if (type.kind != Struct.Kind.Char) error(CONST_TYPE);
            scan();
            constObj.val = t.val;
        } else {
            error(CONST_DECL);
        }
        check(semicolon);
    }

    private StructImpl Type() {
        check(ident);
        Obj slo = tab.find(t.str);
        if (slo.kind != Obj.Kind.Type) {
            error(NO_TYPE);
        }
        StructImpl type = slo.type;
        if (sym == lbrack) {
            scan();
            check(rbrack);
            return new StructImpl(type);
        }
        return type;
    }

    private void MethodDecl() {
        StructImpl type = noType;
        if (sym == void_) {
            scan();
        } else if (sym == ident) {
            type = Type();
        } else {
            error(METH_DECL);
            recoverMeth();
        }
        check(ident);
        Obj method = tab.insert(Obj.Kind.Meth, t.str, type);
        method.adr = code.pc;
        check(lpar);
        tab.openScope();
        if (sym == ident) {
            FormPars();
        }
        check(rpar);
        method.nPars = tab.curScope.nVars();
        if ("main".equals(method.name)) {
            if (method.nPars != 0) {
                error(MAIN_WITH_PARAMS);
            }
            if (method.type != Tab.noType) {
                error(MAIN_NOT_VOID);
            }
        }
        while (sym == ident) {
            VarDecl();
        }
        if (tab.curScope.nVars() > MAX_LOCALS) {
            error(TOO_MANY_LOCALS);
        }
        Block();
        method.locals = tab.curScope.locals();
        tab.closeScope();
    }

    private void recoverMeth() {
        while (sym != eof && sym != void_ &&
                sym != ident && tab.find(sym.label()).kind != Obj.Kind.Type) {
            scan();
        }
        errorDist = 0;
    }

    private void FormPars() {
        do {
            final StructImpl type = Type();
            check(ident);
            tab.insert(Obj.Kind.Var, t.str, type);
            if (sym == comma) scan();
            else break;
        } while (true);
    }

    private void Block() {
        check(lbrace);
        while (sym != rbrace && sym != eof) {
            Statement();
        }
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
                recoverStat();
                break;
        }
    }

    private void recoverStat() {
        error(INVALID_STAT);
        do {
            scan();
        } while (catchStat.contains(sym));
        errorDist = 0;
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
