//TODO USING SAVINGS.CSV IT'S A WIP
//Savings Account: is where you hold on to your money and in a while you can get interest for holding that money within a specific time period.
//this is used for the random generator.

import java.io.BufferedReader; //Random is used for the random ID generator
import java.io.BufferedWriter;
import java.io.IOException; //This helps us write data to the CSV file.
import java.nio.file.Files; //catch errors if anything silly happens.
import java.nio.file.Path; //to make it easier to access the files read and write functions. Our HasSavings is a static so we need static methods to make the code work. Files work hand to hand with path objects instead of using Strings we could use that which makes it platform independent.
import java.nio.file.StandardCopyOption; //Array list is needed when we don't know the size of an array or when we resize an array if you see SavingsIDexists I used array list to capture all the columns and use it to compare with the current savings ID with the savings ID in the current array list. Something like this psuedocode currentsavingsID = currentarraylistsavings.
import java.nio.file.StandardOpenOption;
import java.time.LocalDate; //This function is used to find the file you want for example I'm using this to find my Savings.csv
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList; //we don't want to overwrite when we create a savings ID account we want to append.
import java.util.List;
import java.util.Random; //is used to read line by line
import java.util.Scanner;

public class SavingsAccount {

    /**
     * * Variables **
     */
    private double savingsbalance = 0; // TODO this is our savings balance which will start in a range if the customer first creates their account in about 100-300 $                                  
    private final double minBalanceFee = 15; // minimum balance fee is 15 dollars.
    private final double monthlyFee = 12; // TODO monthly fee is 12 dollars.
    private final double yearlyFee = 48; // TODO 48 dollars.
    //private static csvFile file; // csvFile equals to the path of the CSV file.
    //private static csvFile FeeCheck; // savingsFee will equal to CSVpathFee
    private String userID; // current userID lets say they registered or they were recent the constructor we'll need userID as a verification method
    private String SavingsID; //savings ID is a unique verification method to see if the user has a savings account or not.
    private final double interestamount = 0.0042; //interest is 5% 

    private String phoneNumber; // store a users phone number.
    private String driversLicense;
    private String birthcertificate;
    private boolean isEmployee = false;
    private LocalDate datecreation;
    private boolean hasSavings;



        //early closure account variables:
    private static final int MIN_DAY = 0;
    private static final int MAX_DAY = 180;
    private static final double MAX_FEE_LIMIT = 50;
    private static final double MIN_FEE = 5;



    /* Static Final variables */
    private static final Path csvPath = Path.of("Savings.csv"); // fine the path for the CSV file
    private static final Path csvPathFee = Path.of("SavingsFeeCheck.csv"); // measuring monthly
    private static final Path csvCustomerInfo = Path.of("customerinfo.csv");
    private static final Path csvEmployeecsv = Path.of("employeecards.csv");
    private static final Path csvEmployeesavingscsv = Path.of("EmployeeSavings.csv");

    private static final long MAX = 199_999_999_999L; // This is the maximum for the savings number generator.
    // //1000000000000 is Savings Account UNIQUE ID this is only for
    // savings.
    private static final long MIN = 100_000_000_000L; // minimum for the random number generator
    private static final double minimumbalance = 100;
    private static final double maximumbalance = 300;
    
    private final List<String> transactionHistory = new ArrayList<>();

