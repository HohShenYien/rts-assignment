package benchmark;

import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.LinePlot;
import tech.tablesaw.plotly.components.Figure;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class IterationChart {
    private final List<Double> data;
    private final String metric;

    public IterationChart(List<Double> data, String metric) {
        this.data = data;
        this.metric = metric;
    }

    public void plot() {
        AtomicInteger index = new AtomicInteger(1);

        Figure lineChart = LinePlot.create(metric + " against Iterations",
                "Iterations", data.stream().mapToDouble(aDouble -> index.getAndIncrement()).toArray(),
                metric, data.stream().mapToDouble(aDouble -> aDouble).toArray());

        Plot.show(lineChart);
    }

    public void display() {
        plot();
    }
}
