// TreasuryBondSystem.java
import java.time.LocalDate;
import java.util.*;
import java.io.*;

public class TreasuryBondSystem {

    static class User {
        CheckingAccount.CheckingUser bankUser;
        CheckingAccount.Account checking;
        CheckingAccount.SavingsAccount savings;
        List<Bond> bonds = new ArrayList<>();

        User(CheckingAccount.CheckingUser bankUser, CheckingAccount.Account checking) {
            this.bankUser = bankUser;
            this.checking = checking;
            this.savings = bankUser.savingsAccount;
        }
    }

    static class Bond {
        String userID;
        String userName;

        String id;
        double value;
        double rate;
        int years;

        LocalDate issueDate;
        LocalDate lastPaymentDate;

        int paymentsMade = 0;
        boolean active = true;

        String sourceType;      // CHECKING or SAVINGS
        String sourceAccountID; // checking account ID or savings account ID

        CheckingAccount.Account checking;
        CheckingAccount.SavingsAccount savings;

        Bond(String userID, String userName, CheckingAccount.Account acc, double value, double rate, int years) {
            this.userID = userID;
            this.userName = userName;
            this.id = UUID.randomUUID().toString();
            this.checking = acc;
            this.value = value;
            this.rate = rate;
            this.years = years;
            this.issueDate = LocalDate.now();
            this.sourceType = "CHECKING";
            this.sourceAccountID = acc.accountID;
        }

        Bond(String userID, String userName, CheckingAccount.SavingsAccount acc, double value, double rate, int years) {
            this.userID = userID;
            this.userName = userName;
            this.id = UUID.randomUUID().toString();
            this.savings = acc;
            this.value = value;
            this.rate = rate;
            this.years = years;
            this.issueDate = LocalDate.now();
            this.sourceType = "SAVINGS";
            this.sourceAccountID = acc.accountID;
        }

        Bond(String userID, String userName, String id, double value, double rate, int years,
             LocalDate issueDate, LocalDate lastPaymentDate, int paymentsMade, boolean active,
             String sourceType, String sourceAccountID,
             CheckingAccount.Account checking, CheckingAccount.SavingsAccount savings) {
            this.userID = userID;
            this.userName = userName;
            this.id = id;
            this.value = value;
            this.rate = rate;
            this.years = years;
            this.issueDate = issueDate;
            this.lastPaymentDate = lastPaymentDate;
            this.paymentsMade = paymentsMade;
            this.active = active;
            this.sourceType = sourceType;
            this.sourceAccountID = sourceAccountID;
            this.checking = checking;
            this.savings = savings;
        }

        double interest() {
            return (value * rate) / 2.0;
        }

        LocalDate maturityDate() {
            return issueDate.plusYears(years);
        }

        boolean isFromChecking() {
            return "CHECKING".equalsIgnoreCase(sourceType);
        }

        void payInterest() {
            if (!active || paymentsMade >= years * 2) return;

            if (lastPaymentDate != null && lastPaymentDate.plusMonths(6).isAfter(LocalDate.now())) {
                System.out.println("Interest not due yet for bond " + id);
                return;
            }

            double i = interest();

            if (isFromChecking()) {
                if (checking == null) {
                    System.out.println("Linked checking account missing for bond " + id);
                    return;
                }
                checking.balance += i;
                checking.addTransaction("Bond Interest", i);
                checking.updateFlags();
            } else {
                if (savings == null) {
                    System.out.println("Linked savings account missing for bond " + id);
                    return;
                }
                savings.balance += i;
            }

            paymentsMade++;
            lastPaymentDate = LocalDate.now();

            System.out.printf("Interest paid for bond %s: $%.2f%n", id, i);
        }

        void redeem() {
            if (!active) return;

            double payout = value;

            if (LocalDate.now().isBefore(maturityDate())) {
                double penalty = value * 0.10;
                payout -= penalty;
                System.out.printf("Early redemption penalty on bond %s: -$%.2f%n", id, penalty);
            }

            if (isFromChecking()) {
                if (checking == null) {
                    System.out.println("Linked checking account missing for bond " + id);
                    return;
                }
                checking.balance += payout;
                checking.addTransaction("Bond Redemption", payout);
                checking.updateFlags();
            } else {
                if (savings == null) {
                    System.out.println("Linked savings account missing for bond " + id);
                    return;
                }
                savings.balance += payout;
            }

            active = false;
            System.out.printf("Bond redeemed: $%.2f%n", payout);
        }

        @Override
        public String toString() {
            return "Bond ID: " + id +
                    " | Owner: " + userName + " (" + userID + ")" +
                    " | Source: " + sourceType + " [" + sourceAccountID + "]" +
                    " | Value: $" + String.format("%.2f", value) +
                    " | Rate: " + String.format("%.2f", rate * 100) + "%" +
                    " | Payments: " + paymentsMade +
                    " | Issued: " + issueDate +
                    " | Matures: " + maturityDate() +
                    " | Active: " + active;
        }

        public String toCSV() {
            return escape(userID) + "," +
                    escape(userName) + "," +
                    escape(sourceType) + "," +
                    escape(sourceAccountID) + "," +
                    escape(id) + "," +
                    value + "," +
                    rate + "," +
                    years + "," +
                    issueDate + "," +
                    (lastPaymentDate == null ? "" : lastPaymentDate.toString()) + "," +
                    paymentsMade + "," +
                    active;
        }

