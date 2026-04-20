import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class MortgageLoan {

    // =========================
    // MODEL
    // =========================
    static class Mortgage {

        String loanID;
        String userID;

        double propertyValue;
        double downPayment;
        double principal;
        double balance;

        double interestRate;
        int termMonths;

        double propertyTaxRate;
        double insuranceRate;
        double escrowBalance;

        LocalDate createdDate;
        LocalDate nextDueDate;

        double monthlyPrincipal;
        double monthlyEscrow;

        int missedPayments;
        String status;
        String delinquency;

        Mortgage(String userID,
                 double propertyValue,
                 double downPayment,
                 double interestRate,
                 int termMonths,
                 double taxRate,
                 double insuranceRate) {

            this.loanID = "MTG-" + UUID.randomUUID().toString().substring(0, 8);
            this.userID = userID;

            this.propertyValue = propertyValue;
            this.downPayment = downPayment;

            this.principal = propertyValue - downPayment;
            this.balance = principal;

            this.interestRate = interestRate;
            this.termMonths = termMonths;

            this.propertyTaxRate = taxRate;
            this.insuranceRate = insuranceRate;

            this.createdDate = LocalDate.now();
            this.nextDueDate = LocalDate.now().plusMonths(1);

            this.monthlyPrincipal = calcMonthly();
            this.monthlyEscrow = calcEscrow();

            this.escrowBalance = 0;
            this.missedPayments = 0;

            this.status = "ACTIVE";
            this.delinquency = "CURRENT";
        }

        double calcMonthly() {
            double r = interestRate / 12.0;
            return (principal * r) / (1 - Math.pow(1 + r, -termMonths));
        }

        double calcEscrow() {
            return (propertyValue * propertyTaxRate) / 12.0
                 + (propertyValue * insuranceRate) / 12.0;
        }

        double totalDue() {
            return monthlyPrincipal + monthlyEscrow;
        }

        void update() {

            LocalDate today = LocalDate.now();

            while (today.isAfter(nextDueDate)) {

                double r = interestRate / 12.0;

                balance += balance * r;
                balance += monthlyPrincipal;

                escrowBalance += monthlyEscrow;

                missedPayments++;
                nextDueDate = nextDueDate.plusMonths(1);
            }

            updateStatus();
        }

        void updateStatus() {
            if (missedPayments >= 3) delinquency = "90_DAYS_LATE";
            else if (missedPayments == 2) delinquency = "60_DAYS_LATE";
            else if (missedPayments == 1) delinquency = "30_DAYS_LATE";
            else delinquency = "CURRENT";
        }

        void pay(double amount,
                 SavingsAccount savings,
                 CheckingAccount.Account checking) {

            if (!status.equals("ACTIVE")) {
                System.out.println("Mortgage closed.");
                return;
            }

            if (amount <= 0) {
                System.out.println("Invalid amount.");
                return;
            }

            boolean paid = false;

            System.out.println("\nPay using:");
            System.out.println("[1] Savings");
            System.out.println("[2] Checking");
            System.out.print("Choice: ");

            Scanner sc = new Scanner(System.in);
            String choice = sc.nextLine();

            switch (choice) {

                case "1" -> {
                    if(savings !=null){
                    if (savings.getSavings() >= amount) {
                        savings.setSavings(savings.getSavings() - amount);
                        paid = true;
                    }
                    }
                }

                case "2" -> {
                    if (checking != null && checking.balance >= amount) {
                        checking.balance -= amount;
                        paid = true;
                    }
                }
            }

            if (!paid) {
                System.out.println("Insufficient funds.");
                return;
            }

            update();

            balance -= amount;

            if (missedPayments > 0) missedPayments--;

            if (balance <= 0) {
                balance = 0;
                status = "CLOSED";
                delinquency = "PAID_OFF";
            }

            System.out.println("Payment successful.");
        }

        void display() {
            update();

            System.out.println("\nID: " + loanID);
            System.out.println("Balance: $" + balance);
            System.out.println("Escrow: $" + escrowBalance);
            System.out.println("Monthly Due: $" + totalDue());
            System.out.println("Missed: " + missedPayments);
            System.out.println("Status: " + delinquency);
        }

        String toCSV() {
            return String.join(",",
                    loanID, userID,
                    String.valueOf(propertyValue),
                    String.valueOf(downPayment),
                    String.valueOf(principal),
                    String.valueOf(balance),
                    String.valueOf(interestRate),
                    String.valueOf(termMonths),
                    String.valueOf(propertyTaxRate),
                    String.valueOf(insuranceRate),
                    String.valueOf(escrowBalance),
                    String.valueOf(missedPayments),
                    status,
                    delinquency,
                    createdDate.toString(),
                    nextDueDate.toString()
            );
        }

        static Mortgage fromCSV(String line) {

            String[] d = line.split(",", -1);

            Mortgage m = new Mortgage(
                    d[1],
                    Double.parseDouble(d[2]),
                    Double.parseDouble(d[3]),
                    Double.parseDouble(d[6]),
                    Integer.parseInt(d[7]),
                    Double.parseDouble(d[8]),
                    Double.parseDouble(d[9])
            );

            m.loanID = d[0];
            m.principal = Double.parseDouble(d[4]);
            m.balance = Double.parseDouble(d[5]);
            m.escrowBalance = Double.parseDouble(d[10]);
            m.missedPayments = Integer.parseInt(d[11]);
            m.status = d[12];
            m.delinquency = d[13];
            m.createdDate = LocalDate.parse(d[14]);
            m.nextDueDate = LocalDate.parse(d[15]);

            return m;
        }
    }

    // =========================
    // STORAGE
    // =========================
    static final String FILE = "mortgage.csv";

    static List<Mortgage> load(String userID) throws IOException {

        List<Mortgage> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;

        while ((line = br.readLine()) != null) {
            if (line.startsWith("loanID")) continue;

            Mortgage m = Mortgage.fromCSV(line);
            if (m.userID.equals(userID)) list.add(m);
        }

        br.close();
        return list;
    }

    static void save(List<Mortgage> list) throws IOException {

        PrintWriter pw = new PrintWriter(new FileWriter(FILE));

        pw.println("loanID,userID,propertyValue,downPayment,principal,balance,interestRate,termMonths,taxRate,insuranceRate,escrow,missed,status,delinquency,created,nextDue");

        for (Mortgage m : list) {
            pw.println(m.toCSV());
        }

        pw.close();
    }


        private static CheckingAccount.Account pickAccount(Scanner sc, List<CheckingAccount.Account> accounts) {
        // Filter to active only and assign clean display numbers
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

    

    // =========================
    // MENU
    // =========================
    static void launch(Scanner sc, User user) throws IOException {

        String userID = user.customerID;


        

        SavingsAccount savings = SavingsAccount.OpenSavingsAccount(userID);
        if (savings == null) {
            savings = SavingsAccount.createSavingsAccount(userID, 100);
        }

        List<CheckingAccount.Account> checkingAccounts = new ArrayList<>();
        List<CheckingAccount.CheckingUser> allCheckingUsers = new ArrayList<>();

        try {
            allCheckingUsers = CheckingAccount.readCSV("checking_accounts.csv");
            CheckingAccount.CheckingUser checkUser = CheckingAccount.findUser(allCheckingUsers, userID);
            if (checkUser != null) {
                checkingAccounts = checkUser.accounts;
            }
        } catch (IOException e) {
            System.out.println("  Note: Could not load checking accounts for transfer.");
        }

        List<Mortgage> mortgages = load(userID);

        while (true) {

            System.out.println("\n--- MORTGAGE ---");
            System.out.println("[1] View");
            System.out.println("[2] Pay");
            System.out.println("[3] Create");
            System.out.println("[0] Exit");

            String c = sc.nextLine();

            switch (c) {

                case "1" -> {
                    for (Mortgage m : mortgages) m.display();
                    save(mortgages);
                }

                case "2" -> {

                    if (mortgages.isEmpty()) break;

                    for (int i = 0; i < mortgages.size(); i++) {
                        System.out.println("[" + (i + 1) + "] " + mortgages.get(i).loanID);
                    }

                    int idx = Integer.parseInt(sc.nextLine()) - 1;
                    if (idx < 0 || idx >= mortgages.size()) break;
                    
                    Mortgage m = mortgages.get(idx);

                    System.out.print("Amount: ");
                    double amt = Double.parseDouble(sc.nextLine());

                    if (checkingAccounts.isEmpty()) {
                        System.out.println("  No checking accounts available.");
                        break;
                    }
                    CheckingAccount.Account from = pickAccount(sc, checkingAccounts);
                    if (from == null) {
                        break;
                    }

                    m.pay(amt, savings, from);

                    save(mortgages);
                }

                case "3" -> {

                    System.out.print("Property value: ");
                    double pv = Double.parseDouble(sc.nextLine());

                    System.out.print("Down payment: ");
                    double dp = Double.parseDouble(sc.nextLine());

                    System.out.print("Interest rate: ");
                    double ir = Double.parseDouble(sc.nextLine());

                    System.out.print("Term months: ");
                    int tm = Integer.parseInt(sc.nextLine());

                    mortgages.add(new Mortgage(userID, pv, dp, ir, tm, 0.015, 0.01));

                    save(mortgages);
                    System.out.println("Created.");
                }

                case "0" -> {
                    return;
                }
            }
        }
    }
}
