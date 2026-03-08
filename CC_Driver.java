import java.util.Scanner;

public class CC_Driver {

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } else {
                System.out.println("Invalid input. Enter a whole number.");
                scanner.nextLine();
            }
        }
    }

    public static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            } else {
                System.out.println("Invalid input. Enter a number.");
                scanner.nextLine();
            }
        }
    }

    public static String readText(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CreditCard card = null;
        String fileName = "creditcards.csv";
        int choice;

        do {
            System.out.println("\nVisa Credit Card System");
            System.out.println("1 Open Credit Card");
            System.out.println("2 View Card Details");
            System.out.println("3 Add Charge");
            System.out.println("4 Add Charge With Category");
            System.out.println("5 Make Payment");
            System.out.println("6 View Minimum Payment");
            System.out.println("7 End Billing Cycle");
            System.out.println("8 Freeze Card");
            System.out.println("9 Unfreeze Card");
            System.out.println("10 View Transactions");
            System.out.println("11 Close Card");
            System.out.println("12 Cash Advance");
            System.out.println("13 Redeem Rewards");
            System.out.println("14 Request Credit Limit Increase");
            System.out.println("15 Add Charge With Merchant");
            System.out.println("16 Replace Card");
            System.out.println("17 View Monthly Statement");
            System.out.println("18 Exit");

            choice = readInt(scanner, "Choose option: ");

            switch (choice) {

                case 1:
                    if (card == null || !card.isOpen()) {
                        card = new CreditCard();
                        card.saveOrUpdateCSV(fileName);
                        System.out.println("Visa card created.");
                    } else {
                        System.out.println("Card already open.");
                    }
                    break;

                case 2:
                    if (card != null) {
                        System.out.println("\nCard Type: " + card.getCreditCardType());
                        System.out.println("Card Number: " + card.getFormattedCardNumber());
                        System.out.println("CVV: " + card.getCVV());
                        System.out.println("Expiration: " + card.getExpirationDate());
                        System.out.printf("Balance: $%.2f%n", card.getBalance());
                        System.out.printf("Credit Limit: $%.2f%n", card.getCreditLimit());
                        System.out.printf("Monthly Spent: $%.2f%n", card.getMonthlySpent());
                        System.out.printf("Available Credit: $%.2f%n", card.getAvailableCredit());
                        System.out.println("Credit Score: " + card.getCreditScore());
                        System.out.println("Reward Points: " + card.getRewardPoints());
                        System.out.println("Missed Payments: " + card.getMissedPayments());
                        System.out.println("Card Status: " + card.getCardStatus());
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 3:
                    if (card != null) {
                        double charge = readDouble(scanner, "Enter charge amount: ");
                        card.addCharge(charge);
                        card.saveOrUpdateCSV(fileName);
                        System.out.printf("New Balance $%.2f%n", card.getBalance());
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 4:
                    if (card != null) {
                        double amount = readDouble(scanner, "Charge amount: ");
                        String category = readText(scanner, "Category: ");
                        card.addChargeWithCategory(amount, category);
                        card.saveOrUpdateCSV(fileName);
                        System.out.printf("New Balance $%.2f%n", card.getBalance());
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 5:
                    if (card != null) {
                        double payment = readDouble(scanner, "Payment amount: ");
                        card.makePayment(payment);
                        card.saveOrUpdateCSV(fileName);
                        System.out.printf("Balance $%.2f%n", card.getBalance());
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 6:
                    if (card != null) {
                        System.out.printf("Minimum payment $%.2f%n", card.calculateMinimumPayment());
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 7:
                    if (card != null) {
                        card.endBillingCycle();
                        card.saveOrUpdateCSV(fileName);
                        System.out.printf("Balance after billing cycle $%.2f%n", card.getBalance());
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 8:
                    if (card != null) {
                        card.freezeCard();
                        card.saveOrUpdateCSV(fileName);
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 9:
                    if (card != null) {
                        card.unfreezeCard();
                        card.saveOrUpdateCSV(fileName);
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 10:
                    if (card != null) {
                        card.printTransactionHistory();
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 11:
                    if (card != null && card.closeCard()) {
                        card.saveOrUpdateCSV(fileName);
                        System.out.println("Card closed.");
                    } else {
                        System.out.println("Balance must be zero.");
                    }
                    break;

                case 12:
                    if (card != null) {
                        double advance = readDouble(scanner, "Cash advance amount: ");
                        card.cashAdvance(advance);
                        card.saveOrUpdateCSV(fileName);
                        System.out.printf("Balance $%.2f%n", card.getBalance());
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 13:
                    if (card != null) {
                        int pts = readInt(scanner, "Points to redeem: ");
                        card.redeemRewards(pts);
                        card.saveOrUpdateCSV(fileName);
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 14:
                    if (card != null) {
                        double inc = readDouble(scanner, "Increase amount: ");
                        card.requestCreditLimitIncrease(inc);
                        card.saveOrUpdateCSV(fileName);
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 15:
                    if (card != null) {
                        double amount = readDouble(scanner, "Charge amount: ");
                        String merchant = readText(scanner, "Merchant: ");
                        card.addChargeWithMerchant(amount, merchant);
                        card.saveOrUpdateCSV(fileName);
                        System.out.printf("New Balance $%.2f%n", card.getBalance());
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 16:
                    if (card != null) {
                        card.replaceCard();
                        card.saveOrUpdateCSV(fileName);
                        System.out.println("New Card Number: " + card.getFormattedCardNumber());
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 17:
                    if (card != null) {
                        card.printMonthlyStatement();
                    } else {
                        System.out.println("No card created.");
                    }
                    break;

                case 18:
                    System.out.println("Exiting program.");
                    break;

                default:
                    System.out.println("Invalid option.");
            }

        } while (choice != 18);

        scanner.close();
    }
}