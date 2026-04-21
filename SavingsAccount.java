//TODO USING SAVINGS.CSV IT'S A WIP
//Savings Account: is where you hold on to your money and in a while you can get interest for holding that money within a specific time period.
//this is used for the random generator.

import java.util.Random; //Random is used for the random ID generator  
import java.util.Scanner;
import java.io.BufferedWriter; //This helps us write data to the CSV file.
import java.io.IOException; //catch errors if anything silly happens.
import java.nio.file.Files; //to make it easier to access the files read and write functions. Our HasSavings is a static so we need static methods to make the code work. Files work hand to hand with path objects instead of using Strings we could use that which makes it platform independent.
import java.util.ArrayList; //Array list is needed when we don't know the size of an array or when we resize an array if you see SavingsIDexists I used array list to capture all the columns and use it to compare with the current savings ID with the savings ID in the current array list. Something like this psuedocode currentsavingsID = currentarraylistsavings.
import java.util.List;
import java.nio.file.Path; //This function is used to find the file you want for example I'm using this to find my Savings.csv
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption; //we don't want to overwrite when we create a savings ID account we want to append.
import java.io.BufferedReader; //is used to read line by line

import java.time.LocalDate;

public class SavingsAccount {
    /*** Variables ***/
    private double savingsbalance = 0; // TODO this is our savings balance which will start in a range if the customer first creates their account in about 100-300 $                                  
    private final double minBalanceFee = 15; // minimum balance fee is 15 dollars.
    private final double monthlyFee = 12; // TODO monthly fee is 12 dollars.
    private final double yearlyFee = 48; // TODO 48 dollars.
    private String userid; // current userID lets say they registered or they were recent the constructor we'll need userID as a verification method
    private String SavingsID; //savings ID is a unique verification method to see if the user has a savings account or not.
    private final double interestamount = 0.0042; //interest is 5%
    private boolean isEmployee = false;
    private boolean hasSavings;

    /*transaction data*/
    private final List<String> transactionHistory = new ArrayList<>();

    /* Static Final variables */
    private static final Path csvPath = Path.of("Savings.csv"); // fine the path for the CSV file
    private static final Path csvPathFee = Path.of("SavingsFeeCheck.csv"); // measuring monthly
    private static final Path csvCustomerInfo = Path.of("customerinfo.csv");
    private static final Path csvEmployeecsv= Path.of("employeecards.csv");
    private static final Path csvEmployeesavingscsv= Path.of("EmployeeSavings.csv");

    private static final long MAX = 199_999_999_999L;
    private static final long MIN = 100_000_000_000L;
    private static final double minimumbalance = 100;
    private static final double maximumbalance = 300;

    public SavingsAccount()
    {
        savingsbalance = minimumbalance;
    }
    public SavingsAccount(String userid, double savingsamount)
    {
        this.userid = userid;
        savingsbalance = savingsamount;
    }

