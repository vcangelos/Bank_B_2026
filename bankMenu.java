import java.util.Scanner;


public class bankMenu {
    static Scanner keyboard = new Scanner(System.in);

    public static void main(String[] args) {
        while(true) {

            System.out.println("Options: \n 1)Login\n2)Register");
            {
                int option = keyboard.nextInt();
                switch (option) {
                    case 1:
                        login();
                        break;
                    case 2:
                        register();
                        break;
                    default:
                        System.out.println("Invalid option");
                }
            }
        }
    }

    private static void register() {

    }

    private static void login() {
        System.out.println("Enter Username:");
        String username = keyboard.next();
        System.out.println("Enter Password:");
        String username = keyboard.next();
    }

}
