// đọc file
package utils;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {

    // Tạo đối tượng Logger để ghi log lỗi hoặc thông báo
    private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getName());
    /**
     * Đọc file log và trả về danh sách các dòng log.
     * @param filePath Đường dẫn đến file log cần đọc
     * @return List<String> Danh sách các dòng log
     */
    public static List<String> readLogFile(String filePath) {
        List<String> logs = new ArrayList<>();  // Danh sách để chứa các dòng log
        Path path = Paths.get(filePath);  // Chuyển đường dẫn thành đối tượng Path

        // Kiểm tra sự tồn tại của file
        if (!Files.exists(path)) {
            LOGGER.log(Level.SEVERE, "File không tồn tại: " + filePath);
            return logs;  // Nếu không tìm thấy file, trả về danh sách rỗng
        }

        try (BufferedReader br = Files.newBufferedReader(path)) {
            // Dùng BufferedReader để đọc file, không dùng điều cũ là FileReader
            String line;
            // Đọc từng dòng của file và thêm vào danh sách logs
            while ((line = br.readLine()) != null) {
                logs.add(line);
            }
        } catch (IOException e) {
            // Ghi lại lỗi nếu xảy ra trong quá trình đọc file
            LOGGER.log(Level.SEVERE, "Đọc file thất bại: " + filePath, e);
        }
        return logs;  // Trả về danh sách log
    }

    /**
     * Ghi danh sách dữ liệu vào file.
     * @param filePath Đường dẫn đến file để ghi dữ liệu
     * @param data Danh sách dữ liệu cần ghi vào file
     */
    public static void writeToFile(String filePath, List<String> data) {
        Path path = Paths.get(filePath);  // Chuyển đường dẫn thành đối tượng Path

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            // Duyệt qua từng dòng dữ liệu trong danh sách và ghi vào file
            for (String line : data) {
                writer.write(line);  // Ghi dòng vào file
                writer.newLine();  // Chèn dòng mới sau mỗi dòng ghi
            }
            LOGGER.log(Level.INFO, "Ghi dữ liệu thành công vào file: " + filePath);  // Thông báo khi ghi thành công
        } catch (IOException e) {
            // Ghi lại lỗi nếu xảy ra trong quá trình ghi file
            LOGGER.log(Level.SEVERE, "Ghi file thất bại: " + filePath, e);
        }
    }
}
