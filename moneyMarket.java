//Moneymarket is a premium version of MoneyMarket account.

import java.io.BufferedReader; //Random is used for the random ID generator
import java.io.BufferedWriter;
import java.io.IOException; //This helps us write data to the CSV file.
import java.nio.file.Files; //catch errors if anything silly happens.
import java.nio.file.Path; //to make it easier to access the files read and write functions. Our hasMoney is a static so we need static methods to make the code work. Files work hand to hand with path objects instead of using Strings we could use that which makes it platform independent.
import java.nio.file.StandardCopyOption; //Array list is needed when we don't know the size of an array or when we resize an array if you see MoneyIDexists I used array list to capture all the columns and use it to compare with the current MoneyMarket ID with the MoneyMarket ID in the current array list. Something like this psuedocode currentMoneyID = currentarraylistMoneyMarket.
import java.nio.file.StandardOpenOption;
import java.time.*; //This function is used to find the file you want for example I'm using this to find my MoneyMarket.csv
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit; //we don't want to overwrite when we create a MoneyMarket ID account we want to append.
import java.util.ArrayList; //is used to read line by line
import java.util.List;
import java.util.Random;
import java.util.Scanner;

//CURRENT WORK:  DriversLicense: almost done. 
public class moneyMarket {

    /**
     * * Variables **
     */
    private double balance = 0; // TODO this is our MoneyMarket balance which will start in a range if the customer first creates their account in about 100-300 $                                  
    private final double minBalanceFee = 15; // minimum balance fee is 15 dollars.
    private final double monthlyFee = 12; // TODO monthly fee is 12 dollars.
    private final double yearlyFee = 48; // TODO 48 dollars.
    private final double overlimitfee = 15;
    //private static csvFile file; // csvFile equals to the path of the CSV file.
    //private static csvFile FeeCheck; // MoneyMarketFee will equal to CSVpathFee
    private String userID; // current userID lets say they registered or they were recent the constructor we'll need userID as a verification method
    private String phoneNumber; // store a users phone number.
    private String driversLicense;
    private String birthcertificate;
    private String MoneyID; //MoneyMarket ID is a unique verification method to see if the user has a MoneyMarket account or not.
    private double currentwithdraw = 0;
    private boolean isLogged = false;
    private boolean isEmployee = false;
    private LocalDate datecreation;
    private boolean hasMoney;

    /* Static Final variables */
    private static final Path csvPath = Path.of("Money.csv"); // fine the path for the CSV file
    private static final Path csvPathFee = Path.of("MoneyFee.csv"); // measuring monthly
    private static final Path csvCustomerInfo = Path.of("customerInfo.csv");
    private static final Path csvEmployeecsv = Path.of("employeecards.csv");
    private static final Path csvEmployeeMoneyMarketcsv = Path.of("EmployeeMoneyMarket.csv");
    private final Path isEmployeeCSV = isEmployee ? csvEmployeeMoneyMarketcsv : csvPath;

    private static final long MAX = 599_999_999_999L; // This is the maximum for the MoneyMarket number generator 3000000000000 is MoneyMarket Account UNIQUE ID this is only for MoneyMarket.

    private static final long MIN = 500_000_000_000L; // minimum for the random number generator
    private static final double minimumbalance = 2500;
    private static final double maximumbalance = 5000;
    private static final double MAXWITHDRAW = 6;
    private final double interestamount = 0.005; //interest is 5% 

    //early closure account variables:
    private static final int MIN_DAY = 0;
    private static final int MAX_DAY = 180;
    private static final double MAX_FEE_LIMIT = 50;
    private static final double MIN_FEE = 5;

    //COLUMNS Money.csv AND EployeeMoneyMarket.csv
    private static int COL_USERID = 0;
    private static int COL_MONEYMARKETID = 1;
    private static int COL_BALANCE = 2;
    private static int COL_DRIVERSLICENSE = 3;
    private static int COL_DATECREATED = 4;

    private final List<String> transactionHistory = new ArrayList<>();

    private void logTransaction(String type, double amount) {
        String entry = String.format(
                "%s | %s | $%.2f | bal=%.2f",
                LocalDate.now(),
                type,
                amount,
                balance
        );
        transactionHistory.add(entry);
    }


    // Instant now = Instant.now(); example of how to use Instance the
    // variable(object) equals to the current time only once meaning time is moving
    // while the variable is only equal to a non incrementing time this is useful
    // for when we want to start initiating fee 24 hour charge.
    // ZonedDateTime nowEastern = ZonedDateTime.now(EASTERN_ZONE);
    // TODO long number = MIN + (long)(rand.nextDouble() * (MAX - MIN + 1));
    // try catch exceptions if the csvfile fails to load
    /*static {
       try {
           file = new csvFile(csvPath);
           FeeCheck = new csvFile(csvPathFee);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }*/
    public moneyMarket() // when creating
    {
        balance = minimumbalance; // make MoneyMarket start at 100 to start with a default starting value.
    }

    public moneyMarket(String userID, double MoneyMarketamount) // Work in progress.
    {
        this.userID = userID;
        balance = MoneyMarketamount;
    }

