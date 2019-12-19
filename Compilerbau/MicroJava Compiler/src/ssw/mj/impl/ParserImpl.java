package ssw.mj.impl;

import ssw.mj.Errors;
import ssw.mj.Parser;
import ssw.mj.Scanner;
import ssw.mj.Token.Kind;
import ssw.mj.codegen.Code;
import ssw.mj.codegen.Operand;
import ssw.mj.symtab.Obj;
import ssw.mj.symtab.Struct;
import ssw.mj.symtab.Tab;

import java.util.EnumSet;

import static ssw.mj.Errors.Message.*;
import static ssw.mj.Token.Kind.print;
import static ssw.mj.Token.Kind.read;
import static ssw.mj.Token.Kind.*;
import static ssw.mj.codegen.Code.OpCode.*;

public final class ParserImpl extends Parser {

    private static final EnumSet<Kind> firstTSAssignOp = EnumSet.of
            (assign, plusas, minusas, timesas, slashas, remas);
    private static final EnumSet<Kind> firstTSExpr = EnumSet.of
            (minus, ident, number, charConst, Kind.new_, lpar);
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
        Obj program = tab.insert(Obj.Kind.Prog, t.str, Tab.noType);

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
        while (sym != eof && sym != rbrace) {
            MethodDecl();
        }

