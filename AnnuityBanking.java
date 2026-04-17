import java.time.*;
import java.time.format.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.InputMismatchException;
import java.io.*;

/**
 *
 * CSV files used
 * ─────────────────────────────────────────────────────────────────────────────
 *  banking_main.csv      – master user/account table (written on first run)
 *  annuities.csv         – annuity purchase & return records
 *
 * banking_main.csv columns:
 *   UserID, Name, PIN, AccountID, AccountType, Balance, CreditLimit, IsActive
 *
 * annuities.csv columns:
 *   AnnuityID, UserID, Type, PrincipalPaid, PurchaseDate,
 *   MaturityDate,
 *   PaymentSourceID, ReturnDestinationID, Status
 */
public class AnnuityBanking {


    // Constants

    static final double CREDIT_CARD_SURCHARGE = 0.025; // 2.5 % surcharge for CC payments
    static final DateTimeFormatter DATE_FMT    = DateTimeFormatter.ISO_LOCAL_DATE;

    // Indexed annuity: fixed floor + market-linked cap
    static final double INDEXED_FLOOR_RATE = 0.00;  // 0 % – can't go negative
    static final double INDEXED_CAP_RATE   = 0.08;  // 8 % max annual
    // Variable annuity: random rate between -10% and +10% at maturity


    // Inner classes


    /** Represents any linked financial account (checking / savings / credit card). */
    static class LinkedAccount {
        String  accountID;
        String  type;        // "CHECKING" | "SAVINGS" | "CREDIT"
        double  balance;     // available balance (or remaining credit limit for CC)
        double  creditLimit; // only for CREDIT type; 0 otherwise
        boolean isActive;

        LinkedAccount(String accountID, String type, double balance,
                      double creditLimit, boolean isActive) {
            this.accountID   = accountID;
            this.type        = type.toUpperCase();
            this.balance     = balance;
            this.creditLimit = creditLimit;
            this.isActive    = isActive;
        }

        /** Available spending power for this account. */
        double available() {
            return type.equals("CREDIT") ? creditLimit - balance : balance;
        }

        @Override
        public String toString() {
            if (type.equals("CREDIT")) {
                return String.format("[%s] %s  Available Credit: $%.2f / $%.2f",
                        type, accountID, available(), creditLimit);
            }
            return String.format("[%s] %s  Balance: $%.2f", type, accountID, balance);
        }
    }

    /** A user with a PIN and a list of linked accounts. */
    static class User {
        String              userID;
        String              name;
        String              pin;
        List<LinkedAccount> accounts = new ArrayList<>();

        User(String userID, String name, String pin) {
            this.userID = userID;
            this.name   = name;
            this.pin    = pin;
        }

        void addAccount(LinkedAccount acc) { accounts.add(acc); }

        /** Find a specific linked account by ID. */
        LinkedAccount findAccount(String accountID) {
            for (LinkedAccount a : accounts)
                if (a.accountID.equalsIgnoreCase(accountID)) return a;
            return null;
        }

        /** Return all accounts eligible to be a payment SOURCE. */
        List<LinkedAccount> paymentSources() {
            List<LinkedAccount> list = new ArrayList<>();
            for (LinkedAccount a : accounts)
                if (a.isActive) list.add(a);
            return list;
        }

        /** Return accounts eligible to RECEIVE annuity returns (no credit cards). */
        List<LinkedAccount> returnDestinations() {
            List<LinkedAccount> list = new ArrayList<>();
            for (LinkedAccount a : accounts)
                if (a.isActive && !a.type.equals("CREDIT")) list.add(a);
            return list;
        }

        void printAccounts() {
            System.out.println("\n--- Your Linked Accounts ---");
            for (LinkedAccount a : accounts)
                System.out.println("  " + a);
        }
    }

