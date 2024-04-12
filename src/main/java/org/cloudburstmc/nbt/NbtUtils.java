package org.cloudburstmc.nbt;

import org.cloudburstmc.nbt.util.stream.LittleEndianDataInputStream;
import org.cloudburstmc.nbt.util.stream.LittleEndianDataOutputStream;
import org.cloudburstmc.nbt.util.stream.NetworkDataInputStream;
import org.cloudburstmc.nbt.util.stream.NetworkDataOutputStream;

import java.io.*;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.util.Objects.requireNonNull;

public class NbtUtils {
    public static final int MAX_DEPTH = 16;
    public static final long MAX_READ_SIZE = 0; // Disabled by default

    private NbtUtils() {
    }

    public static NBTInputStream createReader(InputStream stream, boolean internKeys, boolean internValues) {
        return createReader(stream, internKeys, internValues, MAX_READ_SIZE);
    }

    public static NBTInputStream createReader(InputStream stream, boolean internKeys, boolean internValues, long maxReadSize) {
        requireNonNull(stream, "stream");
        return new NBTInputStream(new DataInputStream(stream), internKeys, internValues, maxReadSize);
    }

    public static NBTInputStream createReaderLE(InputStream stream, boolean internKeys, boolean internValues) {
        return createReaderLE(stream, internKeys, internValues, MAX_READ_SIZE);
    }

    public static NBTInputStream createReaderLE(InputStream stream, boolean internKeys, boolean internValues, long maxReadSize) {
        requireNonNull(stream, "stream");
        return new NBTInputStream(new LittleEndianDataInputStream(stream, maxReadSize), internKeys, internValues);
    }

    public static NBTInputStream createGZIPReader(InputStream stream, boolean internKeys, boolean internValues) throws IOException {
        return createGZIPReader(stream, internKeys, internValues, MAX_READ_SIZE);
    }

    public static NBTInputStream createGZIPReader(InputStream stream, boolean internKeys, boolean internValues, long maxReadSize) throws IOException {
        return createReader(new GZIPInputStream(stream), internKeys, internValues, maxReadSize);
    }

    public static NBTInputStream createNetworkReader(InputStream stream, boolean internKeys, boolean internValues) {
        return createNetworkReader(stream, internKeys, internValues, MAX_READ_SIZE);
    }
    
    public static NBTInputStream createNetworkReader(InputStream stream, boolean internKeys, boolean internValues, long maxReadSize) {
        requireNonNull(stream, "stream");
        return new NBTInputStream(new NetworkDataInputStream(stream, maxReadSize), internKeys, internValues);
    }

    public static NBTInputStream createReader(InputStream stream) {
        return createReader(stream, MAX_READ_SIZE);
    }
    
    public static NBTInputStream createReader(InputStream stream, long maxReadSize) {
        return createReader(stream, false, false, maxReadSize);
    }

    public static NBTInputStream createReaderLE(InputStream stream) {
        return createReaderLE(stream, MAX_READ_SIZE);
    }
    
    public static NBTInputStream createReaderLE(InputStream stream, long maxReadSize) {
        return createReaderLE(stream, false, false, maxReadSize);
    }

    public static NBTInputStream createGZIPReader(InputStream stream) throws IOException {
        return createGZIPReader(stream, MAX_READ_SIZE);
    }

    public static NBTInputStream createGZIPReader(InputStream stream, long maxReadSize) throws IOException {
        return createGZIPReader(stream, false, false, maxReadSize);
    }

    public static NBTInputStream createNetworkReader(InputStream stream) {
        return createNetworkReader(stream, MAX_READ_SIZE);
    }

    public static NBTInputStream createNetworkReader(InputStream stream, long maxReadSize) {
        return createNetworkReader(stream, false, false, maxReadSize);
    }

    public static NBTOutputStream createWriter(OutputStream stream) {
        requireNonNull(stream, "stream");
        return new NBTOutputStream(new DataOutputStream(stream));
    }

    public static NBTOutputStream createWriterLE(OutputStream stream) {
        requireNonNull(stream, "stream");
        return new NBTOutputStream(new LittleEndianDataOutputStream(stream));
    }

    public static NBTOutputStream createGZIPWriter(OutputStream stream) throws IOException {
        return createWriter(new GZIPOutputStream(stream));
    }

    public static NBTOutputStream createNetworkWriter(OutputStream stream) {
        return new NBTOutputStream(new NetworkDataOutputStream(stream));
    }

    public static String toString(Object o) {
        if (o instanceof Byte) {
            return ((byte) o) + "b";
        } else if (o instanceof Short) {
            return ((short) o) + "s";
        } else if (o instanceof Integer) {
            return ((int) o) + "i";
        } else if (o instanceof Long) {
            return ((long) o) + "l";
        } else if (o instanceof Float) {
            return ((float) o) + "f";
        } else if (o instanceof Double) {
            return ((double) o) + "d";
        } else if (o instanceof byte[]) {
            return "0x" + printHexBinary((byte[]) o);
        } else if (o instanceof String) {
            return "\"" + o + "\"";
        } else if (o instanceof int[]) {
            StringJoiner joiner = new StringJoiner(", ");
            for (int i : (int[]) o) {
                joiner.add(i + "i");
            }
            return "[ " + joiner + " ]";
        } else if (o instanceof long[]) {
            StringJoiner joiner = new StringJoiner(", ");
            for (long l : (long[]) o) {
                joiner.add(l + "l");
            }
            return "[ " + joiner + " ]";
        }
        return o.toString();
    }

    public static <T> T copy(T val) {
        if (val instanceof byte[]) {
            byte[] bytes = (byte[]) val;
            return (T) Arrays.copyOf(bytes, bytes.length);
        } else if (val instanceof int[]) {
            int[] ints = (int[]) val;
            return (T) Arrays.copyOf(ints, ints.length);
        } else if (val instanceof long[]) {
            long[] longs = (long[]) val;
            return (T) Arrays.copyOf(longs, longs.length);
        }
        return val;
    }

    public static String indent(String string) {
        StringBuilder builder = new StringBuilder("  " + string);
        for (int i = 2; i < builder.length(); i++) {
            if (builder.charAt(i) == '\n') {
                builder.insert(i + 1, "  ");
                i += 2;
            }
        }
        return builder.toString();
    }

    private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();

    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length << 1);
        for (byte b : data) {
            r.append(HEX_CODE[(b >> 4) & 0xF]);
            r.append(HEX_CODE[(b & 0xF)]);
        }
        return r.toString();
    }
}
