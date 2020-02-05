package ssw.mj.impl;

import ssw.mj.Errors;
import ssw.mj.Parser;
import ssw.mj.Scanner;
import ssw.mj.Token.Kind;
import ssw.mj.codegen.Code;
import ssw.mj.codegen.Code.CompOp;
import ssw.mj.codegen.Operand;
import ssw.mj.symtab.Obj;
import ssw.mj.symtab.Struct;
import ssw.mj.symtab.Tab;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Stack;

import static ssw.mj.Errors.Message.*;
import static ssw.mj.Token.Kind.print;
import static ssw.mj.Token.Kind.read;
import static ssw.mj.Token.Kind.*;
import static ssw.mj.codegen.Code.OpCode.*;
import static ssw.mj.symtab.Obj.Kind.Type;

public final class ParserImpl extends Parser {

    private static final EnumSet<Kind> firstTSAssignOp = EnumSet.of
            (assign, plusas, minusas, timesas, slashas, remas);
    private static final EnumSet<Kind> firstTSExpr = EnumSet.of
            (minus, ident, number, charConst, Kind.new_, lpar);

    private static final EnumSet<Kind> catchDecl = EnumSet.of(eof, final_, class_, lbrace, semicolon);
    private static final EnumSet<Kind> catchStat = EnumSet.of(eof, if_, while_, break_, compare_, read, print, semicolon, compare_);


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