        private static String escape(String s) {
            if (s == null) return "";
            if (s.contains(",") || s.contains("\"")) {
                return "\"" + s.replace("\"", "\"\"") + "\"";
            }
            return s;
        }
    }

    public static CheckingAccount.Account findCheckingAccount(CheckingAccount.CheckingUser user, String accountID) {
        for (CheckingAccount.Account acc : user.accounts) {
            if (acc.accountID.equals(accountID)) return acc;
        }
        return null;
    }

    public static User createBondUser(CheckingAccount.CheckingUser bankUser, String selectedCheckingID) {
        CheckingAccount.Account selected = findCheckingAccount(bankUser, selectedCheckingID);
        if (selected == null) return null;
        return new User(bankUser, selected);
    }

    public static void buyBond(User user, boolean useChecking, double amount, double rate, int years) {
        if (user == null) {
            System.out.println("No user session loaded.");
            return;
        }

        if (amount <= 0 || rate <= 0 || years <= 0) {
            System.out.println("Amount, rate, and years must all be greater than 0.");
            return;
        }

        if (useChecking) {
            if (user.checking == null) {
                System.out.println("No checking account is linked to this bond session.");
                return;
            }

            if (!user.checking.isActive) {
                System.out.println("Selected checking account is inactive.");
                return;
            }

            if (user.checking.balance >= amount) {
                user.checking.balance -= amount;
                user.checking.addTransaction("Bond Purchase", amount);
                user.checking.updateFlags();
                user.bonds.add(new Bond(user.bankUser.userID, user.bankUser.name, user.checking, amount, rate, years));
                System.out.println("Bond purchased from checking.");
            } else {
                System.out.println("Not enough checking balance.");
            }
        } else {
            if (user.savings == null) {
                System.out.println("No savings account linked to this user.");
                return;
            }

            if (user.savings.balance >= amount) {
                user.savings.balance -= amount;
                user.bonds.add(new Bond(user.bankUser.userID, user.bankUser.name, user.savings, amount, rate, years));
                System.out.println("Bond purchased from savings.");
            } else {
                System.out.println("Not enough savings balance.");
            }
        }
    }

    public static void payInterest(User user) {
        if (user == null) return;
        for (Bond b : user.bonds) {
            b.payInterest();
        }
    }

    public static void redeem(User user) {
        if (user == null) return;
        for (Bond b : user.bonds) {
            b.redeem();
        }
        user.bonds.removeIf(b -> !b.active);
    }

    public static void showBonds(User user) {
        if (user == null || user.bonds.isEmpty()) {
            System.out.println("No bonds owned.");
            return;
        }

        for (Bond b : user.bonds) {
            System.out.println(b);
        }
    }

    public static void saveBonds(User user, String file) {
        if (user == null) return;
        saveBondsForAllUsers(Collections.singletonList(user), file);
    }

    public static void saveBondsForAllUsers(List<User> sessions, String file) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("UserID,UserName,SourceType,SourceAccountID,BondID,Value,Rate,Years,IssueDate,LastPaymentDate,PaymentsMade,Active");
            for (User session : sessions) {
                for (Bond b : session.bonds) {
                    pw.println(b.toCSV());
                }
            }
            System.out.println("Bonds saved to CSV: " + file);
        } catch (Exception e) {
            System.out.println("Error saving bond file: " + e.getMessage());
        }
    }

    public static void loadBonds(User user, String file) {
        if (user == null) return;

        user.bonds.clear();

        File csv = new File(file);
        if (!csv.exists()) {
            System.out.println("Bond CSV not found yet. A new one will be created when you save.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            br.readLine(); // header
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = splitCSV(line);
                if (parts.length < 12) continue;

                String userID = parts[0];
                String userName = parts[1];
                String sourceType = parts[2];
                String sourceAccountID = parts[3];
                String bondID = parts[4];
                double value = Double.parseDouble(parts[5]);
                double rate = Double.parseDouble(parts[6]);
                int years = Integer.parseInt(parts[7]);
                LocalDate issueDate = LocalDate.parse(parts[8]);
                LocalDate lastPaymentDate = parts[9].isBlank() ? null : LocalDate.parse(parts[9]);
                int paymentsMade = Integer.parseInt(parts[10]);
                boolean active = Boolean.parseBoolean(parts[11]);

                if (!user.bankUser.userID.equals(userID)) continue;

                CheckingAccount.Account linkedChecking = null;
                CheckingAccount.SavingsAccount linkedSavings = null;

                if ("CHECKING".equalsIgnoreCase(sourceType)) {
                    linkedChecking = findCheckingAccount(user.bankUser, sourceAccountID);
                } else if ("SAVINGS".equalsIgnoreCase(sourceType)) {
                    if (user.bankUser.savingsAccount != null &&
                            user.bankUser.savingsAccount.accountID.equals(sourceAccountID)) {
                        linkedSavings = user.bankUser.savingsAccount;
                    }
                }

                Bond bond = new Bond(
                        userID, userName, bondID, value, rate, years,
                        issueDate, lastPaymentDate, paymentsMade, active,
                        sourceType, sourceAccountID, linkedChecking, linkedSavings
                );

                user.bonds.add(bond);
            }

            System.out.println("Bonds loaded for user " + user.bankUser.userID);
        } catch (Exception e) {
            System.out.println("Error loading bond file: " + e.getMessage());
        }
    }

    private static String[] splitCSV(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        values.add(current.toString());
        return values.toArray(new String[0]);
    }
}