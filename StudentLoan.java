import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class StudentLoan {

    // =========================
    // DATA MODEL
    // =========================
    static class Loan {
        String loanID;
        String userID;
        double principal;
        double interestRate;
        int termMonths;
        double balance;
        String status;

        LocalDate createdDate;
        LocalDate lastPaymentDate;
        LocalDate nextDueDate;

        double monthlyPayment;
        int missedPayments;
        String delinquencyStatus;

        double lateFee = 25.0;

        Loan(String userID, double principal, double interestRate, int termMonths) {
            this.loanID = "LN-" + UUID.randomUUID().toString().substring(0, 6);
            this.userID = userID;
            this.principal = principal;
            this.balance = principal;
            this.interestRate = interestRate;
            this.termMonths = termMonths;
            this.status = "ACTIVE";

            this.createdDate = LocalDate.now();
            this.lastPaymentDate = LocalDate.now();
            this.nextDueDate = LocalDate.now().plusMonths(1);

            this.monthlyPayment = calculateMonthlyPayment();
            this.missedPayments = 0;
            this.delinquencyStatus = "CURRENT";
        }

        double calculateMonthlyPayment() {
            double r = interestRate / 12.0;
            return (principal * r) / (1 - Math.pow(1 + r, -termMonths));
        }

        String toCSV() {
            return String.join(",",
                    loanID, userID,
                    String.valueOf(principal),
                    String.valueOf(interestRate),
                    String.valueOf(termMonths),
                    String.valueOf(balance),
                    status,
                    createdDate.toString(),
                    lastPaymentDate.toString(),
                    nextDueDate.toString(),
                    String.valueOf(monthlyPayment),
                    String.valueOf(missedPayments),
                    delinquencyStatus
            );
        }

        static Loan fromCSV(String line) {
            String[] d = line.split(",", -1);

            Loan l = new Loan(
                    d[1],
                    Double.parseDouble(d[2]),
                    Double.parseDouble(d[3]),
                    Integer.parseInt(d[4])
            );

            l.loanID = d[0];
            l.balance = Double.parseDouble(d[5]);
            l.status = d[6];
            l.createdDate = LocalDate.parse(d[7]);
            l.lastPaymentDate = LocalDate.parse(d[8]);
            l.nextDueDate = LocalDate.parse(d[9]);
            l.monthlyPayment = Double.parseDouble(d[10]);
            l.missedPayments = Integer.parseInt(d[11]);
            l.delinquencyStatus = d[12];

            return l;
        }
    }

    static final String FILE = "loans.csv";
    static Scanner sc = new Scanner(System.in);

    static List<Loan> loadLoans(String userID) throws IOException {
        List<Loan> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;

        while ((line = br.readLine()) != null) {
            if (line.startsWith("loanID")) continue;

            Loan l = Loan.fromCSV(line);
            if (l.userID.equals(userID)) {
                list.add(l);
            }
        }

        br.close();
        return list;
    }

    static void saveAll(List<Loan> all) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(FILE));

        pw.println("loanID,userID,principal,interestRate,termMonths,balance,status,createdDate,lastPaymentDate,nextDueDate,monthlyPayment,missedPayments,delinquencyStatus");

        for (Loan l : all) {
            pw.println(l.toCSV());
        }

        pw.close();
    }

    // =========================
    // CORE LOGIC
    // =========================

    static void updateLoan(Loan loan) {
        LocalDate today = LocalDate.now();

        while (today.isAfter(loan.nextDueDate)) {

            double monthlyRate = loan.interestRate / 12.0;

            loan.balance += loan.balance * monthlyRate;
            loan.balance += loan.monthlyPayment;
            loan.balance += loan.lateFee;

            loan.missedPayments++;
            loan.nextDueDate = loan.nextDueDate.plusMonths(1);
        }

        updateDelinquency(loan);
    }

    static void updateDelinquency(Loan loan) {
        if (loan.missedPayments >= 3) {
            loan.delinquencyStatus = "90_DAYS_LATE";
        } else if (loan.missedPayments == 2) {
            loan.delinquencyStatus = "60_DAYS_LATE";
        } else if (loan.missedPayments == 1) {
            loan.delinquencyStatus = "30_DAYS_LATE";
        } else {
            loan.delinquencyStatus = "CURRENT";
        }
    }

    static void payLoan(
            Loan loan,
            double amount,
            SavingsAccount savings,
            CheckingAccount.Account checking,
            moneyMarket moneyMarket,
            Scanner sc
    ) {

        if (!loan.status.equals("ACTIVE")) {
            System.out.println("Loan is closed.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        System.out.println("\nPay using:");
        System.out.println("[1] Savings");
        System.out.println("[2] Checking");
        System.out.println("[3] Money Market");
        System.out.print("Select: ");

        String choice = sc.nextLine();
        boolean success = false;

        switch (choice) {

            case "1" -> {
                if (savings.getSavings() >= amount) {
                    savings.setSavings(savings.getSavings() - amount);
                    success = true;
                }
            }

            case "2" -> {
                if (checking != null && checking.balance >= amount) {
                    checking.balance -= amount;
                    success = true;
                }
            }

            case "3" -> {
                if (moneyMarket != null && moneyMarket.getMoneyMarket() >= amount) {
                    moneyMarket.setMoneyMarket(moneyMarket.getMoneyMarket() - amount);
                    success = true;
                }
            }
        }

        if (!success) {
            System.out.println("Payment failed: insufficient funds.");
            return;
        }

        updateLoan(loan);

        loan.balance -= amount;

        if (loan.missedPayments > 0) {
            loan.missedPayments--;
        }

        if (loan.balance <= 0) {
            loan.balance = 0;
            loan.status = "CLOSED";
            loan.delinquencyStatus = "PAID_OFF";
        }

        loan.lastPaymentDate = LocalDate.now();
        System.out.println("Loan payment successful.");
    }

    static void createLoan(String userID, List<Loan> all) {
        System.out.print("Loan amount: ");
        double amt = Double.parseDouble(sc.nextLine());

        System.out.print("Term months: ");
        int term = Integer.parseInt(sc.nextLine());

        all.add(new Loan(userID, amt, 0.07, term));
        System.out.println("Loan created.");
    }

    static void viewLoans(List<Loan> loans) {
        for (Loan l : loans) {
            updateLoan(l);

            System.out.println("\nLoan ID: " + l.loanID);
            System.out.println("Balance: " + l.balance);
            System.out.println("Next Due: " + l.nextDueDate);
            System.out.println("Monthly Payment: " + l.monthlyPayment);
            System.out.println("Missed Payments: " + l.missedPayments);
            System.out.println("Status: " + l.delinquencyStatus);
        }
    }

//menu

        private static CheckingAccount.Account pickAccount(Scanner sc, List<CheckingAccount.Account> accounts) {
        //filter to active only and assign clean display numbers
        List<CheckingAccount.Account> active = new ArrayList<>();
        for (CheckingAccount.Account a : accounts) {
            if (a.isActive) {
                active.add(a);
            }
        }
        if (active.isEmpty()) {
            System.out.println("  No active checking accounts available.");
            return null;
        }
        System.out.println("  Select a checking account:");
        for (int i = 0; i < active.size(); i++) {
            System.out.printf("  [%d] %s  ($%.2f)%n", i + 1, active.get(i).accountID, active.get(i).balance);
        }
        System.out.print("  Enter number: ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 1 && choice <= active.size()) {
                return active.get(choice - 1);
            }
        } catch (NumberFormatException e) {
            /* fall through */ }
        System.out.println("  Invalid choice.");
        return null;
    }


    static void launch(Scanner sc, User appUser) throws IOException {

        String userID = appUser.customerID;

        SavingsAccount savings = SavingsAccount.OpenSavingsAccount(userID);
        if (savings == null) {
        }

        List<CheckingAccount.Account> checkingAccounts = new ArrayList<>();
        
        List<moneyMarket> accounts = moneyMarket.OpenmoneyMarket(userID);
        if (savings == null) {
        }

        try {
            List<CheckingAccount.CheckingUser> users =
                    CheckingAccount.readCSV("checking_accounts.csv");

            CheckingAccount.CheckingUser u =
                    CheckingAccount.findUser(users, userID);

            if (u != null) {
                checkingAccounts = u.accounts;
            }
        } catch (IOException e) {
            System.out.println("Could not load checking accounts.");
        }
        boolean running = true;
        while (running) {

            System.out.println("\n--- STUDENT LOAN MENU ---");
            System.out.println("[1] View Loans");
            System.out.println("[2] Pay Loan");
            System.out.println("[3] Create Loan");
            System.out.println("[0] Exit");

            String choice = sc.nextLine();
            List<Loan> loans = loadLoans(userID);

            switch (choice) {

                case "1" -> {
                    viewLoans(loans);
                    saveAll(loans);
                }

                case "2" -> {
                    if (loans.isEmpty()) break;

                    for (int i = 0; i < loans.size(); i++) {
                        System.out.println("[" + (i + 1) + "] " +
                                loans.get(i).loanID + " $" + loans.get(i).balance);
                    }

                    System.out.print("Pick: ");
                    int idx = Integer.parseInt(sc.nextLine()) - 1;

                    if (idx < 0 || idx >= loans.size()) break;

                    Loan l = loans.get(idx);

                    System.out.print("Amount: ");
                    double amt = Double.parseDouble(sc.nextLine());

                    CheckingAccount.Account from = pickAccount(sc, checkingAccounts);
                    moneyMarket account = moneyMarket.pickAccount(accounts);


                    payLoan(l, amt, savings, from, account, sc);
                    saveAll(loans);
                }

                case "3" -> {
                    createLoan(userID, loans);
                    saveAll(loans);
                }

                case "0" ->
                    running = false;
                default ->
                    System.out.println("  Invalid option.");
            }
        }
    }
}
