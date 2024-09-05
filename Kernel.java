import java.util.concurrent.Semaphore;

public class Kernel implements Runnable, Device {

    // Manages the process
    private final Scheduler scheduler = new Scheduler();
    private final Thread thread;
    private final Semaphore semaphore = new Semaphore(0);
    private VFS vfs = new VFS(); 

    public Kernel() {
        this.thread = new Thread(this);
    }

    // GetPid method
    public int GetPid() {
        KernelandProcess currentProcess = scheduler.getCurrentlyRunning();
        return currentProcess != null ? currentProcess.getPid() : -1;
    }

    // GetPidByName method
    public int GetPidByName(String name) {
        return scheduler.getPidByName(name);
    }

    //SendMessage - uses the copy constructor to make a copy of the original message
    public void sendMessage(KernelMessage km) {
    // Adds the message to the targets message queue
    KernelandProcess targetProcess = scheduler.getProcessById(km.getTargetPid());
    if (targetProcess != null) {
        targetProcess.getMessageQueue().add(new KernelMessage(km)); 
    }
}

// WaitForMessage Method -check to see if the current process has a message
public KernelMessage WaitForMessage() {
    KernelandProcess currentProcess = scheduler.getCurrentlyRunning();
    if (currentProcess != null) { ; //if so take it off of the queue and return it
        return currentProcess.waitForMessage();
    }
    return null;
}

    public void start() {
        semaphore.release();
        // Starts the kernel thread
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    @Override
    public void run() {
        // Checks for the system calls
        while (true) {
            try {
               semaphore.acquire();
              // Checks if the system call is to create a process
                if (OS.getCurrentCall() == OS.CallType.CREATEPROCESS) { 
                // Gets the UserlandProcess from parameters
                  UserlandProcess up = (UserlandProcess) OS.parameters.get(0);
                  // Determines which priority is used for the process
                  // If there is none, then the default becomes INTERACTIVE
                    OS.Priority priority = OS.parameters.size() > 1 && OS.parameters.get(1) instanceof OS.Priority ?                          
                    (OS.Priority) OS.parameters.get(1) : OS.Priority.INTERACTIVE; 
                  // Creates the process with the priority
                scheduler.createProcess(up, priority);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    // Device interface implementation for Kernel
    @Override
    public int Open(String s) {
        KernelandProcess currentProcess = scheduler.getCurrentlyRunning();
        if (currentProcess == null) {
            return -1;
        }

        int[] openDevices = currentProcess.getOpenDevices();
        for (int i = 0; i < openDevices.length; i++) {
            if (openDevices[i] == -1) {
                int vfsId = vfs.Open(s);
                if (vfsId == -1) return -1; // VFS failed to open the device
                openDevices[i] = vfsId; // Store the VFS ID
                return i; // Return the index as the device ID
            }
        }
        return -1; // No space left for new devices
    }

    @Override
    public void Close(int id) {
        KernelandProcess currentProcess = scheduler.getCurrentlyRunning();
       if (currentProcess != null && id >= 0 && id < currentProcess.getOpenDevices().length) {
           int vfsId = currentProcess.getOpenDevices()[id];
          if (vfsId != -1) {
             vfs.Close(vfsId);
                currentProcess.getOpenDevices()[id] = -1; // Reset the entry after closing
            }
        }
    }

    @Override
    //Read
    public byte[] Read(int id, int size) {
        //gets the currently running process
        KernelandProcess currentProcess = scheduler.getCurrentlyRunning();
     //checkls the ID
        if (currentProcess != null && id >= 0 && id < currentProcess.getOpenDevices().length) {
          int vfsId = currentProcess.getOpenDevices()[id];
            if (vfsId != -1) {
              return vfs.Read(vfsId, size);
            }
        }
        return new byte[0];  //return empoty array
    }
    
    @Override
    public void Seek(int id, int position) {
        KernelandProcess currentProcess = scheduler.getCurrentlyRunning();
        if (currentProcess != null && id >= 0 && id < currentProcess.getOpenDevices().length) {
            int vfsId = currentProcess.getOpenDevices()[id];
          if (vfsId != -1) { //// If the VFS ID is valid, call the seek method
                vfs.Seek(vfsId, position);
          }
      }
    }
    
    @Override
    public int Write(int id, byte[] data) {
        KernelandProcess currentProcess = scheduler.getCurrentlyRunning();
        if (currentProcess != null && id >= 0 && id < currentProcess.getOpenDevices().length) {
            int vfsId = currentProcess.getOpenDevices()[id];
         //write request to the VFS
            if (vfsId != -1) {
              return vfs.Write(vfsId, data);
            }
        }
        return 0;  // Return 0 
    }
}
    