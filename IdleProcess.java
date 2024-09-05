public class IdleProcess extends UserlandProcess {
    @Override
//Runs an infinite loop of cooperate() and Thread.sleep(50).
    public void main() {
        while (true) {
            cooperate();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
