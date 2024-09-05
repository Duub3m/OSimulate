public class HelloWorld extends UserlandProcess {
    @Override

    //Infinite While loop that prints “Hello World” and calls cooperate() inside the loop
    public void main() {
        while (true) {
            System.out.println("Hello World");
            cooperate();
        }
    }
}
