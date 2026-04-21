import java.io.*;
import java.util.*;

public class MortgageLoan {

    // ===== ENUMS =====
    enum LoanType { FIXED, ARM }

    enum LoanTerm {
        FIXED_30, FIXED_20, FIXED_15,
        ARM_5_1, ARM_5_6, ARM_7_1, ARM_7_6, ARM_10_1, ARM_10_6
    }

    // ===== INTEREST RATE STORAGE =====
    static Map<LoanTerm, Double> rates = new HashMap<>();

    public static void loadRatesFromCSV(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                LoanTerm term  = LoanTerm.valueOf(parts[0].trim());
                double   rate  = Double.parseDouble(parts[1].trim());
                rates.put(term, rate);
            }
        } catch (Exception e) {
            // rates.csv may not exist; defaults will be used
        }
    }

    public static double getRate(LoanTerm term) {
        return rates.getOrDefault(term, 6.5);
    }

    // ===== PRE-APPROVAL =====
    public static boolean isPreApproved(int creditScore, double income, double debt) {
        double dti = debt / income;
        return creditScore >= 620 && dti < 0.43;
    }

    // ===== CALCULATIONS =====
    public static double calculateMonthlyPayment(double loanAmount, double rate, int years) {
        double monthlyRate = rate / 100.0 / 12.0;
        int    payments    = years * 12;
        return (loanAmount * monthlyRate * Math.pow(1 + monthlyRate, payments)) /
                (Math.pow(1 + monthlyRate, payments) - 1);
    }

    public static int getYears(LoanTerm term) {
        switch (term) {
            case FIXED_30: return 30;
            case FIXED_20: return 20;
            case FIXED_15: return 15;
            default:       return 30;
        }
    }

    // ===== LAUNCH (called by LoansModule) =====
    public static void launch(Scanner sc, User appUser,
                              List<CheckingAccount.Account> checkingAccounts,
                              SavingsAccount savingsAcc,
                              List<CheckingAccount.CheckingUser> checkingUsersRef) throws IOException {

        // Load rates if the file exists
        loadRatesFromCSV("rates.csv");

        System.out.println("\n  ─── Mortgage Loan Application ───────────");

        double loanAmount, downPayment, income, debt;
        try {
            System.out.print("  Loan amount: $");
            loanAmount  = Double.parseDouble(sc.nextLine().trim());
            System.out.print("  Down payment: $");
            downPayment = Double.parseDouble(sc.nextLine().trim());
            System.out.print("  Monthly income: $");
            income      = Double.parseDouble(sc.nextLine().trim());
            System.out.print("  Monthly debt: $");
            debt        = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  Invalid input."); return;
        }

        if (!isPreApproved(appUser.creditScore, income, debt)) {
            System.out.println("  Not pre-approved. Credit score must be ≥620 and DTI < 43%.");
            return;
        }
        System.out.println("  Pre-approved!");

        // Loan type
        System.out.println("  Select loan type: [1] Fixed  [2] ARM");
        System.out.print("  Select: ");
        String typeInput = sc.nextLine().trim();
        LoanType type;
        if      (typeInput.equals("1")) type = LoanType.FIXED;
        else if (typeInput.equals("2")) type = LoanType.ARM;
        else { System.out.println("  Invalid choice."); return; }

        // Loan term
        LoanTerm[] terms = LoanTerm.values();
        System.out.println("  Select loan term:");
        for (int i = 0; i < terms.length; i++) {
            System.out.printf("  [%d] %s  (rate: %.2f%%)%n", i + 1, terms[i], getRate(terms[i]));
        }
        System.out.print("  Select: ");
        int termChoice;
        try { termChoice = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("  Invalid choice."); return; }

        if (termChoice < 1 || termChoice > terms.length) { System.out.println("  Invalid choice."); return; }
        LoanTerm term  = terms[termChoice - 1];
        double   rate  = getRate(term);
        double   finalLoan    = loanAmount - downPayment;
        int      years        = getYears(term);
        double   monthlyPmt   = calculateMonthlyPayment(finalLoan, rate, years);

        System.out.println("\n  ─── Mortgage Summary ───────────────────");
        System.out.println("  Type:            " + type);
        System.out.println("  Term:            " + term);
        System.out.printf ("  Rate:            %.2f%%%n", rate);
        System.out.printf ("  Loan Amount:     $%.2f%n", finalLoan);
        System.out.printf ("  Monthly Payment: $%.2f%n", monthlyPmt);

        // Autopay
        System.out.print("  Enable autopay? (yes/no): ");
        boolean autopay = sc.nextLine().trim().equalsIgnoreCase("yes");

        if (autopay) {
            // Determine source
            System.out.println("  AutoPay from: [1] Checking  [2] Savings");
            System.out.print("  Select: ");
            String src = sc.nextLine().trim();

            if (src.equals("1")) {
                if (checkingAccounts.isEmpty()) { System.out.println("  No checking accounts."); return; }
                CheckingAccount.Account acc = LoansModule.pickAccount(sc, checkingAccounts);
                if (acc == null) return;
                if (acc.balance < monthlyPmt) { System.out.println("  Insufficient funds in checking."); return; }
                acc.balance -= monthlyPmt;
                acc.addTransaction("Mortgage Payment", monthlyPmt);
                acc.updateFlags();
                CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                appUser.checkingAccount = acc.balance;
                System.out.printf("  Payment of $%.2f processed from checking.%n", monthlyPmt);
            } else if (src.equals("2")) {
                if (savingsAcc == null || savingsAcc.getSavings() < monthlyPmt) {
                    System.out.println("  Insufficient funds in savings."); return;
                }
                savingsAcc.withdrawSavings(monthlyPmt);
                savingsAcc.update();
                appUser.savingsAccount = savingsAcc.getSavings();
                System.out.printf("  Payment of $%.2f processed from savings.%n", monthlyPmt);
            } else {
                System.out.println("  Invalid source.");
            }
        } else {
            System.out.println("  Manual payment required. Visit the branch.");
        }
    }
}