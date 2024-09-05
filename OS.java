import java.util.ArrayList;
import java.util.List;

public class OS {
    private static final Kernel kernel = new Kernel();
    private static CallType currentCall;
    private static final List<Object> parameters = new ArrayList<>();
    private static Object returnValue;
    private static final int pageSize = 1024;

    public static int allocateMemory(int size) {
        if (size % pageSize != 0) {
            System.out.println("Has to be a multiple 1024.");
            return -1; // Failed
        }
        // Request to KernelandProcess
        KernelandProcess currentProcess = kernel.getScheduler().getCurrentlyRunning();
        return currentProcess != null ? currentProcess.allocateMemory(size) : -1;
    }

    public static boolean freeMemory(int pointer, int size) {
        if (pointer % pageSize != 0 || size % pageSize != 0) {
            System.out.println("The Pointer and  the size has to be a multiple of 1024.");
            return false; 
        }
        // Request to KernelandProcess
        KernelandProcess currentProcess = kernel.getScheduler().getCurrentlyRunning();
        return currentProcess != null && currentProcess.freeMemory(pointer, size);
    }


    // getMapping
    public static void getMapping(int virtualPageNumber) {
        // get the physical page mapping with Kernel
        KernelandProcess currentProcess = kernel.getCurrentProcess();
        if (currentProcess != null) {
            int physicalPageNumber = currentProcess.getPageMapping(virtualPageNumber);
            UserlandProcess.loadTLB(virtualPageNumber, physicalPageNumber);
        }
    }

    //
    //
    //

    //GetPid
    public static int GetPid() {
        KernelandProcess currentProcess = kernel.getScheduler().getCurrentlyRunning();
    return currentProcess != null ? currentProcess.getPid() : -1;
    }

    //GetPidByName
    public static int GetPidByName(String name) {
        return kernel.getScheduler().getPidByName(name); 
    }
//SendMessage
public static void SendMessage(KernelMessage km) {
    kernel.sendMessage(km);
}

//WaitForMessage
public static KernelMessage WaitForMessage() {
    return kernel.waitForMessage();
}

    
    //An enum of what function to call
    public enum CallType {
        CREATEPROCESS,
        SWITCHPROCESS,
        SLEEP
    }

    public enum Priority {
        REALTIME,
        INTERACTIVE,
        BACKGROUND
    }

    public static int createProcess(UserlandProcess up) {
        //method with priority (Interactive)
        return createProcess(up, Priority.INTERACTIVE);
    }

    public static int createProcess(UserlandProcess up, Priority priority) {
        resetParameters();
        parameters.add(up);
        parameters.add(priority); // Adds the priority to the parameters
        currentCall = CallType.CREATEPROCESS;
        kernel.start();
        return (Integer) returnValue;
    }

    //Sleep
    public static void Sleep(int milliseconds) {
        resetParameters(); // resets the parameters
        parameters.add(milliseconds); // Adds a sleep duration
        currentCall = CallType.SLEEP; // Sets the call type to SLEEP
        kernel.start(); // Switch to kernel 
    }

    // Getter for currentCall
    public static CallType getCurrentCall() {
        return currentCall;
    }
    
    //switchProcess
    public static void switchProcess() {
        currentCall = CallType.SWITCHPROCESS;
        kernel.start();
    }

    //Resets the parameters. 
    public static void resetParameters() {
        parameters.clear();
        returnValue = null;
    }

    //Creates the Kernel() and calls CreateProcess twice – once for “init” and once for the idle process. 
    public static void startup(UserlandProcess init) {
        createProcess(init); // initial process
        createProcess(new IdleProcess()); // startes the idle process
    }

    //Methods to call from Kernel class through currentCall
    protected static void handleSystemCall() {
        switch (currentCall) {
            case CREATEPROCESS:
                returnValue = kernel.getScheduler().createProcess((UserlandProcess) parameters.get(0));
                break;
            case SWITCHPROCESS:
                kernel.getScheduler().switchProcess();
                break;
            default:
                throw new IllegalStateException("Error: " + currentCall);
        }
    }
}
