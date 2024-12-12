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
            totalLength += entry.getLength();
        }
        return totalLength;
    }

    /**
     * Tính số lượng request bị chặn (status = FAIL).
     *
     * @param logEntries Danh sách log đã phân tích.
     * @return Tổng số request bị chặn.
     */
    public static int calculateBlockedRequests(List<LogEntry> logEntries) {
        return (int) logEntries.stream()
                .filter(entry -> "FAIL".equals(entry.getStatus()))
                .count();
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
}
