package com.nukkitx.nbt.tag;

import java.util.Objects;

public class FloatTag extends NumberTag<Float> {
    private final float value;

    public FloatTag(String name, float value) {
        super(name);
        this.value = value;
    }

    public float getPrimitiveValue() {
        return value;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public FloatTag rename(String newName) {
        return new FloatTag(newName, value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloatTag that = (FloatTag) o;
        return value == that.value &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public String toString() {
        return "TAG_Float" + super.toString() + value;
    }
}
