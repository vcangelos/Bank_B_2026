import java.time.LocalDate;
import java.util.*;

public class TreasuryBondSystem {

    // ================= ACCOUNT =================
    static class CheckingAccount {
        String id;
        double balance;

        CheckingAccount(String id, double balance) {
            this.id = id;
            this.balance = balance;
        }
    }

    static class SavingsAccount {
        String id;
        double balance;

        SavingsAccount(String id, double balance) {
            this.id = id;
            this.balance = balance;
        }
    }

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

        void payInterest() {
            if (!active || paymentsMade >= years * 2) return;

            if (fromChecking) {
                checking.balance += interest();
            } else {
                savings.balance += interest();
            }

            paymentsMade++;
        }

        void redeem() {
            if (!active) return;

            if (LocalDate.now().isBefore(issueDate.plusYears(years))) return;

            if (fromChecking) {
                checking.balance += value;
            } else {
                savings.balance += value;
            }

            active = false;
        }
    }

    // ================= BANK OPERATIONS =================

    public static void buyBond(User user, boolean useChecking, double amount, double rate, int years) {

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
        System.out.println("Interest processed.");
    }

    public static void redeem(User user) {
        for (Bond b : user.bonds) {
            b.redeem();
        }
        System.out.println("Redemption checked.");
    }
}