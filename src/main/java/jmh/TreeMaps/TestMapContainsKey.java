package jmh.TreeMaps;

import org.openjdk.jmh.annotations.*;
import utils.Actuators;
import utils.Functions;
import utils.Log;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class TestMapContainsKey {
    public Map<Integer, Log> logStore;

    @Setup(Level.Trial)
    public void setUp() {
        logStore = new TreeMap<>();
        for (int i = 0; i < 1000; i++) {
            Log log = new Log(Functions.intToBytes(Functions.getRandom(-5, 5)),
                    Actuators.HEATING_SYSTEM, false);
            logStore.put(i, log);
        }
    }

    //    @Threads(10)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(1)
    @Timeout(time = 100)
    public void testContains() {
        // checking 100 that is inside, and another 100 that is not inside
        for (int i = 0; i < 2000; i++) {
            logStore.containsKey(i);
        }
    }
}
