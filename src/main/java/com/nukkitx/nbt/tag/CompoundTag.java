package com.nukkitx.nbt.tag;

import com.nukkitx.nbt.CompoundTagBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
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

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Tag<?>> T get(String key) {
        return (T) value.get(key);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getAs(String key, Class<T> clazz) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(clazz, "clazz");

        Tag<?> tag = this.value.get(key);
        if (tag != null && clazz.isAssignableFrom(tag.getValue().getClass())) {
            return (T) tag.getValue();
        }
        return null;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> List<T> getListAs(String key, Class<T> clazz) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(clazz, "clazz");

        Tag<?> tag = this.value.get(key);
        if (!(tag instanceof ListTag)) {
            return Collections.emptyList();
        }
        List list = ((ListTag) tag).getValue();
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        Object firstValue = list.get(0);
        if (firstValue != null && clazz.isAssignableFrom(firstValue.getClass())) {
            return (List<T>) list;
        }
        return Collections.emptyList();
    }

    public <T> void listen(String key, Class<T> clazz, Consumer<T> listener) {
        Objects.requireNonNull(listener, "listener");

        T value = this.getAs(key, clazz);
        if (value != null) {
            listener.accept(value);
        }
    }

    public <T> void listenList(String key, Class<T> clazz, Consumer<List<T>> listener) {
        Objects.requireNonNull(listener, "listener");

        listener.accept(this.getListAs(key, clazz));
    }
}
