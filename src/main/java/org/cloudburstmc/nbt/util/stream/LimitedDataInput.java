package org.cloudburstmc.nbt.util.stream;

import org.cloudburstmc.nbt.NbtUtils;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;

public class LimitedDataInput implements DataInput, Closeable {
    // Initial buffer size for length prefixed values, doubled as needed
    private static final int MAX_PREALLOCATED_BYTES = 64 * 1024;

    private final DataInput delegate;
    private final long maxReadSize;

    private long readSize = 0;
    private long arrayReadSize = 0;

    public LimitedDataInput(DataInput delegate) {
        this(delegate, NbtUtils.MAX_READ_SIZE);
    }

    public LimitedDataInput(DataInput delegate, long maxReadSize) {
        this.delegate = delegate;
        this.maxReadSize = maxReadSize;
    }

    @Override
    public void close() throws IOException {
        if (delegate instanceof Closeable) {
            ((Closeable) delegate).close();
        }
    }

    protected void tryRead(long size) throws IOException {
        this.readSize += requireNonNegative(size);
        if (this.maxReadSize > 0 && this.readSize > this.maxReadSize) {
            throw new IOException("Read size exceeded limit: read=" + this.readSize + ", limit=" + this.maxReadSize);
        }
    }

    public void tryReadArray(long size) throws IOException {
        this.arrayReadSize += requireNonNegative(size);
        if (this.maxReadSize > 0 && this.arrayReadSize > this.maxReadSize) {
            throw new IOException("Read size exceeded limit: read=" + this.arrayReadSize + ", limit=" + this.maxReadSize);
        }
    }

    private static long requireNonNegative(long size) throws IOException {
        if (size < 0) {
            throw new IOException("Negative read size: " + size);
        }
        return size;
    }

    /**
     * Reads {@code length} bytes.
     *
     * @param length The number of bytes to read
     * @return The bytes read
     * @throws IOException If the length is negative or the input ends first
     */
    public byte[] readBytes(int length) throws IOException {
        if (length < 0) {
            throw new IOException("Negative length: " + length);
        }
        byte[] bytes = new byte[Math.min(length, MAX_PREALLOCATED_BYTES)];
        int read = 0;
        while (true) {
            this.readFully(bytes, read, bytes.length - read);
            read = bytes.length;
            if (read == length) {
                return bytes;
            }
            bytes = Arrays.copyOf(bytes, (int) Math.min(length, 2L * read));
        }
    }

    /**
     * The capacity to start an array of {@code length} elements with, growing it as they arrive.
     *
     * @param length      The number of elements
     * @param elementSize The bytes one element takes in memory
     * @return The initial capacity
     */
    public static int initialCapacity(int length, int elementSize) {
        return Math.min(length, MAX_PREALLOCATED_BYTES / elementSize);
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.tryRead(b.length);
        this.delegate.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.tryRead(len);
        this.delegate.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        this.tryRead(n);
        return this.delegate.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        this.tryRead(1);
        return this.delegate.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        this.tryRead(1);
        return this.delegate.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        this.tryRead(1);
        return this.delegate.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        this.tryRead(2);
        return this.delegate.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        this.tryRead(2);
        return this.delegate.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        this.tryRead(2);
        return this.delegate.readChar();
    }

    @Override
    public int readInt() throws IOException {
        this.tryRead(4);
        return this.delegate.readInt();
    }

    @Override
    public long readLong() throws IOException {
        this.tryRead(8);
        return this.delegate.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        this.tryRead(4);
        return this.delegate.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        this.tryRead(8);
        return this.delegate.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        String line =  this.delegate.readLine();
        this.tryRead(line.length()); // not ideal, but we do not have a better way to estimate the size
        return line;
    }

    @Override
    public String readUTF() throws IOException {
        String utf = this.delegate.readUTF();
        this.tryRead(utf.length()); // not ideal, but we do not have a better way to estimate the size
        return utf;
    }

    public DataInput delegate() {
        return this.delegate;
    }
}
