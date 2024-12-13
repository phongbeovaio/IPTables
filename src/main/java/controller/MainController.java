package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import log.IptablesLogProcessor;
import log.LogAnalyzer;
import models.LogEntry;
import utils.FileUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    private Label totalRequestLabel;
    @FXML
    private Label totalFailLabel;
    @FXML
    private Label totalSizeLabel;
    @FXML
    private PieChart requestPieChart;
    @FXML
    private TableView<LogEntry> logTable;
    @FXML
    private TableColumn<LogEntry, Date> timestampColumn;
    @FXML
    private TableColumn<LogEntry, String> sourceIPColumn;
    @FXML
    private TableColumn<LogEntry, String> destinationIPColumn;
    @FXML
    private TableColumn<LogEntry, String> protocolColumn;
    @FXML
    private TableColumn<LogEntry, Integer> lengthColumn;
    @FXML
    private TableColumn<LogEntry, String> statusColumn;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TextField searchField;

    private ObservableList<LogEntry> logEntries = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Đọc log từ file
        List<String> rawLogs = FileUtils.readLogFile("src/main/resources/abcdeee.txt");
        List<LogEntry> parsedLogs = IptablesLogProcessor.parseLogs(rawLogs);
        logEntries.addAll(parsedLogs);

        // Cấu hình bảng
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        sourceIPColumn.setCellValueFactory(new PropertyValueFactory<>("sourceIP"));
        destinationIPColumn.setCellValueFactory(new PropertyValueFactory<>("destinationIP"));
        protocolColumn.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        logTable.setItems(logEntries);

        // Cập nhật thống kê
        updateStatistics();
    }

    @FXML
    public void onSearchClicked() {
        String searchTerm = searchField.getText();
        Date from = fromDatePicker.getValue() != null ? java.sql.Date.valueOf(fromDatePicker.getValue()) : null;
        Date to = toDatePicker.getValue() != null ? java.sql.Date.valueOf(toDatePicker.getValue()) : null;

        List<LogEntry> filteredLogs = logEntries.stream()
                .filter(entry -> (searchTerm.isEmpty() || entry.getSourceIP().contains(searchTerm) || entry.getProtocol().contains(searchTerm)) &&
                        (from == null || !entry.getTimestamp().before(from)) &&
                        (to == null || !entry.getTimestamp().after(to)))
                .collect(Collectors.toList()); // Sử dụng Collectors.toList()

        logTable.setItems(FXCollections.observableArrayList(filteredLogs));
        updateStatistics();
    }

    private void updateStatistics() {
        int totalRequests = LogAnalyzer.calculateTotalRequests(logEntries);
        int totalBlocked = LogAnalyzer.calculateBlockedRequests(logEntries);
        int totalSize = LogAnalyzer.calculateThroughput(logEntries);

        totalRequestLabel.setText(String.valueOf(totalRequests));
        totalFailLabel.setText(String.valueOf(totalBlocked));
        totalSizeLabel.setText(totalSize + " bytes");

        // Cập nhật biểu đồ
        PieChart.Data tcpData = new PieChart.Data("TCP", logEntries.stream().filter(e -> e.getProtocol().equals("TCP")).count());
        PieChart.Data udpData = new PieChart.Data("UDP", logEntries.stream().filter(e -> e.getProtocol().equals("UDP")).count());
        requestPieChart.setData(FXCollections.observableArrayList(tcpData, udpData));
    }
}
