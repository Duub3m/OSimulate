public class Pong extends UserlandProcess {
    @Override
    public void main() {
        int pingPid = OS.GetPidByName("Ping");
        System.out.println("I am PONG, ping = " + pingPid);

        while (true) {
            KernelMessage message = OS.WaitForMessage();
            System.out.println("PONG: " + message);

            // Response that gets sent back to Ping
            OS.SendMessage(new KernelMessage(OS.GetPid(), pingPid, message.getMessageType() + 1, 
            new byte[]{(byte) (message.getMessageType() + 1)}));
        }
    }
}
