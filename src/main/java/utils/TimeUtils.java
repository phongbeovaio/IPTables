// chuyển đổi chuỗi thời gian từ log sang đúng với định dạng Java
// So sánh thời gian để lọc log theo khoảng thời gian

package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeUtils {
    public static boolean isISOFormat(String timestampStr) {
        // ISO 8601 có dạng: yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX
        return timestampStr.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}[+-]\\d{2}:\\d{2}$");
    }


    // Map chuyển đổi tháng từ tiếng Việt sang tiếng Anh
    private static final Map<String, String> monthMap = new HashMap<>();

    static {
        monthMap.put("Thg 1", "Jan");
        monthMap.put("Thg 2", "Feb");
        monthMap.put("Thg 3", "Mar");
        monthMap.put("Thg 4", "Apr");
        monthMap.put("Thg 5", "May");
        monthMap.put("Thg 6", "Jun");
        monthMap.put("Thg 7", "Jul");
        monthMap.put("Thg 8", "Aug");
        monthMap.put("Thg 9", "Sep");
        monthMap.put("Thg 10", "Oct");
        monthMap.put("Thg 11", "Nov");
        monthMap.put("Thg 12", "Dec");
    }

    /**
     * Chuyển đổi chuỗi thời gian từ log sang dạng chuẩn trong Java.
     * @param timestampStr Chuỗi thời gian trong log (vd: "Thg 11 21 23:50:03").
     * @return Đối tượng Date nếu thành công, null nếu thất bại.
     */
    public static Date parseTimestamp(String timestampStr) {
        try {
            if (isISOFormat(timestampStr)) {
                // Định dạng ISO 8601
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
                return isoFormat.parse(timestampStr);
            } else {
                // Định dạng log tiếng Việt
                String[] parts = timestampStr.split(" ");
                if (parts.length < 4) {
                    throw new ParseException("Chuỗi thời gian không đầy đủ: " + timestampStr, 0);
                }

                String month = parts[0] + " " + parts[1]; // vd: "Thg 12"
                String day = parts[2];                   // vd: "04"
                String time = parts[3];                  // vd: "15:53:12"

                // Chuyển tháng từ tiếng Việt sang tiếng Anh
                String monthInEnglish = monthMap.getOrDefault(month, "Jan");

                // Tạo chuỗi thời gian phù hợp với định dạng SimpleDateFormat
                String formattedTimestamp = monthInEnglish + " " + day + " " + time;

                // Định dạng thời gian: "Dec 04 15:53:12"
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm:ss");
                return sdf.parse(formattedTimestamp);
            }
        } catch (ParseException e) {
            // Ghi log lỗi và trả về null
            System.err.println("Lỗi khi chuyển đổi thời gian từ log: " + timestampStr);
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseUserFriendlyTimestamp(String timestampStr) {
        try {
            // Thử định dạng đơn giản yyyy-MM-dd HH:mm:ss
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.parse(timestampStr);
        } catch (ParseException e1) {
            try {
                // Thử định dạng cũ (Thg 11 21 23:50:00)
                return parseTimestamp(timestampStr); // Gọi phương thức parseTimestamp hiện tại
            } catch (Exception e2) {
                System.err.println("Lỗi: Thời gian không hợp lệ - " + timestampStr);
                return null;
            }
        }
    }




    /**
     * Kiểm tra xem thời gian trong log có nằm trong khoảng thời gian hay không.
     * @param logTime Thời gian trong log.
     * @param startTime Thời gian bắt đầu.
     * @param endTime Thời gian kết thúc.
     * @return true nếu logTime nằm trong khoảng thời gian, ngược lại false.
     */
    public static boolean isWithinTimeRange(Date logTime, Date startTime, Date endTime) {
        return logTime != null && logTime.after(startTime) && logTime.before(endTime);
    }
}

