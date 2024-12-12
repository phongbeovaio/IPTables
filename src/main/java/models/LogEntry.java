package models;
import java.util.Date;

public class LogEntry {
    private Date timestamp;          // Thời gian ghi log
    private String sourceIP;         // Địa chỉ IP nguồn (SRC)
    private String destinationIP;    // Địa chỉ IP đích (DST)
    private String sourcePort;       // Cổng nguồn (SRC port)
    private String destinationPort;  // Cổng đích (DST port)
    private String protocol;         // Giao thức (PROTO)
    private int length;              // Chiều dài của gói tin (LEN)
    private int packetCount;         // Số lượng gói tin (PACKETS)
    private int byteCount;           // Số lượng byte (BYTES)
    private String inInterface;      // Interface vào (IN)
    private String outInterface;     // Interface ra (OUT)
    private String logType;          // Loại log (INPUT/OUTPUT)
    private String status;           // Trạng thái log (SUCCESS/FAIL)

    // Constructor
    public LogEntry(Date timestamp, String sourceIP, String destinationIP, String sourcePort,
                    String destinationPort, String protocol, int length, int packetCount, int byteCount,
                    String inInterface, String outInterface, String logType, String status) {
        this.timestamp = timestamp;
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.protocol = protocol;
        this.length = length;
        this.packetCount = packetCount;
        this.byteCount = byteCount;
        this.inInterface = inInterface;
        this.outInterface = outInterface;
        this.logType = logType;
        this.status = status; // Gán trạng thái
    }

    // Getters and Setters
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getSourceIP() { return sourceIP; }
    public void setSourceIP(String sourceIP) { this.sourceIP = sourceIP; }

    public String getDestinationIP() { return destinationIP; }
    public void setDestinationIP(String destinationIP) { this.destinationIP = destinationIP; }

    public String getSourcePort() { return sourcePort; }
    public void setSourcePort(String sourcePort) { this.sourcePort = sourcePort; }

    public String getDestinationPort() { return destinationPort; }
    public void setDestinationPort(String destinationPort) { this.destinationPort = destinationPort; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }

    public int getPacketCount() { return packetCount; }
    public void setPacketCount(int packetCount) { this.packetCount = packetCount; }

    public int getByteCount() { return byteCount; }
    public void setByteCount(int byteCount) { this.byteCount = byteCount; }

    public String getInInterface() { return inInterface; }
    public void setInInterface(String inInterface) { this.inInterface = inInterface; }

    public String getOutInterface() { return outInterface; }
    public void setOutInterface(String outInterface) { this.outInterface = outInterface; }

    public String getLogType() { return logType; }
    public void setLogType(String logType) { this.logType = logType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
