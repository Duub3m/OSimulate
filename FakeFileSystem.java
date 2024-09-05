import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FakeFileSystem implements Device {
     //keeps an array (10 items) of java.util.Random
    private final RandomAccessFile[] files = new RandomAccessFile[10];

    @Override
    //Open
    public int Open(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("The filename can't be empty/null");
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i] == null) { // Find the first empty slot
                try {
                    files[i] = new RandomAccessFile(filename, "rw");
                    return i; // Uses the index as the file ID
                } catch (FileNotFoundException e) {
                    System.err.println("Couldn't find the file" + filename);
                    return -1;
                }
            }
        }

        return -1; 
    }

    @Override
    //Close
    //Note: close the RandomAccessFile and clear out your internal array when close() is called for this device.
    public void Close(int id) {
        if (id >= 0 && id < files.length && files[id] != null) {
            try {
                files[id].close();// Close
                files[id] = null; // Clears the entry 
            } catch (IOException e) {
                System.err.println("Error with close" + e.getMessage());
            }
        }
    }

    @Override
    //Read
    public byte[] Read(int id, int size) {
        if (id >= 0 && id < files.length && files[id] != null) {
            byte[] buffer = new byte[size];
            try {
                files[id].read(buffer);
                return buffer;
            } catch (IOException e) {
                System.err.println("Error with read" + e.getMessage());
            }
        }
        return new byte[0]; // Returns an empty array 
    }

    @Override
    //Seek
    public void Seek(int id, int position) {
        if (id >= 0 && id < files.length && files[id] != null) {
            try {
                files[id].seek(position);
            } catch (IOException e) {
                System.err.println("Error with seek" + e.getMessage());
            }
        }
    }

    @Override
    //Write
    public int Write(int id, byte[] data) {
        if (id >= 0 && id < files.length && files[id] != null) {
            try {
                files[id].write(data);
                return data.length; // Return the number of bytes written
            } catch (IOException e) {
                System.err.println("Error with write " + e.getMessage());
            }
        }
        return 0; // in case theres an error we Return 0 
    }
}
