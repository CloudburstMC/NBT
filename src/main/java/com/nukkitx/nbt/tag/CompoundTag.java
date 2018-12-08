package com.nukkitx.nbt.tag;

import com.nukkitx.nbt.CompoundTagBuilder;

import java.util.*;
import java.util.function.Consumer;

public class CompoundTag extends Tag<Map<String, Tag<?>>> {
    private final Map<String, Tag<?>> value;

    public CompoundTag(String name, Map<String, Tag<?>> value) {
        super(name);
        this.value = Collections.unmodifiableMap(new HashMap<>(Objects.requireNonNull(value, "value")));
    }

    public static CompoundTag createFromList(String name, List<Tag<?>> list) {
        Map<String, Tag<?>> map = new HashMap<>();
        for (Tag<?> tag : list) {
            if (tag.getName() == null || tag.getName().isEmpty()) {
                throw new IllegalArgumentException("Tag " + tag + " does not have a name.");
            }
            map.put(tag.getName(), tag);
        }
        return new CompoundTag(name, map);
    }

    @Override
    public Map<String, Tag<?>> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundTag that = (CompoundTag) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), value);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TAG_Compound").append(super.toString()).append(value.size()).append(" entries\r\n(\r\n");
        for (Tag entry : value.values()) {
            builder.append("   ").append(entry.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
        }
        builder.append(")");
        return builder.toString();
    }

    public CompoundTagBuilder toBuilder() {
        return CompoundTagBuilder.from(this);
    }

    public <T extends Tag<?>> T get(String key) {
        return (T) value.get(key);
    }

    public <T extends Tag<?>> void listen(String key, Consumer<T> listener) {
        if (value.containsKey(key)) {
            try {
                listener.accept((T) value.get(key));
            } catch (ClassCastException e) {
                // ignore
            }
        }
    }
}
