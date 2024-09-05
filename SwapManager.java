import java.io.IOException;
import java.io.RandomAccessFile;

public class SwapManager {
    private RandomAccessFile swapFile;
    private int nextPageToWrite = 0;

    public SwapManager() throws IOException {
        swapFile = new RandomAccessFile("swapfile.sys", "rw");
    }

    //writing and reading from the swapped file
    public int writeToSwap(byte[] data) throws IOException {
        int page = nextPageToWrite++;
        swapFile.seek(page * 1024L); // 1024 bytes for each page
        swapFile.write(data);
        return page; //returns the page
    }

    //reads from swap
    public byte[] readFromSwap(int page) throws IOException {
        byte[] data = new byte[1024];
        swapFile.seek(page * 1024L);
        swapFile.readFully(data);
        return data;
    }
}
