package ssw.mj.impl;

import ssw.mj.Errors;
import ssw.mj.Parser;
import ssw.mj.Scanner;
import ssw.mj.Token.Kind;

import java.util.EnumSet;

import static ssw.mj.Token.Kind.*;

public final class ParserImpl extends Parser {

	private static final EnumSet<Kind> firstOfStatement = EnumSet.of
			(ident, if_, while_, break_, compare_, return_, read, print, lbrace, semicolon);
	private static final EnumSet<Kind> firstOfAssignOp = EnumSet.of
			(assign, plusas, minusas, timesas, slashas, remas);
	private static final EnumSet<Kind> firstOfExpr = EnumSet.of
			(minus, ident, number, charConst, new_, lpar);
	private static final EnumSet<Kind> firstOfRelOp = EnumSet.of
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
			error(Errors.Message.CONST_DECL);
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
			error(Errors.Message.METH_DECL);
		}
		check(ident);
		check(lpar);
		check(rpar);
	}
}
