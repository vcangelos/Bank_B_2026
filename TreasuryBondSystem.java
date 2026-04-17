import java.time.LocalDate;
import java.util.*;
import java.io.*;

public class TreasuryBondSystem {

    // ================= USER =================
    static class User {
        String name;
        CheckingAccount checking;
        SavingsAccount savings;
        List<Bond> bonds = new ArrayList<>();

        User(String name, CheckingAccount c, SavingsAccount s) {
            this.name = name;
            this.checking = c;
            this.savings = s;
        }
    }

    // ================= BOND =================
    static class Bond {
        String id;
        double value;
        double rate;
        int years;

        LocalDate issueDate;
        LocalDate lastPaymentDate;

        int paymentsMade = 0;
        boolean active = true;

        CheckingAccount checking;
        SavingsAccount savings;
        boolean fromChecking;

        Bond(CheckingAccount acc, double value, double rate, int years) {
            this.id = UUID.randomUUID().toString();
            this.checking = acc;
            this.value = value;
            this.rate = rate;
            this.years = years;
            this.issueDate = LocalDate.now();
            this.fromChecking = true;
        }

        Bond(SavingsAccount acc, double value, double rate, int years) {
            this.id = UUID.randomUUID().toString();
            this.savings = acc;
            this.value = value;
            this.rate = rate;
            this.years = years;
            this.issueDate = LocalDate.now();
            this.fromChecking = false;
        }

        double interest() {
            return (value * rate) / 2;
        }

        LocalDate maturityDate() {
            return issueDate.plusYears(years);
        }

        void payInterest() {

            if (!active || paymentsMade >= years * 2) return;

            if (lastPaymentDate != null &&
                    lastPaymentDate.plusMonths(6).isAfter(LocalDate.now())) {
                System.out.println("Interest not due yet.");
                return;
            }

            double i = interest();

            if (fromChecking) {
                checking.balance += i;
            } else {
                savings.balance += i;
            }

            paymentsMade++;
            lastPaymentDate = LocalDate.now();

            System.out.println("Interest paid: $" + i);
        }

        void redeem() {

            if (!active) return;

            double payout = value;

            if (LocalDate.now().isBefore(maturityDate())) {
                double penalty = value * 0.10;
                payout -= penalty;
                System.out.println("Early redemption penalty: -$" + penalty);
            }

            if (fromChecking) {
                checking.balance += payout;
            } else {
                savings.balance += payout;
            }

            active = false;
            System.out.println("Bond redeemed: $" + payout);
        }

        public String toString() {
            return "ID: " + id +
                    " | $" + value +
                    " | Rate: " + (rate * 100) + "%" +
                    " | Payments: " + paymentsMade +
                    " | Matures: " + maturityDate() +
                    " | Active: " + active;
        }

        public String toCSV() {
            return id + "," + value + "," + rate + "," + years + "," +
                    issueDate + "," + paymentsMade + "," + active;
        }
    }

    // ================= OPERATIONS =================

    public static void buyBond(User user, boolean useChecking,
                               double amount, double rate, int years) {

        if (useChecking) {

            if (user.checking.balance >= amount) {
                user.checking.balance -= amount;
                user.bonds.add(new Bond(user.checking, amount, rate, years));
                System.out.println("Bond purchased from checking.");
            } else {
                System.out.println("Not enough checking balance.");
            }

        } else {

            if (user.savings.balance >= amount) {
                user.savings.balance -= amount;
                user.bonds.add(new Bond(user.savings, amount, rate, years));
                System.out.println("Bond purchased from savings.");
            } else {
                System.out.println("Not enough savings balance.");
            }
        }
    }

    public static void payInterest(User user) {
        for (Bond b : user.bonds) {
            b.payInterest();
        }
    }

    public static void redeem(User user) {
        for (Bond b : user.bonds) {
            b.redeem();
        }

        user.bonds.removeIf(b -> !b.active);
    }

    public static void showBonds(User user) {
        if (user.bonds.isEmpty()) {
            System.out.println("No bonds owned.");
            return;
        }

        for (Bond b : user.bonds) {
            System.out.println(b);
        }
    }

    public static void saveBonds(User user, String file) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {

            pw.println("ID,Value,Rate,Years,IssueDate,Payments,Active");

            for (Bond b : user.bonds) {
                pw.println(b.toCSV());
            }

            System.out.println("Bonds saved to CSV.");

        } catch (Exception e) {
            System.out.println("Error saving file.");
        }
    }
}