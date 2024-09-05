import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UnitTest {
    private Scheduler scheduler;
    private KernelandProcess kernelProcess;

    @Before
    public void beforeReadWrite() {
        // starts the scheduler and kernel process
        scheduler = new Scheduler();
        kernelProcess = new KernelandProcess(); 
    }
    @Test
    public void testReadWrite() {
        // Allocation of memory
        int size = 1024; // 1 page
        int address = kernelProcess.allocateMemory(size);
        Assert.assertNotEquals("Memory allocation failed", -1, address);

        // writing and reading from the allocated memory
        byte testValue = 123;
        kernelProcess.freeMemory(address, testValue);
        byte readValue = kernelProcess.readMemory(address);

        Assert.assertEquals("The value Read isn't the same as the Written value", testValue, readValue);
    }

    @Test
    // Test using more than the allocated memory 
    public void testOverAllocation() {
        for (int i = 0; i < 20; i++) { 
            int address = kernelProcess.allocateMemory(100 * 1024); // process allocates 100 KB
            if (i < 10) { // 10  allocations
                Assert.assertNotEquals("Allocation Passed", -1, address);
            } else {
                Assert.assertEquals("Allocation failed", 0, address);
            }
        }
    }

//Test Read and Write in Memory
    @Test
    public void testReadWriteMemory() {
    KernelandProcess process = new KernelandProcess();
    int allocateAddress = process.allocateMemory(KernelandProcess.pageSize);
    assertFalse("Failed", allocateAddress == -1);

    byte writtenValue = 123;
    process.write(allocateAddress, writtenValue);
    byte readValue = process.read(allocateAddress);

    assertEquals("Error with written value", writtenValue, readValue);
}

//Test the extended Memory
@Test
public void testExtendMemory() {
    KernelandProcess process = new KernelandProcess();
    int firstAllocation = process.allocateMemory(KernelandProcess.pageSize);
    int secondAllocation = process.allocateMemory(KernelandProcess.pageSize * 2);

    // Test if allocations succeeded and are not contiguous 
    assertTrue("First allocation is successful", firstAllocation != -1);
    assertTrue("Second allocation is successful", secondAllocation != -1);
    assertTrue("Allocations should isn't contiguous", secondAllocation - firstAllocation >= KernelandProcess.pageSize);
}

@Test
public void testUnauthorizedMemoryAccess() {
    KernelandProcess process = new KernelandProcess();
    process.allocateMemory(KernelandProcess.pageSize); 

    // reading the memory past the allocated range should cause an error in tprocess
    process.readMemory(KernelandProcess.pageSize * 1024); 

    // termination error
    fail("Terminated");
}


//
//
//

    @Test
    public void testSystemProcesses() {

        // Starts the Ping process
        OS.startup(new Ping());

        // Start the Pong process
        OS.startup(new Pong());

        // Waits for the system and the processes to interact
        try {
            Thread.sleep(5000); // lets the system run
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        //Checks if the output contains both Ping and Pong to see if the process works properly
        assertTrue(response.contains("Ping:"));
        assertTrue(response.contains("Pong:"));
    }


    @Test
    public void testDifferentDevices() throws InterruptedException {
        Scheduler scheduler = new Scheduler();
        Kernel kernel = new Kernel(); 

        // Creating multiple processes and connecting them to different devices
        UserlandProcess process1 = new UserlandProcess();
        UserlandProcess process2 = new UserlandProcess();

        int device1Id = kernel.Open("file data1.dat"); // Open a file device
        int device2Id = kernel.Open("random 100");     // Open a random device

        scheduler.createProcess(process1, OS.Priority.INTERACTIVE);
        scheduler.createProcess(process2, OS.Priority.INTERACTIVE);

        scheduler.startScheduler();
        Thread.sleep(500);

        Assert.assertNotEquals(device1Id, -1);
        Assert.assertNotEquals(device2Id, -1);
    }

    @Test
    public void testSameDevice() throws InterruptedException {
        Scheduler scheduler = new Scheduler();
        Kernel kernel = new Kernel(); 

        // Creating multiple processes and connecting them to the same device
        UserlandProcess process1 = new UserlandProcess();
        UserlandProcess process2 = new UserlandProcess();

        int device1Id = kernel.Open("file shared.dat"); // Both processes connect to the same file
        int device2Id = kernel.Open("file shared.dat");

        scheduler.createProcess(process1, OS.Priority.INTERACTIVE);
        scheduler.createProcess(process2, OS.Priority.BACKGROUND);

        scheduler.startScheduler();
        Thread.sleep(500);

        Assert.assertEquals(device1Id, device2Id);
    }
    
    @Test
    public void testSleep() throws InterruptedException {
    Scheduler scheduler = new Scheduler();
    UserlandProcess process = new UserlandProcess();

    scheduler.createProcess(process, OS.Priority.INTERACTIVE);
    scheduler.Sleep(1000, process); // Sleeps for 1 second

    // Waits for sleep to be finished
    Thread.sleep(1100);
}

@Test
    public void RealTimeProcesses() throws InterruptedException {
        // Schedules the processes with the priorities
        scheduler.createProcess(OS.Priority.REALTIME);
        scheduler.createProcess(OS.Priority.INTERACTIVE);
        scheduler.createProcess(OS.Priority.BACKGROUND);

        //start the scheduler
        scheduler.startScheduler();

        // Sleeps so the scheduler can to run the processes
        Thread.sleep(500); 

        assertTrue(true, "Test");
    }
}
