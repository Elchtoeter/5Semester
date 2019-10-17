package ssw.mj.impl;

import java.io.IOException;
import java.io.Reader;

import ssw.mj.Scanner;
import ssw.mj.Token;
import ssw.mj.Token.Kind;

public final class ScannerImpl extends Scanner {

	// TODO Exercise 2: implementation of scanner

	public ScannerImpl(Reader r) {
		super(r);
	}

	@Override
	public Token next() {
		while (ch <= ' ') nextCh();
		Token token = new Token(Kind.none, line, col);
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
			case ';':
				token.kind = Kind.semicolon;
				break;
			case '.':
				token.kind = Kind.period;
				break;
			case EOF:
				token.kind = Kind.eof;
				break;
			case '&':
				nextCh();
				if (ch == '&'){
					nextCh();
					token.kind = Kind.and;
				}
				break;
			case '&':
				nextCh();
				if (ch == '&'){
					nextCh();
					token.kind = Kind.and;
				}
				break;
			case '&':
				nextCh();
				if (ch == '&'){
					nextCh();
					token.kind = Kind.and;
				}
				break;
			case '&':
				nextCh();
				if (ch == '&'){
					nextCh();
					token.kind = Kind.and;
				}
				break;
			case '/':
			case '=':
				nextCh();
				if (ch == '=') {
					token.kind = Kind.eql;
				} else {
					token.kind = Kind.assign;
				}
				break;
			default:
				nextCh();
				break;
		}
	}

	private void readNumber(Token token) {
	}

	private void readName(Token token) {
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
