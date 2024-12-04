// Tính toán thong ke
// Tính toán thông lượng từ trường LEN trong log
// Tinh so luong request trong khung thoi gian nao do
package log;

import models.LogEntry;
import utils.TimeUtils;

import java.util.Date;
import java.util.List;

public class LogAnalyzer {

    /**
     * Tính tổng số request trong danh sách log.
     *
     * @param logEntries Danh sách các log đã phân tích.
     * @return Tổng số request.
     */
    public static int calculateTotalRequests(List<LogEntry> logEntries) {
        return logEntries.size();
    }

    /**
     * Tính thông lượng (tổng chiều dài của tất cả gói tin) từ trường LEN.
     *
     * @param logEntries Danh sách các log đã phân tích.
     * @return Tổng thông lượng (bytes).
     */
    public static int calculateThroughput(List<LogEntry> logEntries) {
        int totalLength = 0;
        for (LogEntry entry : logEntries) {
            totalLength += entry.getLength(); // Cộng dồn chiều dài của mỗi gói tin
        }
        return totalLength;
    }

    /**
     * Tính số lượng request trong một khung thời gian cụ thể.
     *
     * @param logEntries Danh sách các log đã phân tích.
     * @param startTime Thời gian bắt đầu.
     * @param endTime Thời gian kết thúc.
     * @return Số lượng request trong khung thời gian.
     */
    public static int calculateRequestsInTimeRange(List<LogEntry> logEntries, Date startTime, Date endTime) {
        int count = 0;
        for (LogEntry entry : logEntries) {
            if (TimeUtils.isWithinTimeRange(entry.getTimestamp(), startTime, endTime)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Tính số lượng request theo loại log (INPUT hoặc OUTPUT).
     *
     * @param logEntries Danh sách các log đã phân tích.
     * @param logType Loại log cần đếm (INPUT hoặc OUTPUT).
     * @return Số lượng request thuộc loại log.
     */
    public static int calculateRequestsByType(List<LogEntry> logEntries, String logType) {
        int count = 0;
        for (LogEntry entry : logEntries) {
            if (logType.equalsIgnoreCase(entry.getLogType())) {
                count++;
            }
        }
        return count;
    }
}

