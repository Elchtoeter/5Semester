package ssw.mj.impl;

import ssw.mj.Errors;
import ssw.mj.Parser;
import ssw.mj.codegen.Code;
import ssw.mj.codegen.Operand;
import ssw.mj.symtab.Tab;

import static ssw.mj.Errors.*;
import static ssw.mj.Errors.Message.*;
import static ssw.mj.codegen.Code.OpCode.*;
import static ssw.mj.codegen.Operand.Kind.*;

public final class CodeImpl extends Code {

	public CodeImpl(Parser p) {
		super(p);
	}

	public void loadNoStack(Operand op) {
		switch (op.kind) {
			case Con:
				loadConst(op.val);
				break;
			case Local:
				localAddr(op, load_0, load_1, load_2, load_3, load);
				break;
			case Static:
				put(getstatic);
				put2(op.adr);
				break;
			case Stack:
				break; // nothing to do (already loaded)
			case Fld:
				put(getfield);
				put2(op.adr);
				break;
			case Elem:
				if (op.type == TabImpl.charType) {
					put(baload);
				} else {
					put(aload);
				}
				break;
			default:
				parser.error(NO_VAL);
		}
		// op.kind = Operand.Kind.Stack;
	}

	private void localAddr(Operand op, OpCode load_0, OpCode load_1, OpCode load_2, OpCode load_3, OpCode load) {
		switch (op.adr) {
			case 0:
				put(load_0);
				break;
			case 1:
				put(load_1);
				break;
			case 2:
				put(load_2);
				break;
			case 3:
				put(load_3);
				break;
			default:
				put(load);
				put(op.adr);
				break;
		}
	}

	public void load(Operand op) {
		loadNoStack(op);
		op.kind = Stack;
	}

	public void loadConst(int val) {
		switch (val) {
			case -1:
				put(const_m1);
				break;
			case 0:
				put(const_0);
				break;
			case 1:
				put(const_1);
				break;
			case 2:
				put(const_2);
				break;
			case 3:
				put(const_3);
				break;
			case 4:
				put(const_4);
				break;
			case 5:
				put(const_5);
				break;
			default:
				put(const_);
				put4(val);
				break;
		}
	}

	public void store(Operand x) {
		switch (x.kind) {
			case Local:
				localAddr(x, store_0, store_1, store_2, store_3, store);
				break;
			case Static:
				put(putstatic);
				put2(x.adr);
				break;
			case Fld:
				put(putfield);
				put2(x.adr);
				break;
			case Elem:
				if (x.type == Tab.charType) {
					put(bastore);
				} else {
					put(astore);
				}
				break;
			default:
				parser.error(NO_VAL);
		}
	}

	public void assign(Operand x, Operand y) {
		loadNoStack(y);
		store(x);
	}

	private void increment(Operand op, int amount) {
		dup(op);
		if (op.kind == Local) {
			put(inc);
			put(op.adr);
			put(amount);
		} else {
			loadNoStack(op);
			loadNoStack(new Operand(amount));
			put(add);
			store(op);
		}
	}

	public void dup(Operand op) {
		if (op.kind == Operand.Kind.Fld) {
			put(OpCode.dup);
		} else if (op.kind == Operand.Kind.Elem) {
			put(OpCode.dup2);
		}
	}

	public void increment(Operand op) {
		increment(op, 1);
	}

	public void decrement(Operand op) {
		increment(op, -1);
	}


}