    public double readSafeDouble(String[] data, int col, double fallback) {
        try {
            return (data.length > col && !data[col].isEmpty())
                    ? Double.parseDouble(data[col])
                    : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    public int readSafeInt(String[] data, int col, int fallback) {
        try {
            return (data.length > col && !data[col].isEmpty())
                    ? Integer.parseInt(data[col])
                    : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    public boolean readSafeBoolean(String[] data, int col, Boolean fallback) {
        try {
            return (data.length > col && !data[col].isEmpty())
                    ? Boolean.parseBoolean(data[col])
                    : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    public String readSafeString(String[] data, int col, String fallback) {
        try {
            return (data.length > col && !data[col].isEmpty())
                    ? data[col]
                    : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    public LocalDate readSafeLocalDate(String[] data, int col, LocalDate fallback) {
        if (data.length <= col || data[col] == null || data[col].trim().isEmpty()) {
            return fallback;
        }

        try {
            return LocalDate.parse(data[col]);
        } catch (Exception e) {
            return fallback;
        }
    }

    public static boolean userIDExists(String userID) throws IOException { //hasMoney or user ID //lets use this as a chance to create a boolean employee.
        Path csvEmployeeornot = isEmployee(userID) ? csvEmployeeMoneyMarketcsv : csvPath;

        try (BufferedReader reader = Files.newBufferedReader(csvEmployeeornot)) {
            reader.readLine(); //skip the header
            String line; //String line not initialized yet
            while ((line = reader.readLine()) != null) {
                String[] columnsplit = line.split(",", -1); //split line into 3 columns instead of one huge string because we don't want that.
                if (columnsplit.length > 0 && columnsplit[0].trim().equals(userID.trim())) { //trim is useful for comparing data when white space exists what it does is removes those white spaces.
                    return true; //return true because userID exists
                }
            }
        }
        return false;
    }

    public static boolean MoneyMarketIDExistsfeefile(String MoneyID) throws IOException { //USE MONEY ID no userid because user chooses the moneyid during creation or load and all moneyid are unique meaning the reason we just used moneyid because either it is loaded or during id creation
        try (BufferedReader reader = Files.newBufferedReader(csvPathFee)) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columnsplit = line.split(",", -1);
                if (columnsplit.length == 0) {
                    continue; // skip malformed lines

                }if (columnsplit[0].trim().equals(MoneyID.trim())) {
                    return true; // moneyID found
                }
            }
        }
        return false; // not found
    }

    public static void writeMoneyCSV(String userID, String MoneyID, double newbalance, boolean isEmployee) throws IOException //make an employee or moneymarket system.
    {
        Path isEmployeeCSV = isEmployee ? csvEmployeeMoneyMarketcsv : csvPath;
        Path temp = Files.createTempFile("temp", ".csv");
        try (BufferedReader read = Files.newBufferedReader(isEmployeeCSV); BufferedWriter writetemp = Files.newBufferedWriter(temp)) {
            String line;
            while ((line = read.readLine()) != null) {
                String[] datacur = line.split(",", -1);
                if (datacur[0].trim().equals(userID) && datacur[1].trim().equals(MoneyID)) {

                    datacur[2] = String.valueOf(newbalance);
                    writetemp.write(String.join(",", datacur));
                } else {
                    writetemp.write(line);
                }
                writetemp.newLine();
            }
        }
        Files.move(temp, isEmployeeCSV, StandardCopyOption.REPLACE_EXISTING);
    }
    //under here is a customerinfo read for last name in customer info.

    public static char ReadCustomerinfolastnamechar(int column, String userID) throws IOException {
        try (BufferedReader read = Files.newBufferedReader(csvCustomerInfo)) {
            String line;
            while ((line = read.readLine()) != null) {
                String[] datacur = line.split(",", -1);
                if (datacur[0].trim().equals(userID)) { //column 0 which is userID
                    /*for(String Value:datacur){
                    System.out.println(Value + ", ");
                    }*/
                    char ch = datacur[column].charAt(0);
                    return ch;
                }
            }
        }
        return '\0';
    }

    public static String ReadCustomerinfo(int column, String userID) throws IOException {
        try (BufferedReader read = Files.newBufferedReader(csvCustomerInfo)) {
            String line;
            while ((line = read.readLine()) != null) {
                String[] datacur = line.split(",", -1);
                if (datacur[0].trim().equals(userID)) { //column 0 which is userID
                    /*for(String Value:datacur){
                    System.out.println(Value + ", ");
                    }*/
                    if (column < 0 || column > datacur.length) {
                        return null;
                    }
                    return datacur[column].trim();
                }
            }
        }
        return null;
    }

    public static boolean isEmployee(String EmployeeID) throws IOException { //This method checks if there is the userID in employee.csv if that appears there then this is an employee MoneyMarket account.
        try (BufferedReader reader = Files.newBufferedReader(csvEmployeecsv)) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                ArrayList<String> data = csvParsing.parseLine(line);
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (!data.isEmpty() && data.get(COL_USERID).equals(EmployeeID)) { //read column 0 and lines that match with the employeeID
                    return true;
                }
            }
        }
        return false; // not found
    }

    /*unused code   public static boolean isValiduserID(String userID){
    if(userID == null || userID.isEmpty())
    {
        return false;
    }
    for(char c : userID.toCharArray())
    {

        if(!Character.isDigit(c))
        {
            return false;
        }
       
    }
    return true;
   }
     */
    public static moneyMarket createmoneyMarket(String userID, double MoneyMarketamount) throws IOException {//

        if (MoneyMarketamount <= 0 || userID == null || userID.isEmpty()) {
            System.out.println("Error: Type a proper amount.");
            return null;
        }

        if (birthCertificate(userID)) {
            System.out.println("Birth certificate verified successfully.");
        } else {
            System.out.println("Birth certificate verification failed. Account creation cancelled.");
            return null;
        }

        boolean isEmployee_t = isEmployee(userID);
        String MoneyID = RandomIDGenerator(isEmployee_t);
        String phoneNum = getPhonenumber(userID);
        LocalDate today = LocalDate.now(); //date creation for the account.
        String date = today.toString();
        String socialSecurity = getSocialSecurity(userID);
        String BC = RandomIDBirthCertificate(userID);
        String DL = RandomIDGeneratorDriversLicense(userID, isEmployee_t); //DL is drivers license by the way.
        if ((phoneNum == null || socialSecurity == null) || (phoneNum.isEmpty() || socialSecurity.isEmpty())) {
            System.out.println("Error: Either your phone number or social security number is corrupted or doesn't exist.");
            return null;
        }
        moneyMarket account = null; //added null so nothing bad can happen such as unitialization.
        if (MoneyMarketamount >= minimumbalance && MoneyMarketamount <= maximumbalance) {
            account = new moneyMarket();
            account.userID = userID;
            account.setMoneyID(MoneyID);
            account.isEmployee = isEmployee_t;
            account.phoneNumber = phoneNum;
            account.driversLicense = DL;
            account.birthcertificate = BC;
            account.hasMoney = true;
        } else if (MoneyMarketamount <= maximumbalance && MoneyMarketamount > minimumbalance) {
            account = new moneyMarket(userID, MoneyMarketamount);
            account.setMoneyID(MoneyID);
            account.phoneNumber = phoneNum;
            account.driversLicense = DL;
            account.birthcertificate = BC;
            account.hasMoney = true;
            account.isEmployee = isEmployee_t;
        } else {

            System.out.println("The MoneyMarket amount has to be in the range of 2500-5000");
            return null; //print back that the amount isn't correct and return null to stop the account creation.

        }
        Path currentCSV = isEmployee(userID) ? csvEmployeeMoneyMarketcsv : csvPath;
        try (BufferedWriter bw = Files.newBufferedWriter(currentCSV, StandardOpenOption.APPEND)) {

            bw.write(String.join(",",
                    account.userID,
                    account.MoneyID,
                    String.valueOf(account.balance),
                    account.driversLicense,
                    date,
                    account.birthcertificate,
                    "ACTIVE"
            ));
            bw.newLine(); // make a new line when written.
        }
        writefeeUser(account.MoneyID);
        return account;
    }

    public moneyMarket closeMoneyMarket() throws IOException {

        if (!userIDExists(userID)) {
            System.out.println("Account doesn't exist.");
            return null;
        }

        String result = updateNegativeBalance();

        if (result.equals("CLOSED")) {
            removeMoneyMarket(this.userID, this.MoneyID, this.isEmployee);
            System.out.println("Account already closed due to debt violation.");
            return null;
        }

        if (result.equals("NEGATIVE")) {
            System.out.println("Cannot close: outstanding debt not cleared.");
            return this;
        }

        if (result.equals("ACTIVE")) {

            double fee = getEarlyClosureFee();

            // NO FEE CASE → auto close
            if (fee == 0) {
                removeMoneyMarket(this.userID, this.MoneyID, this.isEmployee);
                System.out.println("Account closed successfully (no fee).");
                return null;
            }

            // FEE CASE → ask user
            System.out.println("Early closure fee: $" + fee);
            System.out.println("Pay fee to close account? (yes/no)");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("no")) {
                return this;
            }

            if (!input.equalsIgnoreCase("yes")) {
                System.out.println("Invalid input.");
                return this;
            }

            if (!earlyClosureMoneyMarketAccount()) {
                return this;
            }

            removeMoneyMarket(this.userID, this.MoneyID, this.isEmployee);
            System.out.println("Account closed successfully.");
            return null;
        }

        return this;
    }

    public double getEarlyClosureFee() {
        LocalDate today = LocalDate.now();
        long daysOpen = ChronoUnit.DAYS.between(getDateCreated(), today);
        return earlyClosureCalculator(daysOpen);
    }

    public static boolean removeMoneyMarket(String userID, String moneyID, boolean isEmployee) throws IOException { //DO NOT use this this is used for closed money as a helper
        boolean success = false;
        boolean successfee = false;
        Path temp = Files.createTempFile("temp", ".csv");
        Path csvPick = isEmployee ? csvEmployeeMoneyMarketcsv : csvPath;
        try (BufferedReader reader = Files.newBufferedReader(csvPick); BufferedWriter writer = Files.newBufferedWriter(temp)) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data[0].equals(userID) && data[1].equals(moneyID)) {
                    success = true;
                    System.out.println("user delete");
                    continue;
                }

                writer.write(line);
                writer.newLine();
            }
        }
        Files.move(temp, csvPick, StandardCopyOption.REPLACE_EXISTING);
        Path tempFee = Files.createTempFile("tempfee", ".csv");

        try (BufferedReader reader = Files.newBufferedReader(csvPathFee); BufferedWriter writer = Files.newBufferedWriter(tempFee)) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data[0].equals(moneyID)) {
                    successfee = true;
                    System.out.println("Deleting: " + line);
                    continue;
                }

