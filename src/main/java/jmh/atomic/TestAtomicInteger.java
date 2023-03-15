package jmh.atomic;

import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@State(Scope.Benchmark)
public class TestAtomicInteger {
    public AtomicInteger atomicInteger;

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }

    @Setup(Level.Invocation)
    public void setUp() {
        atomicInteger = new AtomicInteger(0);
    }

    //    @Threads(10)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(1)
    @Timeout(time = 100)
    public void testIncrementAndGet() {
        for (int i = 0; i < 100_000; i++) {
            atomicInteger.getAndIncrement();
        }
    }
}