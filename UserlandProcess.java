import java.util.concurrent.Semaphore;
import java.util.Random;


public abstract class UserlandProcess implements Runnable {
    protected Thread thread;
    protected Semaphore semaphore = new Semaphore(0);
    protected boolean quantumExpired = false;
    private static int[][] TLB = {{-1, -1}, {-1, -1}};
    private static final int pageSize = 1024; // 1KB 
    private static final Random random = new Random();


    //Memory as an array of bytes.
    private static byte[] memory = new byte[1024 * 1024]; // 1MB 

    public byte Read(int virtualAddress) {
        int virtualPageNumber = virtualAddress / pageSize;
        int pageOffset = virtualAddress % pageSize;

        // Checks TLB virtual page number
        for (int i = 0; i < TLB.length; i++) {
            if (TLB[i][0] == virtualPageNumber) {
                
                int physicalPageNumber = TLB[i][1];
                int physicalAddress = physicalPageNumber * pageSize + pageOffset;
                return memory[physicalAddress]; // read operation
            }
        }
       

        return 0;
    }

    public void Write(int virtualAddress, byte value) {
        int virtualPageNumber = virtualAddress / pageSize;
        int pageOffset = virtualAddress % pageSize;

        // Checks  TLB for virtual page number
        for (int i = 0; i < TLB.length; i++) {
            if (TLB[i][0] == virtualPageNumber) {
                
                int physicalPageNumber = TLB[i][1];
                int physicalAddress = physicalPageNumber * pageSize + pageOffset;
                memory[physicalAddress] = value; //write operation
                return;
            }
        }
    }
    //Helper method 
    // Loads a mapping to the TLB
    public static void loadTLB(int virtualPage, int physicalPage) {
        int switchEntry = random.nextInt(TLB.length); // switches TLB entry 
        TLB[switchEntry][0] = virtualPage;
        TLB[switchEntry][1] = physicalPage;
    }


    public UserlandProcess() {
        this.thread = new Thread(this);
    }

    //void requestStop() – sets the boolean indicating that this process’ quantum has expired 
    public void requestStop() {
        quantumExpired = true;
    }

    // abstract void main() – will represent the main of our “program” 
    public abstract void main();

    // boolean isStopped() – indicates if the semaphore is 0 
    public boolean isStopped() {
        return semaphore.availablePermits() == 0;
    }

    // boolean isDone() – true when the Java thread is not alive 
    public boolean isDone() {
        return !thread.isAlive();
    }

    // void start() – releases (increments) the semaphore, allowing this thread to run 
    public void start() {
        semaphore.release(); // Allows the thread to start running
        thread.start();
    }

    //void stop() – acquires (decrements) the semaphore, stopping this thread from running 
    public void stop() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //void run() – acquire the semaphore, then call main 
    @Override
    public void run() {
        try {
            semaphore.acquire();
            main();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //void cooperate() – if the boolean is true, set the boolean to false and call OS.switchProcess() 
    protected void cooperate() {
        if (quantumExpired) {
            quantumExpired = false;
            OS.switchProcess();
        }
    }
}

