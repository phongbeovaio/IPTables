package log;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import models.LogEntry;
import utils.TimeUtils;

public class IptablesLogProcessor {

    /**
     * Phân tích danh sách log thô thành các đối tượng LogEntry.
     *
     * @param rawLogs Danh sách log thô.
     * @return Danh sách các đối tượng LogEntry đã được phân tích.
     */
    public static List<LogEntry> parseLogs(List<String> rawLogs) {
        List<LogEntry> logEntries = new ArrayList<>();
        for (String log : rawLogs) {
            // Kiểm tra log hợp lệ
            if (log.contains("SRC=") && log.contains("DST=")) {
                try {
                    // Trích xuất các trường dữ liệu từ log
                    Date timestamp = TimeUtils.parseTimestamp(log.substring(0, 15)); // Phần đầu log chứa thời gian
                    String inInterface = log.contains("IN=") ? log.split("IN=")[1].split(" ")[0] : null;
                    String outInterface = log.contains("OUT=") ? log.split("OUT=")[1].split(" ")[0] : null;
                    String sourceIP = log.split("SRC=")[1].split(" ")[0];
                    String destinationIP = log.split("DST=")[1].split(" ")[0];
                    String protocol = log.contains("PROTO=") ? log.split("PROTO=")[1].split(" ")[0] : "UNKNOWN";
                    String sourcePort = log.contains("SPT=") ? log.split("SPT=")[1].split(" ")[0] : null;
                    String destinationPort = log.contains("DPT=") ? log.split("DPT=")[1].split(" ")[0] : null;
                    int length = log.contains("LEN=") ? Integer.parseInt(log.split("LEN=")[1].split(" ")[0]) : 0;
                    int packetCount = log.contains("PACKETS=") ? Integer.parseInt(log.split("PACKETS=")[1].split(" ")[0]) : 0;
                    int byteCount = log.contains("BYTES=") ? Integer.parseInt(log.split("BYTES=")[1].split(" ")[0]) : 0;
                    String logType = log.contains("INPUT") ? "INPUT" : log.contains("OUTPUT") ? "OUTPUT" : "UNKNOWN";

                    // Tạo đối tượng LogEntry
                    LogEntry entry = new LogEntry(
                            timestamp, sourceIP, destinationIP, sourcePort, destinationPort, protocol,
                            length, packetCount, byteCount, inInterface, outInterface, logType
                    );

                    // Thêm vào danh sách
                    logEntries.add(entry);

                } catch (Exception e) {
                    System.err.println("Lỗi khi phân tích log: " + log);
                    e.printStackTrace();
                }
            }
        }
        return logEntries;
    }

    /**
     * Tìm kiếm log chứa chuỗi cụ thể.
     *
     * @param logEntries Danh sách các đối tượng LogEntry.
     * @param searchTerm Chuỗi cần tìm kiếm.
     * @return Danh sách các log khớp với chuỗi tìm kiếm.
     */
    public static List<LogEntry> searchLogs(List<LogEntry> logEntries, String searchTerm) {
        List<LogEntry> results = new ArrayList<>();
        for (LogEntry entry : logEntries) {
            if ((entry.getSourceIP() != null && entry.getSourceIP().contains(searchTerm)) ||
                    (entry.getDestinationIP() != null && entry.getDestinationIP().contains(searchTerm)) ||
                    (entry.getProtocol() != null && entry.getProtocol().contains(searchTerm)) ||
                    (entry.getSourcePort() != null && entry.getSourcePort().contains(searchTerm)) ||
                    (entry.getDestinationPort() != null && entry.getDestinationPort().contains(searchTerm))) {
                results.add(entry);
            }
        }
        return results;
    }

    /**
     * Tìm log trong một khoảng thời gian.
     *
     * @param logEntries Danh sách các đối tượng LogEntry.
     * @param startTime Thời gian bắt đầu.
     * @param endTime Thời gian kết thúc.
     * @return Danh sách các log trong khoảng thời gian.
     */
    public static List<LogEntry> filterLogsByTime(List<LogEntry> logEntries, Date startTime, Date endTime) {
        List<LogEntry> results = new ArrayList<>();
        for (LogEntry entry : logEntries) {
            if (TimeUtils.isWithinTimeRange(entry.getTimestamp(), startTime, endTime)) {
                results.add(entry);
            }
        }
        return results;
    }
}
