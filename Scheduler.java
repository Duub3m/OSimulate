import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//Added priority (REALTIME,INTERACTIVE,BACKGROUND)
public class Scheduler {
    private final List<UserlandProcess> realTimeProcesses = new LinkedList<>();
    private final List<UserlandProcess> interactiveProcesses = new LinkedList<>();
    private final List<UserlandProcess> backgroundProcesses = new LinkedList<>();
    private final List<UserlandProcess> processes = new LinkedList<>();
    private final List<SleepingProcess> sleepingProcesses = new LinkedList<>();
    private final Timer timer = new Timer();
    private UserlandProcess currentlyRunning = null;
    private final Clock clock = Clock.systemDefaultZone();

    public KernelandProcess getRandomProcess() {
        List<KernelandProcess> eligibleProcesses = new ArrayList<>();
        for (UserlandProcess process : processes) {
            KernelandProcess kProcess = (KernelandProcess) process;
            if (kProcess.hasPhysicalPages()) { 
                eligibleProcesses.add(kProcess);
            }
        }

        if (eligibleProcesses.isEmpty()) {
            return null; // No process 
        }

        return eligibleProcesses.get(random.nextInt(eligibleProcesses.size()));
    }


    //getPid MEthod
     // Method for getting the PID for the currently running process
     public int getPid() {
        return currentlyRunning != null ? currentlyRunning.getPid() : -1;
    }

    // Method for getPidByName
    public int getPidByName(String name) {
 UserlandProcess process = processMap.get(name);
      return process != null ? process.getPid() : -1;
    }
    
    public Scheduler() {
        // Schedules the TimerTask
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            wakeUpSleepingProcesses(); // wakes up any processes ready to run
              if (currentlyRunning != null) {
                   currentlyRunning.requestStop(); //quantum stopped
                switchProcess(); // Switches to the next process
                }
            }
        }, 0, 250); // Starts after 0 seconds then repeats every 250ms
    }

    //add the userland process it to the list of processes and, 
    //if nothing else is running, call switchProcess() to get it started.  
    //Apriority handling
    //priority queues
    public int createProcess(UserlandProcess up, OS.Priority priority) {
        switch (priority) {
            case REALTIME:
                realTimeProcesses.add(up);
                break;
            case INTERACTIVE:
                interactiveProcesses.add(up);
                break;
            case BACKGROUND:
                backgroundProcesses.add(up);
                break;
        }
        if (currentlyRunning == null) {
            switchProcess(); // Starts the scheduling theres no process thats running
        }
        //return 
        return -1; 
    }

    
    public int createProcess(UserlandProcess up) {
        return createProcess(up, OS.Priority.INTERACTIVE); // priority-INTERACTIVE
    }

    // take the currently running process and put it at the end of the list. 
    //It then takes the head of the list and runs it.
    public void switchProcess() {
        if (currentlyRunning != null && !currentlyRunning.isDone()) {
            // Moves the process to the end of the list if its not done
            processes.remove(currentlyRunning);
            processes.add(currentlyRunning);
            
        }

        if (!processes.isEmpty()) {
            // Switches to the next process on the list
            currentlyRunning = processes.remove(0);
            currentlyRunning.start();
        }
    }

    // Sleep
    public void Sleep(int milliseconds, UserlandProcess process) {
        Instant wakeUpTime = clock.instant().plusMillis(milliseconds);
        sleepingProcesses.add(new SleepingProcess(process, wakeUpTime));
        switchProcess(); //switches the process
    }

    // Wakes up sleeping process using a method with  Clock
    private void wakeUpSleepingProcesses() {
        Instant now = clock.instant();
        Iterator<SleepingProcess> iterator = sleepingProcesses.iterator();
        while (iterator.hasNext()) {
            SleepingProcess sleepingProcess = iterator.next();
            if (now.isAfter(sleepingProcess.wakeUpTime) || now.equals(sleepingProcess.wakeUpTime)) {
                processes.add(sleepingProcess.process); // Ready to run again
                iterator.remove();
            }
        }
    }

    // Helper for the sleeping processes
    private static class SleepingProcess {
        UserlandProcess process;
        Instant wakeUpTime;

        SleepingProcess(UserlandProcess process, Instant wakeUpTime) {
            this.process = process;
            this.wakeUpTime = wakeUpTime;
        }
    }

    
}