    /** Represents one annuity contract. */
    static class Annuity {
        String  annuityID;
        String  userID;
        String  type;              // "INDEXED" | "VARIABLE"
        double  principalPaid;     // amount charged to payment source (after surcharge if CC)
        double  premium;           // actual investment principal (before surcharge)
        LocalDate purchaseDate;
        LocalDate maturityDate;
        String  paymentSourceID;
        String  returnDestinationID;
        String  status;            // "ACTIVE" | "MATURED" | "SURRENDERED"

        Annuity(String annuityID, String userID, String type,
                double premium, double principalPaid,
                LocalDate purchaseDate, LocalDate maturityDate,
                String paymentSourceID, String returnDestinationID,
                String status) {
            this.annuityID           = annuityID;
            this.userID              = userID;
            this.type                = type;
            this.premium             = premium;
            this.principalPaid       = principalPaid;
            this.purchaseDate        = purchaseDate;
            this.maturityDate        = maturityDate;
            this.paymentSourceID     = paymentSourceID;
            this.returnDestinationID = returnDestinationID;
            this.status              = status;
        }

        void print() {
            System.out.printf(
                "  Annuity ID   : %s%n" +
                "  Type         : %s%n" +
                "  Premium      : $%.2f  (charged: $%.2f)%n" +
                "  Purchased    : %s%n" +
                "  Matures      : %s%n" +
                "  Payment From : %s%n" +
                "  Returns To   : %s%n" +
                "  Status       : %s%n",
                annuityID, type, premium, principalPaid,
                purchaseDate, maturityDate,
                paymentSourceID, returnDestinationID, status);
        }
    }


    // In-memory store

    static List<User>     allUsers     = new ArrayList<>();
    static List<Annuity>  allAnnuities = new ArrayList<>();

    static final String MAIN_CSV        = "banking_main.csv";
    static final String CUSTOMER_CSV    = "customerinfo.csv";
    static final String ANNUITIES_CSV   = "annuities.csv";


    // Main

    public static void main(String[] args) throws IOException {
        seedAndLoadUsers();
        loadAnnuities();
        matureAnnuities(); // credit any matured annuities on startup

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n ");
            System.out.println("      Annuity Banking System          ");
            System.out.println(" ");
            System.out.print("Enter User ID (or 'exit'): ");
            String inputID = scanner.nextLine().trim();

            if (inputID.equalsIgnoreCase("exit")) {
                System.out.println("Thank you. Goodbye!");
                break;
            }

            User user = findUser(inputID);
            if (user == null) {
                System.out.println("User ID not found.");
                continue;
            }

            // PIN verification
            System.out.print("Enter PIN: ");
            String inputPin = scanner.nextLine().trim();
            if (!user.pin.equals(inputPin)) {
                System.out.println("Incorrect PIN. Access denied.");
                continue;
            }

            System.out.println("\nWelcome, " + user.name + "!");
            userMenu(scanner, user);
        }

