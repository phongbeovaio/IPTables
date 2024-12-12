package charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.util.Map;

public class ChartGenerator {
    // Vẽ biểu đồ cột
    public static void generateBarChart(String title, String xAxisLabel, String yAxisLabel, int[] values, String[] categories) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < categories.length; i++) {
            dataset.addValue(values[i], "Requests", categories[i]);
        }
        JFreeChart chart = ChartFactory.createBarChart(title, xAxisLabel, yAxisLabel, dataset);
        ChartFrame frame = new ChartFrame("Bar Chart", chart);
        frame.pack();
        frame.setVisible(true);
    }

    // Vẽ biểu đồ cột cho số request trong các khoảng thời gian
    public static void generateBarChartForTimeRanges(String title, Map<String, Integer> timeRanges) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : timeRanges.entrySet()) {
            dataset.addValue(entry.getValue(), "Requests", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart(title, "Time Range", "Number of Requests", dataset);
        ChartFrame frame = new ChartFrame("Time Range Bar Chart", chart);
        frame.pack();
        frame.setVisible(true);
    }

    // Vẽ biểu đồ tròn cho các địa chỉ IP
    public static void generatePieChartForIPFrequency(String title, Map<String, Integer> ipFrequency) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : ipFrequency.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        ChartFrame frame = new ChartFrame("Pie Chart", chart);
        frame.pack();
        frame.setVisible(true);
    }
}
