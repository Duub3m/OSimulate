public class PCB {
    private static int nextPid = 1; //PIDs
    private final int pid; // Process ID for PCB
    private final UserlandProcess up; //UserlandProcess
    private Thread processThread; // Thread for UserlandProcess

    public PCB(UserlandProcess up) {
        this.up = up;
        this.pid = nextPid++;
        // Create a thread for the UserlandProcess + run
        this.processThread = new Thread(up::start);
    }

    public void stop() {
        // makes the UserlandProcess stop
        up.requestStop();
        // Wait until UserlandProcess stops
        while (!up.isStopped()) {
            try {
                Thread.sleep(10); // Checks repeadetly every 10 milli seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isDone() {
        // UserlandProcess isDone method
        return up.isDone();
    }

    public void run() {
        // Starts the thread for UserlandProcess
        processThread.start();
    }

    // Process ID getter
    public int getPid() {
        return pid;
    }
}
