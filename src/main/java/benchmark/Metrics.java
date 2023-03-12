package benchmark;

import java.util.List;

public class Metrics {
    private final List<Double> arrayList;

    private Metrics(List<Double> inputList) {
        this.arrayList = inputList.stream().toList();
    }

    public static Metrics createDoubleMetrics(List<Double> inputList) {
        return new Metrics(inputList);
    }

    public static Metrics createLongMetrics(List<Long> inputList) {
        return new Metrics((List<Double>) inputList.stream().map(Long::doubleValue).toList());
    }

    public double computeTotal() {
        return arrayList.stream().reduce(0D, Double::sum);
    }

    public double computeAverage() {
        return (double) computeTotal() / arrayList.size();
    }

    public double findMin() {
        return arrayList.stream().min((o1, o2) -> (int) (o1 - o2)).get();
    }

    public double findMax() {
        return arrayList.stream().max((o1, o2) -> (int) (o1 - o2)).get();
    }

    public double computeThroughput() {
        return 1.0 / computeAverage();
    }

    public double computeSD() {
        double squareDiff = 0;
        double mean = (double) computeTotal() / arrayList.size();

        for (double duration : arrayList) {
            squareDiff += Math.pow(duration - mean, 2);
        }

        return Math.sqrt(squareDiff / arrayList.size());
    }

    public double computeConfidenceInterval99() {
        double confidenceLevel = 2.58;
        return confidenceLevel * computeSD() / Math.sqrt(arrayList.size());
    }

    public String confidenceInterval99Boundary() {
        return "[" + (computeAverage() - computeConfidenceInterval99()) + "," +
                (computeAverage() + computeConfidenceInterval99()) +
                "]";
    }
}
