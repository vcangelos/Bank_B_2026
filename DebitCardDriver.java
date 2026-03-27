import java.util.Scanner;
import java.util.List;

public class DebitCardDriver {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // SYSTEM INITIALIZATION
        System.out.println("Initializing Bank System...");
        
        
        System.out.println("Ready.\n");

        boolean running = true;
        
        // MAIN LOGIN LOOP
        while (running) {
            System.out.println("===================================");
            System.out.println("    DEBIT CARD SERVICES TERMINAL   ");
            System.out.println("===================================");
            System.out.println("1. Issue New Debit Card (Bank Teller)");
            System.out.println("2. Access ATM Menu (Customer Login)");
            System.out.println("3. Exit System");
            System.out.print("Select an option (1-3): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline character

            if (choice == 1) {
                // FLOW: ISSUING A NEW CARD
                System.out.print("\nEnter Customer ID to issue card: ");
                String custId = scanner.nextLine().trim();
                
                // Calls the massive static method you built
                DebitCard newCard = DebitCard.issueCard(scanner, custId);
                
                if (newCard != null) {
                    System.out.println("\nTaking you to the ATM Menu to test your new card...");
                    atmMenu(scanner, newCard);
                }
                
            } else if (choice == 2) {
                // FLOW: LOGGING IN TO AN EXISTING CARD
                System.out.print("\nEnter Customer ID: ");
                String custId = scanner.nextLine().trim();

                // 1. Check the CSV to see if they actually have a PIN saved
                String savedPin = DebitCard.getPINFromCSV(custId);
                
                if (savedPin == null) {
                    System.out.println("No debit card found for this user. Please see a teller to issue one.");
                    continue; // Send them back to the main menu
                }

                // 2. Prompt for PIN
                System.out.print("Enter your 4-digit PIN: ");
                String enteredPin = scanner.nextLine().trim();

                // 3. Verify PIN
                if (savedPin.equals(enteredPin)) {
                    System.out.println("PIN Verified! Accessing account...");

                    /* * RECONSTRUCT THE CARD OBJECT:
                     * because the CSV only saves the PIN and Balance, we have to 
                     * build a temporary session object to use your methods.
                     * We use a masked card number for security since it wasn't saved in the CSV.
                     */
                    String accountId = "ACC-" + custId; // Placeholder until linked with checking
                    DebitCard sessionCard = new DebitCard("XXXX-XXXX-XXXX-XXXX", "Visa", savedPin, custId, accountId);

                    atmMenu(scanner, sessionCard);
                } else {
                    System.out.println("Incorrect PIN. Access Denied.");
                }
                
            } else if (choice == 3) {
                running = false;
                System.out.println("Shutting down terminal. Goodbye!");
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
        
        scanner.close();
    }

    // THE ATM TERMINAL MENU
    private static void atmMenu(Scanner scanner, DebitCard card) {
        boolean sessionActive = true;
        
        while (sessionActive) {
            System.out.println("\n--- ATM MENU (User ID: " + card.getLinkedCustomerId() + ") ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Withdraw Cash (In-Network)");
            System.out.println("3. Withdraw Cash (Out-of-Network ATM)");
            System.out.println("4. Deposit Cash");
            System.out.println("5. Make Foreign Transaction");
            System.out.println("6. Display Fee Schedule & Details");
            System.out.println("7. Replace Lost/Stolen Card");
            System.out.println("8. Logout / Remove Card");
            System.out.print("Select an action: ");

            int action = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (action) {
                case 1:
                    card.checkBalance();
                    break;
                case 2:
                    System.out.print("Enter withdrawal amount: $");
                    double wAmt = scanner.nextDouble();
                    card.withdraw(wAmt);
                    break;
                case 3:
                    System.out.print("Enter ATM withdrawal amount: $");
                    double atmAmt = scanner.nextDouble();
                    // isOwnBankATM is false, so it applies the $5 fee
                    card.withdrawFromATM(atmAmt, false); 
                    break;
                case 4:
                    System.out.print("Enter deposit amount: $");
                    double dAmt = scanner.nextDouble();
                    card.deposit(dAmt);
                    break;
                case 5:
                    System.out.print("Enter foreign transaction amount: $");
                    double fAmt = scanner.nextDouble();
                    scanner.nextLine(); // consume newline
                    System.out.print("Enter currency (e.g., EUR, GBP, JPY): ");
                    String currency = scanner.nextLine();
                    card.foreignTransaction(fAmt, currency);
                    break;
                case 6:
                    card.displayFeeSchedule();
                    break;
                case 7:
                    System.out.print("Enter reason for replacement (Lost/Stolen/Damaged): ");
                    String reason = scanner.nextLine();
                    DebitCard newCard = card.replaceCard(scanner, reason);
                    if (newCard != null) {
                        card = newCard; // Swap the session over to the brand new card
                    }
                    break;
                case 8:
                    sessionActive = false;
                    System.out.println("Please take your card. Logging out...");
                    break;
                default:
                    System.out.println("Invalid action. Please select 1-8.");
            }
        }
    }
}