import java.io.*;
import java.util.*;

public class MortgageLoan {

    // ===== ENUMS =====
    enum LoanType {
        FIXED,
        ARM
    }

    enum LoanTerm {
        FIXED_30,
        FIXED_20,
        FIXED_15,
        ARM_5_1,
        ARM_5_6,
        ARM_7_1,
        ARM_7_6,
        ARM_10_1,
        ARM_10_6
    }

    // ===== INTEREST RATE STORAGE (CSV) =====
    static Map<LoanTerm, Double> rates = new HashMap<>();

    public static void loadRatesFromCSV(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;

            br.readLine(); 
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                LoanTerm term = LoanTerm.valueOf(parts[0]);
                double rate = Double.parseDouble(parts[1]);

                rates.put(term, rate);
            }

        } catch (Exception e) {
            System.out.println("Error loading rates: " + e.getMessage());
        }
    }

    public static double getRate(LoanTerm term) {
        return rates.getOrDefault(term, 6.5);
    }

    // ===== CREDIT + PRE-APPROVAL =====
    public static boolean isPreApproved(int creditScore, boolean goodHistory, double income, double debt) {
        double dti = debt / income;
        return creditScore >= 620 && goodHistory && dti < 0.43;
    }

    // ===== MONTHLY PAYMENT CALC =====
    public static double calculateMonthlyPayment(double loanAmount, double rate, int years) {
        double monthlyRate = rate / 100 / 12;
        int payments = years * 12;

        return (loanAmount * monthlyRate * Math.pow(1 + monthlyRate, payments)) /
               (Math.pow(1 + monthlyRate, payments) - 1);
    }

    // ===== GET YEARS FROM TERM =====
    public static int getYears(LoanTerm term) {
        switch (term) {
            case FIXED_30: return 30;
            case FIXED_20: return 20;
            case FIXED_15: return 15;
            default: return 30; // ARM default
        }
    }

    // ===== AUTOPAY SYSTEM =====
    public static void processAutoPay(boolean enabled, double amount) {
        if (enabled) {
            System.out.println("Auto-paying: $" + String.format("%.2f", amount));
        } else {
            System.out.println("Manual payment required.");
        }
    }

    // ===== MAIN METHOD =====
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Load interest rates
        loadRatesFromCSV("rates.csv");

        // ===== USER INPUT =====
        System.out.print("Enter loan amount: ");
        double loanAmount = scanner.nextDouble();

        System.out.print("Enter down payment: ");
        double downPayment = scanner.nextDouble();

        System.out.print("Enter credit score: ");
        int creditScore = scanner.nextInt();

        System.out.print("Good credit history? (true/false): ");
        boolean history = scanner.nextBoolean();

        System.out.print("Monthly income: ");
        double income = scanner.nextDouble();

        System.out.print("Monthly debt: ");
        double debt = scanner.nextDouble();

        // ===== PRE-APPROVAL CHECK =====
        if (!isPreApproved(creditScore, history, income, debt)) {
            System.out.println("You are NOT pre-approved.");
            return;
        }
        System.out.println("You are pre-approved!");
        System.out.println("\nSelect Loan Type:");
        System.out.println("1 - Fixed");
        System.out.println("2 - ARM");
        
        int typeChoice = scanner.nextInt();

        LoanType type;
        if (typeChoice == 1) {
            type = LoanType.FIXED;
        } else if (typeChoice == 2) {
            type = LoanType.ARM;
        } else {
         System.out.println("Invalid choice.");
            return;        
        }
        // ===== SELECT LOAN =====
   System.out.println("\nSelect Loan Term:");
   LoanTerm[] terms = LoanTerm.values();

   for (int i = 0; i < terms.length; i++) {
    System.out.println((i + 1) + " - " + terms[i]);
}

int termChoice = scanner.nextInt();

if (termChoice < 1 || termChoice > terms.length) {
    System.out.println("Invalid choice.");
    return;
}

LoanTerm term = terms[termChoice - 1];
        

        double rate = getRate(term);

        double finalLoan = loanAmount - downPayment;

        int years = getYears(term);

        double monthlyPayment = calculateMonthlyPayment(finalLoan, rate, years);

        System.out.println("Loan Type: " + type);
        System.out.println("Loan Term: " + term);
        System.out.println("Interest Rate: " + rate + "%");
        System.out.println("Monthly Payment: $" + String.format("%.2f", monthlyPayment));

        // ===== AUTOPAY =====
        System.out.print("Enable autopay? (true/false): ");
        boolean autopay = scanner.nextBoolean();

     System.out.print("Enter starting savings balance: ");
double balance = scanner.nextDouble();

SavingsAccount account = new SavingsAccount("user123", balance);

    if (autopay) {
    boolean success = account.withdrawSavings(monthlyPayment);

    if (success) {
        System.out.println("Payment successful.");
        System.out.println("Remaining balance: $" + account.getSavings());
    } else {
        System.out.println("Payment failed (insufficient funds).");
    }
} else {
    System.out.println("Manual payment required.");
}

        scanner.close();
    }
}
