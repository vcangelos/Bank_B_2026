import java.util.Scanner;

public class BankApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        menu app = new menu(scanner);
        app.run();
        scanner.close();
    }
}

