package benchmark;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeManager {
    private static final ArrayList<Double> durations = new ArrayList<>();
    private static final Lock lock = new ReentrantLock();

    public static void addDuration(long durationsInNano) {
        lock.lock();
        durations.add(durationsInNano / 1_000_000.0);
        lock.unlock();
    }

    public static void summary() {
        System.out.println("=".repeat(30) + " BENCHMARKING RESULT " + "=".repeat(30));
        System.out.println("-".repeat(20) + " Duration");
        printDuration();

        System.out.println("\n" + "-".repeat(20) + " Throughput");
        printThroughput();
    }

    private static void printDuration() {
        Metrics delayMetrics = Metrics.createDoubleMetrics(durations, "Delay (ms)");
        printResult("Iterations", delayMetrics.iteration());
        printResult("Total duration", delayMetrics.computeTotal() + "ms");
        printResult("Minimum duration", delayMetrics.findMin() + " ms");
        printResult("Maximum duration", delayMetrics.findMax() + " ms");
        printResult("Average duration", delayMetrics.computeAverage() + " ±(99%) " +
                delayMetrics.computeConfidenceInterval99() + " ms");
        printResult("Standard deviation", delayMetrics.computeSD() + " ms");

        printResult("Confidence Interval (99%)", delayMetrics.confidenceInterval99Boundary() +
                " ms");
        delayMetrics.displayChart();
    }

    private static void printThroughput() {
        Metrics throughputMetrics =
                Metrics.createDoubleMetrics(durations.stream()
                        .filter(aLong -> aLong > 0)
                        .map(aLong -> 1000.0 / aLong).toList(), "Throughput (ops/s)");
        printResult("Iterations", throughputMetrics.iteration());
        printResult("Minimum throughput", throughputMetrics.findMin() + " ops/s");
        printResult("Maximum throughput", throughputMetrics.findMax() + " ops/s");
        printResult("Average throughput", throughputMetrics.computeAverage() + " ±(99%) " +
                throughputMetrics.computeConfidenceInterval99() + " ops/s");
        printResult("Standard deviation", throughputMetrics.computeSD() + " ops/s");

        printResult("Confidence Interval (99%)",
                throughputMetrics.confidenceInterval99Boundary() + " ops/s");
        throughputMetrics.displayChart();
    }

    private static <T> void printResult(String key, T value) {
        System.out.println(key + " ".repeat(30 - key.length()) + ": " + value);
    }
}