    private void logTransaction(String type, double amount) {
        String entry = String.format(
                "%s | %s | $%.2f | bal=%.2f",
                LocalDate.now(),
                type,
                amount,
                savingsbalance
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
    public SavingsAccount() // when creating
    {
        savingsbalance = minimumbalance; // make savings start at 100 to start with a default starting value.
    }

    public SavingsAccount(String userID, double savingsamount) // Work in progress.
    {
        this.userID = userID;
        savingsbalance = savingsamount;// TODO I need to create a method that checks in the CSV file, if the userID has
        // a savings account or not if not then request back to them they don't have it.
        // Request the User if he wants to create a savings account.
    }

    /*
    * CHECK IF THE USER HAS A SAVINGS ACCOUNT THE RETURN FUNCTION WILL BE THE FINAL
    * SIGNAL, use this inside createSavings or existingSavings
     */
    public static boolean userIDExists (String userID) throws IOException { //hasMoney or user ID //lets use this as a chance to create a boolean employee.
        Path csvEmployeeornot = isEmployee(userID) ? csvEmployeesavingscsv : csvPath;

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

    public static boolean isEmployee(String EmployeeID) throws IOException { //This method checks if there is the userID in employee.csv if that appears there then this is an employee MoneyMarket account.
        try (BufferedReader reader = Files.newBufferedReader(csvEmployeecsv)) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                ArrayList<String> data = csvParsing.parseLine(line);
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (!data.isEmpty() && data.get(0).equals(EmployeeID)) { //read column 0 and lines that match with the employeeID
                    return true;
                }
            }
        }
        return false; // not found
    }

    public static boolean SavingsIDExistsfeefile(String SavingsID) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(csvPathFee)) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columnsplit = line.split(",", -1);
                if (columnsplit.length == 0) {
                    continue; // skip malformed lines

                                }if (columnsplit[0].trim().equals(SavingsID.trim())) {
                    return true; // userID found
                }
            }
        }
        return false; // not found
    }

    //write to csv
    public static boolean writefeeUser(String savingsID, String DriversLicense, String BirthCertificate) throws IOException {
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

                if (data.length > 0 && data[0].equals(savingsID)) {
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
                        savingsID,
                        today.toString(),
                        today.toString(),
                        today.toString(),
                        today.toString(),
                        DriversLicense,
                        BirthCertificate,
                        today.toString(),
                        "ACTIVE",
                        ""));
                writer.newLine();
            }
        }

        Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return !found;
    }

    public static void writeSavingsCSV(String userID, String SavingsId, double newbalance, boolean isEmployee) throws IOException //make a automatic writing system.
    {
        Path csvpick = isEmployee ? csvEmployeesavingscsv : csvPath;
        Path temp = Files.createTempFile("temp", ".csv");
        try (BufferedReader read = Files.newBufferedReader(csvpick); BufferedWriter writetemp = Files.newBufferedWriter(temp)) {
            String line;
            while ((line = read.readLine()) != null) {
                String[] datacur = line.split(",", -1);
                if (datacur[0].trim().equals(userID)) {

                    datacur[2] = String.valueOf(newbalance);
                    writetemp.write(String.join(",", datacur));
                } else {
                    writetemp.write(line);
                }
                writetemp.newLine();
            }
        }
        Files.move(temp, csvpick, StandardCopyOption.REPLACE_EXISTING);
    }

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

    public static void writeCustomerinfo(double balance, String userID) throws IOException //make a automatic writing system.
    {
        Path temp = Files.createTempFile("temp", ".csv");
        try (BufferedReader read = Files.newBufferedReader(csvCustomerInfo); BufferedWriter writetemp = Files.newBufferedWriter(temp)) {
            String line;
            while ((line = read.readLine()) != null) {
                String[] datacur = line.split(",", -1);
                if (datacur[0].trim().equals(userID)) {
                    for (String Value : datacur) {
                        System.out.println(Value + ", ");
                    }
                    datacur[9] = String.valueOf(balance);
                    writetemp.write(String.join(",", datacur));
                } else {

                    writetemp.write(line);
                }
                writetemp.newLine();
            }
        }
        Files.move(temp, csvCustomerInfo, StandardCopyOption.REPLACE_EXISTING);
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
    public static SavingsAccount createSavingsAccount(String userID, double savingsamount) throws IOException {//

        if (userIDExists(userID)) {
            System.out.println("You already have an account.");
            return null;
        }

        if (birthCertificate(userID)) {
            System.out.println("Birth certificate verified successfully.");
        }
        if (savingsamount <= 0 || userID == null || userID.isEmpty()) {
            System.out.println("Error: Type a proper amount.");
            return null;
        }
        boolean isEmployee_t = isEmployee(userID);
        String SavingsID = RandomIDGenerator(isEmployee_t);
        String phoneNum = getPhonenumber(userID);
        LocalDate today = LocalDate.now(); //date creation for the account.
        String date = today.toString();
        String socialSecurity = getSocialSecurity(userID);
        String BC = RandomIDBirthCertificate(userID);
        String DL = RandomIDGeneratorDriversLicense(userID, isEmployee_t); //DL is drivers license by the way.

        SavingsAccount account = null; //added null so nothing bad can happen such as unitialization.
        if (savingsamount == minimumbalance) {
            account = new SavingsAccount();
            account.userID = userID;
            account.setSavings(savingsamount);
            account.setSavingsID(SavingsID);
            account.isEmployee = isEmployee_t;
            account.birthcertificate = BC;
            account.driversLicense = DL;
            account.hasSavings = true;
        } else if (savingsamount <= maximumbalance && savingsamount > minimumbalance) {
            account = new SavingsAccount(userID, savingsamount);
            account.setSavingsID(SavingsID);
            account.setSavings(savingsamount);
            account.hasSavings = true;
            account.isEmployee = isEmployee_t;
            account.birthcertificate = BC;
            account.driversLicense = DL;
        } else {
            System.out.println("The Savings amount has to be in the range of 100-300");
            return null;
        }
        Path currentCSV = isEmployee(userID) ? csvEmployeesavingscsv : csvPath;
        try (BufferedWriter bw = Files.newBufferedWriter(currentCSV, StandardOpenOption.APPEND)) {

            bw.write(userID + "," + SavingsID + "," + account.savingsbalance);
            bw.newLine(); // make a new line when written.
        }
        writeCustomerinfo(savingsamount, account.getuserID());
        writefeeUser(account.SavingsID, account.driversLicense, account.birthcertificate);
        return account;
    }

    public static SavingsAccount OpenSavingsAccount(String userID) throws IOException {
        if (userIDExists(userID)) {
            Path isEmployee = (isEmployee(userID)) ? csvEmployeesavingscsv : csvPath;
            try (BufferedReader readlines = Files.newBufferedReader(isEmployee)) {

                readlines.readLine();
                String currentline;
                while ((currentline = readlines.readLine()) != null) {
                    String[] currentdata = currentline.split(",", -1);
                    if (currentdata[0].trim().equals(userID)) {
                        SavingsAccount account = new SavingsAccount();
                        account.userID = userID;
                        account.savingsbalance = Double.parseDouble(currentdata[2]);
                        account.SavingsID = currentdata[1];
                        account.isEmployee = isEmployee(userID);
                        account.update();
                        return account;
                    }

                }
            }
            System.out.println("You're logged in");
            return null;
        } else {
            System.out.println("Account doesn't exist, create it.");

        }
        return null;
    }

    public SavingsAccount closeSavings() throws IOException {

        if (!userIDExists(userID)) {
            System.out.println("Account doesn't exist.");
            return null;
        }

        String result = updateNegativeBalance();

        if (result.equals("CLOSED")) {
            removeSavings(this.userID, this.SavingsID, this.isEmployee);
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
                removeSavings(this.userID, this.SavingsID, this.isEmployee);
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

            removeSavings(this.userID, this.SavingsID, this.isEmployee);
            System.out.println("Account closed successfully.");
            return null;
        }

        return this;
    }


    public double getEarlyClosureFee() throws IOException {
        LocalDate today = LocalDate.now();
        long daysOpen = ChronoUnit.DAYS.between(getDateCreated(), today);
        return earlyClosureCalculator(daysOpen);
    }

    public boolean earlyClosureMoneyMarketAccount() throws IOException {
        Scanner scanner = new Scanner(System.in);
        LocalDate today = LocalDate.now();
        long daysOpen = ChronoUnit.DAYS.between(getDateCreated(), today);

        try {
            double fee = earlyClosureCalculator(daysOpen);

            double balance = getSavings();

            if (balance < fee) {
                System.out.println("Insufficient funds to cover early closure fee.");
                return false;
            }

            setSavings(balance - fee);

            System.out.printf("Early closure fee applied: $%.2f%n", fee);
            System.out.printf("Remaining balance: $%.2f%n", getSavings());
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


    public static boolean removeSavings(String userID, String moneyID, boolean isEmployee) throws IOException { //DO NOT use this this is used for closed money as a helper
        boolean success = false;
        boolean successfee = false;
        Path temp = Files.createTempFile("temp", ".csv");
        Path csvPick = isEmployee ? csvEmployeesavingscsv: csvPath;
        try (BufferedReader reader = Files.newBufferedReader(csvPick); BufferedWriter writer = Files.newBufferedWriter(temp)) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data[0].equals(userID) && data[1].equals(moneyID)) {
                    success = true;
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
                    continue;
                }

                writer.write(line);
                writer.newLine();
            }
        }
        Files.move(tempFee, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return success;
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

    public static String getSocialSecurity(String userID) throws IOException { // Grab phone number by searching through CSV
        try (BufferedReader readfile = Files.newBufferedReader(csvCustomerInfo)) {
            String line;
            while ((line = readfile.readLine()) != null) {
                boolean valid = true;
                String[] dataline = line.split(",", -1);
                if (dataline.length > 0 && dataline[0].trim().equals(userID)) {
                    String socialSecurity = dataline.length > 3 ? dataline[3] : ""; //social security is in the fourth column. 
                    socialSecurity = socialSecurity.replaceAll("[^0-9]", "");
                    if (socialSecurity.length() == 9) { //it has to be equal to 9 otherwise return a null to interrupt account creation

                        for (int i = 0; i < socialSecurity.length(); i++) { //just in case replaceall missed something.
                            if (!Character.isDigit(socialSecurity.charAt(i))) { //this is copied from phone number code.
                                valid = false;
                                break;//break out of here and return a null few lines down if there is a character inside a socialsecurity number
                            }
                        }
                        if (valid) { //valid helps us here by checking if characters are in the digits or not
                            return socialSecurity;
                        }
                    }
                }
            }
        }
        return null;
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

    public static String RandomIDGenerator(boolean isEmployee) throws IOException // make the randomIDGenerator a static so it doesn't belong to an object but an standard ID generator for savings ids
    {
        Random rand = new Random(); // rand can generate random numbers
        String ID; // make ID String so we can easily manipulate it in CSV like reading or writing
        // it.
        do {
            long number = (MIN) + (long) (rand.nextDouble() * (MAX - MIN + 1)); //the random generator that is in the range of min-max
            ID = String.valueOf(number); // convert number to String so ID can equal to that string.
        } while (savingsIDExists(ID, isEmployee)); // Check if there is any Savings ID like it in the CSV file.
        return ID;
    }

    public static boolean savingsIDExists(String SavingsID, boolean isEmployee) throws IOException { //SavingsID is used in the random generator so it wouldn't generate the same Savings ID as another persons Savings ID.
        Path isEmployee_t = isEmployee ? csvEmployeesavingscsv : csvPath;
        try (BufferedReader reader = Files.newBufferedReader(isEmployee_t)) {
            reader.readLine(); // this line skips the header for example (userID,SavingsID,savings)
            String line;
            while ((line = reader.readLine()) != null) { // as line doesn't equal to NULL (end of file) continue.
                String[] currentdata_to_col = line.split(",", -1);
                if (currentdata_to_col.length > 1 && currentdata_to_col[1].equals(SavingsID)) {
                    return true;
                }
            }
        }
        return false; // return false if there is no savings ID equal to another savings ID
    }

    public static boolean birthCertificateExists(String BCID, boolean isEmployee) throws IOException { //Generate a unique birth certificate ID. No same ID distributed.
        Path Csvchoice;
        if (isEmployee) {
            Csvchoice = csvEmployeesavingscsv;
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

    public static boolean DriversLicenseExists(String DriversID, boolean isEmployee) throws IOException { //Generate a unique drivers ID. No same ID distributed.
        Path choiceCSV;
        if (isEmployee) {
            choiceCSV = csvEmployeesavingscsv;
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

    //START Both getSavings() and setSavings() are used for debugging.
    public double getSavings() {
        return savingsbalance;
    }

    public void setSavings(double savings) {//field to the parameter savings.
        savingsbalance = savings;
    }
    //END

    public void setSavingsID(String SavingsID) {
        this.SavingsID = SavingsID;
    }

    public double depositSavings(double depositamt) throws IOException {
        if (depositamt > 0) {
            writeSavingsCSV(userID, SavingsID, savingsbalance, isEmployee);
            logTransaction("DEPOSIT", depositamt);
            return savingsbalance += depositamt;
        } else {
            System.out.println("Deposit has to be a positive.");
        }
        return savingsbalance;
    }

    public String getuserID() { //testing to see userID string
        return userID;
    }

    public String getSavingsID() {
        return SavingsID;
    }
    public LocalDate getDateCreated() throws IOException {
        try(BufferedReader read = Files.newBufferedReader(csvPathFee))
        {
            LocalDate today = LocalDate.now();
            String line;
            while((line = read.readLine()) != null){
                String[] data = line.split(",", -1);
                if(data[0].trim().equals(SavingsID)){
                    datecreation = readSafeLocalDate(data, 7, today);
                    return datecreation;
                }

            }


        }

        return LocalDate.now();
    }

    //******************************* */
    // ************Withdraw system**************/
    public boolean withdrawSavings(double amt) throws IOException // withdraw system that records the amount. //right now this isn't use in the code at all.
    {
        if (amt <= 0) {
            System.out.println("Account can't be less than or equal to 0, so please choose a higher value.");
            return false;
        }
        if (amt > savingsbalance) {
            System.out.println("The amount for withdraw is too high");
            return false; // deny negative withdraw
        } else {
            savingsbalance -= amt;
            System.out.println("Savings has been successfully withdrew.");
            writeSavingsCSV(userID, SavingsID, savingsbalance, isEmployee);
            logTransaction("WITHDRAW", amt);
            return true;
        }

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
                savingsbalance += amount;
                writeSavingsCSV(userID, SavingsID, savingsbalance, isEmployee);
                logTransaction("TRANSFER IN", amount);
                fromAccount.addTransaction("Transfer out for Savings", amount);
                fromAccount.updateFlags();

                System.out.printf("  Moved $%.2f: savings ($%.2f) <- checking %s ($%.2f)%n",
                amount, getSavings(), fromAccount.accountID, fromAccount.balance);

            } else if (fromsource && fromAccount == null) {

                if (amount > externalValue) {
                    System.out.println("Insufficient external funds.");
                    continue;
                }

                externalValue -= amount;
                savingsbalance += amount;
                logTransaction("TRANSFER IN", amount);
                writeSavingsCSV(userID, SavingsID, savingsbalance, isEmployee);
                System.out.printf("  Moved $%.2f: savings ($%.2f) → Source: ($%.2f)%n",
                        amount, savingsbalance, externalValue);
            } else if (fromAccount != null) {

                if (!fromAccount.isActive) {
                    System.out.println("Account is inactive.");
                    return externalValue;
                }

                if (amount > savingsbalance) {
                    System.out.println("Insufficient funds.");
                    continue;
                }

                savingsbalance -= amount;
                fromAccount.balance += amount;
                writeSavingsCSV(userID, SavingsID, savingsbalance, isEmployee);
                fromAccount.addTransaction("Transfer in from Savings", amount);
                logTransaction("TRANSFER OUT", amount);
                fromAccount.updateFlags();

                System.out.printf("  Moved $%.2f: savings ($%.2f) -> checking %s ($%.2f)%n",
                amount, getSavings(), fromAccount.accountID, fromAccount.balance);

            } //from SAVINGS to External value.
            else {

                if (amount > savingsbalance) {
                    System.out.println("Insufficient funds.");
                    continue;
                }

                savingsbalance -= amount;
                externalValue += amount;
                logTransaction("TRANSFER OUT", amount);
                System.out.printf("  Moved $%.2f: savings ($%.2f) <- Source: ($%.2f)%n",
                        amount, savingsbalance, externalValue);

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
            closeSavings();
        }
        writeSavingsCSV(userID, SavingsID, savingsbalance, isEmployee);
    }
// ************ FEES AND INTEREST METHODS ************
// This is the framework for all the fees such as monthly fee, minimum balance fee, and yearly fee.
// They'll be used in one method called updateFees
// SavingsID, LastMinMonth, lastMonthlyDate, LowestBalance, LastYear, LastInterest

    public double minBalanceFee() throws IOException { //Min month goal is to write in the csv a starting month for example "2026-03" if todays month is 2026-04 it'll check the lowest balance currently in
        LocalDate today = LocalDate.now();
        boolean found = false;
        Path temp = Files.createTempFile("temp", ".csv");

        try (BufferedReader reader = Files.newBufferedReader(csvPathFee); BufferedWriter writer = Files.newBufferedWriter(temp)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data[0].equals(SavingsID)) {
                    found = true;
                    // Retrieve previous CSV data safely
                    LocalDate lastMinMonth = readSafeLocalDate(data, 1, today);

                    //check if anything isn't null and check if lastminmonth isn't empty and make lastminmonth not equal to current month otherwise skip this.
                    long months = ChronoUnit.MONTHS.between(lastMinMonth, today);

                    if (months > 0) {

                        if (savingsbalance < minimumbalance) {
                            savingsbalance -= minBalanceFee * months;
                        }

                        lastMinMonth = lastMinMonth.plusMonths(months);
                    }

                    //write updated line
                    writer.write(String.join(",",
                            SavingsID,
                            lastMinMonth.toString(),
                            data[2],
                            data[3],
                            data[4],
                            data[5],
                            data[6],
                            data[7],
                            data[8],
                            data[9]
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
        return savingsbalance;
    }

    public double monthlyFee() throws IOException {
        LocalDate today = LocalDate.now();
        boolean found = false;
        Path tempfile = Files.createTempFile("csv_temp", ".csv");

        try (BufferedReader reader = Files.newBufferedReader(csvPathFee); BufferedWriter writer = Files.newBufferedWriter(tempfile)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data[0].equals(SavingsID)) {
                    found = true;
                    // Retrieve previous CSV data safely
                    LocalDate lastMonthlyDate = readSafeLocalDate(data, 2, today);

                    //check if anything isn't null and check if lastminmonth isn't empty and make lastminmonth not equal to current month otherwise skip this.
                    long months = ChronoUnit.MONTHS.between(lastMonthlyDate, today);

                    if (months > 0) {
                        savingsbalance -= months * monthlyFee;
                        lastMonthlyDate = lastMonthlyDate.plusMonths(months);
                    }

                    //write updated line
                    writer.write(String.join(",",
                            SavingsID,
                            data[1],
                            lastMonthlyDate.toString(),
                            data[3],
                            data[4],
                            data[5],
                            data[6],
                            data[7],
                            data[8],
                            data[9]
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
        return savingsbalance;
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

    public double readSafeDouble(String[] data, int col, double fallback) {
        try {
            return (data.length > col && !data[col].isEmpty())
                    ? Double.parseDouble(data[col])
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

    public double yearlyFee() throws IOException { //every year passing the user gets a fee.
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

                if (data[0].equals(SavingsID)) {
                    found = true;

                    LocalDate lastYear = readSafeLocalDate(data, 3, today);

                    long yearspassed = ChronoUnit.YEARS.between(lastYear, today);
                    if (yearspassed > 0) {
                        savingsbalance -= (yearspassed * yearlyFee);
                        lastYear = lastYear.plusYears(yearspassed);
                    }

                    writer.write(String.join(",",
                            SavingsID,
                            data[1],
                            data[2],
                            lastYear.toString(),
                            data[4],
                            data[5],
                            data[6],
                            data[7],
                            data[8],
                            data[9]
                    ));
                    writer.newLine();
                    continue;
                }

                writer.write(line);
                writer.newLine();

            }
        }

        Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return savingsbalance;
    }

    public double applyInterest() throws IOException {
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

                if (data.length > 0 && data[0].equals(SavingsID)) {
                    found = true;

                    LocalDate lastInterest = readSafeLocalDate(data, 4, today);

                    long daysMissed = ChronoUnit.DAYS.between(lastInterest, today);

                    if (daysMissed > 0) {
                        double dailyRate = interestamount / 365.0;
                        savingsbalance = savingsbalance * Math.pow(1 + dailyRate, daysMissed);

                        lastInterest = today;

                        // ONLY place where balance is persisted
                    }

                    writer.write(String.join(",",
                            SavingsID,
                            data[1],
                            data[2],
                            data[3],
                            lastInterest.toString(),
                            data[5],
                            data[6],
                            data[7],
                            data[8],
                            data[9]
                    ));
                    writer.newLine();
                    continue;
                }

                writer.write(line);
                writer.newLine();
            }

            // If account not found → initialize tracking row
        }

        Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
        return savingsbalance;
    }

    //NEGATIVE
    public String updateNegativeBalance() throws IOException {

        LocalDate today = LocalDate.now();
        String finalStatus = null;

        Path temp = Files.createTempFile("temp", ".csv");
        
        try (BufferedReader reader = Files.newBufferedReader(csvPathFee); BufferedWriter writer = Files.newBufferedWriter(temp)) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (!data[0].equals(SavingsID)) {
                    writer.write(line);
                    writer.newLine();
                    continue;
                }

                String status = readSafeString(data, 8, "ACTIVE");
                String negativeTimestamp = readSafeString(data, 9, "");
                // TERMINAL STATE: do nothing
                if (status.equals("CLOSED")) {
                    writer.write(line);
                    writer.newLine();
                    finalStatus = "CLOSED";
                    continue;
                }

                // START negative tracking
                if (savingsbalance < 0 && negativeTimestamp.isEmpty()) {
                    negativeTimestamp = today.toString();
                    status = "NEGATIVE";
                }

                // STILL NEGATIVE
                if (savingsbalance < 0 && !negativeTimestamp.isEmpty()) {

                    LocalDate start = LocalDate.parse(negativeTimestamp);
                    long days = ChronoUnit.DAYS.between(start, today);

                    if (days >= 30) {
                        status = "NEGATIVE";
                    }
                }

                // RECOVERY
                if (savingsbalance >= 0 && !negativeTimestamp.isEmpty()) {

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
                        SavingsID,
                        data[1],
                        data[2],
                        data[3],
                        data[4],
                        data[5],
                        data[6],
                        data[7],
                        status,
                        negativeTimestamp
                ));

                writer.newLine();
            }
        }

        Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);

        return finalStatus;
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
            System.out.println("  [3] Transfer (Checking -> Savings)");
            System.out.println("  [4] Transfer (Savings -> Checking)");
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
                        acc.transfer(from, sc, true, 0);
                        acc.update();
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
                    try {
                        // Move the money
                        acc.transfer(to, sc, false, 0);
                        acc.update();
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
                    if (acc.transactionHistory.isEmpty()) {
                        System.out.println("No transactions yet.");
                    } else {
                        for (String t : acc.transactionHistory) {
                            System.out.println(t);
                        }
                    }
                }

                case "6" ->{
                    if(acc.closeSavings() == null){
                    running = false;
                    }
                }
                case "0" ->
                    running = false;
                default ->
                    System.out.println("  Invalid option.");
            }
        }

        // Sync final savings balance back to appUser so db.updateUser() in menu.java saves correctly
        appUser.savingsAccount = acc.getSavings();
    }
}
