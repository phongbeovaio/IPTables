package utils;

import models.IptablesModel;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {

    // Logger để ghi lại thông tin, cảnh báo...
    private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getName());

    /**
     * Đọc toàn bộ nội dung file dưới dạng List<String> (mỗi phần tử là 1 dòng).
     * @param filePath Đường dẫn đến file log
     * @return Danh sách chuỗi, mỗi chuỗi là 1 dòng
     */
    public static List<String> readLogFile(String filePath) {
        List<String> logs = new ArrayList<>();
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            LOGGER.log(Level.SEVERE, "File không tồn tại: " + filePath);
            return logs;
        }

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                logs.add(line);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Đọc file thất bại: " + filePath, e);
        }

        return logs;
    }

    /**
     * Viết danh sách chuỗi ra file (mỗi chuỗi trên 1 dòng).
     * @param filePath Đường dẫn file xuất
     * @param data Danh sách chuỗi cần ghi
     */
    public static void writeToFile(String filePath, List<String> data) {
        Path path = Paths.get(filePath);

        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
            LOGGER.log(Level.INFO, "Ghi dữ liệu thành công vào file: " + filePath);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Ghi file thất bại: " + filePath, e);
        }
    }

    /**
     * Đọc file log iptables rồi parse thành danh sách LogEntry.
     * @param filePath Đường dẫn đến file log
     * @return Danh sách LogEntry đã parse thành công (nếu dòng nào parse lỗi, có thể bỏ qua hoặc trả về null)
     */
    public static List<IptablesModel> readLogEntries(String filePath) {
        List<String> lines = readLogFile(filePath);  // Đọc từng dòng dạng text
        List<IptablesModel> entries = new ArrayList<>();

        for (String line : lines) {
            IptablesModel entry = parseIptablesLogLine(line);
            if (entry != null) {
                entries.add(entry);
            }
        }
        return entries;
    }

    /**
     * Parse 1 dòng log iptables thành LogEntry (nếu không parse được thì trả về null).
     * Log ví dụ:
     * 2025-01-15T14:08:55.800240+07:00 phong-VirtualBox kernel: Dropped UDP: IN=enp0s3 OUT= MAC=... SRC=192.168.20.79 DST=255.255.255.255 ...
     */
    private static IptablesModel parseIptablesLogLine(String line) {
        try {
            // Bước 1: Tách timestamp (ISO8601) ở đầu, hostName, 'kernel:' và phần còn lại
            // Giả sử format:
            // [TIMESTAMP] [HOST] kernel: [PREFIX] [CÁC TRƯỜNG ...]
            // => Dùng split giới hạn
            // Tìm vị trí khoảng trắng sau timestamp
            int firstSpace = line.indexOf(' ');
            if (firstSpace < 0) return null; // Không tìm thấy

            String timePart = line.substring(0, firstSpace).trim();
            String remain = line.substring(firstSpace).trim();

            // Tách tiếp cho đến "kernel:"
            // "phong-VirtualBox kernel: Dropped UDP: IN=..."
            // Tìm "kernel: "

            int kernelIndex = remain.indexOf("kernel:");
            if (kernelIndex < 0) {
                // Format không đúng như mong đợi, vẫn cố parse prefix
                kernelIndex = 0; // Cho an toàn, skip
            } else {
                kernelIndex += "kernel:".length();
            }

            String afterKernel = remain.substring(kernelIndex).trim(); // "Dropped UDP: IN=..."

            // Bước 2: Parse timestamp -> LocalDateTime (dùng TimeUtils)
            // TimeUtils trả về Date, ta convert sang LocalDateTime

            java.util.Date date = TimeUtils.parseTimestamp(timePart);
            java.time.LocalDateTime dateTime = null;
            if (date != null) {
                dateTime = date.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
            }

            // Bước 3: Lấy prefix (VD: "Dropped UDP:" hay "Accept SSH:")
            // => prefix kết thúc trước khi bắt đầu "IN=" hoặc "PROTO=" v.v. (thường có ": ").
            // Có thể tách theo ": " 1-2 lần:
            // Ví dụ "Dropped UDP:" => prefix = "Dropped UDP"
            // Chú ý: prefix đôi khi có 2 dấu ":", ví dụ "Dropped DDoS:" =>cẩn thận
            String prefix;
            String fields;

            int firstInIdx = afterKernel.indexOf("IN=");
            if (firstInIdx < 0) {
                // Trường hợp không có "IN=" => Tách prefix = entire
                prefix = afterKernel;
                fields = "";
            } else {
                prefix = afterKernel.substring(0, firstInIdx).trim();
                fields = afterKernel.substring(firstInIdx).trim();
            }
            // Bỏ dấu ":" ở cuối prefix (nếu có)
            if (prefix.endsWith(":")) {
                prefix = prefix.substring(0, prefix.length() - 1).trim();
            }

            // Bước 4: parse các cặp key=value
            // Thí dụ: "IN=enp0s3 OUT= MAC=ff:ff:ff... SRC=192.168.20.79 DST=255.255.255.255 LEN=356 ..."
            // Ta sẽ split bằng khoảng trắng, sau đó tách key=value
            String[] tokens = fields.split("\\s+");

            // Tạo LogEntry
            IptablesModel logEntry = new IptablesModel();
            logEntry.setTimestamp(dateTime);
            logEntry.setLogPrefix(prefix);  // "Dropped UDP" hoặc "Accept SSH", v.v.

            for (String token : tokens) {
                // Mỗi token kiểu KEY=VAL (hoặc KEY=)
                int eqIndex = token.indexOf('=');
                if (eqIndex < 1) continue;

                String key = token.substring(0, eqIndex).toUpperCase().trim();  // IN, OUT, SRC, DST, ...
                String val = token.substring(eqIndex + 1).trim();               // enp0s3, 192.168.x.x, ...
                if (val.isEmpty()) continue; // rỗng => bỏ qua

                switch (key) {
                    case "IN":
                        logEntry.setInInterface(val);
                        break;
                    case "OUT":
                        logEntry.setOutInterface(val);
                        break;
                    case "MAC":
                        logEntry.setMacAddress(val);
                        break;
                    case "SRC":
                        logEntry.setSourceIP(val);
                        break;
                    case "DST":
                        logEntry.setDestinationIP(val);
                        break;
                    case "PROTO":
                        logEntry.setProtocol(val);
                        break;
                    case "LEN":
                        logEntry.setLength(tryParseInt(val));
                        break;
                    case "TOS":
                        logEntry.setTos(val);
                        break;
                    case "PREC":
                        logEntry.setPrec(val);
                        break;
                    case "TTL":
                        logEntry.setTtl(tryParseInt(val));
                        break;
                    case "ID":
                        logEntry.setId(tryParseInt(val));
                        break;
                    case "SPT":
                        logEntry.setSourcePort(tryParseInt(val));
                        break;
                    case "DPT":
                        logEntry.setDestinationPort(tryParseInt(val));
                        break;
                    case "WINDOW":
                        logEntry.setWindow(tryParseInt(val));
                        break;
                    case "URGP":
                        logEntry.setUrgp(tryParseInt(val));
                        break;
                    case "DF":
                        // DF không có dạng DF=1, thường DF là cờ. Có khi log sẽ ghi "DF" thay vì DF=?
                        // Ở đây nếu key="DF" => ta set df=true
                        logEntry.setDf(true);
                        break;
                    default:
                        // Các key khác bỏ qua hoặc sau này mở rộng
                        break;
                }
            }

            return logEntry;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Lỗi khi parse dòng log: " + line, e);
            return null;
        }
    }

    /**
     * Hàm tiện ích để parse chuỗi thành Integer (nếu lỗi thì trả về null).
     */
    private static Integer tryParseInt(String val) {
        try {
            return Integer.valueOf(val);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
