package com.nukkitx.nbt.stream;

import com.nukkitx.nbt.TagType;
import com.nukkitx.nbt.tag.*;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import static com.nukkitx.nbt.NbtUtils.MAX_DEPTH;

public class NBTOutputStream implements Closeable {
    private final DataOutput output;
    private boolean closed = false;

    public NBTOutputStream(DataOutput output) {
        this.output = Objects.requireNonNull(output, "output");
    }

    public void write(Tag<?> tag) throws IOException {
        Objects.requireNonNull(tag, "tag");
        if (closed) {
            throw new IllegalStateException("closed");
        }
        serialize(tag, false, 0);
    }

    private void serialize(Tag<?> tag, boolean skipHeader, int depth) throws IOException {
        if (depth >= MAX_DEPTH) {
            throw new IllegalArgumentException("Reached depth limit");
        }
        TagType type = TagType.byClass(tag.getClass());

        if (!skipHeader) {
            output.writeByte(type.ordinal() & 0xFF);
            output.writeUTF(tag.getName() == null ? "" : tag.getName());
        }

        switch (type) {
            case END:
                break;
            case BYTE:
                ByteTag byteTag = (ByteTag) tag;
                output.writeByte(byteTag.getPrimitiveValue());
                break;
            case SHORT:
                ShortTag shortTag = (ShortTag) tag;
                output.writeShort(shortTag.getPrimitiveValue());
                break;
            case INT:
                IntTag intTag = (IntTag) tag;
                output.writeInt(intTag.getPrimitiveValue());
                break;
            case LONG:
                LongTag longTag = (LongTag) tag;
                output.writeLong(longTag.getPrimitiveValue());
                break;
            case FLOAT:
                FloatTag floatTag = (FloatTag) tag;
                output.writeFloat(floatTag.getPrimitiveValue());
                break;
            case DOUBLE:
                DoubleTag doubleTag = (DoubleTag) tag;
                output.writeDouble(doubleTag.getPrimitiveValue());
                break;
            case BYTE_ARRAY:
                ByteArrayTag byteArrayTag = (ByteArrayTag) tag;
                byte[] byteArray = byteArrayTag.getValue();
                output.writeInt(byteArray.length);
                output.write(byteArray);
                break;
            case STRING:
                StringTag stringTag = (StringTag) tag;
                output.writeUTF(stringTag.getValue());
                break;
            case LIST:
                ListTag<?> listTag = (ListTag<?>) tag;
                output.writeByte(TagType.byClass(listTag.getTagClass()).ordinal());
                output.writeInt(listTag.getValue().size());
                for (Tag<?> tag1 : listTag.getValue()) {
                    serialize(tag1, true, depth + 1);
                }
                break;
            case COMPOUND:
                CompoundTag compoundTag = (CompoundTag) tag;
                for (Tag<?> tag1 : compoundTag.getValue().values()) {
                    serialize(tag1, false, depth + 1);
                }
                output.writeByte(0); // End tag
                break;
            case INT_ARRAY:
                IntArrayTag intArrayTag = (IntArrayTag) tag;
                int[] iValue = intArrayTag.getValue();
                output.writeInt(iValue.length);
                for (int i : iValue) {
                    output.writeInt(i);
                }
                break;
            case LONG_ARRAY:
                LongArrayTag longArrayTag = (LongArrayTag) tag;
                long[] longValues = longArrayTag.getValue();
                output.writeInt(longValues.length);
                for (long l : longValues) {
                    output.writeLong(l);
                }
                break;
        }
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        if (output instanceof Closeable) {
            ((Closeable) output).close();
        }
    }
}