                writer.write(line);
                writer.newLine();
            }
        }
        Files.move(tempFee, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return success;
    }

    public boolean earlyClosureMoneyMarketAccount() throws IOException {
        Scanner scanner = new Scanner(System.in);
        LocalDate today = LocalDate.now();
        long daysOpen = ChronoUnit.DAYS.between(getDateCreated(), today);

        try {
            double fee = earlyClosureCalculator(daysOpen);

            double balance = getMoneyMarket();

            if (balance < fee) {
                System.out.println("Insufficient funds to cover early closure fee.");
                return false;
            }

            setMoneyMarket(balance - fee);

            System.out.printf("Early closure fee applied: $%.2f%n", fee);
            System.out.printf("Remaining balance: $%.2f%n", getMoneyMarket());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    private static double earlyClosureCalculator(long days) {
        if (days < 0) {
            throw new IllegalArgumentException("Days cannot be negative");
        }

        double fee;

        if (days <= MAX_DAY / 2) {
            fee = 50.0;
        } else if (days <= MAX_DAY) {
            fee = 20.0;
        } else {
            fee = 0.0;
        }

        if (fee > 0) {
            fee = Math.max(MIN_FEE, Math.min(fee, MAX_FEE_LIMIT));
        }

        return fee;
    }

    public static List<moneyMarket> OpenmoneyMarket(String userID) throws IOException { //use static list to open MULTIPLE accounts during run time lets say if userid has more than 6 accounts 
        if (userIDExists(userID)) //c
        {
            List<moneyMarket> acc = new ArrayList<>();
            Path csvToUse = isEmployee(userID) ? csvEmployeeMoneyMarketcsv : csvPath;

            try (BufferedReader readlines = Files.newBufferedReader(csvToUse)) {
                readlines.readLine();
                String currentline;
                while ((currentline = readlines.readLine()) != null) {
                    String[] currentdata = currentline.split(",", -1);
                    if (currentdata[0].trim().equals(userID)) {

                        moneyMarket account = new moneyMarket();
                        account.userID = userID;
                        //that uses currentdata
                        account.MoneyID = currentdata[1];
                        account.balance = Double.parseDouble(currentdata[COL_BALANCE]);
                        account.driversLicense = currentdata[COL_DRIVERSLICENSE];
                        account.datecreation = LocalDate.parse(currentdata[COL_DATECREATED]);
                        //logic aspect
                        account.isLogged = true;
                        account.isEmployee = isEmployee(userID);
                        //account.update();
                        acc.add(account);
                    }

                }
            }

            System.out.println("You're logged in");
            return acc;
        }

        System.out.println("Account doesn't exist, create it.");

        return null;
    }

    public static boolean DriversLicenseExists(String DriversID, boolean isEmployee) throws IOException { //Generate a unique drivers ID. No same ID distributed.
        Path choiceCSV;
        if (isEmployee) {
            choiceCSV = csvEmployeeMoneyMarketcsv;
        } else {
            choiceCSV = csvPath;
        }

        try (BufferedReader reader = Files.newBufferedReader(choiceCSV)) {
            reader.readLine(); // this line skips the header for example (userID,MoneyID,MoneyMarket,DriversLicense)
            String line;
            while ((line = reader.readLine()) != null) { // as line doesn't equal to NULL (end of file) continue.
                String[] currentdata_to_col = line.split(",", -1);
                if (currentdata_to_col.length > 3 && currentdata_to_col[3].equals(DriversID)) {
                    return true;
                }
            }
        }
        return false; // return false if there is no MoneyMarket ID equal to another MoneyMarket ID
    }

    public String updateNegativeBalance() throws IOException {

        LocalDate today = LocalDate.now();
        String finalStatus = null;

        Path temp = Files.createTempFile("temp", ".csv");

        Path isEmployeecsv = isEmployee ? csvEmployeeMoneyMarketcsv : csvPath;

        try (BufferedReader reader = Files.newBufferedReader(isEmployeecsv); BufferedWriter writer = Files.newBufferedWriter(temp)) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (!data[0].equals(userID)) {
                    writer.write(line);
                    writer.newLine();
                    continue;
                }

                String status = readSafeString(data, 6, "ACTIVE");
                String negativeTimestamp = readSafeString(data, 7, "");
                // TERMINAL STATE: do nothing
                if (status.equals("CLOSED")) {
                    writer.write(line);
                    writer.newLine();
                    finalStatus = "CLOSED";
                    continue;
                }

                // START negative tracking
                if (balance < 0 && negativeTimestamp.isEmpty()) {
                    negativeTimestamp = today.toString();
                    status = "NEGATIVE";
                }

                // STILL NEGATIVE
                if (balance < 0 && !negativeTimestamp.isEmpty()) {

                    LocalDate start = LocalDate.parse(negativeTimestamp);
                    long days = ChronoUnit.DAYS.between(start, today);

                    if (days >= 30) {
                        status = "NEGATIVE";
                    }
                }

                // RECOVERY
                if (balance >= 0 && !negativeTimestamp.isEmpty()) {

                    LocalDate start = LocalDate.parse(negativeTimestamp);
                    long days = ChronoUnit.DAYS.between(start, today);

                    if (days >= 30) {
                        status = "CLOSED";
                    } else {
                        status = "ACTIVE";
                        negativeTimestamp = "";
                    }

                }

                finalStatus = status;

                writer.write(String.join(",",
                        data[0],
                        data[1],
                        String.format("%.2f", balance),
                        data[3],
                        data[4],
                        data[5],
                        status,
                        negativeTimestamp
                ));

                writer.newLine();
            }
        }

        Files.move(temp, isEmployeecsv, StandardCopyOption.REPLACE_EXISTING);

        return finalStatus;
    }

    public static boolean birthCertificateExists(String BCID, boolean isEmployee) throws IOException { //Generate a unique birth certificate ID. No same ID distributed.
        Path Csvchoice;
        if (isEmployee) {
            Csvchoice = csvEmployeeMoneyMarketcsv;
        } else {
            Csvchoice = csvPath;
        }

        try (BufferedReader reader = Files.newBufferedReader(Csvchoice)) {
            reader.readLine(); // this line skips the header for example (userID,MoneyID,MoneyMarket,DriversLicense)
            String line;
            while ((line = reader.readLine()) != null) { // as line doesn't equal to NULL (end of file) continue.
                String[] currentdata_to_col = line.split(",", -1);
                if (currentdata_to_col.length > 5 && currentdata_to_col[5].equals(BCID)) {
                    return true;
                }
            }
        }
        return false; // return false if there is no MoneyMarket ID equal to another MoneyMarket ID
    }

    public static String RandomIDGeneratorDriversLicense(String userID, boolean isEmployee) throws IOException {
        Random rand = new Random();
        char firstchar = ReadCustomerinfolastnamechar(2, userID);
        String DriversID;
        do {
            if (firstchar == '\0') {
                return null;
            }
            firstchar = Character.toUpperCase(firstchar);
            StringBuilder string = new StringBuilder();
            string.append(firstchar);

            for (int i = 0; i < 14; i++) {
                string.append(rand.nextInt(10));
            }
            DriversID = string.toString();

        } while (DriversLicenseExists(DriversID, isEmployee));
        return DriversID;
    }

    public static String RandomIDBirthCertificate(String userID) throws IOException {
        Random rand = new Random();
        boolean isEmployee_t = isEmployee(userID);
        String BC = "BC-";
        String birthCertificate;
        do {
            StringBuilder string = new StringBuilder();
            string.append(BC);

            for (int i = 0; i < 14; i++) {
                string.append(rand.nextInt(10));
            }
            birthCertificate = string.toString();

        } while (birthCertificateExists(birthCertificate, isEmployee_t));
        return birthCertificate;
    }

    public static boolean birthCertificate(String userID) throws IOException {

        int COL_FIRSTNAME = 1;
        int COL_LASTNAME = 2;
        int COL_DOB = 4;

        String firstNameFromCSV = ReadCustomerinfo(COL_FIRSTNAME, userID);
        String lastNameFromCSV = ReadCustomerinfo(COL_LASTNAME, userID);
        String DOBfromcsv = ReadCustomerinfo(COL_DOB, userID);

        String firstName;
        String lastName;
        Scanner scanner = new Scanner(System.in);
        //FIRST NAME
        while (true) {
            System.out.print("Enter first name: ");
            firstName = scanner.nextLine();

            if (firstName.equalsIgnoreCase("exit")) {
                System.out.println("Exiting birth certificate verification.");
                return false;
            }

            if (firstNameFromCSV != null
                    && firstName.equalsIgnoreCase(firstNameFromCSV)) {
                break;
            }

            System.out.println("This is not your first name. Please try again.");
        }

        //LAST NAME
        while (true) {
            System.out.print("Enter last name: ");
            lastName = scanner.nextLine();

            if (lastName.equalsIgnoreCase("exit")) {
                System.out.println("Exiting birth certificate verification.");
                return false;
            }

            if (lastNameFromCSV != null
                    && lastName.equalsIgnoreCase(lastNameFromCSV)) {
                break;
            }

            System.out.println("Invalid last name. Please try again.");
        }

        //DOB FROM CSV
        LocalDate expectedDob = null;

        if (DOBfromcsv != null && !DOBfromcsv.isEmpty()) {
            DateTimeFormatter csvFormatter
                    = DateTimeFormatter.ofPattern("[M/d/yyyy][d/M/yyyy][yyyy-MM-dd]");

            expectedDob = LocalDate.parse(DOBfromcsv, csvFormatter);
        }

        //INPUT DOB
        while (true) {

            System.out.print("Enter date of birth (yyyy-MM-dd, dd-MM-yyyy, MM/dd/yyyy) or type 'exit': ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting birth certificate verification.");
                return false;
            }

            LocalDate dob = null;

            DateTimeFormatter[] formats = new DateTimeFormatter[]{
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                    DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                    DateTimeFormatter.ofPattern("MM/dd/yyyy")
            };

            for (DateTimeFormatter f : formats) {
                try {
                    dob = LocalDate.parse(input, f);
                    break;
                } catch (Exception ignored) {
                }
            }

            if (dob == null) {
                System.out.println("Invalid date format. Try again.");
                continue;
            }

            if (dob.isAfter(LocalDate.now())) {
                System.out.println("Date cannot be in the future.");
                continue;
            }

            if (expectedDob != null && !dob.equals(expectedDob)) {
                System.out.println("Date of birth does not match our records. Please try again.");
                continue;
            }

            break;
        }

        return true;
    }

    public static moneyMarket pickAccount(List<moneyMarket> accounts) throws IOException {
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("There is no accounts here. |Money Market");
            return null;
        }

        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("Select an account:");
            for (int i = 0; i < accounts.size(); i++) {
                moneyMarket account = accounts.get(i);
                System.out.printf("%d: Account %d | Balance=$%.2f%n", i, i, account.getMoneyMarket());
            }

            System.out.print("Enter the account number (or type 'q' to cancel): ");
            String line = input.nextLine().trim();

            if (line.equalsIgnoreCase("q") || line.equalsIgnoreCase("exit")) {
                System.out.println("Account selection cancelled.");
                return null;
            }

            int choice;
            try {
                choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number or 'q' to cancel.");
                continue;
            }

            if (choice >= 0 && choice < accounts.size()) {
                return accounts.get(choice);
            }

            System.out.println("Invalid account number. Try again.");
        }
    }

    public static String RandomIDGenerator(boolean isEmployee) throws IOException // make the randomIDGenerator a static so it doesn't belong to an object but an standard ID generator for MoneyMarket ids
    {
        Random rand = new Random(); // rand can generate random numbers
        String ID; // make ID String so we can easily manipulate it in CSV like reading or writing
        // it.
        do {
            long number = (MIN) + (long) (rand.nextDouble() * (MAX - MIN + 1)); //the random generator that is in the range of min-max
            ID = String.valueOf(number); // convert number to String so ID can equal to that string.
        } while (MoneyIDExists(ID, isEmployee)); // Check if there is any MoneyMarket ID like it in the CSV file.
        return ID;
    }

    public static boolean MoneyIDExists(String MoneyID, boolean isEmployee) throws IOException { //MoneyID is used in the random generator so it wouldn't generate the same MoneyMarket ID as another persons MoneyMarket ID.
        Path isEmployee_t = isEmployee ? csvEmployeeMoneyMarketcsv : csvPath;
        try (BufferedReader reader = Files.newBufferedReader(isEmployee_t)) {
            reader.readLine(); // this line skips the header for example (userID,SavingsID,savings)
            String line;
            while ((line = reader.readLine()) != null) { // as line doesn't equal to NULL (end of file) continue.
                String[] currentdata_to_col = line.split(",", -1);
                if (currentdata_to_col.length > 1 && currentdata_to_col[1].equals(MoneyID)) {
                    return true;
                }
            }
        }
        return false; // return false if there is no savings ID equal to another savings ID
    }

    //START Both getMoneyMarket() and setMoneyMarket() are used for debugging.
    public double getMoneyMarket() //get balance when using saving account objects.
    {
        return balance;
    }

    public void setMoneyMarket(double MoneyMarket) //setting the balance by the way.
    {//field to the parameter MoneyMarket.
        balance = MoneyMarket;
    }

    public static String getPhonenumber(String userID) throws IOException { // Grab phone number by searching through CSV
        try (BufferedReader readfile = Files.newBufferedReader(csvCustomerInfo)) {
            String line;

            while ((line = readfile.readLine()) != null) {
                boolean valid = true;
                String[] dataline = line.split(",", -1);
                if (dataline.length > 0 && dataline[0].trim().equals(userID)) {
                    String phonenum = dataline.length > 6 ? dataline[6] : "";
                    phonenum = phonenum.replaceAll("[^0-9]", "");
                    if (phonenum.length() == 10) {

                        for (int i = 0; i < phonenum.length(); i++) {
                            if (!Character.isDigit(phonenum.charAt(i))) {
                                valid = false;
                                break;
                            }
                        }
                        if (valid) {
                            return phonenum;
                        }
                    }
                }
            }
        }
        return null;

    }

    //END
    public void setMoneyID(String MoneyID) {
        this.MoneyID = MoneyID;
    }

    public static String getSocialSecurity(String userID) throws IOException {
        try (BufferedReader readfile = Files.newBufferedReader(csvCustomerInfo)) {

            String line;
            while ((line = readfile.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data.length > 3 && data[0].trim().equals(userID)) {

                    String ssn = data[3].trim();

                    // normalize 123-45-6789 → 123456789
                    ssn = ssn.replaceAll("[^0-9]", "");

                    if (ssn.length() == 9) {
                        return ssn;
                    }
                }
            }
        }
        return null;
    }

    public double depositMoneyMarket(double depositamt) throws IOException {
        if (depositamt > 0) {
            balance += depositamt;
            writeMoneyCSV(userID, MoneyID, balance, isEmployee);
            logTransaction("DEPOSIT", depositamt);
            return balance;
        } else {
            System.out.println("Deposit must be positive.");
        }
        return balance;
    }

    public String getuserID() { //testing to see userID string
        return userID;
    }

    public String getMoneyID() {
        return MoneyID;
    }

    public LocalDate getDateCreated() {
        return datecreation;
    }

    public static double getBalance(String userID) throws IOException { //Use this when you don't want to create an account and just want to know the balance from the CSV
        List<moneyMarket> accountlist = OpenmoneyMarket(userID);
        if (accountlist == null || accountlist.isEmpty()) {
            System.out.println("No accounts found for this user.");
            return 0;
        }

        if (accountlist.size() == 1) {
            return accountlist.get(0).getMoneyMarket();
        }

        moneyMarket account = pickAccount(accountlist);
        if (account == null) {
            System.out.println("No account selected.");
            return 0;
        }

        return account.getMoneyMarket();
    }

    public static boolean writefeeUser(String MoneyID) throws IOException {
        Path temp = Files.createTempFile("temp", ".csv");

        LocalDate today = LocalDate.now();
        boolean found = false;

        try (BufferedReader reader = Files.newBufferedReader(csvPathFee); BufferedWriter writer = Files.newBufferedWriter(temp)) {

            String header = reader.readLine();
            if (header != null) {
                writer.write(header);
                writer.newLine();
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data.length > 0 && data[0].equals(MoneyID)) {
                    found = true;
                    writer.newLine();
                    continue;
                }

                writer.write(line);
                writer.newLine();
            }

            // If account not found → initialize tracking row
            if (!found) {
                writer.write(String.join(",",
                        MoneyID,
                        today.toString(),
                        today.toString(),
                        today.toString(),
                        today.toString(),
                        Double.toString(0),
                        today.toString()
                ));
                writer.newLine();
            }
        }

        Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return !found;
    }

    // ************Withdraw system**************/
    public boolean withdrawMoneyMarket(double amt) throws IOException { //use a working variable so our data wouldn't be corrupted.

        LocalDate today = LocalDate.now();
        Path temp = Files.createTempFile("tempmoney", ".csv");
        boolean found = false;
        boolean success = false;
        try (BufferedReader read = Files.newBufferedReader(csvPathFee); BufferedWriter writer = Files.newBufferedWriter(temp)) {

            String line;

            while ((line = read.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data[0].trim().equals(MoneyID)) {
                    found = true;


                    int withdrawCount = readSafeInt(data, 5, 0);
                    LocalDate lastReset = readSafeLocalDate(data, 6, today);
                    double workingBalance = balance;
                    System.out.println(withdrawCount);
                    int newWithdrawCount = withdrawCount;
                    LocalDate newReset = lastReset;

                    long months = ChronoUnit.MONTHS.between(newReset, today);

                    if (months > 0) {
                        newWithdrawCount = 0;
                        newReset = newReset.plusMonths(months);
                    }

                    if (amt <= 0) {
                        System.out.println("Amount must be > 0");
                    } else if (amt > workingBalance) {
                        System.out.println("Insufficient balance");
                    } else {

                        if (newWithdrawCount >= MAXWITHDRAW) {

                            double totalCost = amt + overlimitfee;

                            if (totalCost <= workingBalance) {
                                workingBalance -= totalCost;
                                System.out.println("You've paid the withdraw limit fee which is 15 dollars.");
                                logTransaction("WITHDRAW", totalCost);
                                newWithdrawCount++;
                                success = true;

                            } else {
                                System.out.println("Not enough for fee + withdrawal. Please earn more cash.");

                            }

                        } else {
                            workingBalance -= amt;
                            newWithdrawCount++;
                            logTransaction("WITHDRAW", amt);
                            success = true;
                        }

                    }

                    if (success) {
                        balance = workingBalance;
                        withdrawCount = newWithdrawCount;
                        lastReset = newReset;
                        writeMoneyCSV(userID, MoneyID, balance, isEmployee);
                        writer.write(String.join(",",
                                MoneyID,
                                data[1],
                                data[2],
                                data[3],
                                data[4],
                                String.valueOf(newWithdrawCount),
                                lastReset.toString()
                        ));
                    } else {
                        writer.write(line);
                    }

                    writer.newLine();
                    continue;
                }

                writer.write(line);
                writer.newLine();
            }
        }

        Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return success;
    }



    public double transfer(CheckingAccount.Account fromAccount, Scanner scanner, boolean fromsource, double externalValue) throws IOException {

        while (true) {

            System.out.print("  Amount to transfer: $");

            if (!scanner.hasNextDouble()) {
                System.out.println("Enter a proper number.");
                scanner.next();
                continue;
            }

            double amount = scanner.nextDouble();
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("No negative amounts or 0.");
                continue;
            }

            if (fromsource && fromAccount != null) {

                if (!fromAccount.isActive) {
                    System.out.println("Checking Account is inactive.");
                    return externalValue;
                }

                if (amount > fromAccount.balance) {
                    System.out.println("Insufficient funds in checking");
                    continue;
                }

                fromAccount.balance -= amount;
                balance += amount;
                writeMoneyCSV(userID, MoneyID, balance, isEmployee);
                logTransaction("TRANSFER IN", amount);
                fromAccount.addTransaction("Transfer out for Savings", amount);
                fromAccount.updateFlags();

                System.out.printf("  Moved $%.2f: Money Market ($%.2f) <- checking %s ($%.2f)%n",
                        amount, getMoneyMarket(), fromAccount.accountID, fromAccount.balance);

            } else if (fromsource && fromAccount == null) {

                if (amount > externalValue) {
                    System.out.println("Insufficient external funds.");
                    continue;
                }

                externalValue -= amount;
                balance += amount;
                logTransaction("TRANSFER IN", amount);
                writeMoneyCSV(userID, MoneyID, balance, isEmployee);
                System.out.printf("  Moved $%.2f: Money Market($%.2f) → Source: ($%.2f)%n",
                        amount, balance, externalValue);
            } else if (fromAccount != null) {

                if (!fromAccount.isActive) {
                    System.out.println("Account is inactive.");
                    return externalValue;
                }

                if (amount > balance) {
                    System.out.println("Insufficient funds.");
                    continue;
                }

                balance -= amount;
                fromAccount.balance += amount;
                writeMoneyCSV(userID, MoneyID, balance, isEmployee);
                fromAccount.addTransaction("Transfer in from Savings", amount);
                logTransaction("TRANSFER OUT", amount);
                fromAccount.updateFlags();

                System.out.printf("  Moved $%.2f: Money Market ($%.2f) -> checking %s ($%.2f)%n",
                        amount, getMoneyMarket(), fromAccount.accountID, fromAccount.balance);

            } //from SAVINGS to External value.
            else {

                if (amount > balance) {
                    System.out.println("Insufficient funds.");
                    continue;
                }

                balance -= amount;
                externalValue += amount;
                logTransaction("TRANSFER OUT", amount);
                System.out.printf("  Moved $%.2f: Money Market ($%.2f) <- Source: ($%.2f)%n",
                        amount, balance, externalValue);

            }

            break;
        }

        return externalValue;
    }

    public void update() throws IOException {
        yearlyFee();
        monthlyFee();
        minBalanceFee();
        applyInterest();
        String state = updateNegativeBalance();
        if (state.equalsIgnoreCase("CLOSED")) {
            closeMoneyMarket();
        }
        writeMoneyCSV(getuserID(), getMoneyID(), getMoneyMarket(), isEmployee); //moneymarket is balance
    }
