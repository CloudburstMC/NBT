package com.nukkitx.nbt.stream;

import com.nukkitx.nbt.util.VarInts;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class NetworkDataOutputStream implements DataOutput, Closeable {
    private final DataOutputStream stream;

    public NetworkDataOutputStream(OutputStream stream) {
        this.stream = new DataOutputStream(stream);
    }

    public NetworkDataOutputStream(DataOutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void write(int bytes) throws IOException {
        stream.write(bytes);
    }

    @Override
    public void write(@Nonnull byte[] bytes) throws IOException {
        stream.write(bytes);
    }

    @Override
    public void write(@Nonnull byte[] bytes, int offset, int length) throws IOException {
        stream.write(bytes, offset, length);
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        stream.writeBoolean(value);
    }

    @Override
    public void writeByte(int value) throws IOException {
        stream.writeByte(value);
    }

    @Override
    public void writeShort(int value) throws IOException {
        stream.writeShort(Short.reverseBytes((short) value));
    }

    @Override
    public void writeChar(int value) throws IOException {
        stream.writeChar(Character.reverseBytes((char) value));
    }

    @Override
    public void writeInt(int value) throws IOException {
        VarInts.writeInt(stream, value);
    }

    @Override
    public void writeLong(long value) throws IOException {
        VarInts.writeLong(stream, value);
    }

    @Override
    public void writeFloat(float value) throws IOException {
        writeInt(Float.floatToIntBits(value));
    }

    @Override
    public void writeDouble(double value) throws IOException {
        writeLong(Double.doubleToLongBits(value));
    }

    @Override
    public void writeBytes(@Nonnull String string) throws IOException {
        stream.writeBytes(string);
    }

    @Override
    public void writeChars(@Nonnull String string) throws IOException {
        stream.writeChars(string);
    }

    @Override
    public void writeUTF(@Nonnull String string) throws IOException {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        VarInts.writeUnsignedInt(stream, bytes.length);
        write(bytes);
    }
}
