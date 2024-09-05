OSimulate is a project that simulates the core functionalities of an operating system. Built as part of an operating systems course, this project implements key components including process scheduling, virtual memory management, and device I/O management. It provides a practical framework to understand the internal workings of operating systems and how they handle concurrent processes, memory allocation, and resource management.

The system includes the following components:

___________
Process Management: Handles process creation, scheduling, and termination. The Scheduler.java manages different process states, while PCB.java represents the Process Control Block for each process.

Memory Management: Implements virtual memory and mapping using VirtualToPhysicalMapping.java and SwapManager.java, ensuring efficient memory allocation.

Device Management: Manages simulated hardware devices using device.java, RandomDevice.java, and devicemapping.java.

File System: A basic file system is implemented through VFS.java and fakefilesystem.java, simulating storage management in the operating system.

Kernel Interaction: The kernel (Kernel.java and KernelandProcess.java) handles system-level operations and communicates with user processes (UserlandProcess.java).

Interprocess Communication: Simulated processes such as Ping.java and Pong.java demonstrate process communication, while KernelMessage.java simulates message passing between processes.
___________

The project also includes various test cases (UnitTest.java) to validate the correctness of process scheduling, memory allocation, and file system management. By simulating these fundamental features, OSimulate provides insight into the intricate operations that take place in modern operating systems.
