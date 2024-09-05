public class Main {
    //Call OS.Startup() with a  new HelloWorld then CreateProcess() with a new GoodbyeWorld(). 
    public static void main(String[] args) {
        OS.startup(new HelloWorld());

        try {
            Thread.sleep(50); //sleep
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        OS.createProcess(new GoodbyeWorld());

        //Starts the Ping process
        OS.startup(new Ping());

        //Starts the Pong process
        OS.startup(new Pong());
    }
}
