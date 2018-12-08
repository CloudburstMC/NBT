package com.nukkitx.nbt;

import com.nukkitx.nbt.stream.*;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@UtilityClass
public class NbtUtils {
    public static int MAX_DEPTH = 16;

    public static NBTInputStream createReader(InputStream stream) {
        Objects.requireNonNull(stream, "stream");
        return new NBTInputStream(new DataInputStream(stream));
    }

    public static NBTInputStream createReaderLE(InputStream stream) {
        Objects.requireNonNull(stream, "stream");
        return new NBTInputStream(new LittleEndianDataInputStream(stream));
    }

    public static NBTInputStream createGZIPReader(InputStream stream) throws IOException {
        return createReader(new GZIPInputStream(stream));
    }

    public static NBTInputStream createNetworkReader(InputStream stream) {
        Objects.requireNonNull(stream, "stream");
        return new NBTInputStream(new NetworkDataInputStream(stream));
    }

    public static NBTOutputStream createWriter(OutputStream stream) {
        Objects.requireNonNull(stream, "stream");
        return new NBTOutputStream(new DataOutputStream(stream));
    }

    public static NBTOutputStream createWriterLE(OutputStream stream) {
        Objects.requireNonNull(stream, "stream");
        return new NBTOutputStream(new LittleEndianDataOutputStream(stream));
    }

    public static NBTOutputStream createGZIPWriter(OutputStream stream) throws IOException {
        return createWriter(new GZIPOutputStream(stream));
    }

    public static NBTOutputStream createNetworkWriter(OutputStream stream) {
        return new NBTOutputStream(new NetworkDataOutputStream(stream));
    }
}
