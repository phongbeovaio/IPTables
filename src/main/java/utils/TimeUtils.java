// chuyển đổi chuỗi thời gian từ log sang đúng với định dạng Java
// So sánh thời gian để lọc log theo khoảng thời gian

package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeUtils {

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
            // Tách chuỗi để xử lý tháng, ngày và thời gian
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

        } catch (ParseException e) {
            // Ghi log lỗi và trả về null
            System.err.println("Lỗi khi chuyển đổi thời gian từ log: " + timestampStr);
            e.printStackTrace();
            return null;
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

/*
   public static void main(String[] args) {
    String logTime = "Thg 11 21 23:50:03"; // Dữ liệu mẫu
    Date parsedDate = TimeUtils.parseTimestamp(logTime);
    if (parsedDate != null) {
        System.out.println("Thời gian đã chuyển đổi: " + parsedDate);
    } else {
        System.out.println("Không thể chuyển đổi thời gian từ log.");
    }
}

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws Exception {
        String logTime = "Thg 11 21 23:50:03";
        Date parsedLogTime = TimeUtils.parseTimestamp(logTime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = sdf.parse("2022-11-21 23:00:00");
        Date endTime = sdf.parse("2022-11-21 23:59:59");

        if (TimeUtils.isWithinTimeRange(parsedLogTime, startTime, endTime)) {
            System.out.println("Log nằm trong khoảng thời gian.");
        } else {
            System.out.println("Log không nằm trong khoảng thời gian.");
        }
    }
}

*/