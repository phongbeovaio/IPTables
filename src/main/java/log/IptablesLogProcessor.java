package log;

import models.IptablesModel;
import utils.TimeUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IptablesLogProcessor {

    /**
     * Phân tích danh sách log thô (chuỗi) thành các đối tượng LogEntry.
     * Chú ý: Đã có hàm tương tự trong FileUtils (parseIptablesLogLine).
     * @param rawLogs Danh sách log thô.
     * @return Danh sách LogEntry đã được phân tích.
     */
    public static List<IptablesModel> parseLogs(List<String> rawLogs) {
        List<IptablesModel> logEntries = new ArrayList<>();

        for (String line : rawLogs) {
            try {
                // 1) Tách timestamp (phần đầu, giả sử log "2025-01-15T14:08:55.800240+07:00 ...")
                int firstSpace = line.indexOf(' ');
                if (firstSpace < 0) {
                    // Không đúng format => bỏ qua
                    continue;
                }
                String timePart = line.substring(0, firstSpace).trim();  // "2025-01-15T14:08:55.800240+07:00"
                String remain    = line.substring(firstSpace).trim();     // "phong-VirtualBox kernel: Dropped UDP: IN=... "

                // Parse sang Date, rồi chuyển sang LocalDateTime
                Date dateParsed = TimeUtils.parseTimestamp(timePart);
                LocalDateTime dateTime = null;
                if (dateParsed != null) {
                    dateTime = dateParsed.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                }

                // 2) Tách prefix (vd "Dropped UDP:"), sau đó tách key=value
                // Sử dụng logic giống FileUtils.parseIptablesLogLine
                int kernelIndex = remain.indexOf("kernel:");
                String afterKernel = (kernelIndex >= 0)
                        ? remain.substring(kernelIndex + "kernel:".length()).trim()
                        : remain;  // fallback

                // Lấy prefix (trước "IN=")
                int inIndex = afterKernel.indexOf("IN=");
                String prefix = (inIndex > 0)
                        ? afterKernel.substring(0, inIndex).replaceAll(":$", "").trim()
                        : afterKernel;

                // Tách các cặp key=value
                String keyValues = (inIndex > 0)
                        ? afterKernel.substring(inIndex).trim()
                        : "";

                // 3) Tạo LogEntry
                IptablesModel entry = new IptablesModel();
                entry.setTimestamp(dateTime);      // LocalDateTime
                entry.setLogPrefix(prefix);        // "Dropped UDP", "Accept SSH", v.v.

                // Tách token "IN=...", "OUT=...", "SRC=...", ...
                String[] tokens = keyValues.split("\\s+");
                for (String token : tokens) {
                    int eqIdx = token.indexOf('=');
                    if (eqIdx < 1) {
                        // Trường hợp key không có "=" (vd DF) thì tùy ý handle
                        if ("DF".equalsIgnoreCase(token)) {
                            entry.setDf(true);
                        }
                        continue;
                    }
                    String key = token.substring(0, eqIdx).toUpperCase();
                    String val = token.substring(eqIdx + 1);

                    switch (key) {
                        case "IN":
                            entry.setInInterface(val);
                            break;
                        case "OUT":
                            entry.setOutInterface(val);
                            break;
                        case "SRC":
                            entry.setSourceIP(val);
                            break;
                        case "DST":
                            entry.setDestinationIP(val);
                            break;
                        case "PROTO":
                            entry.setProtocol(val);
                            break;
                        case "LEN":
                            entry.setLength(tryParseInt(val));
                            break;
                        case "SPT":
                            entry.setSourcePort(tryParseInt(val));
                            break;
                        case "DPT":
                            entry.setDestinationPort(tryParseInt(val));
                            break;
                        case "MAC":
                            entry.setMacAddress(val);
                            break;
                        case "TOS":
                            entry.setTos(val);
                            break;
                        case "PREC":
                            entry.setPrec(val);
                            break;
                        case "TTL":
                            entry.setTtl(tryParseInt(val));
                            break;
                        case "ID":
                            entry.setId(tryParseInt(val));
                            break;
                        case "WINDOW":
                            entry.setWindow(tryParseInt(val));
                            break;
                        case "URGP":
                            entry.setUrgp(tryParseInt(val));
                            break;
                        default:
                            // key khác không quan tâm
                            break;
                    }
                }

                logEntries.add(entry);

            } catch (Exception e) {
                System.err.println("Lỗi khi parse log dòng: " + line);
                e.printStackTrace();
            }
        }

        return logEntries;
    }

    /**
     * Tìm kiếm log theo 1 chuỗi. (VD: IP, protocol, prefix…)
     * @param logEntries Danh sách LogEntry
     * @param searchTerm Từ khóa tìm
     * @return Danh sách LogEntry khớp
     */
    public static List<IptablesModel> searchLogs(List<IptablesModel> logEntries, String searchTerm) {
        List<IptablesModel> results = new ArrayList<>();
        if (searchTerm == null || searchTerm.isEmpty()) {
            return results; // hoặc trả luôn logEntries nếu muốn
        }

        String lowerSearch = searchTerm.toLowerCase();

        for (IptablesModel entry : logEntries) {
            boolean match = false;

            if (entry.getLogPrefix() != null
                    && entry.getLogPrefix().toLowerCase().contains(lowerSearch)) {
                match = true;
            }
            if (!match && entry.getSourceIP() != null
                    && entry.getSourceIP().toLowerCase().contains(lowerSearch)) {
                match = true;
            }
            if (!match && entry.getDestinationIP() != null
                    && entry.getDestinationIP().toLowerCase().contains(lowerSearch)) {
                match = true;
            }
            if (!match && entry.getProtocol() != null
                    && entry.getProtocol().toLowerCase().contains(lowerSearch)) {
                match = true;
            }
            // sourcePort, destinationPort là Integer => convert sang String để .contains(...)
            if (!match && entry.getSourcePort() != null
                    && String.valueOf(entry.getSourcePort()).contains(searchTerm)) {
                match = true;
            }
            if (!match && entry.getDestinationPort() != null
                    && String.valueOf(entry.getDestinationPort()).contains(searchTerm)) {
                match = true;
            }

            if (match) {
                results.add(entry);
            }
        }
        return results;
    }

    /**
     * Lọc log theo khoảng thời gian (startTime, endTime) - đều là Date.
     * Nhưng LogEntry.timestamp là LocalDateTime => chuyển đổi để so sánh.
     */
    public static List<IptablesModel> filterLogsByTime(List<IptablesModel> logEntries, Date startTime, Date endTime) {
        List<IptablesModel> results = new ArrayList<>();
        if (startTime == null || endTime == null) {
            // Tuỳ nhu cầu, có thể trả về rỗng hoặc trả về toàn bộ
            return results;
        }

        for (IptablesModel entry : logEntries) {
            if (entry.getTimestamp() == null) {
                continue;
            }
            // Chuyển LocalDateTime -> Date
            Date logDate = toDate(entry.getTimestamp());
            // Dùng TimeUtils.isWithinTimeRange
            if (TimeUtils.isWithinTimeRange(logDate, startTime, endTime)) {
                results.add(entry);
            }
        }
        return results;
    }

    /**
     * Hàm phụ: chuyển LocalDateTime -> Date (dùng zone mặc định)
     */
    private static Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Hàm phụ parse integer
     */
    private static Integer tryParseInt(String val) {
        try {
            return Integer.valueOf(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
