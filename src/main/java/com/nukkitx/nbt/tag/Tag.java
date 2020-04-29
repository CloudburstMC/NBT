package com.nukkitx.nbt.tag;

import javax.annotation.Nonnull;

public abstract class Tag<T> implements Comparable<Tag<T>> {
    private final String name;

    public Tag(String name) {
        this.name = name;
    }

    public abstract T getValue();

    public String getName() {
        return name;
    }

    public abstract Tag<T> rename(String newName);

    @Override
    public int compareTo(@Nonnull Tag<T> other) {
        if (equals(other)) {
            return 0;
        } else {
            if (other.getName().equals(getName())) {
                throw new IllegalStateException("Cannot compare two Tags with the same name but different values for sorting");
            } else {
                return getName().compareTo(other.getName());
            }
        }
    }

    @Override
    public String toString() {
        String append = ": ";
        if (name != null && !name.isEmpty()) {
            append = "(\"" + this.getName() + "\")" + append;
        }
        return append;
    }
}
