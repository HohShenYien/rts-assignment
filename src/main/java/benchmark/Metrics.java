package benchmark;

import java.util.List;

public class Metrics {
    private final List<Double> arrayList;
    private final IterationChart timeChart;

    private Metrics(List<Double> inputList, String name) {
        this.arrayList = inputList.stream().toList();
        this.timeChart = new IterationChart(this.arrayList, name);
    }

    public static Metrics createDoubleMetrics(List<Double> inputList, String name) {
        return new Metrics(inputList, name);
    }

    public double computeTotal() {
        return arrayList.stream().reduce(0D, Double::sum);
    }

    public double computeAverage() {
        return computeTotal() / arrayList.size();
    }

    public double findMin() {
        double min = arrayList.get(0);
        return arrayList.stream().reduce(min,
                (aDouble, aDouble2) -> aDouble2 < aDouble ? aDouble2 : aDouble);
    }

    public double findMax() {
        double max = arrayList.get(0);
        return arrayList.stream().reduce(max,
                (aDouble, aDouble2) -> aDouble2 > aDouble ? aDouble2 : aDouble);
    }

    public int iteration() {
        return arrayList.size();
    }

    public double computeSD() {
        double squareDiff = 0;
        double mean = computeTotal() / arrayList.size();

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

    public void displayChart() {
        this.timeChart.display();
    }
}
