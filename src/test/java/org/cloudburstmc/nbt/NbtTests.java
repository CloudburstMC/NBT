package org.cloudburstmc.nbt;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

class NbtTests {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final byte[] TEST_BYTES;
    private static final int[] TEST_INTS;
    private static final long[] TEST_LONGS;
    private static final NbtMap[] TEST_LIST = new NbtMap[16];

    private static final NbtMap TEST_MAP;

    static {
        TEST_BYTES = RANDOM.generateSeed(32);
        TEST_INTS = RANDOM.ints(32).toArray();
        TEST_LONGS = RANDOM.longs(32).toArray();

        for (int i = 0; i < TEST_LIST.length; i++) {
            TEST_LIST[i] = NbtMap.builder()
                    .putInt("ListInt", RANDOM.nextInt())
                    .putLong("ListLong", RANDOM.nextLong())
                    .putByteArray("ListByteArray", RANDOM.generateSeed(16))
                    .build();
        }

        NbtMapBuilder builder = NbtMap.builder()
                .putBoolean("BooleanTest", true)
                .putByte("ByteTest", (byte) RANDOM.nextInt())
                .putShort("ShortTest", (short) RANDOM.nextInt())
                .putInt("IntTest", RANDOM.nextInt())
                .putLong("LongTest", RANDOM.nextLong())
                .putFloat("FloatTest", RANDOM.nextFloat())
                .putDouble("DoubleTest", RANDOM.nextDouble())
                .putByteArray("ByteArrayTest", TEST_BYTES)
                .putIntArray("IntArrayTest", TEST_INTS)
                .putLongArray("LongArrayTest", TEST_LONGS)
                .putString("StringTest", new String(RANDOM.generateSeed(32), StandardCharsets.UTF_8))
                .putCompound("CompoundTest", NbtMap.builder()
                        .putString("CompoundStringTest", "Test value 123")
                        .build())
                .putList("ListTest", NbtType.COMPOUND, TEST_LIST);

        builder.put("BooleanTest2", RANDOM.nextBoolean());
        TEST_MAP = builder.build();
    }

    @Test
    @DisplayName("Network Encoding Test")
    void networkTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (NBTOutputStream out = NbtUtils.createNetworkWriter(baos)) {
            out.writeTag(TEST_MAP);
        } catch (IOException e) {
            throw new AssertionError("Error whilst encoding tag", e);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (NBTInputStream in = NbtUtils.createNetworkReader(bais)) {
            Object o = in.readTag();
            Assertions.assertEquals(TEST_MAP, o);
            Assertions.assertEquals(TEST_MAP.hashCode(), o.hashCode());
        } catch (Exception e) {
            throw new AssertionError("Error whilst decoding tag", e);
        }
    }

    @Test
    @DisplayName("Biome Definitions Test")
    void biomeDefinitionsTest() {
        InputStream stream = NbtTests.class.getClassLoader().getResourceAsStream("biome_definitions.nbt");
        try (NBTInputStream in = NbtUtils.createNetworkReader(stream)) {
            Object o = in.readTag();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError("Error whilst decoding tag", e);
        }
    }
}
