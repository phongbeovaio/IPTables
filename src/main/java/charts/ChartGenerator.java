// Tạo biểu đồ
package charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartGenerator {
    public static void generateBarChart(String title, String xAxisLabel, String yAxisLabel, int[] values, String[] categories) {
        // Tạo dataset cụ thể từ DefaultCategoryDataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < categories.length; i++) {
            dataset.addValue(values[i], "Requests", categories[i]); // Thêm giá trị
        }

        // Tạo biểu đồ dạng cột
        JFreeChart chart = ChartFactory.createBarChart(title, xAxisLabel, yAxisLabel, dataset);

        // Hiển thị biểu đồ trong khung
        ChartFrame frame = new ChartFrame("Bar Chart", chart);
        frame.pack();
        frame.setVisible(true);
    }
}
