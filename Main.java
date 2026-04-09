import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BankApp app = new BankApp(scanner);
        app.run();
        scanner.close();
    }
}