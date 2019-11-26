package com.nukkitx.nbt.tag;

public abstract class NumberTag<T extends Number> extends Tag<T> {

    NumberTag(String name) {
        super(name);
    }
}
