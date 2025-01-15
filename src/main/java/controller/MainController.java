package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import log.IptablesLogProcessor;
import log.LogAnalyzer;
import models.IptablesModel;
import utils.FileUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainController {

    @FXML private Label totalRequestLabel;
    @FXML private Label totalFailLabel;
    @FXML private Label totalSizeLabel;
    @FXML private PieChart requestPieChart;
    @FXML private BarChart<String, Number> logBarChart;

    // Table columns
    @FXML private TableView<IptablesModel> logTable;
    @FXML private TableColumn<IptablesModel, String> prefixColumn;
    @FXML private TableColumn<IptablesModel, String> sourceIPColumn;
    @FXML private TableColumn<IptablesModel, String> destinationIPColumn;
    @FXML private TableColumn<IptablesModel, Number> sourcePortColumn;
    @FXML private TableColumn<IptablesModel, Number> destinationPortColumn;
    @FXML private TableColumn<IptablesModel, String> protocolColumn;
    @FXML private TableColumn<IptablesModel, Number> lengthColumn;
    @FXML private TableColumn<IptablesModel, String> inInterfaceColumn;
    @FXML private TableColumn<IptablesModel, String> outInterfaceColumn;
    @FXML private TableColumn<IptablesModel, String> macColumn;
    @FXML private TableColumn<IptablesModel, String> timestampColumn; // hiển thị LocalDateTime as String

    // Filter controls
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TextField searchField;

    private ObservableList<IptablesModel> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Đọc file + parse
        List<String> rawLogs = FileUtils.readLogFile("src/main/resources/mergeok.txt");
        List<IptablesModel> parsedLogs = IptablesLogProcessor.parseLogs(rawLogs);

        masterData.addAll(parsedLogs);

        // Setup columns
        prefixColumn.setCellValueFactory(new PropertyValueFactory<>("logPrefix"));
        sourceIPColumn.setCellValueFactory(new PropertyValueFactory<>("sourceIP"));
        destinationIPColumn.setCellValueFactory(new PropertyValueFactory<>("destinationIP"));
        sourcePortColumn.setCellValueFactory(new PropertyValueFactory<>("sourcePort"));
        destinationPortColumn.setCellValueFactory(new PropertyValueFactory<>("destinationPort"));
        protocolColumn.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        inInterfaceColumn.setCellValueFactory(new PropertyValueFactory<>("inInterface"));
        outInterfaceColumn.setCellValueFactory(new PropertyValueFactory<>("outInterface"));
        macColumn.setCellValueFactory(new PropertyValueFactory<>("macAddress"));
        // timestampColumn: LocalDateTime => string
        timestampColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getTimestamp() != null) {
                return new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getTimestamp().toString()
                );
            } else {
                return new ReadOnlyObjectWrapper<>("null");
            }
        });

        logTable.setItems(masterData);

        // Cập nhật
        updateStatistics(masterData);
    }

    @FXML
    public void onSearchClicked() {
        // Lấy from, to
        LocalDate fromLocal = fromDatePicker.getValue();
        LocalDate toLocal = toDatePicker.getValue();
        // Chuyển sang Date
        Date fromDate = (fromLocal != null) ? Date.valueOf(fromLocal) : null;
        Date toDate = (toLocal != null) ? Date.valueOf(toLocal) : null;

        String search = searchField.getText().trim().toLowerCase();

        // Filter
        List<IptablesModel> filtered = masterData.stream()
                .filter(e -> {
                    // 1) search
                    if (!search.isEmpty()) {
                        boolean match = false;
                        if (e.getLogPrefix() != null && e.getLogPrefix().toLowerCase().contains(search)) match = true;
                        if (!match && e.getSourceIP() != null && e.getSourceIP().toLowerCase().contains(search)) match = true;
                        if (!match && e.getProtocol() != null && e.getProtocol().toLowerCase().contains(search)) match = true;
                        // v.v. => destinationIP, in/outInterface
                        if (!match) return false;
                    }
                    // 2) time range
                    if (e.getTimestamp() != null) {
                        // convert to Date
                        java.util.Date d = java.util.Date.from(
                                e.getTimestamp().atZone(java.time.ZoneId.systemDefault()).toInstant()
                        );
                        if (fromDate != null && d.before(fromDate)) {
                            return false;
                        }
                        if (toDate != null && d.after(toDate)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        ObservableList<IptablesModel> filteredList = FXCollections.observableArrayList(filtered);
        logTable.setItems(filteredList);

        // Cập nhật chart
        updateStatistics(filteredList);
    }

    private void updateStatistics(List<IptablesModel> data) {
        int totalReq = LogAnalyzer.calculateTotalRequests(data);
        int blocked  = LogAnalyzer.calculateBlockedRequests(data);
        long size    = LogAnalyzer.calculateThroughput(data);

        totalRequestLabel.setText(String.valueOf(totalReq));
        totalFailLabel.setText(String.valueOf(blocked));
        totalSizeLabel.setText(size + " bytes");

        updatePieChart(data);
        updateBarChart(data);
    }

    private void updatePieChart(List<IptablesModel> data) {
        long droppedCount = data.stream()
                .filter(e -> e.getLogPrefix() != null && e.getLogPrefix().toLowerCase().contains("dropped"))
                .count();
        long acceptCount = data.stream()
                .filter(e -> e.getLogPrefix() != null && e.getLogPrefix().toLowerCase().contains("accept"))
                .count();


        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Dropped", droppedCount),
                new PieChart.Data("Accept", acceptCount)

        );
        requestPieChart.setData(pieData);
    }

    private void updateBarChart(List<IptablesModel> data) {
        logBarChart.getData().clear();
        Map<String, Long> prefixCount = data.stream()
                .filter(e -> {
                    String p = e.getLogPrefix();
                    return p != null && !p.toLowerCase().contains("message repeated");
                })
                .collect(Collectors.groupingBy(
                        IptablesModel::getLogPrefix,
                        Collectors.counting()
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Log Prefix Stats");

        for (var entry : prefixCount.entrySet()) {
            String prefix = entry.getKey();
            long count = entry.getValue();
            // cắt bớt if needed
            if (prefix.length() > 20) {
                prefix = prefix.substring(0, 20) + "...";
            }
            series.getData().add(new XYChart.Data<>(prefix, count));
        }

        logBarChart.getData().add(series);
        // fix text xoay
        CategoryAxis xAxis = (CategoryAxis) logBarChart.getXAxis();
        xAxis.setTickLabelRotation(0);
    }
}
