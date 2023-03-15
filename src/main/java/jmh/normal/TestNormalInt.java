package jmh.normal;

import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class TestNormalInt {
    public int num;

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }

    @Setup(Level.Invocation)
    public void setUp() {
        num = 0;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(1)
    @Timeout(time = 100)
    public void testIncrementAndGet() {
        for (int i = 0; i < 100_000; i++) {
            num++;
        }
    }
}