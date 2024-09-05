public class Ping extends UserlandProcess {
    @Override
    public void main() {
        int pongPid = OS.GetPidByName("Pong");
        System.out.println("I am PING, pong = " + pongPid);

        for (int i = 0; i < 5; i++) {
            // Sends a message to Pong
            OS.SendMessage(new KernelMessage(OS.GetPid(), pongPid, i, new byte[]{(byte) i}));
            KernelMessage response = OS.WaitForMessage();
            System.out.println("PING: " + response);
        }
    }
}
