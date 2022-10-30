package org.cloudburstmc.nbt.util.stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.cloudburstmc.nbt.util.VarInts;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class NetworkDataInputStream extends LittleEndianDataInputStream {

    public NetworkDataInputStream(InputStream stream) {
        super(stream);
    }

    public NetworkDataInputStream(DataInputStream stream) {
        super(stream);
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
    @NonNull
    public String readUTF() throws IOException {
        int length = VarInts.readUnsignedInt(stream);
        byte[] bytes = new byte[length];
        readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