        program.locals = tab.curScope.locals();
        check(rbrace);
        code.dataSize = tab.curScope.nVars();
        tab.closeScope();
    }

    private void recoverDecl() {
        error(INVALID_DECL);
        while (true) {
            scan();
            if (catchDecl.contains(sym))
                if (sym != ident || tab.find(t.str).kind == Type) {
                    break;
                }
        }
        if (sym == semicolon) {
            scan();
        }
    }

    private void ClassDecl() {
        scan();
        check(ident);
        Obj clas = tab.insert(Type, t.str, new StructImpl(Struct.Kind.Class));
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
        if (slo.kind != Type) {
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
        Block(method, new Stack<>());
        method.locals = tab.curScope.locals();
        code.exitMethod(method);
        tab.closeScope();
    }

    private void recoverMeth() {
        error(METH_DECL);
        while (sym != eof && sym != void_ && sym != ident
                && tab.find(sym.label()).kind != Type) {
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

    private void Block(Obj currMethod, Stack<LabelImpl> breakLabels) {
        check(lbrace);
        while (sym != rbrace && sym != eof) {
            Statement(currMethod, breakLabels);
        }
        check(rbrace);
    }

    private void Statement(Obj currMethod, Stack<LabelImpl> breakLabels) {
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
                            ActPars(designator);
                            code.call(designator);
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
                Operand condition = Condition();
                check(rpar);
                final LabelImpl endLabel = new LabelImpl(code);
                code.fJump(condition);
                condition.tLabel.here();
                Statement(currMethod, breakLabels);
                if (sym == else_) {
                    scan();
                    code.jump(endLabel);
                    condition.fLabel.here();
                    Statement(currMethod, breakLabels);
                } else {
                    condition.fLabel.here();
                }
                endLabel.here();
                break;
            case while_:
                scan();
                check(lpar);
                final LabelImpl topLabel = new LabelImpl(code);
                topLabel.here();
                final Operand whileCond = Condition();
                check(rpar);
                code.fJump(whileCond);
                whileCond.tLabel.here();
                breakLabels.push(whileCond.fLabel);
                Statement(currMethod, breakLabels);
                code.jump(topLabel);
                breakLabels.pop().here();
                break;
            case break_:
                scan();
                if (breakLabels.isEmpty()) error(Errors.Message.NO_LOOP);
                else code.jump(breakLabels.peek());

                check(semicolon);
                break;
            case compare_:
                scan();
                check(lpar);
                final LabelImpl endL = new LabelImpl(code);
                final Operand lessThan = new Operand(CompOp.lt, code);
                final Operand equals = new Operand(CompOp.eq, code);
                final Operand expX = Expr();
                if (expX.type != Tab.intType) error(NO_INT_OP);
                code.load(expX);
                check(comma);
                final Operand expY = Expr();
                if (expY.type != Tab.intType) error(NO_INT_OP);
                code.load(expY);
                code.put(dup2);
                code.fJump(lessThan);
                check(rpar);
                Block(currMethod, breakLabels);
                code.put(pop);
                code.put(pop);
                code.jump(endL);
                lessThan.fLabel.here();
                code.fJump(equals);
                Block(currMethod, breakLabels);
                code.jump(endL);
                equals.fLabel.here();
                Block(currMethod, breakLabels);
                endL.here();
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
                Block(currMethod, breakLabels);
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
        errorDist = 0;
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


    private void ActPars(Operand meth) {
        check(lpar);

        if (meth.kind != Operand.Kind.Meth) {
            error(Errors.Message.NO_METH);
            meth.obj = tab.noObj;
        }

        int actPars = 0, formPars = meth.obj.nPars;

        Iterator<Obj> parameters = meth.obj.locals.values().iterator();

        if (firstTSExpr.contains(sym)) {
            Operand op = Expr();
            code.load(op);
            actPars++;

            if (parameters.hasNext()
                    && !op.type.assignableTo(parameters.next().type))
                error(Errors.Message.PARAM_TYPE);

            while (sym == comma) {
                scan();

                op = Expr();
                code.load(op);
                actPars++;

                if (parameters.hasNext()
                        && !op.type.assignableTo(parameters.next().type))
                    error(Errors.Message.PARAM_TYPE);
            }
        }

        if (actPars > formPars)
            error(Errors.Message.MORE_ACTUAL_PARAMS);
        else if (actPars < formPars)
            error(Errors.Message.LESS_ACTUAL_PARAMS);

        check(rpar);
    }

    private Operand Condition() {
        final Operand cTerm = CondTerm();
        while (sym == or) {
            code.tJump(cTerm);
            scan();
            cTerm.fLabel.here();
            final Operand secondTerm = CondTerm();
            cTerm.fLabel = secondTerm.fLabel;
            cTerm.op = secondTerm.op;
        }
        return cTerm;
    }

    private Operand CondTerm() {
        final Operand cFact = CondFact();
        while (sym == and) {
            code.fJump(cFact);
            scan();
            final Operand secondFact = CondFact();
            cFact.op = secondFact.op;
        }
        return cFact;
    }

    private Operand CondFact() {
        final Operand expr = Expr();
        code.load(expr);
        final CompOp comp = Relop();
        final Operand secondExp = Expr();
        code.load(secondExp);

        if (!expr.type.compatibleWith(secondExp.type)) error(INCOMP_TYPES);
        if ((expr.type.isRefType() || secondExp.type.isRefType()) && !(comp == CompOp.eq || comp == CompOp.ne))
            error(EQ_CHECK);

        return new Operand(comp, code);
    }

    private CompOp Relop() {
        if (sym == eql) {
            scan();
            return CompOp.eq;
        } else if (sym == neq) {
            scan();
            return CompOp.ne;
        } else if (sym == gtr) {
            scan();
            return CompOp.gt;
        } else if (sym == geq) {
            scan();
            return CompOp.ge;
        } else if (sym == lss) {
            scan();
            return CompOp.lt;
        } else if (sym == leq) {
            scan();
            return CompOp.le;
        }
        error(Errors.Message.REL_OP);
        return CompOp.eq;
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
        Operand x;
        switch (sym) {
            case ident:
                x = Designator();
                if (sym == lpar) {
                    if (x.obj.type == Tab.noType) error(INVALID_CALL);
                    ActPars(x);
                    code.call(x);
                }
                break;
            case number:
                scan();
                x = new Operand(t.val);
                x.type = Tab.intType;
                break;
            case charConst:
                scan();
                x = new Operand(t.val);
                x.type = Tab.charType;
                break;
            case new_:
                scan();
                check(ident);

                final Obj obj = tab.find(t.str);
                if (obj.kind != Type) error(NO_TYPE);
                StructImpl type = obj.type;

                if (sym == lbrack) {
                    scan();
                    final Operand size = Expr();
                    if (size.type != Tab.intType) error(ARRAY_SIZE);

                    code.load(size);
                    code.put(Code.OpCode.newarray);

                    code.put(type == Tab.charType ? 0 : 1);

                    type = new StructImpl(type);
                    check(rbrack);
                } else if (type.kind == Struct.Kind.Class) {
                    code.put(Code.OpCode.new_);
                    code.put2(obj.type.nrFields());
                } else error(NO_CLASS_TYPE);

                x = new Operand(type);
                break;
            case lpar:
                scan();
                x = Expr();
                check(rpar);
                break;
            default:
                error(INVALID_FACT);
                x = new Operand(Tab.noType);
                break;
        }
        return x;
    }

    private Operand Designator() {
        check(ident);
        Operand des = new Operand(tab.find(t.str), this);
        while (true) {
            if (sym == period) {
                if (des.type.kind != Struct.Kind.Class) error(NO_CLASS);
                scan();
                code.load(des);
                check(ident);
                final Obj obj = tab.findMember(t.str, des.type);
                des.kind = Operand.Kind.Fld;
                des.type = obj.type;
                des.adr = obj.adr;
            } else if (sym == lbrack) {
                code.load(des);
                scan();
                final Operand index = Expr();
                if (des.obj != null || des.type.kind != Struct.Kind.Arr) error(NO_ARRAY);
                if (index.type != Tab.intType) error(ARRAY_INDEX);
                code.load(index);
                des.kind = Operand.Kind.Elem;
                des.type = des.type.elemType;
                check(rbrack);
            } else break;
        }
        return des;
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