// ************ FEES AND INTEREST METHODS ************
// This is the framework for all the fees such as monthly fee, minimum balance fee, and yearly fee.
// They'll be used in one method called updateFees
// MoneyID, LastMinMonth, LastMonthlyMonth, LowestBalance, LastYear, LastInterest

    public double minBalanceFee() throws IOException { //Min month goal is to write in the csv a starting month for example "2026-03" if todays month is 2026-04 it'll check the lowest balance currently in
        LocalDate today = LocalDate.now();
        boolean found = false;
        Path temp = Files.createTempFile("temp", ".csv");

        try (BufferedReader reader = Files.newBufferedReader(csvPathFee); BufferedWriter writer = Files.newBufferedWriter(temp)) {
            String header = reader.readLine();
            if (header != null) {
                writer.write(header);
                writer.newLine();
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data[0].equals(MoneyID)) {
                    found = true;
                    // Retrieve previous CSV data safely
                    LocalDate lastMinMonth = readSafeLocalDate(data, 1, today);

                    //check if anything isn't null and check if lastminmonth isn't empty and make lastminmonth not equal to current month otherwise skip this.
                    long months = ChronoUnit.MONTHS.between(lastMinMonth, today);

                    if (months > 0) {

                        if (balance < minimumbalance) {
                            balance -= minBalanceFee * months;
                        }

                        lastMinMonth = lastMinMonth.plusMonths(months);
                    }

                    //write updated line
                    writer.write(String.join(",",
                            MoneyID,
                            lastMinMonth.toString(),
                            data[2],
                            data[3],
                            data[4],
                            data[5],
                            data[6]
                    ));
                    writer.newLine();
                    continue;
                }

                //write the otherlines
                writer.write(line);
                writer.newLine();
            }
        }

        //replace old CSV with updated temp file.
        Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return balance;
    }

    public double monthlyFee() throws IOException {
        LocalDate today = LocalDate.now();
        boolean found = false;
        Path tempfile = Files.createTempFile("csv_temp", ".csv");

        try (BufferedReader reader = Files.newBufferedReader(csvPathFee); BufferedWriter writer = Files.newBufferedWriter(tempfile)) {
            String header = reader.readLine();
            if (header != null) {
                writer.write(header);
                writer.newLine();
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data[0].equals(MoneyID)) {
                    found = true;
                    // Retrieve previous CSV data safely
                    LocalDate lastMonthlyDate = readSafeLocalDate(data, 2, today);

                    long months = ChronoUnit.MONTHS.between(lastMonthlyDate, today);

                    if (months > 0) {
                        balance -= monthlyFee * months;
                        lastMonthlyDate = lastMonthlyDate.plusMonths(months);
                    }

                    //write updated line
                    writer.write(String.join(",",
                            MoneyID,
                            data[1],
                            lastMonthlyDate.toString(),
                            data[3],
                            data[4],
                            data[5],
                            data[6]
                    ));
                    writer.newLine();
                    continue;
                }

                //write the otherlines
                writer.write(line);
                writer.newLine();

            }
        }
        Files.move(tempfile, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return balance;
    }

    public double yearlyFee() throws IOException { //every year passing the user gets a fee. //DONE

        LocalDate today = LocalDate.now();
        boolean found = false;
        Path temp = Files.createTempFile("temp", ".csv");

        try (BufferedReader reader = Files.newBufferedReader(csvPathFee); BufferedWriter writer = Files.newBufferedWriter(temp)) {
            String header = reader.readLine();
            if (header != null) {
                writer.write(header);
                writer.newLine();
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data[0].equals(MoneyID)) {
                    found = true;
                    LocalDate lastYear = readSafeLocalDate(data, 3, today);

                    long yearspassed = ChronoUnit.YEARS.between(lastYear, today);
                    if (yearspassed > 0) {
                        balance -= (yearspassed * yearlyFee);
                        lastYear = lastYear.plusYears(yearspassed);
                    }

                    writer.write(String.join(",",
                            MoneyID,
                            data[1],
                            data[2],
                            lastYear.toString(),
                            data[4],
                            data[5],
                            data[6]
                    ));
                    writer.newLine();
                    continue;
                }

                writer.write(line);
                writer.newLine();
            }

        }

        Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return balance;
    }

    public double applyInterest() throws IOException { //DONE
        Path temp = Files.createTempFile("temp", ".csv");

        LocalDate today = LocalDate.now();
        boolean found = false;

        try (BufferedReader reader = Files.newBufferedReader(csvPathFee); BufferedWriter writer = Files.newBufferedWriter(temp)) {

            String header = reader.readLine();
            if (header != null) {
                writer.write(header);
                writer.newLine();
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data.length > 0 && data[0].equals(MoneyID)) {
                    found = true;

                    LocalDate lastInterest = readSafeLocalDate(data, 4, today);

                    long daysMissed = ChronoUnit.DAYS.between(lastInterest, today);

                    if (daysMissed > 0) {
                        double dailyRate = interestamount / 365.0;
                        balance = balance * Math.pow(1 + dailyRate, daysMissed);

                        lastInterest = today;

                        // ONLY place where balance is persisted
                    }

                    writer.write(String.join(",",
                            MoneyID,
                            data[1],
                            data[2],
                            data[3],
                            lastInterest.toString(),
                            data[5],
                            data[6]
                    ));
                    writer.newLine();
                    continue;
                }

                writer.write(line);
                writer.newLine();
            }
        }

        Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return balance;
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
        List<moneyMarket> moneyMarketUsers = OpenmoneyMarket(userid);
        try {
            allCheckingUsers = CheckingAccount.readCSV("checking_accounts.csv");
            CheckingAccount.CheckingUser checkUser = CheckingAccount.findUser(allCheckingUsers, userid);
            if (checkUser != null) {
                checkingAccounts = checkUser.accounts;
            }
        } catch (IOException e) {
            System.out.println("  Note: Could not load checking accounts for transfer.");
        }


        moneyMarket moneyUser = null;
        try {
            if (moneyMarketUsers == null || moneyMarketUsers.isEmpty()) {

                System.out.println("Money Market account doesn't exist... Creating account.");

                moneyUser = moneyMarket.createmoneyMarket(userid, minimumbalance);

                moneyMarketUsers = new ArrayList<>();
                moneyMarketUsers.add(moneyUser);

            } else {

                moneyUser = moneyMarketUsers.get(0); // or pickAccount if needed
            }

        } catch (IOException e) {
            System.out.println("Note: Could not load money market");
        }
        // Null guard — createmoneyMarket returns null if birth certificate,
        // phone, or SSN verification fails in the CSV
        if (moneyUser == null) {
            System.out.println("  Could not load or create a Money Market account.");
            System.out.println("  Ensure your profile has a phone number and SSN on file.");
            return;
        }

        final List<CheckingAccount.CheckingUser> checkingUsersRef = allCheckingUsers;

        // Transaction menu
        boolean running = true;
        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Money Market");
            System.out.println("────────────────────────────────────────");
            System.out.printf("  Balance: $%.2f%n", moneyUser.getMoneyMarket());
            System.out.println("  [1] Deposit");
            System.out.println("  [2] Withdraw");
            System.out.println("  [3] Transfer (Checking -> Money Market)");
            System.out.println("  [4] Transfer (Money Market -> Checking)");
            System.out.println("  [5] View Transaction History");
            System.out.println("  [6] Close Account");
            System.out.println("  [0] Back to Dashboard");
            System.out.println("────────────────────────────────────────");
            System.out.print("  Select: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("  Deposit amount: $");
                    try {
                        double amt = Double.parseDouble(sc.nextLine().trim());
                        moneyUser.depositMoneyMarket(amt);
                        moneyUser.update();
                        System.out.printf("  Deposited $%.2f. New balance: $%.2f%n", amt, moneyUser.getMoneyMarket());
                    } catch (NumberFormatException e) {
                        System.out.println("  Invalid amount.");
                    }
                }
                case "2" -> {
                    System.out.print("  Withdraw amount: $");
                    try {
                        double amt = Double.parseDouble(sc.nextLine().trim());
                        if (moneyUser.withdrawMoneyMarket(amt)) {
                            moneyUser.update();
                            System.out.printf("  Withdrew $%.2f. New balance: $%.2f%n", amt, moneyUser.getMoneyMarket());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("  Invalid amount.");
                    }
                }
                case "3" -> {
                    // Checking → Savings
                    if (checkingAccounts.isEmpty()) {
                        System.out.println("  No checking accounts available.");
                        break;
                    }
                    CheckingAccount.Account from = pickAccount(sc, checkingAccounts);
                    if (from == null) {
                        break;
                    }
                    System.out.print("  Amount to transfer: $");
                    // Move the money
                    moneyUser.transfer(from, sc, true, 0);
                    moneyUser.update();
                    // Save both CSVs
                    CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                    // BUG FIX 2: sync updated checking balance back to appUser
                    appUser.checkingAccount = from.balance;
                }
                case "4" -> {
                    // Savings → Checking
                    if (checkingAccounts.isEmpty()) {
                        System.out.println("  No checking accounts available.");
                        break;
                    }
                    CheckingAccount.Account to = pickAccount(sc, checkingAccounts);
                    if (to == null) {
                        break;
                    }
                    System.out.print("  Amount to transfer: $");
                    try {
                        // Move the money
                        moneyUser.transfer(to, sc, false, 0);
                        moneyUser.update();
                        // Save both CSVs
                        CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                        // BUG FIX 2: sync updated checking balance back to appUser
                        appUser.checkingAccount = to.balance;
                    } catch (NumberFormatException e) {
                        System.out.println("  Invalid amount.");
                    }
                }
                case "5" -> {
                    System.out.println("\n──── TRANSACTION HISTORY ────");
                    if (moneyUser.transactionHistory.isEmpty()) {
                        System.out.println("No transactions yet.");
                    } else {
                        for (String t : moneyUser.transactionHistory) {
                            System.out.println(t);
                        }
                    }
                }

                case "6" ->{
                    if(moneyUser.closeMoneyMarket() == null){
                        running = false;
                    }
                }
                case "0" ->
                        running = false;
                default ->
                        System.out.println("  Invalid option.");
            }
        }

    }
}