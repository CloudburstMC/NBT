package com.nukkitx.nbt.tag;

import java.util.Arrays;
import java.util.Objects;

public class LongArrayTag extends Tag<long[]> {
    private final long[] value;

    public LongArrayTag(String name, long[] value) {
        super(name);
        this.value = value;
    }

    @Override
    public long[] getValue() {
        return value;
    }

    @Override
    public LongArrayTag rename(String newName) {
        return new LongArrayTag(newName, value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongArrayTag that = (LongArrayTag) o;
        return Arrays.equals(value, that.value) &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public String toString() {
        return "TAG_Long_Array" + super.toString() + "[" + value.length + " values]";
    }
}
