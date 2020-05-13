package com.nukkitx.nbt;

import com.nukkitx.nbt.stream.NBTInputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;


public class NbtBenchmarkTests {

    @Test
    @DisplayName("Benchmark")
    public void launchBenchmark() throws Exception {
        Options opt = new OptionsBuilder()
                // Specify which benchmarks to run.
                // You can be more specific if you'd like to run only one benchmark per test.
                .include(this.getClass().getName() + ".*")
                // Set the following options as needed
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(5)
                .measurementTime(TimeValue.seconds(1))
                .measurementIterations(10)
                .threads(2)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                //.jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
                //.addProfiler(WinPerfAsmProfiler.class)
                .build();

        new Runner(opt).run();
    }

    @State(Scope.Thread)
    public static class BenchmarkState {
        byte[] nbtBytes;

        @Setup(Level.Trial)
        public void initialize() {
            InputStream stream = NbtBenchmarkTests.class.getClassLoader().getResourceAsStream("benchmark.nbt");
            try (GZIPInputStream is = new GZIPInputStream(stream)) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                nbtBytes = buffer.toByteArray();
            } catch (IOException e) {
                throw new AssertionError("Unable to load benchmark NBT");
            }
        }
    }

    @Benchmark
    public void nukkitBenchmark(BenchmarkState state, Blackhole bh) throws IOException {
        NBTInputStream stream = NbtUtils.createReader(new ByteArrayInputStream(state.nbtBytes));
        stream.readTag();
    }

    @Benchmark
    public void querzBenchmark(BenchmarkState state, Blackhole bh) throws IOException {
        net.querz.nbt.io.NBTInputStream stream = new net.querz.nbt.io.NBTInputStream(new ByteArrayInputStream(state.nbtBytes));
        stream.readTag(16);
    }
}