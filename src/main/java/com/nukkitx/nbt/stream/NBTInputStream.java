package com.nukkitx.nbt.stream;

import com.nukkitx.nbt.TagType;
import com.nukkitx.nbt.tag.*;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.util.*;

import static com.nukkitx.nbt.NbtUtils.MAX_DEPTH;

public class NBTInputStream implements Closeable {
    private final DataInput input;
    private boolean closed = false;

    public NBTInputStream(DataInput input) {
        this.input = Objects.requireNonNull(input, "input");
    }

    public Tag<?> readTag() throws IOException {
        return readTag(0);
    }

    private Tag<?> readTag(int depth) throws IOException {
        if (closed) {
            throw new IllegalStateException("Trying to read from a closed reader!");
        }
        int typeId = input.readUnsignedByte();
        TagType type = TagType.byId(typeId);
        if (type == null) {
            throw new IOException("Invalid encoding ID " + typeId);
        }

        return deserialize(type, false, depth);
    }

    private Tag<?> deserialize(TagType type, boolean skipName, int depth) throws IOException {
        if (depth > MAX_DEPTH) {
            throw new IllegalArgumentException("NBT compound is too deeply nested");
        }

        String tagName = "";
        if (type != TagType.END && !skipName) {
            tagName = input.readUTF();
        }

        switch (type) {
            case END:
                if (depth == 0) {
                    throw new IllegalArgumentException("Found a TAG_End in root tag!");
                }
                return EndTag.INSTANCE;
            case BYTE:
                return new ByteTag(tagName, input.readByte());
            case SHORT:
                return new ShortTag(tagName, input.readShort());
            case INT:
                return new IntTag(tagName, input.readInt());
            case LONG:
                return new LongTag(tagName, input.readLong());
            case FLOAT:
                return new FloatTag(tagName, input.readFloat());
            case DOUBLE:
                return new DoubleTag(tagName, input.readDouble());
            case BYTE_ARRAY:
                int arraySize = input.readInt();
                byte[] byteArray = new byte[arraySize];
                input.readFully(byteArray);
                return new ByteArrayTag(tagName, byteArray);
            case STRING:
                return new StringTag(tagName, input.readUTF());
            case COMPOUND:
                Map<String, Tag<?>> map = new HashMap<>();
                Tag<?> inTag1;
                while ((inTag1 = readTag(depth + 1)) != EndTag.INSTANCE) {
                    map.put(inTag1.getName(), inTag1);
                }
                return new CompoundTag(tagName, map);
            case LIST:
                int inId = input.readUnsignedByte();
                TagType listType = TagType.byId(inId);
                if (listType == null) {
                    throw new IllegalArgumentException("Found invalid type in TAG_List('" + tagName + "'): " + inId);
                }
                List<Tag<?>> list = new ArrayList<>();
                int listLength = input.readInt();
                for (int i = 0; i < listLength; i++) {
                    list.add(deserialize(listType, true, depth + 1));
                }
                //noinspection unchecked
                return new ListTag(tagName, listType.getTagClass(), list);
            case INT_ARRAY:
                arraySize = input.readInt();
                int[] intArray = new int[arraySize];
                for (int i = 0; i < arraySize; i++) {
                    intArray[i] = input.readInt();
                }
                return new IntArrayTag(tagName, intArray);
            case LONG_ARRAY:
                arraySize = input.readInt();
                long[] longArray = new long[arraySize];
                for (int i = 0; i < arraySize; i++) {
                    longArray[i] = input.readLong();
                }
                return new LongArrayTag(tagName, longArray);
        }

        throw new IllegalArgumentException("Unknown type " + type);
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        if (input instanceof Closeable) {
            ((Closeable) input).close();
        }
    }
}
