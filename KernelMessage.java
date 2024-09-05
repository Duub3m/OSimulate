public class KernelMessage {
    private int senderPid;
    private int targetPid;
    private int messageType;
    private byte[] data;

    // Constructor for Kernel Message -  clones the array
    public KernelMessage(int senderPid, int targetPid, int messageType, byte[] data) {
     this.senderPid = senderPid;
    this.targetPid = targetPid;
     this.messageType = messageType;
        this.data = data.clone(); 
    }

    // Copies the constructor -Creates a copy of the data array
    public KernelMessage(KernelMessage other) {
    this.senderPid = other.senderPid;
        this.targetPid = other.targetPid;
     this.messageType = other.messageType;
    this.data = other.data.clone(); 
    }


    // Getter: senderPid
    public int getSenderPid() {
        return senderPid;
    }

    // Getter: targetPid
    public int getTargetPid() {
        return targetPid;
    }

    // Getter: messageType
    public int getMessageType() {
        return messageType;
    }

    // Getter: data - Returns a clone 
    public byte[] getData() {
        return data.clone(); 
    }
}