        final Obj main = tab.curScope.findLocal("main");
        if (main == null || main.kind != Obj.Kind.Meth) {
            error(METH_NOT_FOUND, "main");
        } else {
            code.mainpc = main.adr;
        }
        check(rbrace);
        program.locals = tab.curScope.locals();
        tab.closeScope();
    }

    private void recoverDecl() {
        error(INVALID_DECL);
        while (true){
            scan();
           if(catchDecl.contains(sym))
               if (sym != ident || tab.find(t.str).kind == Obj.Kind.Type) {
               break;}
        }
        if (sym == semicolon) {
            scan();
        }
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
        StructImpl type = Tab.noType;
        switch (sym) {
            case ident:
                type = Type();
                break;
            case void_:
                scan();
                break;
            default:
                recoverMeth();
                break;
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
        code.dataSize = tab.curScope.nVars();
        code.enterMethod(method, tab.curScope.nVars());
        Block(method);
        method.locals = tab.curScope.locals();
        code.exitMethod(method);
        tab.closeScope();
    }

    private void recoverMeth() {
        error(METH_DECL);
        while (sym != eof && sym != void_ &&
                sym != ident && tab.find(sym.label()).kind != Obj.Kind.Type) {
            scan();
        }
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

    private void Block(Obj currMethod) {
        check(lbrace);
        while (sym != rbrace && sym != eof) {
            Statement(currMethod);
        }
        check(rbrace);
    }

    private void Statement(Obj currMethod) {
        switch (sym) {
            case ident:
                final Operand designator = Designator();
                if (firstTSAssignOp.contains(sym)) {
                    final Code.OpCode opCode = Assignop();
                    if (opCode != store && designator.kind == Operand.Kind.Meth) error(NO_VAR);
                    if (opCode != store) {
                        code.dup(designator);
                        code.loadNoStack(designator);
                    }
                    final Operand d2 = Expr();
                    if (designator.obj != null && designator.obj.kind != Obj.Kind.Var) error(NO_VAR);
                    if (opCode == store) {
                        if (d2.type.assignableTo(designator.type)) code.assign(designator, d2);
                        else error(INCOMP_TYPES);
                    } else {
                        if (designator.type != Tab.intType || d2.type != Tab.intType) error(NO_INT_OP);
                        code.load(d2);
                        code.put(opCode);
                        code.store(designator);
                    }
                } else {
                    switch (sym) {
                        case lpar:
                            if (designator.kind != Operand.Kind.Meth) error(NO_METH);
                            ActPars();
                            if (designator.type != Tab.noType) code.put(pop);
                            break;
                        case pplus:
                            if (designator.type != Tab.intType) error(NO_INT);
                            if (designator.obj != null && designator.obj.kind != Obj.Kind.Var) error(NO_VAR);
                            scan();
                            code.increment(designator);
                            break;
                        case mminus:
                            if (designator.type != Tab.intType) error(NO_INT);
                            if (designator.obj != null && designator.obj.kind != Obj.Kind.Var) error(NO_VAR);
                            scan();
                            code.decrement(designator);
                            break;
                        default:
                            error(DESIGN_FOLLOW);
                            break;
                    }
                }
                check(semicolon);
                break;
            case if_:
                scan();
                check(lpar);
                Condition();
                check(rpar);
                Statement(currMethod);
                if (sym == else_) {
                    scan();
                    Statement(currMethod);
                }
                break;
            case while_:
                scan();
                check(lpar);
                Condition();
                check(rpar);
                Statement(currMethod);
                break;
            case break_:
                scan();
                check(semicolon);
                break;
            case compare_:
                scan();
                check(lpar);
                Operand exp = Expr();
                if (exp.type != Tab.intType) error(NO_INT_OP);
                check(comma);
                exp = Expr();
                if (exp.type != Tab.intType) error(NO_INT_OP);
                check(rpar);
                Block(currMethod);
                Block(currMethod);
                Block(currMethod);
                break;
            case return_:
                scan();
                if (firstTSExpr.contains(sym)) {
                    if (currMethod.type == Tab.noType) error(RETURN_VOID);
                    final Operand retVal = Expr();
                    if (!currMethod.type.compatibleWith(retVal.type)) error(RETURN_TYPE);
                    code.load(retVal);
                } else if (currMethod.type != Tab.noType) error(RETURN_NO_VAL);
                code.put(exit);
                code.put(Code.OpCode.return_);
                check(semicolon);
                break;
            case read:
                scan();
                check(lpar);
                final Operand des = Designator();
                switch (des.type.kind) {
                    case Int:
                        code.put(Code.OpCode.read);
                        code.store(des);
                        break;
                    case Char:
                        code.put(bread);
                        code.store(des);
                        break;
                    default:
                        error(READ_VALUE);
                        break;
                }
                check(rpar);
                check(semicolon);
                break;
            case print:
                scan();
                check(lpar);
                final Operand expr = Expr();
                int width = 1;
                if (sym == comma) {
                    scan();
                    check(number);
                    width = t.val;
                }

                switch (expr.type.kind) {
                    case Int:
                        code.load(expr);
                        code.loadConst(0);
                        code.put(Code.OpCode.print);
                        break;
                    case Char:
                        code.load(expr);
                        code.loadConst(width);
                        code.put(bprint);
                        break;
                    default:
                        error(PRINT_VALUE);
                        break;
                }
                check(rpar);
                check(semicolon);
                break;
            case lbrace:
                Block(currMethod);
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
        while (!catchStat.contains(sym)) {
            scan();
        }
        if (sym == semicolon) {
            scan();
        }
    }

    private Code.OpCode Assignop() {
        switch (sym) {
            case assign:
                scan();
                return store;
            case plusas:
                scan();
                return add;
            case minusas:
                scan();
                return sub;
            case timesas:
                scan();
                return mul;
            case slashas:
                scan();
                return div;
            case remas:
                scan();
                return Code.OpCode.rem;
            default:
                error(ASSIGN_OP);
                return nop;
        }
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

    private Operand Expr() {
        final Operand term;
        if (sym == minus) {
            scan();
            term = Term();
            if (term.type != Tab.intType) {
                error(NO_INT_OP);
            }
            if (term.kind == Operand.Kind.Con) {
                term.val = -term.val;
            } else {
                code.load(term);
                code.put(neg);
            }
        } else {
            term = Term();
        }

        while (sym == plus || sym == minus) {
            final Code.OpCode opCode = Addop();
            code.load(term);

            final Operand y = Term();
            if (term.type != Tab.intType || y.type != Tab.intType) error(NO_INT_OP);

            code.load(y);
            code.put(opCode);
        }
        return term;
    }

    private Operand Term() {
        final Operand factor = Factor();
        while (sym == times || sym == slash || sym == Kind.rem) {
            final Code.OpCode opCode = Mulop();
            code.load(factor);
            final Operand y = Factor();
            if (factor.type != Tab.intType || y.type != Tab.intType) error(NO_INT_OP);
            code.load(y);
            code.put(opCode);
        }
        return factor;
    }

    private Operand Factor() {
        Operand f;
        switch (sym) {
            case ident:
                f = Designator();
                if (sym == lpar) {
                    if (f.obj.type == Tab.noType) error(INVALID_CALL);
                    if (f.kind != Operand.Kind.Meth) error(NO_METH);
                    ActPars();
                    code.call(f);
                }
                break;
            case number:
                scan();
                f = new Operand(t.val);
                break;
            case charConst:
                scan();
                f = new Operand(t.val);
                f.type = Tab.charType;
                break;
            case new_:
                scan();
                check(ident);
                final Obj obj = tab.find(t.str);
                if (obj.kind != Obj.Kind.Type) error(NO_TYPE);
                StructImpl type = obj.type;
                if (sym == lbrack) {
                    scan();
                    final Operand size = Expr();
                    if (size.type != Tab.intType) error(ARRAY_SIZE);
                    code.load(size);
                    code.put(newarray);
                    code.put(type == Tab.charType ? 0 : 1);
                    type = new StructImpl(type);
                    check(rbrack);
                } else if (type.kind == Struct.Kind.Class) {
                    code.put(Code.OpCode.new_);
                    code.put2(obj.type.nrFields());
                } else error(NO_CLASS_TYPE);
                f = new Operand(type);
                break;
            case lpar:
                scan();
                f = Expr();
                check(rpar);
                break;
            default:
                error(INVALID_FACT);
                f = new Operand(0);
                break;
        }
        return f;
    }

    private Operand Designator() {
        check(ident);
        Operand d = new Operand(tab.find(t.str), this);
        while (true) {
            if (sym == period) {
                if (d.type.kind != Struct.Kind.Class) error(NO_CLASS);
                scan();
                code.load(d);
                check(ident);
                final Obj obj = tab.findMember(t.str, d.type);
                d.kind = Operand.Kind.Fld;
                d.type = obj.type;
                d.adr = obj.adr;
            } else if (sym == lbrack) {
                code.load(d);
                scan();
                final Operand index = Expr();
                if (d.obj != null || d.type.kind != Struct.Kind.Arr) error(NO_ARRAY);
                if (index.type != Tab.intType) error(ARRAY_INDEX);
                code.load(index);
                d.kind = Operand.Kind.Elem;
                d.type = d.type.elemType;
                check(rbrack);
            } else break;
        }
        return d;
    }

    private Code.OpCode Addop() {
        switch (sym) {
            case plus:
                scan();
                return add;
            case minus:
                scan();
                return sub;
            default:
                error(ADD_OP);
                return nop;
        }
    }

    private Code.OpCode Mulop() {
        switch (sym) {
            case times:
                scan();
                return mul;
            case slash:
                scan();
                return div;
            case rem:
                scan();
                return Code.OpCode.rem;
            default:
                error(MUL_OP);
                return nop;
        }
    }
}
