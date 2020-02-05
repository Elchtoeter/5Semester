package ssw.mj.impl;

import ssw.mj.symtab.Struct;
import ssw.mj.symtab.Tab;

public final class StructImpl extends Struct {

    public StructImpl(Kind kind) {
        super(kind);
    }

    public StructImpl(StructImpl elemType) {
        super(elemType);
    }

    @Override
    public boolean compatibleWith(StructImpl other) {
        if (this.equals(other)) {
            return true;
        }
        if (this == Tab.nullType) {
            if (other.isRefType()) {
                return true;
            }
        }
        if (other == Tab.nullType) {
            return this.isRefType();
        }
        return false;
    }

    @Override
    public boolean assignableTo(StructImpl dest) {
        if (this.equals(dest)) {
            return true;
        }
        if (this == Tab.nullType) {
            if (dest.isRefType()) return true;
        }
        if (this.kind == Kind.Arr) {
            if (dest.kind == Kind.Arr) {
                return dest.elemType == Tab.noType;
            }
        }
        return false;
    }

    public boolean isRefType() {
        if (kind == Kind.Class) return true;
        return kind == Kind.Arr;
    }

    public boolean equals(StructImpl other) {
        return kind == Kind.Arr ? other.kind == Kind.Arr && elemType.equals(other.elemType) : this == other;
    }

}
