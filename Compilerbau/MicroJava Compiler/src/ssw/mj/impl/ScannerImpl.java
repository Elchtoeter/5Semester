package ssw.mj.impl;

import ssw.mj.Errors;
import ssw.mj.Scanner;
import ssw.mj.Token;
import ssw.mj.Token.Kind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import static ssw.mj.Token.Kind.*;

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
        in = new BufferedReader(r);
        col = 0;
        line = 1;
        nextCh();
    }

    @Override
    public Token next() {
        while (ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t') nextCh();
        Token token = new Token(none, line, col);

        switch (ch) {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                readName(token);
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                readNumber(token);
                break;
            case '\'':
                readCharConst(token);
                break;
            case ';':
                token.kind = semicolon;
                nextCh();
                break;
            case ',':
                token.kind = comma;
                nextCh();
                break;
            case '.':
                token.kind = period;
                nextCh();
                break;
            case EOF:
                token.kind = eof;
                nextCh();
                break;
            case '{':
                token.kind = lbrace;
                nextCh();
                break;
            case '}':
                token.kind = rbrace;
                nextCh();
                break;
            case '(':
                token.kind = lpar;
                nextCh();
                break;
            case ')':
                token.kind = rpar;
                nextCh();
                break;
            case '[':
                token.kind = lbrack;
                nextCh();
                break;
            case ']':
                token.kind = rbrack;
                nextCh();
                break;
            case '&':
                nextCh();
                if (ch == '&') {
                    token.kind = and;
                    nextCh();
                } else {
                    error(token, Errors.Message.INVALID_CHAR, '&');
                }
                break;
            case '|':
                nextCh();
                if (ch == '|') {
                    token.kind = or;
                    nextCh();
                } else {
                    error(token, Errors.Message.INVALID_CHAR, '|');
                }
                break;
            case '!':
                nextCh();
                if (ch == '=') {
                    token.kind = neq;
                    nextCh();
                } else {
                    error(token, Errors.Message.INVALID_CHAR, '!');
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
                error(token, Errors.Message.INVALID_CHAR, ch);
                nextCh();
                break;
        }
        return token;
    }

    private void readComment(Token token) {
        int openComments = 1;
        nextCh();
        while (openComments > 0) {
            switch (ch) {
                case '/':
                    nextCh();
                    if (ch == '*') {
                        openComments++;
                        nextCh();
                    }
                    break;
                case '*':
                    nextCh();
                    if (ch == '/') {
                        openComments--;
                        nextCh();
                    }
                    break;
                case EOF:
                    error(token, Errors.Message.EOF_IN_COMMENT);
                    openComments = 0;
                    break;
                default:
                    nextCh();
            }
        }
    }


    private void readCharConst(Token token) {
        nextCh();
        token.kind = charConst;
        switch (ch) {
            case '\n':
            case '\r':
                error(token, Errors.Message.ILLEGAL_LINE_END);
                nextCh();
                return;
            case '\'':
                error(token, Errors.Message.EMPTY_CHARCONST);
                nextCh();
                return;
            case '\\':
                nextCh();
                switch (ch) {
                    case 'r':
                        token.val = '\r';
                        break;
                    case 'n':
                        token.val = '\n';
                        break;
                    case '\'':
                        token.val = '\'';
                        break;
                    case '\\':
                        token.val = '\\';
                        break;
                    default:
                        error(token, Errors.Message.UNDEFINED_ESCAPE, ch);
                        break;
                }
                break;
            default:
                token.val = ch;
        }
        nextCh();
        if (ch != '\'') {
            error(token, Errors.Message.MISSING_QUOTE);
            return;
        }
        nextCh();
    }

    private void readNumber(Token token) {
        StringBuilder sb = new StringBuilder();
        while ('0' <= ch && ch <= '9') {
            sb.append(ch);
            nextCh();
        }
        try {
            token.val = Integer.parseInt(sb.toString());
            if (token.val == Integer.MIN_VALUE)
                throw new NumberFormatException("Integer.MIN_VALUE not allowed in MicroJava");
        } catch (NumberFormatException e) {
            error(token, Errors.Message.BIG_NUM, sb.toString());
        }
        token.kind = number;
    }

    private void readName(Token token) {
        StringBuilder sb = new StringBuilder();
        while ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || '0' <= ch && ch <= '9' || ch == '_') {
            sb.append(ch);
            nextCh();
        }
        token.kind = keywords.getOrDefault(sb.toString(), ident);
        if (token.kind == ident) token.str = sb.toString();
    }

    private void nextCh() {
        try {
            ch = (char) in.read();
            if (ch != EOF)
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
