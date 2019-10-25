package ssw.mj.impl;

import ssw.mj.Errors;
import ssw.mj.Parser;
import ssw.mj.Scanner;
import ssw.mj.Token.Kind;

import static ssw.mj.Token.Kind.*;

public final class ParserImpl extends Parser {

    // TODO Exercise 3 - 6: implementation of parser

    public ParserImpl(Scanner scanner) {
        super(scanner);
    }

    @Override
    public void parse() {
        scan();
        pProgram();
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

    private void pProgram() {
        check(program);
        check(ident);
        while (true) {
            if (sym == class_) {
                pClassDecl();
            } else if (sym == ident) {
                pVarDecl();
            } else if (sym == final_) {
                pConstDecl();
            } else {
                break;
            }
        }
        check(lbrace);
        while (true) {
            if (sym == void_ || sym == ident) {
                pMethodDecl();
            } else {
                break;
            }
        }
        check(rbrace);
    }

    private void pClassDecl() {
    	scan();
        check(ident);
        check(lbrace);
        while (true) {
            if (sym == ident) {
                pVarDecl();
            } else {
                break;
            }
        }
        check(rbrace);
    }

    private void pVarDecl() {
    	pType();
		check(ident);
		while (true){
			if (sym == comma){
				check(ident);
			} else break;
		}
		check(semicolon);
    }

    private void pConstDecl() {
    	scan();
    	pType();
    	check(ident);
    	check(assign);
		if (sym == number || sym == charConst){
			scan();
		} else {
			error(Errors.Message.CONST_DECL);
		}
		check(semicolon);
    }

	private void pType() {
    	check(ident);
    	if (sym == lbrack){
    		scan();
    		check(rbrack);
		}
	}

	private void pMethodDecl() {
    	if (sym == void_){
    		scan();
		} else if (sym == ident){
    		pType();
		} else {
    		error(Errors.Message.METH_DECL);
		}
    	check(ident);
    	check(lpar);
    	check(rpar);
    }
}
