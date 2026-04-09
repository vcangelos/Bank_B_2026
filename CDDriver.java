import java.util.Scanner;
import java.util.List;

public class CDDriver {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Initializing Bank System...");
        
        System.out.println("Loading saved CD records...");
        // Loads previously saved CDs from the CSV file into memory
        // CertificateOfDeposit.loadActiveCDs(); 

        System.out.println("System Ready.\n");

        boolean running = true;
        
        while (running) {
            System.out.println("===================================");
            System.out.println("   CERTIFICATE OF DEPOSIT CENTER   ");
            System.out.println("===================================");
            System.out.print("Enter Customer ID to log in (or type 'exit' to quit): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                running = false;
                System.out.println("Shutting down terminal. Goodbye!");
                break;
            }

            boolean sessionActive = true;
            
            // Customer-specific session loop
            while (sessionActive) {
                System.out.println("\n--- Account: " + input + " ---");
                System.out.println("1. Open a new Certificate of Deposit");
                System.out.println("2. Manage / Withdraw from an existing CD");
                System.out.println("3. Log Out");
                System.out.print("Select an option (1-3): ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character left by nextInt()

                switch (choice) {
                    case 1:
                        // Launches the CD creation flow and checks checking account balances
                        CertificateOfDeposit.welcomeScreen(scanner, input);
                        break;
                    case 2:
                        // Opens the menu to view active CDs, check maturity dates, and process withdrawals
                        CertificateOfDeposit.manageCD(scanner, input);
                        break;
                    case 3:
                        sessionActive = false;
                        System.out.println("Logging out...");
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1, 2, or 3.");
                }
            }
        }
        
        scanner.close();
    }
}