        saveMainCSV();
        saveAnnuitiesCSV();
        scanner.close();
    }


    // User menu

    static void userMenu(Scanner scanner, User user) throws IOException {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. View my accounts");
            System.out.println("2. Buy an annuity");
            System.out.println("3. View my annuities");
            System.out.println("4. Surrender an annuity");
            System.out.println("5. Logout");
            System.out.print("Choice: ");

            String line = scanner.nextLine().trim();
            int choice;
            try { choice = Integer.parseInt(line); }
            catch (NumberFormatException e) { System.out.println("Invalid input."); continue; }

            switch (choice) {
                case 1 -> user.printAccounts();
                case 2 -> buyAnnuityFlow(scanner, user);
                case 3 -> viewAnnuities(user);
                case 4 -> surrenderAnnuityFlow(scanner, user);
                case 5 -> { System.out.println("Logged out."); loggedIn = false; }
                default -> System.out.println("Please enter 1–5.");
            }
        }
    }


    // Buy annuity flow

    static void buyAnnuityFlow(Scanner scanner, User user) throws IOException {

        // 1. Annuity type
        System.out.println("\n--- Purchase Annuity ---");
        System.out.println("Select annuity type:");
        System.out.println("  1. Indexed Annuity");
        System.out.println("     - Returns linked to market index, capped at 8%");
        System.out.println("     - Term: 5 years");
        System.out.println("  2. Variable Annuity");
        System.out.println("     - Invests in simulated sub-accounts");
        System.out.println("     - Term: 7 years");
        System.out.print("Choice (1/2): ");
        String typeInput = scanner.nextLine().trim();
        String annuityType;
        int termYears;
        if (typeInput.equals("1")) {
            annuityType = "INDEXED";
            termYears   = 5;
        } else if (typeInput.equals("2")) {
            annuityType = "VARIABLE";
            termYears   = 7;
        } else {
            System.out.println("Invalid selection. Purchase cancelled.");
            return;
        }

        // 2. Premium amount
        System.out.print("Enter premium amount to invest (minimum $1,000): $");
        double premium;
        try { premium = Double.parseDouble(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid amount. Purchase cancelled."); return; }
        if (premium < 1000) { System.out.println("Minimum investment is $1,000. Purchase cancelled."); return; }

        // 3. Payment source
        List<LinkedAccount> sources = user.paymentSources();
        if (sources.isEmpty()) { System.out.println("No active accounts available for payment."); return; }

        System.out.println("\nSelect payment source:");
        for (int i = 0; i < sources.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, sources.get(i));
        System.out.print("Choice: ");
        int srcIdx;
        try { srcIdx = Integer.parseInt(scanner.nextLine().trim()) - 1; }
        catch (NumberFormatException e) { System.out.println("Invalid choice."); return; }
        if (srcIdx < 0 || srcIdx >= sources.size()) { System.out.println("Invalid choice."); return; }

        LinkedAccount source = sources.get(srcIdx);

        // Calculate actual charge (surcharge for credit cards)
        double charged = premium;
        if (source.type.equals("CREDIT")) {
            charged = premium * (1 + CREDIT_CARD_SURCHARGE);
            System.out.printf("[Note] A %.1f%% credit-card surcharge applies. Total charge: $%.2f%n",
                    CREDIT_CARD_SURCHARGE * 100, charged);
        }

        if (source.available() < charged) {
            System.out.printf("Insufficient funds/credit. Available: $%.2f  Required: $%.2f%n",
                    source.available(), charged);
            return;
        }

        // 4. Return destination
        List<LinkedAccount> destinations = user.returnDestinations();
        if (destinations.isEmpty()) { System.out.println("No eligible deposit accounts for returns."); return; }

        System.out.println("\nSelect account to receive annuity returns at maturity:");
        for (int i = 0; i < destinations.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, destinations.get(i));
        System.out.print("Choice: ");
        int dstIdx;
        try { dstIdx = Integer.parseInt(scanner.nextLine().trim()) - 1; }
        catch (NumberFormatException e) { System.out.println("Invalid choice."); return; }
        if (dstIdx < 0 || dstIdx >= destinations.size()) { System.out.println("Invalid choice."); return; }

        LinkedAccount destination = destinations.get(dstIdx);

        // 6. Confirm
        LocalDate purchaseDate = LocalDate.now();
        LocalDate maturityDate = purchaseDate.plusYears(termYears);
        System.out.println("\n Confirm Purchase");
        System.out.printf("  Type             : %s Annuity%n", annuityType);
        System.out.printf("  Premium          : $%.2f%n", premium);
        System.out.printf("  Charged to %s : $%.2f%n", source.accountID, charged);
        System.out.printf("  Term             : %d years%n", termYears);
        System.out.printf("  Returns to       : %s on %s%n", destination.accountID, maturityDate);
        System.out.print("Confirm? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("Purchase cancelled.");
            return;
        }

        // 7. Debit source
        if (source.type.equals("CREDIT")) {
            source.balance += charged; // balance = amount owed on CC
        } else {
            source.balance -= charged;
        }

        // 8. Create annuity record
        String newID = generateAnnuityID();
        Annuity ann  = new Annuity(newID, user.userID, annuityType,
                premium, charged,
                purchaseDate, maturityDate,
                source.accountID, destination.accountID,
                "ACTIVE");
        allAnnuities.add(ann);

        saveMainCSV();
        saveAnnuitiesCSV();

        System.out.println("\n✔ Annuity purchased successfully!");
        ann.print();
    }


    // View annuities

    static void viewAnnuities(User user) {
        System.out.println("\n--- Your Annuities ---");
        boolean found = false;
        for (Annuity a : allAnnuities) {
            if (a.userID.equals(user.userID)) {
                a.print();
                System.out.println("  " + "─".repeat(46));
                found = true;
            }
        }
        if (!found) System.out.println("  No annuities on file.");
    }

    // Surrender annuity (early exit with penalty)
    static void surrenderAnnuityFlow(Scanner scanner, User user) throws IOException {
        List<Annuity> active = new ArrayList<>();
        for (Annuity a : allAnnuities)
            if (a.userID.equals(user.userID) && a.status.equals("ACTIVE"))
                active.add(a);

        if (active.isEmpty()) { System.out.println("No active annuities to surrender."); return; }

        System.out.println("\n--- Surrender an Annuity ---");
        for (int i = 0; i < active.size(); i++)
            System.out.printf("  %d. %s (%s) – Premium: $%.2f  Matures: %s%n",
                    i + 1, active.get(i).annuityID, active.get(i).type,
                    active.get(i).premium, active.get(i).maturityDate);
        System.out.print("Select annuity to surrender (or 0 to cancel): ");
        int idx;
        try { idx = Integer.parseInt(scanner.nextLine().trim()) - 1; }
        catch (NumberFormatException e) { System.out.println("Invalid input."); return; }
        if (idx == -1) { System.out.println("Cancelled."); return; }
        if (idx < 0 || idx >= active.size()) { System.out.println("Invalid choice."); return; }

        Annuity ann = active.get(idx);

        // Surrender charge: 7% if surrendered in first 3 years, else 3%
        long yearsSincePurchase = ChronoUnit.YEARS.between(ann.purchaseDate, LocalDate.now());
        double surrenderPct   = yearsSincePurchase < 3 ? 0.07 : 0.03;
        double surrenderValue = ann.premium * (1 - surrenderPct);

        System.out.printf("[Surrender] Penalty: %.0f%% — you will receive $%.2f (from premium $%.2f)%n",
                surrenderPct * 100, surrenderValue, ann.premium);
        System.out.print("Confirm surrender? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes") && !confirm.equals("y")) { System.out.println("Surrender cancelled."); return; }

        // Credit surrender value to return destination
        LinkedAccount dest = user.findAccount(ann.returnDestinationID);
        if (dest == null) {
            System.out.println("Return destination account not found. Funds not credited — contact support.");
        } else {
            dest.balance += surrenderValue;
            System.out.printf("$%.2f credited to account %s.%n", surrenderValue, dest.accountID);
        }

        ann.status = "SURRENDERED";
        saveMainCSV();
        saveAnnuitiesCSV();
        System.out.println("Annuity " + ann.annuityID + " has been surrendered.");
    }

    // Auto-mature annuities on startup

    static void matureAnnuities() throws IOException {
        boolean anyMatured = false;
        for (Annuity ann : allAnnuities) {
            if (!ann.status.equals("ACTIVE")) continue;
            if (!LocalDate.now().isBefore(ann.maturityDate)) {
                // Find owner
                User user = findUser(ann.userID);
                if (user == null) continue;
                LinkedAccount dest = user.findAccount(ann.returnDestinationID);

                // Compute final credited return
                double finalReturn;
                if (ann.type.equals("INDEXED")) {
                    // Re-simulate: pick random rate within floor/cap
                    double rate = INDEXED_FLOOR_RATE +
                            Math.random() * (INDEXED_CAP_RATE - INDEXED_FLOOR_RATE);
                    finalReturn = ann.premium * Math.pow(1 + rate, ChronoUnit.YEARS.between(ann.purchaseDate, ann.maturityDate));
                } else {
                    // Variable: pick random scenario rate
                    double rate = -0.10 + new Random().nextDouble() * 0.20;
                    finalReturn = ann.premium * Math.pow(1 + rate, ChronoUnit.YEARS.between(ann.purchaseDate, ann.maturityDate));
                }

                if (dest != null) {
                    dest.balance += finalReturn;
                    System.out.printf("[MATURED] Annuity %s: $%.2f deposited to %s (%s)%n",
                            ann.annuityID, finalReturn, dest.accountID, user.name);
                } else {
                    System.out.printf("[MATURED] Annuity %s matured but destination account %s not found.%n",
                            ann.annuityID, ann.returnDestinationID);
                }
                ann.status = "MATURED";
                anyMatured = true;
            }
        }
        if (anyMatured) { saveMainCSV(); saveAnnuitiesCSV(); }
    }


    // CSV – main accounts

    // Columns: UserID, Name, PIN, AccountID, AccountType, Balance, CreditLimit, IsActive

    static void seedAndLoadUsers() throws IOException {
        if (!new File(MAIN_CSV).exists() && !new File(CUSTOMER_CSV).exists()) {
            System.err.println("Error: No user data file found. Please provide " + MAIN_CSV + " or " + CUSTOMER_CSV + ".");
            System.exit(1);
        }
        allUsers = new ArrayList<>();
        if (new File(MAIN_CSV).exists()) {
            allUsers.addAll(readMainCSV());
            System.out.println("Loaded " + allUsers.size() + " users from " + MAIN_CSV);
        }
        if (new File(CUSTOMER_CSV).exists()) {
            List<User> customerUsers = readCustomerCSV();
            allUsers.addAll(customerUsers);
            System.out.println("Loaded " + customerUsers.size() + " users from " + CUSTOMER_CSV);
        }
    }

    static List<User> readCustomerCSV() throws IOException {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMER_CSV))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p       = line.split(",", -1);
                String userID    = p[0].trim();
                String firstName = p[1].trim();
                String lastName  = p[2].trim();
                String name      = firstName + " " + lastName;
                String pin       = p[17].trim(); // password column
                double savings   = p[9].trim().isEmpty()  ? 0.0 : Double.parseDouble(p[9].trim());
                double checking  = p[10].trim().isEmpty() ? 0.0 : Double.parseDouble(p[10].trim());
                double creditLim = p[12].trim().isEmpty() ? 0.0 : Double.parseDouble(p[12].trim());
                String creditCard = p[11].trim();

                User user = new User(userID, name, pin);
                if (savings > 0 || p[9].trim().equals("0.0"))
                    user.addAccount(new LinkedAccount("SAV-" + userID, "SAVINGS", savings, 0.0, true));
                if (checking > 0 || p[10].trim().equals("0.0"))
                    user.addAccount(new LinkedAccount("CHK-" + userID, "CHECKING", checking, 0.0, true));
                if (!creditCard.isEmpty())
                    user.addAccount(new LinkedAccount("CC-" + userID, "CREDIT", 0.0, creditLim, true));
                users.add(user);
            }
        }
        return users;
    }

    static List<User> readMainCSV() throws IOException {
        Map<String, User> map = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MAIN_CSV))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p       = line.split(",", -1);
                String userID    = p[0].trim();
                String name      = p[1].trim();
                String pin       = p[2].trim();
                String accountID = p[3].trim();
                String acctType  = p[4].trim();
                double balance   = Double.parseDouble(p[5].trim());
                double creditLim = Double.parseDouble(p[6].trim());
                boolean isActive = Boolean.parseBoolean(p[7].trim());

                map.putIfAbsent(userID, new User(userID, name, pin));
                map.get(userID).addAccount(new LinkedAccount(accountID, acctType, balance, creditLim, isActive));
            }
        }
        return new ArrayList<>(map.values());
    }

    static void saveMainCSV() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MAIN_CSV))) {
            pw.println("UserID,Name,PIN,AccountID,AccountType,Balance,CreditLimit,IsActive");
            for (User u : allUsers) {
                for (LinkedAccount a : u.accounts) {
                    pw.printf("%s,%s,%s,%s,%s,%.2f,%.2f,%b%n",
                            u.userID, u.name, u.pin,
                            a.accountID, a.type,
                            a.balance, a.creditLimit, a.isActive);
                }
            }
        }
    }

    // CSV – annuities

    // AnnuityID, UserID, Type, Premium, PrincipalPaid, PurchaseDate,
    // MaturityDate, PaymentSourceID, ReturnDestinationID, Status

    static void loadAnnuities() throws IOException {
        File f = new File(ANNUITIES_CSV);
        if (!f.exists()) { allAnnuities = new ArrayList<>(); return; }
        allAnnuities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ANNUITIES_CSV))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                allAnnuities.add(new Annuity(
                        p[0].trim(), p[1].trim(), p[2].trim(),
                        Double.parseDouble(p[3].trim()),
                        Double.parseDouble(p[4].trim()),
                        LocalDate.parse(p[5].trim(), DATE_FMT),
                        LocalDate.parse(p[6].trim(), DATE_FMT),
                        p[7].trim(), p[8].trim(), p[9].trim()));
            }
        }
        System.out.println("Loaded " + allAnnuities.size() + " annuities from " + ANNUITIES_CSV);
    }

    static void saveAnnuitiesCSV() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ANNUITIES_CSV))) {
            pw.println("AnnuityID,UserID,Type,Premium,PrincipalPaid,PurchaseDate," +
                       "MaturityDate,PaymentSourceID,ReturnDestinationID,Status");
            for (Annuity a : allAnnuities) {
                pw.printf("%s,%s,%s,%.2f,%.2f,%s,%s,%s,%s,%s%n",
                        a.annuityID, a.userID, a.type,
                        a.premium, a.principalPaid,
                        a.purchaseDate.format(DATE_FMT),
                        a.maturityDate.format(DATE_FMT),
                        a.paymentSourceID, a.returnDestinationID, a.status);
            }
        }
    }

    // Helpers

    static User findUser(String userID) {
        for (User u : allUsers) if (u.userID.equals(userID)) return u;
        return null;
    }

    static String generateAnnuityID() {
        Set<String> existing = new HashSet<>();
        for (Annuity a : allAnnuities) existing.add(a.annuityID);
        Random rand = new Random();
        String id;
        do { id = "ANN-" + (100000 + rand.nextInt(900000)); } while (existing.contains(id));
        return id;
    }
}

/**
 * AnnuityBanking.java
 *
 * Extends the BankingCSV pattern to support:
 *   - Login via User ID + PIN
 *   - Linked checking, savings, and credit-card accounts (read from banking_main.csv)
 *   - Purchase of Indexed and Variable annuities
 *   - Flexible payment source (checking / savings / credit card)
 *   - Flexible return-deposit destination (checking / savings)
 *   - Full annuity ledger persisted to annuities.csv
 *
 * CSV files used
 * ─────────────────────────────────────────────────────────────────────────────
 *  banking_main.csv      – master user/account table (written on first run)
 *  annuities.csv         – annuity purchase & return records
 *
 * banking_main.csv columns:
 *   UserID, Name, PIN, AccountID, AccountType, Balance, CreditLimit, IsActive
 *
 * annuities.csv columns:
 *   AnnuityID, UserID, Type, PrincipalPaid, PurchaseDate,
 *   MaturityDate,
 *   PaymentSourceID, ReturnDestinationID, Status
 */
