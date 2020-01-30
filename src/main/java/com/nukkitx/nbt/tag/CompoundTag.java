package com.nukkitx.nbt.tag;

import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.util.function.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

@Immutable
@ParametersAreNonnullByDefault
public class CompoundTag extends Tag<Map<String, Tag<?>>> {
    public static final CompoundTag EMPTY = new CompoundTag("", Collections.emptyMap());

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final long[] EMPTY_LONG_ARRAY = new long[0];

    private final Map<String, Tag<?>> value;

    public CompoundTag(String name, Map<String, Tag<?>> value) {
        super(name);
        this.value = Collections.unmodifiableMap(new HashMap<>(Objects.requireNonNull(value, "value")));
    }

    public static CompoundTagBuilder builder() {
        return CompoundTagBuilder.builder();
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

    public CompoundTagBuilder toBuilder() {
        return CompoundTagBuilder.from(this);
    }

    public boolean contains(String key) {
        return this.value.containsKey(key);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Tag<?>> T get(String key) {
        return (T) this.value.get(key);
    }

    public boolean getAsBoolean(String key) {
        return this.getAsBoolean(key, false);
    }

    public boolean getAsBoolean(String key, boolean defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof ByteTag) {
            return ((ByteTag) tag).getAsBoolean();
        }
        return defaultValue;
    }

    public void listenForBoolean(String key, BooleanConsumer consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof ByteTag) {
            consumer.accept(((ByteTag) tag).getAsBoolean());
        }
    }

    public byte getAsByte(String key) {
        return this.getAsByte(key, (byte) 0);
    }

    public byte getAsByte(String key, byte defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof ByteTag) {
            return ((ByteTag) tag).getPrimitiveValue();
        }
        return defaultValue;
    }

    public void listenForByte(String key, ByteConsumer consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof ByteTag) {
            consumer.accept(((ByteTag) tag).getPrimitiveValue());
        }
    }

    public short getAsShort(String key) {
        return this.getAsShort(key, (short) 0);
    }

    public short getAsShort(String key, short defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof ShortTag) {
            return ((ShortTag) tag).getPrimitiveValue();
        }
        return defaultValue;
    }

    public void listenForShort(String key, ShortConsumer consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof ShortTag) {
            consumer.accept(((ShortTag) tag).getPrimitiveValue());
        }
    }

    public int getAsInt(String key) {
        return this.getAsInt(key, 0);
    }

    public int getAsInt(String key, int defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof IntTag) {
            return ((IntTag) tag).getPrimitiveValue();
        }
        return defaultValue;
    }

    public void listenForInt(String key, IntConsumer consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof IntTag) {
            consumer.accept(((IntTag) tag).getPrimitiveValue());
        }
    }

    public long getAsLong(String key) {
        return this.getAsLong(key, 0L);
    }

    public long getAsLong(String key, long defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof LongTag) {
            return ((LongTag) tag).getPrimitiveValue();
        }
        return defaultValue;
    }

    public void listenForLong(String key, LongConsumer consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof LongTag) {
            consumer.accept(((LongTag) tag).getPrimitiveValue());
        }
    }

    public float getAsFloat(String key) {
        return this.getAsFloat(key, 0F);
    }

    public float getAsFloat(String key, float defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof FloatTag) {
            return ((FloatTag) tag).getPrimitiveValue();
        }
        return defaultValue;
    }

    public void listenForFloat(String key, FloatConsumer consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof FloatTag) {
            consumer.accept(((FloatTag) tag).getPrimitiveValue());
        }
    }

    public double getAsDouble(String key) {
        return this.getAsDouble(key, 0D);
    }

    public double getAsDouble(String key, double defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof DoubleTag) {
            return ((DoubleTag) tag).getPrimitiveValue();
        }
        return defaultValue;
    }

    public void listenForDouble(String key, DoubleConsumer consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof DoubleTag) {
            consumer.accept(((DoubleTag) tag).getPrimitiveValue());
        }
    }

    public String getAsString(String key) {
        return this.getAsString(key, "");
    }

    public String getAsString(String key, @Nullable String defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof StringTag) {
            return ((StringTag) tag).getValue();
        }
        return defaultValue;
    }

    public void listenForString(String key, Consumer<String> consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof StringTag) {
            consumer.accept(((StringTag) tag).getValue());
        }
    }

    public byte[] getAsByteArray(String key) {
        return this.getAsByteArray(key, EMPTY_BYTE_ARRAY);
    }

    public byte[] getAsByteArray(String key, @Nullable byte[] defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof ByteArrayTag) {
            return ((ByteArrayTag) tag).getValue();
        }
        return defaultValue;
    }

    public void listenForByteArray(String key, Consumer<byte[]> consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof ByteArrayTag) {
            consumer.accept(((ByteArrayTag) tag).getValue());
        }
    }

    public int[] getAsIntArray(String key) {
        return this.getAsIntArray(key, EMPTY_INT_ARRAY);
    }

    public int[] getAsIntArray(String key, @Nullable int[] defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof IntArrayTag) {
            return ((IntArrayTag) tag).getValue();
        }
        return defaultValue;
    }

    public void listenForIntArray(String key, Consumer<int[]> consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof IntArrayTag) {
            consumer.accept(((IntArrayTag) tag).getValue());
        }
    }

    public long[] getAsLongArray(String key) {
        return this.getAsLongArray(key, EMPTY_LONG_ARRAY);
    }

    public long[] getAsLongArray(String key, @Nullable long[] defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof LongArrayTag) {
            return ((LongArrayTag) tag).getValue();
        }
        return defaultValue;
    }

    public void listenForLongArray(String key, Consumer<long[]> consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof LongArrayTag) {
            consumer.accept(((LongArrayTag) tag).getValue());
        }
    }

    public CompoundTag getAsCompound(String key) {
        return this.getAsCompound(key, EMPTY);
    }

    public CompoundTag getAsCompound(String key, @Nullable CompoundTag defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof CompoundTag) {
            return (CompoundTag) tag;
        }
        return defaultValue;
    }

    public void listenForCompound(String key, Consumer<CompoundTag> consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof CompoundTag) {
            consumer.accept((CompoundTag) tag);
        }
    }

    public <T extends Tag<?>> List<T> getAsList(String key, Class<T> tagClass) {
        return this.getAsList(key, tagClass, Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag<?>> List<T> getAsList(String key, Class<T> tagClass, @Nullable List<T> defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof ListTag && tagClass.isAssignableFrom(((ListTag) tag).getTagClass())) {
            return ((ListTag<T>) tag).getValue();
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag<?>> void listenForList(String key, Class<T> tagClass, Consumer<List<T>> consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof ListTag && tagClass.isAssignableFrom(((ListTag) tag).getTagClass())) {
            consumer.accept(((ListTag<T>) tag).getValue());
        }
    }

    public Number getAsNumber(String key) {
        return getAsNumber(key, 0);
    }

    public Number getAsNumber(String key, Number defaultValue) {
        Tag tag = this.value.get(key);
        if (tag instanceof NumberTag) {
            return ((NumberTag<? extends Number>) tag).getValue();
        }
        return defaultValue;
    }

    public void listenForNumber(String key, NumberConsumer consumer) {
        Tag tag = this.value.get(key);
        if (tag instanceof NumberTag) {
            consumer.accept(((NumberTag<? extends Number>) tag).getValue());
        }
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
        for (Tag<?> entry : value.values()) {
            builder.append("   ").append(entry.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
        }
        builder.append(")");
        return builder.toString();
    }
}
