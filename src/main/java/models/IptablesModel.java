package models;

import java.time.LocalDateTime;

public class IptablesModel {
    private LocalDateTime timestamp;   // Thời gian của mục log (khi gói tin được ghi lại)
    private String logPrefix;          // Mô tả hành động: ví dụ "Dropped DDoS", "Accept SSH", ...
    private String inInterface;        // Cổng vào (Interface In) của gói tin mạng
    private String outInterface;       // Cổng ra (Interface Out) của gói tin mạng
    private String macAddress;         // Địa chỉ MAC của thiết bị gửi/nhận
    private String sourceIP;           // Địa chỉ IP nguồn (SRC)
    private String destinationIP;      // Địa chỉ IP đích (DST)
    private String protocol;           // Giao thức mạng (ví dụ: TCP, UDP)
    private Integer sourcePort;        // Cổng nguồn (SPT)
    private Integer destinationPort;   // Cổng đích (DPT)
    private Integer length;            // Chiều dài của gói tin (LEN)
    private String tos;                // TOS (Type of Service) trong gói tin
    private String prec;               // PREC (Precedence) trong gói tin
    private Integer ttl;               // TTL (Time to Live) trong gói tin
    private Integer id;                // ID của gói tin, giúp nhận diện gói tin duy nhất
    private Boolean df;                // Cờ DF (Don't Fragment), cho biết gói tin có thể bị phân mảnh không
    private Integer window;            // Kích thước cửa sổ TCP (Window Size)
    private Integer urgp;              // URGP, chỉ định vị trí của dữ liệu trong gói tin TCP (nếu có)


    public IptablesModel() {

    }

    // Constructor có tham số, khởi tạo một đối tượng LogEntry với các giá trị cụ thể
    public IptablesModel(LocalDateTime timestamp, String logPrefix, String inInterface,
                         String outInterface, String macAddress, String sourceIP, String destinationIP,
                         String protocol, Integer sourcePort, Integer destinationPort,
                         Integer length, String tos, String prec, Integer ttl, Integer id,
                         Boolean df, Integer window, Integer urgp) {
        this.timestamp = timestamp;
        this.logPrefix = logPrefix;
        this.inInterface = inInterface;
        this.outInterface = outInterface;
        this.macAddress = macAddress;
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
        this.protocol = protocol;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.length = length;
        this.tos = tos;
        this.prec = prec;
        this.ttl = ttl;
        this.id = id;
        this.df = df;
        this.window = window;
        this.urgp = urgp;
    }


    // Getter và Setter
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getLogPrefix() { return logPrefix; }
    public void setLogPrefix(String logPrefix) { this.logPrefix = logPrefix; }

    public String getInInterface() { return inInterface; }
    public void setInInterface(String inInterface) { this.inInterface = inInterface; }

    public String getOutInterface() { return outInterface; }
    public void setOutInterface(String outInterface) { this.outInterface = outInterface; }

    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public String getSourceIP() { return sourceIP; }
    public void setSourceIP(String sourceIP) { this.sourceIP = sourceIP; }

    public String getDestinationIP() { return destinationIP; }
    public void setDestinationIP(String destinationIP) { this.destinationIP = destinationIP; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public Integer getSourcePort() { return sourcePort; }
    public void setSourcePort(Integer sourcePort) { this.sourcePort = sourcePort; }

    public Integer getDestinationPort() { return destinationPort; }
    public void setDestinationPort(Integer destinationPort) { this.destinationPort = destinationPort; }

    public Integer getLength() { return length; }
    public void setLength(Integer length) { this.length = length; }

    public String getTos() { return tos; }
    public void setTos(String tos) { this.tos = tos; }

    public String getPrec() { return prec; }
    public void setPrec(String prec) { this.prec = prec; }

    public Integer getTtl() { return ttl; }
    public void setTtl(Integer ttl) { this.ttl = ttl; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Boolean getDf() { return df; }
    public void setDf(Boolean df) { this.df = df; }

    public Integer getWindow() { return window; }
    public void setWindow(Integer window) { this.window = window; }

    public Integer getUrgp() { return urgp; }
    public void setUrgp(Integer urgp) { this.urgp = urgp; }
}
