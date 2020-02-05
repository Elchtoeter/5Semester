package ssw.mj.impl;

import ssw.mj.codegen.Code;
import ssw.mj.codegen.Label;

import java.util.ArrayList;
import java.util.List;

public final class LabelImpl extends Label {

    private List<Integer> fixupList;
    private int adr;

    public LabelImpl(Code code) {
        super(code);
        fixupList = new ArrayList<>();
        adr = -1;
    }

    private boolean isDefined() {
        return fixupList == null;
    }

    public void here() {
        if (isDefined()) {
            throw new IllegalStateException("nothing to fixup");
        }
        for (int pos : fixupList) {
            code.put2(pos, code.pc - (pos - 1));
        }
        fixupList = null;
        adr = code.pc;
    }

    public void put() {
        if (isDefined()) {
            code.put2(adr - (code.pc - 1));
        } else {
            fixupList.add(code.pc);
            code.put2(0);
        }
    }


}
