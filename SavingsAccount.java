//TODO USING SAVINGS.CSV IT'S A WIP
//Savings Account: is where you hold on to your money and in a while you can get interest for holding that money within a specific time period.
//this is used for the random generator.

import java.util.Random; //Random is used for the random ID generator  


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
import java.time.LocalDateTime; //convert to localdatetime for example New york eastern time.
public class SavingsAccount {
    /*** Variables ***/
    private double savingsbalance = 0; // TODO this is our savings balance which will start in a range if the customer first creates their account in about 100-300 $                                  
    private final double minBalanceFee = 15; // minimum balance fee is 15 dollars.
    private final double monthlyFee = 12; // TODO monthly fee is 12 dollars.
    private final double yearlyFee = 48; // TODO 48 dollars.
    //private static csvFile file; // csvFile equals to the path of the CSV file.
    //private static csvFile FeeCheck; // savingsFee will equal to CSVpathFee
    private String userid; // current userID lets say they registered or they were recent the constructor we'll need userID as a verification method
    private String SavingsID; //savings ID is a unique verification method to see if the user has a savings account or not.
    private final double interestamount = 0.05; //interest is 5% 
    private boolean isEmployee = false;
    private boolean hasSavings;

    /*transaction data*/
    private final List<String> transactionHistory = new ArrayList<>();

    /* Static Final variables */
    private static final Path csvPath = Path.of("Savings.csv"); // fine the path for the CSV file
    private static final Path csvPathFee = Path.of("SavingsFeeCheck.csv"); // measuring monthly
    private static final Path csvCustomerInfo = Path.of("customerinfo.csv");

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
   public SavingsAccount(String userid, double savingsamount) // Work in progress.
   {
       this.userid = userid;
       savingsbalance = savingsamount;// TODO I need to create a method that checks in the CSV file, if the userID has
                                      // a savings account or not if not then request back to them they don't have it.
                                      // Request the User if he wants to create a savings account.
   }
   /*
    * CHECK IF THE USER HAS A SAVINGS ACCOUNT THE RETURN FUNCTION WILL BE THE FINAL
    * SIGNAL, use this inside createSavings or existingSavings
    */
   public static boolean userIDExists(String userid) throws IOException { //hasSavings or user ID //lets use this as a chance to create a boolean employee.
       try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
           reader.readLine(); //skip the header
           String line; //String line not initialized yet
           while ((line = reader.readLine()) != null) {
               String[] columnsplit = line.split(","); //split line into 3 columns instead of one huge string because we don't want that.
               if (columnsplit.length > 0 && columnsplit[0].trim().equals(userid.trim())) { //trim is useful for comparing data when white space exists what it does is removes those white spaces.
                   return true; //return true because userID exists
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

   public static void writeSavingsCSV(String UserID, String SavingsId, double newbalance) throws IOException //make a automatic writing system.
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
      public static void writeCustomerinfo(boolean HasSavings, String UserID) throws IOException //make a automatic writing system.
   {
        Path temp = Files.createTempFile("temp", ".csv");
        try(BufferedReader read = Files.newBufferedReader(csvCustomerInfo); BufferedWriter writetemp = Files.newBufferedWriter(temp)){
            String line;
            System.out.println("h");
            boolean printplease = false;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",", -1);
                if(datacur[0].trim().equals(UserID)){
                    for(String Value:datacur){
                    System.out.println(Value + ", ");
                    }
                    
                    printplease = false;
                    datacur[12] = String.valueOf(HasSavings);
                    writetemp.write(String.join("," , datacur));
                }
                else{

                    writetemp.write(line);
                }
                writetemp.newLine();
                if(printplease = true)
                {
                    System.out.println("User isn't anywhere in that CSV.");
                }
            }
        }
        Files.move(temp, csvCustomerInfo, StandardCopyOption.REPLACE_EXISTING);
   }
   public static void ifEmployeee(){

   }
   public static boolean isValidUserID(String userid){
    if(userid == null && userid.isEmpty())
    {
        return false;
    }
    for(char c : userid.toCharArray())
    {

        if(!Character.isDigit(c))
        {
            System.out.println("Not a digit please fix your userID");
            return false;
        }
       
    }
    return true;
   }
   public static SavingsAccount createSavingsAccount(String userid, double savingsamount) throws IOException {//
       if(!isValidUserID(userid))
       {
        return null;
       }
       if(userIDExists(userid)) { // NEW WORKS I need to make employee CSV
        System.out.println("There is another user with this UserID");
        return null;
       } else {
           // else(hasEmployee()){}
           String SavingsID = RandomIDGenerator();
           SavingsAccount account = null; //added null so nothing bad can happen such as unitialization.
           if (savingsamount == 100) {
               account = new SavingsAccount();
               account.userid = userid;
               account.setSavingsID(SavingsID);
              account.hasSavings = true;
              writeCustomerinfo(account.hasSavings, account.getUserid());
           } else if (savingsamount <= maximumbalance && savingsamount > minimumbalance) {
               account = new SavingsAccount(userid, savingsamount);
               account.setSavingsID(SavingsID);
              account.hasSavings = true;
              writeCustomerinfo(account.hasSavings, account.getUserid());
           }
           else {
                System.out.println("The Saivngs amount has to be in the range of 100-300");
                return null;
           }
           try (BufferedWriter bw = Files.newBufferedWriter(csvPath, StandardOpenOption.APPEND)) {
               bw.write(userid + "," + SavingsID + "," + account.savingsbalance);
               bw.newLine(); // make a new line when written.
           }
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
                    return account;
                }
                
            }
        }
        System.out.println("You're logged in");
    }
    else
    {
        System.out.println("Account doesn't exist create it.");
        
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
           reader.readLine(); // this line skips the header for example (userid,SavingsID,savings)
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
   public String getUserid() { //testing to see UserID string
       return userid;
   }
   public String getSavingsID(){
       return SavingsID;
   }
   // ************Withdraw system**************/
   public boolean withdrawSavings(double amt) // withdraw system that records the amount.
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
   /*public boolean transferToChecking(double amt, CheckingAccount.Account checking) {
        if (amt <= 0) {
            System.out.println("Amount must be > 0.");
            return false;
        }
        if (amt > savingsbalance) {
            System.out.printf("Insufficient funds in savings %s to transfer $%.2f%n", userid, amt);
            return false;
        }
        savingsbalance -= amt;
        checking.balance += amt;
        checking.addTransaction("Transfer In from Savings", amt);
        System.out.printf("Transferred $%.2f from savings to checkings%n", amt);
        System.out.printf("Savings balance: $%.2f | Checking balance: $%.2f%n", savingsbalance, checking.balance);
        return true;
    }*/


    //record transaction and then print history of transaction if needed.
    private void recordTransaction(String type, double amount) {
    String record = type + "," + amount + "," + LocalDateTime.now() + "," + savingsbalance;
    transactionHistory.add(record);
}

public void printTransactionHistory() {
    System.out.println("Transaction History for " + this.userid + ":");
    for(String tx : transactionHistory) {
        System.out.println(tx);
    }
    }



    public void updateFees() throws IOException{
        minBalanceFee();
        yearlyFee();
        monthlyFee();
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
                        writeSavingsCSV(userid, SavingsID, savingsbalance);
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
                    writeSavingsCSV(userid, SavingsID, savingsbalance);
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

