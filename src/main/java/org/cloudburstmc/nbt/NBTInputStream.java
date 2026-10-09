package org.cloudburstmc.nbt;

import org.cloudburstmc.nbt.util.stream.LimitedDataInput;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static org.cloudburstmc.nbt.NbtUtils.MAX_DEPTH;
import static org.cloudburstmc.nbt.NbtUtils.MAX_READ_SIZE;

public class NBTInputStream implements Closeable {
    private static final NbtList<?>[] EMPTY_LISTS = new NbtList<?>[NbtType.Enum.values().length];

    static {
        for (int id = 0; id < EMPTY_LISTS.length; id++) {
            EMPTY_LISTS[id] = emptyList(NbtType.byId(id));
        }
    }

    private final LimitedDataInput input;
    private final boolean internKeys;
    private final boolean internValues;
    private boolean closed = false;

    public NBTInputStream(DataInput input, boolean internKeys, boolean internValues) {
        this(input, internKeys, internValues, MAX_READ_SIZE);
    }

    public NBTInputStream(DataInput input, boolean internKeys, boolean internValues, long maxReadSize) {
        Objects.requireNonNull(input, "input");
        if (input instanceof LimitedDataInput) {
            this.input = (LimitedDataInput) input;
        } else {
            this.input = new LimitedDataInput(input, maxReadSize);
        }
        this.internKeys = internKeys;
        this.internValues = internValues;
    }

    public NBTInputStream(DataInput input) {
        this(input, false, false);
    }

    public NBTInputStream(DataInput input, long maxReadSize) {
        this(input, false, false, maxReadSize);
    }

    public Object readTag() throws IOException {
        return readTag(MAX_DEPTH);
    }

    public Object readTag(int maxDepth) throws IOException {
        if (closed) {
            throw new IllegalStateException("Trying to read from a closed reader!");
        }
        int typeId = input.readUnsignedByte();
        NbtType<?> type = NbtType.byId(typeId);
        input.readUTF(); // Root tag name

        return deserialize(type, maxDepth);
    }

    public <T> T readValue(NbtType<T> type) throws IOException {
        return readValue(type, MAX_DEPTH);
    }

    @SuppressWarnings("unchecked")
    public <T> T readValue(NbtType<T> type, int maxDepth) throws IOException {
        if (closed) {
            throw new IllegalStateException("Trying to read from a closed reader!");
        }

        return (T) deserialize(type, maxDepth);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object deserialize(NbtType<?> type, int maxDepth) throws IOException {
        if (maxDepth < 0) {
            throw new IllegalArgumentException("NBT compound is too deeply nested");
        }

        switch (type.getEnum()) {
            case END:
                return null;
            case BYTE:
                return input.readByte();
            case SHORT:
                return input.readShort();
            case INT:
                if (this.internValues) {
                    return IntegerInternPool.intern(input.readInt());
                } else {
                    return input.readInt();
                }
            case LONG:
                return input.readLong();
            case FLOAT:
                if (this.internValues) {
                    return FloatInternPool.intern(input.readFloat());
                } else {
                    return input.readFloat();
                }
            case DOUBLE:
                return input.readDouble();
            case BYTE_ARRAY:
                return input.readBytes(readArrayLength(Byte.BYTES));
            case STRING:
                return readString(this.internValues);
            case COMPOUND:
                NbtType<?> nbtType = NbtType.byId(input.readUnsignedByte());
                if (nbtType == NbtType.END) {
                    return NbtMap.EMPTY;
                }
                String name = readString(this.internKeys);
                Object value = deserialize(nbtType, maxDepth - 1);
                nbtType = NbtType.byId(input.readUnsignedByte());
                if (nbtType == NbtType.END) {
                    return new NbtMap(Collections.singletonMap(name, value));
                }
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put(name, value);
                do {
                    map.put(readString(this.internKeys), deserialize(nbtType, maxDepth - 1));
                } while ((nbtType = NbtType.byId(input.readUnsignedByte())) != NbtType.END);
                return new NbtMap(map);
            case LIST:
                int typeId = input.readUnsignedByte();
                NbtType<?> listType = NbtType.byId(typeId);
                int listLength = input.readInt();
                if (listType.getEnum() == NbtType.Enum.END || listLength <= 0) {
                    return EMPTY_LISTS[typeId];
                }
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < listLength; i++) {
                    list.add(deserialize(listType, maxDepth - 1));
                }
                return new NbtList(listType, list);
            case INT_ARRAY:
                int arraySize = readArrayLength(Integer.BYTES);
                int[] ints = new int[LimitedDataInput.initialCapacity(arraySize, Integer.BYTES)];
                for (int i = 0; i < arraySize; i++) {
                    if (i == ints.length) {
                        ints = Arrays.copyOf(ints, (int) Math.min(arraySize, 2L * i));
                    }
                    ints[i] = input.readInt();
                }
                return ints;
            case LONG_ARRAY:
                arraySize = readArrayLength(Long.BYTES);
                long[] longs = new long[LimitedDataInput.initialCapacity(arraySize, Long.BYTES)];
                for (int i = 0; i < arraySize; i++) {
                    if (i == longs.length) {
                        longs = Arrays.copyOf(longs, (int) Math.min(arraySize, 2L * i));
                    }
                    longs[i] = input.readLong();
                }
                return longs;
        }

        throw new IllegalArgumentException("Unknown type " + type);
    }

    private int readArrayLength(int elementSize) throws IOException {
        int length = input.readInt();
        if (length < 0) {
            throw new IOException("Negative array length: " + length);
        }
        input.tryReadArray(Math.multiplyExact((long) length, (long) elementSize));
        return length;
    }

    private String readString(boolean intern) throws IOException {
        String string = input.readUTF();
        if (string.isEmpty()) {
            return "";
        }
        return intern ? string.intern() : string;
    }

    private static <E> NbtList<E> emptyList(NbtType<E> type) {
        return new NbtList<>(type, Collections.emptyList());
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        if (input != null) {
            input.close();
        }
    }
}
