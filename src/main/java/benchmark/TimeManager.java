package benchmark;

import utils.Functions;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeManager {
    public static AtomicInteger iterations = new AtomicInteger(0);
    private static long durationsInMs = 0;
    private static final Lock lock = new ReentrantLock();

    public static void addDuration(long durationsInMs) {
        lock.lock();
        TimeManager.durationsInMs += durationsInMs;
        iterations.getAndIncrement();
        lock.unlock();
    }

    public static void printDuration() {
        System.out.println(Functions.center("Total duration (ms)", 20) + ": " + durationsInMs);
        System.out.println(Functions.center("Iterations", 20) + ": " + iterations.get());
        System.out.println(Functions.center("Average duration (ms)", 20) + ": " + (double) durationsInMs / iterations.get());
    }
}
