// main điều hướng, tìm kiếm, phân tích, vẽ biểu đồ
package application;

import log.IptablesLogProcessor;
import log.LogAnalyzer;
import models.LogEntry;
import utils.FileUtils;
import utils.TimeUtils;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Đường dẫn tới file log
        String logFilePath = "src/main/resources/newinput_log.txt";

        // Đọc file log và lưu các dòng log vào danh sách
        List<String> rawLogs = FileUtils.readLogFile(logFilePath);

        if (rawLogs.isEmpty()) {
            System.out.println("File log không có dữ liệu hoặc không tồn tại.");
            return;
        }

        // Phân tích các dòng log và tạo danh sách LogEntry
        List<LogEntry> logEntries = IptablesLogProcessor.parseLogs(rawLogs);

        // Hiển thị menu
        while (true) {
            System.out.println("\nChọn một tùy chọn:");
            System.out.println("1. Tra cứu theo IP");
            System.out.println("2. Phân tích tổng số request");
            System.out.println("3. Tính thông lượng");
            System.out.println("4. Tính số lượng request trong khoảng thời gian");
            System.out.println("5. Đếm số lượng request theo loại (INPUT/OUTPUT)");
            System.out.println("6. Thoát");

            System.out.print("Nhập lựa chọn của bạn: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Đọc dòng trống

            switch (choice) {
                case 1: // Tra cứu theo IP
                    System.out.print("Nhập IP cần tra cứu: ");
                    String searchIP = scanner.nextLine();
                    List<LogEntry> searchResults = IptablesLogProcessor.searchLogs(logEntries, searchIP);
                    System.out.println("Tìm thấy " + searchResults.size() + " log khớp với IP " + searchIP + ":");
                    for (LogEntry entry : searchResults) {
                        System.out.println("Source IP: " + entry.getSourceIP() +
                                ", Destination IP: " + entry.getDestinationIP() +
                                ", Protocol: " + entry.getProtocol() +
                                ", Length: " + entry.getLength());
                    }
                    break;

                case 2: // Phân tích tổng số request
                    int totalRequests = LogAnalyzer.calculateTotalRequests(logEntries);
                    System.out.println("Tổng số request: " + totalRequests);
                    break;

                case 3: // Tính thông lượng
                    int throughput = LogAnalyzer.calculateThroughput(logEntries);
                    System.out.println("Tổng thông lượng: " + throughput + " bytes");
                    break;

                case 4: // Tính số lượng request trong khoảng thời gian
                    System.out.print("Nhập thời gian bắt đầu (ví dụ: Thg 11 21 23:50:00): ");
                    String startTimeStr = scanner.nextLine();
                    System.out.print("Nhập thời gian kết thúc (ví dụ: Thg 11 21 23:55:00): ");
                    String endTimeStr = scanner.nextLine();

                    Date startTime = TimeUtils.parseTimestamp(startTimeStr);
                    Date endTime = TimeUtils.parseTimestamp(endTimeStr);

                    if (startTime != null && endTime != null) {
                        int requestsInRange = LogAnalyzer.calculateRequestsInTimeRange(logEntries, startTime, endTime);
                        System.out.println("Số request trong khoảng thời gian: " + requestsInRange);
                    } else {
                        System.out.println("Thời gian không hợp lệ. Vui lòng kiểm tra lại.");
                    }
                    break;

                case 5: // Đếm số lượng request theo loại
                    System.out.print("Nhập loại log cần đếm (INPUT/OUTPUT): ");
                    String logType = scanner.nextLine();
                    int countByType = LogAnalyzer.calculateRequestsByType(logEntries, logType);
                    System.out.println("Số request loại " + logType + ": " + countByType);
                    break;

                case 6: // Thoát
                    System.out.println("Thoát chương trình.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        }
    }
}