    public static boolean userIDExists(String userid) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columnsplit = line.split(",");
                if (columnsplit.length > 0 && columnsplit[0].trim().equals(userid.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean employeeIDExists(String userid) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(csvEmployeesavingscsv)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columnsplit = line.split(",");
                if (columnsplit.length > 0 && columnsplit[0].trim().equals(userid.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean SavingsIDExistsfeefile(String SavingsID) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(csvPathFee)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columnsplit = line.split(",");
                if (columnsplit.length == 0) continue;
                if (columnsplit[0].trim().equals(SavingsID.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void writeSavingsCSV(String UserID, String SavingsId, double newbalance) throws IOException
    {
        Path temp = Files.createTempFile("temp", ".csv");
        try(BufferedReader read = Files.newBufferedReader(csvPath); BufferedWriter writetemp = Files.newBufferedWriter(temp)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",");
                if(datacur[0].trim().equals(UserID)){
                    datacur[2] = String.valueOf(newbalance);
                    writetemp.write(String.join(",", datacur));
                }
                else{
                    writetemp.write(line);
                }
                writetemp.newLine();
            }
        }
        Files.move(temp, csvPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void writeEmployeeSavingsCSV(String UserID, String SavingsId, double newbalance) throws IOException
    {
        Path temp = Files.createTempFile("temp", ".csv");
        try(BufferedReader read = Files.newBufferedReader(csvEmployeesavingscsv); BufferedWriter writetemp = Files.newBufferedWriter(temp)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",");
                if(datacur[0].trim().equals(UserID)){
                    datacur[2] = String.valueOf(newbalance);
                    writetemp.write(String.join(",", datacur));
                }
                else{
                    writetemp.write(line);
                }
                writetemp.newLine();
            }
        }
        Files.move(temp, csvEmployeesavingscsv, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void writeCustomerinfo(boolean HasSavings, String UserID) throws IOException
    {
        Path temp = Files.createTempFile("temp", ".csv");
        try(BufferedReader read = Files.newBufferedReader(csvCustomerInfo); BufferedWriter writetemp = Files.newBufferedWriter(temp)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",", -1);
                if(datacur[0].trim().equals(UserID)){
                    for(String Value:datacur){
                        System.out.println(Value + ", ");
                    }
                    datacur[9] = String.valueOf(HasSavings);
                    writetemp.write(String.join("," , datacur));
                }
                else{
                    writetemp.write(line);
                }
                writetemp.newLine();
            }
        }
        Files.move(temp, csvCustomerInfo, StandardCopyOption.REPLACE_EXISTING);
    }

    public static boolean isEmployee(String EmployeeID) throws IOException{
        try (BufferedReader reader = Files.newBufferedReader(csvEmployeecsv)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                ArrayList<String> data = csvParsing.parseLine(line);
                if(line.trim().isEmpty())
                {
                    continue;
                }
                if (data.size() > 0 && data.get(0).equals(EmployeeID)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static SavingsAccount createSavingsAccount(String userid, double savingsamount) throws IOException {
        if(userIDExists(userid) || employeeIDExists(userid)) {
            System.out.println("There is another user with this UserID");
            return null;
        } else {
            String SavingsID = RandomIDGenerator();
            SavingsAccount account = null;
            if (savingsamount == 100) {
                account = new SavingsAccount();
                account.userid = userid;
                account.setSavingsID(SavingsID);
                account.isEmployee = isEmployee(userid);
                account.hasSavings = true;
            } else if (savingsamount <= maximumbalance && savingsamount > minimumbalance) {
                account = new SavingsAccount(userid, savingsamount);
                account.setSavingsID(SavingsID);
                account.hasSavings = true;
                account.isEmployee = isEmployee(userid);
                writeCustomerinfo(account.hasSavings, account.getUserid());
            }
            else {
                System.out.println("The Savings amount has to be in the range of 100-300");
                return null;
            }
            Path currentCSV = isEmployee(userid) ? csvEmployeesavingscsv : csvPath;
            try (BufferedWriter bw = Files.newBufferedWriter(currentCSV, StandardOpenOption.APPEND)) {
                bw.write(userid + "," + SavingsID + "," + account.savingsbalance);
                bw.newLine();
            }
            writeCustomerinfo(account.hasSavings, account.getUserid());
            return account;
        }
    }

    public static SavingsAccount OpenSavingsAccount(String userid) throws IOException{
        if(userIDExists(userid))
        {
            try(BufferedReader readlines = Files.newBufferedReader(csvPath)){
                readlines.readLine();
                String currentline;
                while((currentline = readlines.readLine()) != null){
                    String[] currentdata = currentline.split(",", -1);
                    if(currentdata[0].trim().equals(userid))
                    {
                        SavingsAccount account = new SavingsAccount();
                        account.userid = userid;
                        account.savingsbalance = Double.parseDouble(currentdata[2]);
                        account.SavingsID = currentdata[1];
                        account.isEmployee = employeeIDExists(userid);
                        return account;
                    }
                }
            }
            System.out.println("You're logged in");
        }
        else
        {
            System.out.println("Account doesn't exist, create it.");
        }
        return null;
    }

    public static String RandomIDGenerator() throws IOException
    {
        Random rand = new Random();
        String ID;
        do {
            long number = (MIN) + (long) (rand.nextDouble() * (MAX - MIN + 1));
            ID = String.valueOf(number);
        } while (savingsIDExists(ID));
        return ID;
    }

    public static boolean savingsIDExists(String SavingsID) throws IOException{
        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] currentdata_to_col = line.split(",", -1);
                if (currentdata_to_col.length > 0 && currentdata_to_col[1].equals(SavingsID)) {
                    return true;
                }
            }
        }
        return false;
    }

    public double getSavings() { return savingsbalance; }
    public void setSavings(double savings) { savingsbalance = savings; }
    public void setSavingsID(String SavingsID) { this.SavingsID = SavingsID; }
    public String getUserid() { return userid; }
    public String getSavingsID() { return SavingsID; }

    public double depositSavings(double depositamt) {
        if (depositamt > 0) {
            return savingsbalance += depositamt;
        } else {
            System.out.println("Deposit has to be a positive.");
        }
        return savingsbalance;
    }

    public boolean withdrawSavings(double amt)
    {
        if(amt <= 0 )
        {
            System.out.println("Account can't be less than or equal to 0, so please choose a higher value.");
            return false;
        }
        if(amt <= savingsbalance)
        {
            savingsbalance -= amt;
            return true;
        }
        else{
            System.out.println("Insufficient funds.");
            return false;
        }
    }

    public double transfer(List<CheckingAccount.Account> possibleDestinations, Scanner scanner, boolean transfer, double value){
        if(transfer == true){
            if(possibleDestinations != null)
            {
                if (possibleDestinations.isEmpty()) {
                    System.out.println("No checking accounts available for transfer.");
                    return value;
                }
                CheckingAccount.Account from = null;
                while (from == null) {
                    System.out.println("Select the checking account to transfer FROM:");
                    for (int i = 0; i < possibleDestinations.size(); i++) {
                        CheckingAccount.Account acc = possibleDestinations.get(i);
                        if(!acc.isActive)
                        {
                            System.out.println("Don't pick an inactive account");
                            continue;
                        }
                        System.out.printf("%d: %s (Balance: $%.2f, Active: %s)%n",
                                i + 1, acc.accountID, acc.balance, acc.isActive ? "Yes" : "No");
                    }
                    System.out.print("Enter number: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice >= 1 && choice <= possibleDestinations.size()) {
                        from = possibleDestinations.get(choice - 1);
                        if (!from.isActive) {
                            System.out.println("Selected account is inactive. Choose another.");
                            from = null;
                        }
                    } else {
                        System.out.println("Invalid choice. Try again.");
                    }
                }
                while(true)
                {
                    System.out.print("Enter amount to transfer: $");
                    if(!scanner.hasNextDouble())
                    {
                        System.out.println("Enter a proper number.");
                        scanner.next();
                        continue;
                    }
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    if(amount <= 0) { System.out.println("No Negative amounts or 0."); continue; }
                    if (amount > from.balance) { System.out.println("Amount is insufficient, "); continue; }
                    from.balance -= amount;
                    savingsbalance += amount;
                    from.addTransaction("Transfer amount", amount);
                    from.updateFlags();
                    System.out.printf("Transferred $%.2f from %s to %s.%n", amount, from.accountID, getUserid());
                    System.out.printf("New balances -> %s: $%.2f | %s: $%.2f%n", from.accountID, from.balance, getUserid(), getSavings());
                    try { update(); } catch (IOException e) { e.printStackTrace(); }
                    break;
                }
                return value;
            }
            else{
                while(true)
                {
                    System.out.print("Enter amount to transfer: $");
                    if(!scanner.hasNextDouble()) { System.out.println("Enter a proper number."); scanner.next(); continue; }
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    if(amount <= 0) { System.out.println("No Negative amounts or 0."); continue; }
                    if (amount > value) { System.out.println("Amount is insufficient, "); continue; }
                    value += amount;
                    savingsbalance -= amount;
                    System.out.printf("Transferred $%.2f from $%.2f to %s.%n", amount, value, getUserid());
                    System.out.printf("New balances -> $%.2f | %s: $%.2f%n", value, getUserid(), getSavings());
                    try { update(); } catch (IOException e) { e.printStackTrace(); }
                    break;
                }
                return value;
            }
        }
        else{
            if(possibleDestinations != null)
            {
                if (possibleDestinations.isEmpty()) {
                    System.out.println("No checking accounts available for transfer.");
                    return value;
                }
                CheckingAccount.Account from = null;
                while (from == null) {
                    System.out.println("Select the checking account to transfer FROM:");
                    for (int i = 0; i < possibleDestinations.size(); i++) {
                        CheckingAccount.Account acc = possibleDestinations.get(i);
                        if(!acc.isActive) { System.out.println("Don't pick an inactive account"); continue; }
                        System.out.printf("%d: %s (Balance: $%.2f, Active: %s)%n",
                                i + 1, acc.accountID, acc.balance, acc.isActive ? "Yes" : "No");
                    }
                    System.out.print("Enter number: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice >= 1 && choice <= possibleDestinations.size()) {
                        from = possibleDestinations.get(choice - 1);
                        if (!from.isActive) { System.out.println("Selected account is inactive. Choose another."); from = null; }
                    } else { System.out.println("Invalid choice. Try again."); }
                }
                while(true)
                {
                    System.out.print("Enter amount to transfer: $");
                    if(!scanner.hasNextDouble()) { System.out.println("Enter a proper number."); scanner.next(); continue; }
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    if(amount <= 0) { System.out.println("No Negative amounts or 0."); continue; }
                    if (amount > from.balance) { System.out.println("Amount is insufficient, "); continue; }
                    savingsbalance -= amount;
                    from.balance += amount;
                    from.addTransaction("Transfer amount", amount);
                    from.updateFlags();
                    System.out.printf("Transferred $%.2f from %s to %s.%n", amount, from.accountID, getUserid());
                    System.out.printf("New balances -> %s: $%.2f | %s: $%.2f%n", from.accountID, from.balance, getUserid(), getSavings());
                    try { update(); } catch (IOException e) { e.printStackTrace(); }
                    break;
                }
                return value;
            }
            else{
                while(true)
                {
                    System.out.print("Enter amount to transfer: $");
                    if(!scanner.hasNextDouble()) { System.out.println("Enter a proper number."); scanner.next(); continue; }
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    if(amount <= 0) { System.out.println("No Negative amounts or 0."); continue; }
                    if (amount > value) { System.out.println("Amount is insufficient, "); continue; }
                    value += amount;
                    savingsbalance -= amount;
                    System.out.printf("Transferred $%.2f from $%.2f to %s.%n", amount, value, getUserid());
                    System.out.printf("New balances -> $%.2f | %s: $%.2f%n", value, getUserid(), getSavings());
                    try { update(); } catch (IOException e) { e.printStackTrace(); }
                    break;
                }
                return value;
            }
        }
    }

    public void printTransactionHistory() {
        System.out.println("Transaction History for " + this.userid + ":");
        for(String tx : transactionHistory) {
            System.out.println(tx);
        }
    }

    public void update() throws IOException{
        minBalanceFee();
        yearlyFee();
        monthlyFee();
        if(isEmployee)
        {
            SavingsAccount.writeEmployeeSavingsCSV(userid, SavingsID, savingsbalance);
        }
        else{
            SavingsAccount.writeSavingsCSV(userid, SavingsID, savingsbalance);
        }
        applyInterest();
    }

    public double minBalanceFee() throws IOException {
        LocalDate today = LocalDate.now();
        String currentMonth = today.getYear() + "-" + String.format("%02d", today.getMonthValue());
        Path temp = Files.createTempFile("temp", ".csv");
        boolean found = false;
        try (BufferedReader reader = Files.newBufferedReader(csvPathFee);
             BufferedWriter writer = Files.newBufferedWriter(temp)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(SavingsID)) {
                    found = true;
                    String lastMinMonth = data.length > 1 ? data[1] : "";
                    String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                    double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : savingsbalance;
                    String lastYear = data.length > 4 ? data[4] : "";
                    String lastInterest = data.length > 5 ? data[5] : "";
                    if (lastMinMonth != null && !lastMinMonth.isEmpty() && !lastMinMonth.equals(currentMonth)) {
                        if (savingsbalance < minimumbalance) {
                            savingsbalance -= minBalanceFee;
                            writeSavingsCSV(userid, SavingsID, savingsbalance);
                        }
                        lastMinMonth = currentMonth;
                        currentBalance = savingsbalance;
                    } else {
                        currentBalance = savingsbalance;
                    }
                    writer.write(String.join(",",
                            SavingsID,
                            lastMinMonth != null ? lastMinMonth : "",
                            lastMonthlyMonth != null ? lastMonthlyMonth : "",
                            String.format("%.2f",currentBalance),
                            lastYear != null ? lastYear : "",
                            lastInterest != null ? lastInterest : ""
                    ));
                    writer.newLine();
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
            if (!found) {
                writer.write(String.join(",", SavingsID, currentMonth, "", String.format("%.2f",savingsbalance), "", ""));
                writer.newLine();
            }
        }
        Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return savingsbalance;
    }

    public double monthlyFee() throws IOException {
        LocalDate today = LocalDate.now();
        String currentMonth = today.getYear() + "-" + String.format("%02d", today.getMonthValue());
        Path tempfile = Files.createTempFile("csv_temp", ".csv");
        boolean found = false;
        try (BufferedReader reader = Files.newBufferedReader(csvPathFee);
             BufferedWriter writer = Files.newBufferedWriter(tempfile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].trim().equals(SavingsID)) {
                    found = true;
                    String lastMinMonth = data.length > 1 ? data[1] : "";
                    String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                    double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : savingsbalance;
                    String lastYear = data.length > 4 ? data[4] : "";
                    String lastInterest = data.length > 5 ? data[5] : "";
                    if (!lastMonthlyMonth.isEmpty() && !currentMonth.equals(lastMonthlyMonth)) {
                        savingsbalance -= monthlyFee;
                        writeSavingsCSV(userid, SavingsID, savingsbalance);
                    }
                    lastMonthlyMonth = currentMonth;
                    writer.write(String.join(",",
                            SavingsID,
                            lastMinMonth != null ? lastMinMonth : "",
                            lastMonthlyMonth,
                            String.format("%.2f",currentBalance),
                            lastYear != null ? lastYear : "",
                            lastInterest != null ? lastInterest : ""
                    ));
                    writer.newLine();
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
            if (!found) {
                writer.write(String.join(",", SavingsID, "", currentMonth, String.format("%.2f",savingsbalance), "", ""));
                writer.newLine();
            }
        }
        Files.move(tempfile, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return savingsbalance;
    }

    public double yearlyFee() throws IOException {
        String currentYear = String.valueOf(LocalDate.now().getYear());
        Path temp = Files.createTempFile("temp", ".csv");
        boolean found = false;
        try (BufferedReader reader = Files.newBufferedReader(csvPathFee);
             BufferedWriter writer = Files.newBufferedWriter(temp)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(SavingsID)) {
                    found = true;
                    String lastMinMonth = data.length > 1 ? data[1] : "";
                    String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                    double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : savingsbalance;
                    String lastYear = data.length > 4 ? data[4] : "";
                    String lastInterest = data.length > 5 ? data[5] : "";
                    if (!lastYear.isEmpty() && !lastYear.equals(currentYear)) {
                        savingsbalance -= yearlyFee;
                        writeSavingsCSV(userid, SavingsID, savingsbalance);
                    }
                    lastYear = currentYear;
                    writer.write(String.join(",",
                            SavingsID,
                            lastMinMonth != null ? lastMinMonth : "",
                            lastMonthlyMonth != null ? lastMonthlyMonth : "",
                            String.format("%.2f",currentBalance),
                            lastYear,
                            lastInterest != null ? lastInterest : ""
                    ));
                    writer.newLine();
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
            if (!found) {
                writer.write(String.join(",", SavingsID, "", "", String.format("%.2f",savingsbalance), currentYear, ""));
                writer.newLine();
            }
        }
        Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return savingsbalance;
    }

    public double applyInterest() throws IOException {
        LocalDate today = LocalDate.now();
        String currentMonth = today.getYear() + "-" + String.format("%02d", today.getMonthValue());
        Path temp = Files.createTempFile("temp", ".csv");
        boolean found = false;
        try (BufferedReader reader = Files.newBufferedReader(csvPathFee);
             BufferedWriter writer = Files.newBufferedWriter(temp)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(SavingsID)) {
                    found = true;
                    String lastMinMonth = data.length > 1 ? data[1] : "";
                    String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                    double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : savingsbalance;
                    String lastYear = data.length > 4 ? data[4] : "";
                    String lastInterest = data.length > 5 ? data[5] : "";
                    if (!lastInterest.isEmpty() && !lastInterest.equals(currentMonth)) {
                        double monthlyRate = interestamount / 12.0;
                        savingsbalance += savingsbalance * monthlyRate;
                        currentBalance = savingsbalance;
                        writeSavingsCSV(userid, SavingsID, currentBalance);
                    }
                    lastInterest = currentMonth;
                    writer.write(String.join(",",
                            SavingsID,
                            lastMinMonth != null ? lastMinMonth : "",
                            lastMonthlyMonth != null ? lastMonthlyMonth : "",
                            String.format("%.2f",currentBalance),
                            lastYear != null ? lastYear : "",
                            lastInterest
                    ));
                    writer.newLine();
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
            if (!found) {
                writer.write(String.join(",", SavingsID, "", "", String.format("%.2f",savingsbalance), "", currentMonth));
                writer.newLine();
            }
        }
        Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return savingsbalance;
    }

    // ── Helper: let user pick a checking account from a list ─────────────
    // Builds a display list of only active accounts so indices are always correct.
    private static CheckingAccount.Account pickAccount(Scanner sc, List<CheckingAccount.Account> accounts) {
        // Filter to active only and assign clean display numbers
        List<CheckingAccount.Account> active = new ArrayList<>();
        for (CheckingAccount.Account a : accounts) {
            if (a.isActive) active.add(a);
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
        } catch (NumberFormatException e) { /* fall through */ }
        System.out.println("  Invalid choice.");
        return null;
    }

    // ── Launch (called by BankApp's menu.java) ────────────────────────────
    public static void launch(Scanner sc, User appUser) throws IOException {
        String userid = appUser.customerID;

        // BUG FIX 1: Try to open existing account FIRST.
        // createSavingsAccount returns null and prints an error if the user already exists,
        // so we must check OpenSavingsAccount first, only creating if truly not found.
        SavingsAccount acc = SavingsAccount.OpenSavingsAccount(userid);
        if (acc == null) {
            System.out.println("  No savings account found. Opening one now...");
            acc = SavingsAccount.createSavingsAccount(userid, 100);
        }
        if (acc == null) {
            System.out.println("  Could not load or create a savings account.");
            return;
        }

        // Load checking accounts for transfer support — keep the full list so
        // writeCSV has the correct in-memory balances when we save after a transfer.
        List<CheckingAccount.Account> checkingAccounts = new ArrayList<>();
        List<CheckingAccount.CheckingUser> allCheckingUsers = new ArrayList<>();
        try {
            allCheckingUsers = CheckingAccount.readCSV("checking_accounts.csv");
            CheckingAccount.CheckingUser checkUser = CheckingAccount.findUser(allCheckingUsers, userid);
            if (checkUser != null) {
                checkingAccounts = checkUser.accounts;
            }
        } catch (IOException e) {
            System.out.println("  Note: Could not load checking accounts for transfer.");
        }
        final List<CheckingAccount.CheckingUser> checkingUsersRef = allCheckingUsers;

        // Transaction menu
        boolean running = true;
        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Savings");
            System.out.println("────────────────────────────────────────");
            System.out.printf("  Balance: $%.2f%n", acc.getSavings());
            System.out.println("  [1] Deposit");
            System.out.println("  [2] Withdraw");
            System.out.println("  [3] Transfer (Checking → Savings)");
            System.out.println("  [4] Transfer (Savings → Checking)");
            System.out.println("  [5] View Transaction History");
            System.out.println("  [0] Back to Dashboard");
            System.out.println("────────────────────────────────────────");
            System.out.print("  Select: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("  Deposit amount: $");
                    try {
                        double amt = Double.parseDouble(sc.nextLine().trim());
                        acc.depositSavings(amt);
                        acc.update();
                        System.out.printf("  Deposited $%.2f. New balance: $%.2f%n", amt, acc.getSavings());
                    } catch (NumberFormatException e) {
                        System.out.println("  Invalid amount.");
                    }
                }
                case "2" -> {
                    System.out.print("  Withdraw amount: $");
                    try {
                        double amt = Double.parseDouble(sc.nextLine().trim());
                        if (acc.withdrawSavings(amt)) {
                            acc.update();
                            System.out.printf("  Withdrew $%.2f. New balance: $%.2f%n", amt, acc.getSavings());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("  Invalid amount.");
                    }
                }
                case "3" -> {
                    // Checking → Savings
                    if (checkingAccounts.isEmpty()) { System.out.println("  No checking accounts available."); break; }
                    CheckingAccount.Account from = pickAccount(sc, checkingAccounts);
                    if (from == null) break;
                    System.out.print("  Amount to transfer: $");
                    try {
                        double amt = Double.parseDouble(sc.nextLine().trim());
                        if (amt <= 0) { System.out.println("  Amount must be positive."); break; }
                        if (amt > from.balance) { System.out.println("  Insufficient funds in checking."); break; }
                        // Move the money
                        from.balance -= amt;
                        from.addTransaction("Transfer to Savings", amt);
                        from.updateFlags();
                        acc.depositSavings(amt);
                        acc.update();
                        // Save both CSVs
                        CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                        // BUG FIX 2: sync updated checking balance back to appUser
                        appUser.checkingAccount = from.balance;
                        System.out.printf("  Moved $%.2f: checking %s ($%.2f) → savings ($%.2f)%n",
                                amt, from.accountID, from.balance, acc.getSavings());
                    } catch (NumberFormatException e) {
                        System.out.println("  Invalid amount.");
                    }
                }
                case "4" -> {
                    // Savings → Checking
                    if (checkingAccounts.isEmpty()) { System.out.println("  No checking accounts available."); break; }
                    CheckingAccount.Account to = pickAccount(sc, checkingAccounts);
                    if (to == null) break;
                    System.out.print("  Amount to transfer: $");
                    try {
                        double amt = Double.parseDouble(sc.nextLine().trim());
                        if (amt <= 0) { System.out.println("  Amount must be positive."); break; }
                        if (amt > acc.getSavings()) { System.out.println("  Insufficient funds in savings."); break; }
                        // Move the money
                        acc.withdrawSavings(amt);
                        acc.update();
                        to.balance += amt;
                        to.addTransaction("Transfer from Savings", amt);
                        to.updateFlags();
                        // Save both CSVs
                        CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                        // BUG FIX 2: sync updated checking balance back to appUser
                        appUser.checkingAccount = to.balance;
                        System.out.printf("  Moved $%.2f: savings ($%.2f) → checking %s ($%.2f)%n",
                                amt, acc.getSavings(), to.accountID, to.balance);
                    } catch (NumberFormatException e) {
                        System.out.println("  Invalid amount.");
                    }
                }
                case "5" -> acc.printTransactionHistory();
                case "0" -> running = false;
                default  -> System.out.println("  Invalid option.");
            }
        }

        // Sync final savings balance back to appUser so db.updateUser() in menu.java saves correctly
        appUser.savingsAccount = acc.getSavings();
    }
}