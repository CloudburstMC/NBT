package com.nukkitx.nbt.stream;

import com.nukkitx.nbt.util.VarInts;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class NetworkDataInputStream implements DataInput, Closeable {
    private final DataInputStream stream;

    public NetworkDataInputStream(InputStream stream) {
        this.stream = new DataInputStream(stream);
    }

    public NetworkDataInputStream(DataInputStream stream) {
        this.stream = stream;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void readFully(@Nonnull byte[] bytes) throws IOException {
        stream.readFully(bytes);
    }

    @Override
    public void readFully(@Nonnull byte[] bytes, int offset, int length) throws IOException {
        stream.readFully(bytes, offset, length);
    }

    @Override
    public int skipBytes(int amount) throws IOException {
        return stream.skipBytes(amount);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return stream.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return stream.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return stream.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return Short.reverseBytes(stream.readShort());
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return Short.toUnsignedInt(Short.reverseBytes(stream.readShort()));
    }

    @Override
    public char readChar() throws IOException {
        return Character.reverseBytes(stream.readChar());
    }

    @Override
    public int readInt() throws IOException {
        return VarInts.readInt(stream);
    }

    @Override
    public long readLong() throws IOException {
        return VarInts.readLong(stream);
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(Long.reverseBytes(stream.readLong()));
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        return stream.readLine();
    }

    @Override
    @Nonnull
    public String readUTF() throws IOException {
        int length = VarInts.readUnsignedInt(stream);
        byte[] bytes = new byte[length];
        readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
