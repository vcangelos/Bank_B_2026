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
    //private static csvFile file; // csvFile equals to the path of the CSV file.
    //private static csvFile FeeCheck; // savingsFee will equal to CSVpathFee
    private String userID; // current userID lets say they registered or they were recent the constructor we'll need userID as a verification method
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

    private static final long MAX = 199_999_999_999L; // This is the maximum for the savings number generator.
                                                     // //1000000000000 is Savings Account UNIQUE ID this is only for
                                                     // savings.
    private static final long MIN = 100_000_000_000L; // minimum for the random number generator
    private static final double minimumbalance = 100;
    private static final double maximumbalance = 300;

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
   public static boolean userIDExists(String userID) throws IOException { //hasSavings or user ID //lets use this as a chance to create a boolean employee.
       try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
           reader.readLine(); //skip the header
           String line; //String line not initialized yet
           while ((line = reader.readLine()) != null) {
               String[] columnsplit = line.split(","); //split line into 3 columns instead of one huge string because we don't want that.
               if (columnsplit.length > 0 && columnsplit[0].trim().equals(userID.trim())) { //trim is useful for comparing data when white space exists what it does is removes those white spaces.
                   return true; //return true because userID exists
               }
           }
       }
       return false;
   }
    public static boolean employeeIDExists(String userID) throws IOException { //checkifSavingsIDexist
       try (BufferedReader reader = Files.newBufferedReader(csvEmployeesavingscsv)) {
           reader.readLine(); //skip the header
           String line; //String line not initialized yet
           while ((line = reader.readLine()) != null) {
               String[] columnsplit = line.split(","); //split line into 3 columns instead of one huge string because we don't want that.
               if (columnsplit.length > 0 && columnsplit[0].trim().equals(userID.trim())) { //trim is useful for comparing data when white space exists what it does is removes those white spaces.
                   return true; //return true if it is equal to the employee id
               }
           }
       }
       return false;
   }
    public static boolean SavingsIDExistsfeefile(String SavingsID) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(csvPathFee)) {
       reader.readLine(); // skip header
       String line;
       while ((line = reader.readLine()) != null) {
           String[] columnsplit = line.split(",");
           if (columnsplit.length == 0) continue; // skip malformed lines
           if (columnsplit[0].trim().equals(SavingsID.trim())) {
               return true; // userID found
           }
       }
   }
   return false; // not found
   }

   //write to csv

   public static void writeSavingsCSV(String userID, String SavingsId, double newbalance) throws IOException //make a automatic writing system.
   {
        Path temp = Files.createTempFile("temp", ".csv");
        try(BufferedReader read = Files.newBufferedReader(csvPath); BufferedWriter writetemp = Files.newBufferedWriter(temp)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",");
                if(datacur[0].trim().equals(userID)){
                    
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
   public static void writeEmployeeSavingsCSV(String userID, String SavingsId, double newbalance) throws IOException //make a automatic writing system.
   {
        Path temp = Files.createTempFile("temp", ".csv");
        try(BufferedReader read = Files.newBufferedReader(csvEmployeesavingscsv); BufferedWriter writetemp = Files.newBufferedWriter(temp)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",");
                if(datacur[0].trim().equals(userID)){
                    
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
      public static void writeCustomerinfo(boolean HasSavings, String userID) throws IOException //make a automatic writing system.
   {
        Path temp = Files.createTempFile("temp", ".csv");
        try(BufferedReader read = Files.newBufferedReader(csvCustomerInfo); BufferedWriter writetemp = Files.newBufferedWriter(temp)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",", -1);
                if(datacur[0].trim().equals(userID)){
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
   public static boolean isEmployee(String EmployeeID) throws IOException{ //This method checks if there is the userID in employee.csv if that appears there then this is an employee savings account.
       try (BufferedReader reader = Files.newBufferedReader(csvEmployeecsv)) {
       reader.readLine(); // skip header
       String line;
       while ((line = reader.readLine()) != null) {
           ArrayList<String> data = csvParsing.parseLine(line);
            if(line.trim().isEmpty())
            {
                continue;
            }
   
            if (data.size() > 0 && data.get(0).equals(EmployeeID)) { //read column 0 and lines that match with the employeeID
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
    public static List<SavingsAccount> loadallaccounts(String userID) throws IOException{
    List<SavingsAccount> accounts = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
        reader.readLine(); // skip header
        String line;

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",", -1);
            if(data[0].trim().equals(userID)){
                SavingsAccount account = new SavingsAccount();
                account.userID = data[0];
                account.savingsbalance = Double.parseDouble(data[2]);
                accounts.add(account);
            }
            
        }
    }
    return accounts;
   }
   public static SavingsAccount createSavingsAccount(String userID, double savingsamount) throws IOException {//

           String SavingsID = RandomIDGenerator();
           SavingsAccount account = null; //added null so nothing bad can happen such as unitialization.
           if (savingsamount == minimumbalance) {
            account = new SavingsAccount();
            account.userID = userID;
            account.setSavingsID(SavingsID);
            account.isEmployee = isEmployee(userID);
            account.hasSavings = true;
              
              
           } 
           else if (savingsamount <= maximumbalance && savingsamount > minimumbalance) {
                account = new SavingsAccount(userID, savingsamount);
                account.setSavingsID(SavingsID);
                account.hasSavings = true;
                account.isEmployee = isEmployee(userID);
              writeCustomerinfo(account.hasSavings, account.getuserID());
           }
           else {
                System.out.println("The Savings amount has to be in the range of 100-300");
                return null;
           }
           Path currentCSV = isEmployee(userID) ? csvEmployeesavingscsv : csvPath;
           try (BufferedWriter bw = Files.newBufferedWriter(currentCSV, StandardOpenOption.APPEND)) {
                
               bw.write(userID + "," + SavingsID + "," + account.savingsbalance);
               bw.newLine(); // make a new line when written.
           }
           writeCustomerinfo(account.hasSavings, account.getuserID());
           return account;
   }
   public static List<SavingsAccount> OpenSavingsAccount(String userID) throws IOException{
    if(userIDExists(userID))
    {   List<SavingsAccount> acc = new ArrayList<>();
        try(BufferedReader readlines = Files.newBufferedReader(csvPath)){
            
            readlines.readLine();
            String currentline;
            while((currentline = readlines.readLine()) != null){
                String[] currentdata = currentline.split(",", -1);
                if(currentdata[0].trim().equals(userID))
                {
                    SavingsAccount account = new SavingsAccount();
                    account.userID = userID;
                    account.savingsbalance = Double.parseDouble(currentdata[2]);
                    account.SavingsID = currentdata[1];
                    account.isEmployee = employeeIDExists(userID);
                    acc.add(account);
                }
                
            }
        }
        System.out.println("You're logged in");
        return acc;
    }
    else
    {
        System.out.println("Account doesn't exist, create it.");
        
    }
    return null;
   }

   public static String RandomIDGenerator() throws IOException // make the randomIDGenerator a static so it doesn't belong to an object but an standard ID generator for savings ids
   {
       Random rand = new Random(); // rand can generate random numbers
       String ID; // make ID String so we can easily manipulate it in CSV like reading or writing
                  // it.
       do {
           long number = (MIN) + (long) (rand.nextDouble() * (MAX - MIN + 1)); //the random generator that is in the range of min-max
           ID = String.valueOf(number); // convert number to String so ID can equal to that string.
       } while (savingsIDExists(ID)); // Check if there is any Savings ID like it in the CSV file.
       return ID;
   }
   public static boolean savingsIDExists(String SavingsID) throws IOException{ //SavingsID is used in the random generator so it wouldn't generate the same Savings ID as another persons Savings ID.
   try (BufferedReader reader = Files.newBufferedReader(csvPath)) {            
           reader.readLine(); // this line skips the header for example (userID,SavingsID,savings)
           String line;
           while ((line = reader.readLine()) != null) { // as line doesn't equal to NULL (end of file) continue.
               String[] currentdata_to_col = line.split(",", -1);
               if (currentdata_to_col.length > 0 && currentdata_to_col[1].equals(SavingsID)) {
                   return true;
               }
           }
       }                                                                        
       return false; // return false if there is no savings ID equal to another savings ID
   }
   //START Both getSavings() and setSavings() are used for debugging.
       public double getSavings()
       {
       return savingsbalance;
       }
       public void setSavings(double savings)
       {//field to the parameter savings.
       savingsbalance = savings;
       }
    //END

   public void setSavingsID(String SavingsID)
   {
       this.SavingsID = SavingsID;
   }
   public double depositSavings(double depositamt) {
       if (depositamt > 0) {
           return savingsbalance += depositamt;
       } else {
           System.out.println("Deposit has to be a positive.");
       }
       return savingsbalance;
   }
   public String getuserID() { //testing to see userID string
       return userID;
   }
   public String getSavingsID(){
       return SavingsID;
   }
   // ************Withdraw system**************/
   public boolean withdrawSavings(double amt) // withdraw system that records the amount. //right now this isn't use in the code at all.
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

 public double transfer(List<CheckingAccount.Account> possibleDestinations, Scanner scanner, boolean transfer, double value){ //we used list for checking account because there is multiple OR one checking account per user. USE VALUE ONLY IF IT'S OUTSIDE CHECKING ACCOUNT TRANSFER FOR EXAMPLE loans to savings.
    //transfer true from source -> savings
    if(transfer == true){
        if(possibleDestinations != null)  //if not null then transfer with checking otherwise swap defaultly
        {
        if (possibleDestinations.isEmpty()) { //checks if the list is empty we don't want that.
            System.out.println("No checking account available for transfer.");
            return value;
        }

        //select multi accounts
        CheckingAccount.Account from = null;
        while (from == null) {
            System.out.println("Select the checking account to transfer FROM:");
            for (int i = 0; i < possibleDestinations.size(); i++) { //for each account list it so the user can pick between them for example 1: 20000039984 is an checking account for this example
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
            scanner.nextLine(); // consume newline
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
        // --- Enter amount ---
        System.out.print("Enter amount to transfer: $");
        if(!scanner.hasNextDouble())
        {
            System.out.println("Enter a proper number.");
            scanner.next();
            continue;
        }
               
        double amount = scanner.nextDouble();
        scanner.nextLine(); //input a amount
        if(amount <= 0)
        {
            System.out.println("No Negative amounts or 0.");
            continue;
        }

        if (amount > from.balance) {
            System.out.println("Amount is insufficient, ");
            continue;
        }
        from.balance -= amount;
        savingsbalance += amount;
        from.addTransaction("Transfer amount", amount);
        from.updateFlags();

        System.out.printf("Transferred $%.2f from %s to %s.%n",
        amount, from.accountID, getuserID());
        System.out.printf("New balances -> %s: $%.2f | %s: $%.2f%n",
        from.accountID, from.balance, getuserID(), getSavings());
        try {
        update();
        } catch (IOException e) {
        e.printStackTrace(); // handle error
        }
                break;
        }
        return value;
        }
        else{
            while(true)
        {
        // --- Enter amount ---
        System.out.print("Enter amount to transfer: $");
        if(!scanner.hasNextDouble())
        {
            System.out.println("Enter a proper number.");
            scanner.next();
            continue;
        }
               
        double amount = scanner.nextDouble();
        scanner.nextLine(); //input a amount
        if(amount <= 0)
        {
            System.out.println("No Negative amounts or 0.");
            continue;
        }

        if (amount > value) {
            System.out.println("Amount is insufficient, ");
            continue;
        }
        value -= amount;
        savingsbalance += amount;

        System.out.printf("Transferred $%.2f from $%.2f to %s.%n",
        amount, value, getuserID());
        System.out.printf("New balances -> $%.2f | %s: $%.2f%n",
        value, getuserID(), getSavings());
        try {
        update();
        } catch (IOException e) {
        e.printStackTrace(); // handle error
        }
                break;
        
        }
        //adding transaction to
       return value; 
    }
    }
    else{
        if(possibleDestinations != null)  //if not null then transfer with checking otherwise swap defaultly
        {
        if (possibleDestinations.isEmpty()) { //checks if the list is empty we don't want that.
            System.out.println("No checking accounts available for transfer.");
            return value;
        }

        //select multi accounts
        CheckingAccount.Account from = null;
        while (from == null) {
            System.out.println("Select the checking account to transfer FROM:");
            for (int i = 0; i < possibleDestinations.size(); i++) { //for each account list it so the user can pick between them for example 1: 20000039984 is an checking account for this example
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
            scanner.nextLine(); // consume newline
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
        // --- Enter amount ---
        System.out.print("Enter amount to transfer: $");
        if(!scanner.hasNextDouble())
        {
            System.out.println("Enter a proper number.");
            scanner.next();
            continue;
        }
               
        double amount = scanner.nextDouble();
        scanner.nextLine(); //input a amount
        if(amount <= 0)
        {
            System.out.println("No Negative amounts or 0.");
            continue;
        }

        if(amount > savingsbalance)
        {
            System.out.println("Amount is insufficient, ");
            continue;
        }
        
        savingsbalance -= amount;
        from.balance += amount;
        from.addTransaction("Transfer amount", amount);
        from.updateFlags();
        
System.out.printf("Transferred $%.2f from %s to %s.%n",
        amount, from.accountID, getuserID());
System.out.printf("New balances -> %s: $%.2f | %s: $%.2f%n",
        from.accountID, from.balance, getuserID(), getSavings());
        try {
        update();
        } catch (IOException e) {
        e.printStackTrace(); // handle error
        }
                break;
        }
        }
        else{
            while(true)
        {
        // --- Enter amount ---
        System.out.print("Enter amount to transfer: $");
        if(!scanner.hasNextDouble())
        {
            System.out.println("Enter a proper number.");
            scanner.next();
            continue;
        }
               
        double amount = scanner.nextDouble();
        scanner.nextLine(); //input a amount
        if(amount <= 0)
        {
            System.out.println("No Negative amounts or 0.");
            continue;
        }
        if(amount>savingsbalance)
        {
            System.out.println("Amount is insufficient, the maximum transfer is " + savingsbalance);
            continue;
        }
        savingsbalance -= amount;
        value += amount;
        

        System.out.printf("Transferred $%.2f from $%.2f to %s.%n",
        amount, value, getuserID());
        System.out.printf("New balances -> $%.2f | %s: $%.2f%n",
        value, getuserID(), getSavings());
        try {
        update();
        } catch (IOException e) {
        e.printStackTrace(); // handle error
        }
                break;
        
        }
        //adding transaction to
        return value;
        }
    }
    return value;
    }

public void printTransactionHistory() {
    System.out.println("Transaction History for " + this.userID + ":");
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
        SavingsAccount.writeEmployeeSavingsCSV(userID, SavingsID, savingsbalance);
        
        }
        else{
        SavingsAccount.writeSavingsCSV(userID, SavingsID, savingsbalance);
        }
        applyInterest();
    }
// ************ FEES AND INTEREST METHODS ************
// This is the framework for all the fees such as monthly fee, minimum balance fee, and yearly fee.
// They'll be used in one method called updateFees
// SavingsID, LastMinMonth, LastMonthlyMonth, LowestBalance, LastYear, LastInterest

public double minBalanceFee() throws IOException { //Min month goal is to write in the csv a starting month for example "2026-03" if todays month is 2026-04 it'll check the lowest balance currently in
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

                // Retrieve previous CSV data safely
                String lastMinMonth = data.length > 1 ? data[1] : "";
                String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : savingsbalance;
                String lastYear = data.length > 4 ? data[4] : "";
                String lastInterest = data.length > 5 ? data[5] : "";

                //check if anything isn't null and check if lastminmonth isn't empty and make lastminmonth not equal to current month otherwise skip this.
                if (lastMinMonth != null && !lastMinMonth.isEmpty() && !lastMinMonth.equals(currentMonth)) {
                    if (savingsbalance < minimumbalance) {
                        savingsbalance -= minBalanceFee;
                        writeSavingsCSV(userID, SavingsID, savingsbalance);
                    }
                    lastMinMonth = currentMonth;
                    currentBalance = savingsbalance;
                } else {
                    //update current balance
                        currentBalance = savingsbalance;
                    
                }

                //write updated line
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

            //write the otherlines
            writer.write(line);
            writer.newLine();
        }

        //if nothing was found.
        if (!found) {
            writer.write(String.join(",",
                    SavingsID,
                    currentMonth,
                    "",
                    String.format("%.2f",savingsbalance),
                    "",
                    ""
            ));
            writer.newLine();
        }
    }

    //replace old CSV with updated temp file.
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

                //load in csv data for other values.
                String lastMinMonth = data.length > 1 ? data[1] : "";
                String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : savingsbalance;
                String lastYear = data.length > 4 ? data[4] : "";
                String lastInterest = data.length > 5 ? data[5] : "";

                // Apply monthly fee if a month has passed
                if (!lastMonthlyMonth.isEmpty() && !currentMonth.equals(lastMonthlyMonth)) {
                    savingsbalance -= monthlyFee;
                    writeSavingsCSV(userID, SavingsID, savingsbalance);
                }
                lastMonthlyMonth = currentMonth;

                writer.write(String.join(",", //rewrite all data such as last min month and so on.
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

        // First time entry
        if (!found) {
            writer.write(String.join(",",
                    SavingsID,
                    "",
                    currentMonth,
                    String.format("%.2f",savingsbalance),
                    "",
                    ""
            ));
            writer.newLine();
        }
    }

    Files.move(tempfile, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
    return savingsbalance;
}

public double yearlyFee() throws IOException { //every year passing the user gets a fee.
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

                // Apply yearly fee if year has changed
                if (!lastYear.isEmpty() && !lastYear.equals(currentYear)) {
                    savingsbalance -= yearlyFee;
                    writeSavingsCSV(userID, SavingsID, savingsbalance);
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
            writer.write(String.join(",",
                    SavingsID,
                    "",
                    "",
                    String.format("%.2f",savingsbalance),
                    currentYear,
                    ""
            ));
            writer.newLine();
        }
    }

    Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
    return savingsbalance;
}

public double applyInterest() throws IOException { //apply interest over months
    LocalDate today = LocalDate.now(); //grab todays date so we can compare it with last month
    String currentMonth = today.getYear() + "-" + String.format("%02d", today.getMonthValue());

    Path temp = Files.createTempFile("temp", ".csv");
    boolean found = false; //boolean logic so we can either create or update an existing data such as interest balance

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

                //Interest is applied via month and savings is written to savings csv so it can be written overtime
                if (!lastInterest.isEmpty() && !lastInterest.equals(currentMonth)) {
                    double monthlyRate = interestamount / 12.0; //interest amount.
                    savingsbalance += savingsbalance * monthlyRate;
                    currentBalance = savingsbalance;
                    System.out.println("p");
                    writeSavingsCSV(userID, SavingsID, currentBalance);
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

        // First time entry
        if (!found) {
            writer.write(String.join(",",
                    SavingsID,
                    "",
                    "",
                    String.format("%.2f",savingsbalance),
                    "",
                    currentMonth
            ));
            writer.newLine();
        }
    }

    Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
    return savingsbalance;
}

       
}

