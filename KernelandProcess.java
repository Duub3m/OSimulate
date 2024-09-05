import java.util.Arrays;
import java.util.Random;

public class KernelandProcess {
    private int[] openDevices = new int[100]; // For device management
    private VirtualToPhysicalMapping[] pageTable = new VirtualToPhysicalMapping[100]; // Enhanced page table
    private static final int memorySize = 1024 * 1024; // 1MB of memory
    private static final int pageSize = 1024; // Page size of 1KB
    private boolean[] allocatedPages = new boolean[memorySize / pageSize]; // Track pages
    private Random random = new Random(); // For random page allocation
    
    public KernelandProcess() {
        // Device IDs and page table entries to show theres no mapping
        Arrays.fill(openDevices, -1);
        Arrays.fill(allocatedPages, false); // no pages 

        // page table with mapping objects
        for (int i = 0; i < pageTable.length; i++) {
            pageTable[i] = new VirtualToPhysicalMapping(); // Create a mapping object for each entry
        }
    }

    // Method to close all open devices
    public void closeAllDevices(VFS vfs) {
        for (int i = 0; i < openDevices.length; i++) {
            if (openDevices[i] != -1) {
                vfs.Close(openDevices[i]);
                openDevices[i] = -1; // Mark the device as closed
            }
        }
    }

    // GetMapping updates a random TLB entry, handles virtual to physical mapping
    public void getMapping(int virtualPage) {
        VirtualToPhysicalMapping mapping = pageTable[virtualPage];
        if (mapping.physicalPageNumber == -1) {
        int physicalPage = findFreePhysicalPage();
           if (physicalPage == -1) {
          physicalPage = scheduler.handlePageSwap();  
                if (physicalPage == -1) {
                    // Runtime exception in case no physical pages could be freed
               throw new RuntimeException("No physical memory, can't swap!");
                }
            }
            mapping.physicalPageNumber = physicalPage;
            if (mapping.diskPageNumber != -1) {
                byte[] data = swapManager.readFromSwap(mapping.diskPageNumber);
                loadPhysicalPage(physicalPage, data);
            } else {
                byte[] emptyData = new byte[1024]; 
         Arrays.fill(emptyData, (byte) 0);
                loadPhysicalPage(physicalPage, emptyData);
            }
        }
    }

    private void loadPhysicalPage(int physicalPage, byte[] data) {
        // Writes data to the physical memory
        System.out.println("Loading the information to the physical page: " + physicalPage);
    }
    
    public boolean hasPhysicalPages() {
        for (VirtualToPhysicalMapping mapping : pageTable) {
            if (mapping != null && mapping.physicalPageNumber != -1) {
                return true;
            }
        }
        return false;
    }
    

    // Allocate memory to handle paging
    public int allocateMemory(int size) {
        int pagesNeeded = (size + pageSize - 1) / pageSize;
        int startVirtualPage = -1;
    
        // Finds the contiguous virtual pages that havn't been allocated
        for (int i = 0; i <= pageTable.length - pagesNeeded; i++) {
            boolean contiguousSpaceFound = true;
            for (int j = 0; j < pagesNeeded; j++) {
                if (pageTable[i + j] != null && pageTable[i + j].physicalPageNumber != -1) {
                    // Check if the page is already allocated physically
                    contiguousSpaceFound = false;
                    break;
                }
            }
            if (contiguousSpaceFound) {
                startVirtualPage = i;
                break;
            }
        }
    
        if (startVirtualPage == -1) return -1; // Allocation failed 
    
        // We do VirtualToPhysicalMapping for each page
        for (int i = 0; i < pagesNeeded; i++) {
            if (pageTable[startVirtualPage + i] == null) {
                pageTable[startVirtualPage + i] = new VirtualToPhysicalMapping(); // virtual mapping
            }
        }
    
        return startVirtualPage * pageSize; // Returns the virtual address at the start
    }
    
    // Frees the memory by resetting mappings as well as freeing the physical pages
    public boolean freeMemory(int virtualAddress, int size) {
        int startVirtualPage = virtualAddress / pageSize;
        int pagesToFree = (size + pageSize - 1) / pageSize;

        for (int i = 0; i < pagesToFree; i++) {
            int virtualPage = startVirtualPage + i;
            if (virtualPage >= pageTable.length || pageTable[virtualPage].physicalPageNumber == -1) {
                return false; 
            }

            int physicalPage = pageTable[virtualPage].physicalPageNumber;
            allocatedPages[physicalPage] = false; 
            pageTable[virtualPage] = new VirtualToPhysicalMapping(); // Removes mapping
        }
        return true; 
    }
}

