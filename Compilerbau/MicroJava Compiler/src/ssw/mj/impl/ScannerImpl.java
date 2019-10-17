package ssw.mj.impl;

import ssw.mj.Errors;
import ssw.mj.Scanner;
import ssw.mj.Token;
import ssw.mj.Token.Kind;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import static ssw.mj.Token.Kind.*;

;

public final class ScannerImpl extends Scanner {
    private static final HashMap<String, Kind> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put(if_.label(), if_);
        keywords.put(while_.label(), while_);
        keywords.put(break_.label(), break_);
        keywords.put(compare_.label(), compare_);
        keywords.put(return_.label(), return_);
        keywords.put(read.label(), read);
        keywords.put(print.label(), print);
        keywords.put(new_.label(), new_);
        keywords.put(final_.label(), final_);
        keywords.put(program.label(), program);
        keywords.put(class_.label(), class_);
        keywords.put(void_.label(), void_);
        keywords.put(else_.label(), else_);
    }

    public ScannerImpl(Reader r) {
        super(r);
        col = 0;
        line = 0;
    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private boolean isLetter(char c) {
        return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z';
    }

    private boolean isAlphaNumeric(char c) {
        return isLetter(c) || isDigit(c);
    }

    @Override
    public Token next() {
        while (ch <= ' ') nextCh(); // includes line feeds, carriage returns and tabulators
        Token token = new Token(none, line, col);
        if (isLetter(ch)) {
            readName(token);
        } else if (isDigit(ch)) {
            readNumber(token);
        } else {
            switch (ch) {
                case '\'':
                    readCharConst(token);
                    break;
                case ';':
                    token.kind = semicolon;
                    break;
                case ',':
                    token.kind = comma;
                    break;
                case '.':
                    token.kind = period;
                    break;
                case EOF:
                    token.kind = eof;
                    break;
                case '{':
                    token.kind = lbrace;
                    break;
                case '}':
                    token.kind = rbrace;
                    break;
                case '(':
                    token.kind = lpar;
                    break;
                case ')':
                    token.kind = rpar;
                    break;
                case '[':
                    token.kind = lbrack;
                    break;
                case ']':
                    token.kind = rbrack;
                    break;
                case '&':
                    nextCh();
                    if (ch == '&') {
                        token.kind = and;
						nextCh();
                    }
                    break;
                case '|':
                    nextCh();
                    if (ch == '|') {
                        token.kind = or;
						nextCh();
                    }
                    break;
                case '!':
                    nextCh();
                    if (ch == '=') {
                        token.kind = neq;
						nextCh();
                    }
                    break;
                case '+':
                    nextCh();
                    if (ch == '=') {
                        token.kind = plusas;
						nextCh();
                    } else if (ch == '+') {
                        token.kind = pplus;
						nextCh();
                    } else {
                        token.kind = plus;
                    }
                    break;
                case '-':
                    nextCh();
                    if (ch == '=') {
                        token.kind = minusas;
						nextCh();
                    } else if (ch == '-') {
                        token.kind = mminus;
						nextCh();
                    } else {
                        token.kind = minus;
                    }
                    break;
                case '*':
                    nextCh();
                    if (ch == '=') {
                        token.kind = timesas;
						nextCh();
                    } else {
                        token.kind = times;
                    }
                    break;
                case '/':
                    nextCh();
                    if (ch == '=') {
                        token.kind = slashas;
                        nextCh();
                    } else if (ch == '*') {
                        readComment(token);
                        token = next();
                    } else {
                        token.kind = slash;
                    }
                    break;
                case '%':
                    nextCh();
                    if (ch == '=') {
                        token.kind = remas;
						nextCh();
                    } else {
                        token.kind = rem;
                    }
                    break;
                case '>':
                    nextCh();
                    if (ch == '=') {
                        token.kind = geq;
						nextCh();
                    } else {
                        token.kind = gtr;
                    }
                    break;
                case '<':
                    nextCh();
                    if (ch == '=') {
                        token.kind = leq;
						nextCh();
                    } else {
                        token.kind = lss;
                    }
                    break;
                case '=':
                    nextCh();
                    if (ch == '=') {
                        token.kind = eql;
						nextCh();
                    } else {
                        token.kind = assign;
                    }
                    break;
                default:
                	error(token, Errors.Message.INVALID_CHAR,ch);
                    nextCh();
                    break;
            }
        }
        return token;
    }

	private void readComment(Token token) {
	}


	private void readCharConst(Token token) {
    }

    private void readNumber(Token token) {
        StringBuilder sb = new StringBuilder();
        while (isDigit(ch)) {
            sb.append(ch);
            nextCh();
        }
        try {
            token.val = Integer.parseInt(sb.toString());
            if (token.val == Integer.MIN_VALUE)
                throw new NumberFormatException("Integer.MIN_VALUE not allowed in MicroJava");
        } catch (NumberFormatException e) {
            error(token, Errors.Message.BIG_NUM, sb.toString(), e);
        }
        token.kind = number;
    }

    private void readName(Token token) {
        StringBuilder sb = new StringBuilder();
        while (isAlphaNumeric(ch) || ch == '_') {
            sb.append(ch);
            nextCh();
        }
        token.kind = keywords.getOrDefault(sb.toString(), ident);
        if (token.kind == ident) token.str = sb.toString();
    }

    private void nextCh() {
        try {
            ch = (char) in.read();
            col++;
            if (ch == '\n') {
                line++;
                col = 0;
            }
        } catch (IOException e) {
            ch = EOF;
        }
    }

}
