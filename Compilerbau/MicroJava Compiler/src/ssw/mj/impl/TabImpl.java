package ssw.mj.impl;

import ssw.mj.Parser;
import ssw.mj.symtab.Obj;
import ssw.mj.symtab.Scope;
import ssw.mj.symtab.Struct;
import ssw.mj.symtab.Tab;

import static ssw.mj.Errors.Message.*;
import static ssw.mj.symtab.Obj.Kind.*;

public final class TabImpl extends Tab {


    public TabImpl(Parser p) {
        super(p);
        init();
    }

    private void init() {
        curScope = new Scope(null);
        curLevel = 0;

        noObj = new Obj(Var, "none", noType);
        insert(Type, "int", intType);
        insert(Type, "char", charType);
        insert(Con, "null", nullType);

		/*
		Declaration of predefined methods.
		 */

        chrObj = insert(Meth, "chr", charType);
        openScope();
        insert(Var, "i", intType);
        chrObj.nPars = curScope.nVars();
        chrObj.locals = curScope.locals();
        closeScope();

        ordObj = insert(Meth, "ord", intType);
        openScope();
        insert(Var, "ch", charType);
        ordObj.nPars = curScope.nVars();
        ordObj.locals = curScope.locals();
        closeScope();

        lenObj = insert(Meth, "len", intType);
        openScope();
        insert(Var, "arr", new StructImpl(noType));
        lenObj.nPars = curScope.nVars();
        lenObj.locals = curScope.locals();
        closeScope();

        curLevel = -1;
    }

    public void openScope() {
        curScope = new Scope(curScope);
        curLevel++;
    }

    public void closeScope() {
        curScope = curScope.outer();
        curLevel--;
    }

    public Obj insert(Obj.Kind kind, String name, StructImpl type) {
        if (curScope.locals().containsKey(name)) {
            parser.error(DECL_NAME, name);
        }

        final Obj obj = new Obj(kind, name, type);
        if (kind == Obj.Kind.Var) {
            obj.adr = curScope.nVars();
            obj.level = curLevel;
        }

        curScope.insert(obj);
        return obj;
    }

    public Obj find(String name) {
        final Obj global = curScope.findGlobal(name);
        if (global == null) {
            parser.error(NOT_FOUND, name);
            return noObj;
        }
        return global;
    }

    public Obj findMember(String name, Struct type) {
        if (type == null || type.kind != Struct.Kind.Class) {
            parser.error(NO_FIELD, name);
            return noObj;
        }

        final Obj field = type.fields.get(name);
        if (field == null) {
            parser.error(NO_FIELD, name);
            return noObj;
        }
        return field;
    }
}
