package benchmark;

import utils.Functions;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeManager {
    private static final ArrayList<Long> durations = new ArrayList<>();
    private static final Lock lock = new ReentrantLock();

    public static void addDuration(long durationsInMs) {
        lock.lock();
        durations.add(durationsInMs);
        lock.unlock();
    }

    public static void summary() {
        System.out.println("=".repeat(40) + " BENCHMARKING RESULT " + "=".repeat(40));
        printResult("Iterations", durations.size());
        System.out.println("----- Duration");
        printDuration();

        System.out.println("\n----- Throughput");
        printThroughput();
    }

    private static void printDuration() {
        Metrics durationMetrics = Metrics.createLongMetrics(durations);
        printResult("Total duration", (long) durationMetrics.computeTotal() + "ms");
        printResult("Minimum duration", (long) durationMetrics.findMin() + "ms");
        printResult("Maximum duration", (long) durationMetrics.findMax() + "ms");
        printResult("Average duration", durationMetrics.computeAverage() + " ±(99%) " +
                durationMetrics.computeConfidenceInterval99() + "ms");
        printResult("Standard deviation", durationMetrics.computeSD() + "ms");

        printResult("Confidence Interval (99%)", durationMetrics.confidenceInterval99Boundary());
    }

    private static void printThroughput() {
        Metrics throughputMetrics =
                Metrics.createDoubleMetrics(durations.stream().map(aLong -> 1.0 / aLong).toList());
        printResult("Minimum throughput", throughputMetrics.findMin() + "ms");
        printResult("Maximum throughput", throughputMetrics.findMax() + "ms");
        printResult("Average throughput", throughputMetrics.computeAverage() + " ±(99%) " +
                throughputMetrics.computeConfidenceInterval99() + "ms");
        printResult("Standard deviation", throughputMetrics.computeSD() + "ms");

        printResult("Confidence Interval (99%)", throughputMetrics.confidenceInterval99Boundary());
    }

    private static <T> void printResult(String key, T value) {
        System.out.println(Functions.center(key, 80) + ": " + value);
    }
}
