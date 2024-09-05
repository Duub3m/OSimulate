public class GoodbyeWorld extends UserlandProcess {
    @Override
    //Infinite While loop that prints “Goodbye World” and calls cooperate() inside the loop
    public void main() {
        while (true) {
            System.out.println("Goodbye World");
            cooperate();
        }
    }
}
