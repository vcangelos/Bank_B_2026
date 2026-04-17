//Moneymarket is a premium version of MoneyMarket account.
import java.util.Random; //Random is used for the random ID generator  
import java.util.Scanner;
import javax.imageio.IIOException;
import java.io.BufferedWriter; //This helps us write data to the CSV file.
import java.io.IOException; //catch errors if anything silly happens.
import java.nio.file.Files; //to make it easier to access the files read and write functions. Our hasMoney is a static so we need static methods to make the code work. Files work hand to hand with path objects instead of using Strings we could use that which makes it platform independent.
import java.util.ArrayList; //Array list is needed when we don't know the size of an array or when we resize an array if you see MoneyIDexists I used array list to capture all the columns and use it to compare with the current MoneyMarket ID with the MoneyMarket ID in the current array list. Something like this psuedocode currentMoneyID = currentarraylistMoneyMarket.
import java.util.List;
import java.nio.file.Path; //This function is used to find the file you want for example I'm using this to find my MoneyMarket.csv
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption; //we don't want to overwrite when we create a MoneyMarket ID account we want to append.
import java.sql.Driver;
import java.io.BufferedReader; //is used to read line by line
import java.time.temporal.ChronoUnit;
import java.time.*;
import java.time.format.DateTimeFormatter;

//CURRENT WORK:  DriversLicense: almost done. 



public class moneyMarket {
    /*** Variables ***/
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
    private String MoneyID; //MoneyMarket ID is a unique verification method to see if the user has a MoneyMarket account or not.
    private double currentwithdraw = 0;
    private boolean isLogged = false;
    private boolean isEmployee = false;
    private LocalDate datecreation;
    private boolean hasMoney;
    private boolean isAccountClosed;

    
    

    /*transaction data*/
    private final List<String> transactionHistory = new ArrayList<>();

    /* Static Final variables */
    private static final Path csvPath = Path.of("Money.csv"); // fine the path for the CSV file
    private static final Path csvPathFee = Path.of("MoneyFee.csv"); // measuring monthly
    private static final Path csvCustomerInfo = Path.of("customerinfo.csv");
    private static final Path csvEmployeecsv= Path.of("employeecards.csv");
    private static final Path csvEmployeeMoneyMarketcsv= Path.of("EmployeeMoneyMarket.csv");
    private static final Path isEmployeeCSV = isEmployee ? csvEmployeeMoneyMarketcsv : csvPath;

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
    private static int COL_DATECREATED= 4;

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
       balance = MoneyMarketamount;// TODO I need to create a method that checks in the CSV file, if the userID has
                                  // a MoneyMarket account or not if not then request back to them they don't have it.
                                      // Request the User if he wants to create a MoneyMarket account.
   }
   /*
    * CHECK IF THE USER HAS A MoneyMarket ACCOUNT THE RETURN FUNCTION WILL BE THE FINAL
    * SIGNAL, use this inside createMoneyMarket or existingMoneyMarket
    */
   public static boolean userIDExists(String userID) throws IOException { //hasMoney or user ID //lets use this as a chance to create a boolean employee.
        Path csvEmployeeornot = isEmployee(userID) ? csvEmployeeMoneyMarketcsv : csvPath;
        
        try (BufferedReader reader = Files.newBufferedReader(csvEmployeeornot)) {
           reader.readLine(); //skip the header
           String line; //String line not initialized yet
           while ((line = reader.readLine()) != null) {
               String[] columnsplit = line.split(",",-1); //split line into 3 columns instead of one huge string because we don't want that.
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
           String[] columnsplit = line.split(",",-1);
           if (columnsplit.length == 0) continue; // skip malformed lines
           if (columnsplit[0].trim().equals(MoneyID.trim())) {
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
        try(BufferedReader read = Files.newBufferedReader(isEmployeeCSV); BufferedWriter writetemp = Files.newBufferedWriter(temp)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",",-1);
                if(datacur[0].trim().equals(userID) && datacur[1].trim().equals(MoneyID)){
                    
                    datacur[2] = String.valueOf(newbalance);
                    writetemp.write(String.join(",", datacur));
                }
                else{
                    writetemp.write(line);
                }
                writetemp.newLine();
            }
        }
        Files.move(temp, isEmployeeCSV, StandardCopyOption.REPLACE_EXISTING);
   }
   //under here is a customerinfo read for last name in customer info.
   public static char ReadCustomerinfolastnamechar(int column, String userID) throws IOException
     {    
        try(BufferedReader read = Files.newBufferedReader(csvCustomerInfo)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",", -1);
                if(datacur[0].trim().equals(userID)){ //column 0 which is userID
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
      public static String ReadCustomerinfo(int column, String userID) throws IOException
     {    
        try(BufferedReader read = Files.newBufferedReader(csvCustomerInfo)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",", -1);
                if(datacur[0].trim().equals(userID)){ //column 0 which is userID
                    /*for(String Value:datacur){
                    System.out.println(Value + ", ");
                    }*/
                   if(column < 0 || column > datacur.length)
                   {
                    return null;
                   }
                   return datacur[column].trim();
                }
            }
        }
        return null;
   }
   

      public static void writeCustomerinfo(boolean hasMoney, String userID) throws IOException //make a automatic writing system.
   {
        Path temp = Files.createTempFile("temp", ".csv");
        try(BufferedReader read = Files.newBufferedReader(csvCustomerInfo); BufferedWriter writetemp = Files.newBufferedWriter(temp)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",", -1);
                if(datacur[0].trim().equals(userID)){
                    /*for(String Value:datacur){
                    System.out.println(Value + ", ");
                    }*/
                    datacur[9] = String.valueOf(hasMoney);
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


   public static boolean isEmployee(String EmployeeID) throws IOException{ //This method checks if there is the userID in employee.csv if that appears there then this is an employee MoneyMarket account.
       try (BufferedReader reader = Files.newBufferedReader(csvEmployeecsv)) {
       reader.readLine(); // skip header
       String line;
       while ((line = reader.readLine()) != null) {
           ArrayList<String> data = csvParsing.parseLine(line);
            if(line.trim().isEmpty())
            {
                continue;
            }
   
            if (data.size() > 0 && data.get(COL_USERID).equals(EmployeeID)) { //read column 0 and lines that match with the employeeID
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
            if(MoneyMarketamount <= 0 || userID == null || userID.isEmpty())
            {
                System.out.println("Error: Type a proper amount.");
                return null;
            }

            if(birthCertificate(userID))
            {
                System.out.println("Birth certificate verified successfully.");
            }
            else
            {
                System.out.println("Birth certificate verification failed. Account creation cancelled.");
                return null;
            }

            boolean isEmployee_t = isEmployee(userID);
            String MoneyID = RandomIDGenerator();
            String phoneNum = getPhonenumber(userID);
            LocalDate today = LocalDate.now(); //date creation for the account.
            String date = today.toString();
            String socialSecurity = getSocialSecurity(userID);
            String DL = RandomIDGeneratorDriversLicense(userID, isEmployee_t); //DL is drivers license by the way.
           if((phoneNum == null || socialSecurity == null) || (phoneNum.isEmpty() || socialSecurity.isEmpty())){
            System.out.println("Error: Either your phone number or social security number is corrupted or doesn't exist.");
            return null;
            }
           moneyMarket account = null; //added null so nothing bad can happen such as unitialization.
            if (MoneyMarketamount == minimumbalance) {
                account = new moneyMarket();
                account.userID = userID;
                account.setMoneyID(MoneyID);
                account.isEmployee = isEmployee_t;
                account.phoneNumber = phoneNum;
                account.driversLicense = DL;
                account.hasMoney = true;
            } else if (MoneyMarketamount <= maximumbalance && MoneyMarketamount > minimumbalance) {
                account = new moneyMarket(userID, MoneyMarketamount);
                account.setMoneyID(MoneyID);
                account.phoneNumber = phoneNum;
                account.driversLicense = DL;
                account.hasMoney = true;
                account.isEmployee = isEmployee_t;
           }
           else {

                System.out.println("The MoneyMarket amount has to be in the range of 2500-5000");
                return null; //print back that the amount isn't correct and return null to stop the account creation.
                
           }
           Path currentCSV = isEmployee(userID) ? csvEmployeeMoneyMarketcsv : csvPath;
           try (BufferedWriter bw = Files.newBufferedWriter(currentCSV, StandardOpenOption.APPEND)) {
                
               bw.write(userID + "," + MoneyID + "," + account.balance + "," + DL + "," + date);
               bw.newLine(); // make a new line when written.
           }
           writeCustomerinfo(account.hasMoney, account.getuserID());
           return account;
   }

   public static boolean removalAccountforFeecsv(String userID, String MoneyID) throws IOException { //removal of fee csv and money or employee csv
    boolean isEmployee_t = isEmployee(userID);
    Path csvToUse = isEmployee_t ? csvEmployeeMoneyMarketcsv : csvPath;
    Path tempfee = Files.createTempFile("tempfee", ".csv");
    Path tempmande = Files.createTempFile("tempmande", ".csv"); //"mande" means main csv which is money.csv or employee money market csv.
    boolean foundfee = false;
    boolean foundmande = false;

    try (BufferedReader reader = Files.newBufferedReader(csvPathFee);
         BufferedWriter writer = Files.newBufferedWriter(tempfee)) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",", -1);
            if (data.length > 0 && data[0].trim().equals(MoneyID)) {
                foundfee = true;
                continue;
            }
            writer.write(line);
            writer.newLine();
        }
    }

    try (BufferedReader reader = Files.newBufferedReader(csvToUse);
         BufferedWriter writer = Files.newBufferedWriter(tempmande)) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",", -1);
            if (data.length > 1 && data[0].trim().equals(userID) && data[1].trim().equals(MoneyID)) {
                foundmande = true;
                continue;
            }
            writer.write(line);
            writer.newLine();
        }
    }

    if (foundfee) {
        Files.move(tempfee, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
    } else {
        Files.deleteIfExists(tempfee);
    }

    if (foundmande) {
        Files.move(tempmande, csvToUse, StandardCopyOption.REPLACE_EXISTING);
    } else {
        Files.deleteIfExists(tempmande);
    }

    if (foundfee || foundmande) {
        System.out.println("Account removal entries deleted successfully.");
        return true;
    }

    System.out.println("No matching account data found to remove.");
    return false;
   }


   public static moneyMarket closeMoneyMarket(String userID, String MoneyID) throws IOException{
    if(userIDExists(userID) || employeeIDExists(userID))
    {
        List<moneyMarket> accountlist = loadallaccounts(userID);
        if (accountlist == null || accountlist.isEmpty()) {
            System.out.println("No accounts found for this user.");
            return null;
        }
        moneyMarket account = pickAccount(acc);
        if (account != null) {
            if(account.updateNegativeBalance()){

            }else
            {
               System.out.println("Account "); 
            }
            System.out.println("account closed");
            return account;
        } else {
            System.out.println("Account selection cancelled.");
            return null;
        }

    }
    else
    {
        System.out.println("Account doesn't exist, create it.");
        return null;
    }
   }
public static void earlyClosureMoneyMarketAccount(moneyMarket acc, String moneyID) throws IOException {
    if (acc == null) {
        System.out.println("There is no Account selected.");
        return;
    }

    LocalDate today = LocalDate.now();
    long daysOpen = ChronoUnit.DAYS.between(acc.getDateCreated(), today);

    try {
        double fee = earlyClosureCalculator(daysOpen);

        double balance = acc.getMoneyMarket();

        if (balance < fee) {
            System.out.println("Insufficient funds to cover early closure fee.");
            return;
        }

        acc.setMoneyMarket(balance - fee);

        System.out.printf("Early closure fee applied: $%.2f%n", fee);
        System.out.printf("Remaining balance: $%.2f%n", acc.getMoneyMarket());

    } catch (IllegalArgumentException e) {
        System.out.println("Error: " + e.getMessage());
    }
}
private static double earlyClosureCalculator(long days) {
    if (days < 0) {
        throw new IllegalArgumentException("Days cannot be negative");
    }

    double fee;

    if (days <= MAX_DAY / 2) {
        fee = 50.0;
    } 
    else if (days <= MAX_DAY) {
        fee = 20.0;
    } 
    else {
        fee = 0.0;
    }

    if (fee > 0) {
        fee = Math.max(MIN_FEE, Math.min(fee, MAX_FEE_LIMIT));
    }

    return fee;
}
   public static List<moneyMarket> OpenmoneyMarket(String userID) throws IOException{ //use static list to open MULTIPLE accounts during run time lets say if userid has more than 6 accounts 
    if(userIDExists(userID) || employeeIDExists(userID)) //c
    {
        List<moneyMarket> acc = new ArrayList<>();
        Path csvToUse = isEmployee(userID) ? csvEmployeeMoneyMarketcsv : csvPath;

        try(BufferedReader readlines = Files.newBufferedReader(csvToUse)){
            readlines.readLine();
            String currentline;
            while((currentline = readlines.readLine()) != null){
                String[] currentdata = currentline.split(",", -1);
                if(currentdata[0].trim().equals(userID))
                {

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

    public static boolean DriversLicenseExists(String DriversID, boolean isEmployee) throws IOException{ //Generate a unique drivers ID. No same ID distributed.
        Path choiceCSV;
        if(isEmployee){
            choiceCSV = csvEmployeeMoneyMarketcsv;
        }
        else{
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



   public boolean getNegativeBalance() throws IOException {

    LocalDate today = LocalDate.now();
    boolean accountPaid = true;

    Path temp = Files.createTempFile("temp", ".csv");

    try (BufferedReader reader = Files.newBufferedReader(csvPath);
         BufferedWriter writer = Files.newBufferedWriter(temp)) {

        String line;
        boolean found = false;

        while ((line = reader.readLine()) != null) {

            String[] data = line.split(",", -1);

            if (data.length > 0 && data[0].equals(userID)) {
                found = true;

                double currentBalance = Double.parseDouble(data[2]);

                boolean accountClosed = data.length > 6 && Boolean.parseBoolean(data[6]);
                String negativeTimestamp = data.length > 7 ? data[7] : "";


            
                if (currentBalance < 0 && negativeTimestamp.isEmpty()) {
                    negativeTimestamp = today.toString();
                }

            if (!negativeTimestamp.isEmpty()) {

            }
                if (currentBalance < 0 && !negativeTimestamp.isEmpty()) {

                    LocalDate negativeDate = LocalDate.parse(negativeTimestamp);
                    long days = ChronoUnit.DAYS.between(negativeDate, today);
                                    
                
                    if (days >= 30) {
                        System.out.println(
                                "Account has been negative for 30+ days. Must repay before closure."
                        );
                        accountPaid = false;

                    }
                }

                if (currentBalance >= 0 && !negativeTimestamp.isEmpty()) {

                    LocalDate negativeDate = LocalDate.parse(negativeTimestamp);
                    long days = ChronoUnit.DAYS.between(negativeDate, today);
                    if (days >= 30) {
                        System.out.println("Debt cleared AFTER overdue. Closing account.");
                        accountClosed = true;
                    } else {
                        System.out.println("Debt cleared within 30 days. Account stays open.");
                        accountClosed = false;
                    }

                    negativeTimestamp = "";
                    }
            }

                }

    Files.move(temp, csvPath, StandardCopyOption.REPLACE_EXISTING);

    if(accountClosed == true)
    {
        closeMoneyMarket(userID, MoneyID);
    }
    return accountPaid;
}

public boolean updateNegativeBalance() throws IOException {

    LocalDate today = LocalDate.now();
    boolean accountPaid = true;

    Path temp = Files.createTempFile("temp", ".csv");

    try (BufferedReader reader = Files.newBufferedReader(csvPath);
         BufferedWriter writer = Files.newBufferedWriter(temp)) {

        String line;
        boolean found = false;

        while ((line = reader.readLine()) != null) {

            String[] data = line.split(",", -1);

            if (data.length > 0 && data[0].equals(userID)) {
                found = true;

                double currentBalance = Double.parseDouble(data[2]);

                boolean accountClosed = data.length > 6 && Boolean.parseBoolean(data[6]);
                String negativeTimestamp = data.length > 7 ? data[7] : "";


            
                if (currentBalance < 0 && negativeTimestamp.isEmpty()) {
                    negativeTimestamp = today.toString();
                }

            if (!negativeTimestamp.isEmpty()) {

            }
                if (currentBalance < 0 && !negativeTimestamp.isEmpty()) {

                    LocalDate negativeDate = LocalDate.parse(negativeTimestamp);
                    long days = ChronoUnit.DAYS.between(negativeDate, today);
                                    
                
                    if (days >= 30) {
                        System.out.println(
                                "Account has been negative for 30+ days. Must repay before closure."
                        );
                        accountPaid = false;

                    }
                }

                if (currentBalance >= 0 && !negativeTimestamp.isEmpty()) {

                    LocalDate negativeDate = LocalDate.parse(negativeTimestamp);
                    long days = ChronoUnit.DAYS.between(negativeDate, today);
                    if (days >= 30) {
                        System.out.println("Debt cleared AFTER overdue. Closing account.");
                        accountClosed = true;
                    } else {
                        System.out.println("Debt cleared within 30 days. Account stays open.");
                        accountClosed = false;
                    }

                    negativeTimestamp = "";
                    }

                            writer.write(String.join(",",
                                    data[0], // userid
                                    data[1], // MoneyMarketID
                                    String.format("%.2f", currentBalance),
                                    data[3], // DriversLicense
                                    data[4], // datecreation
                                    data[5], // birthCertificateID
                                    String.valueOf(accountClosed),
                                    negativeTimestamp
                            ));

                            writer.newLine();
                            continue;
                        }

                        writer.write(line);
                        writer.newLine();
                    }

                }

    Files.move(temp, csvPath, StandardCopyOption.REPLACE_EXISTING);

    if(accountClosed == true)
    {
        closeMoneyMarket(userID, MoneyID);
    }
    return accountPaid;
}
    public static boolean birthCertificateExists(String BCID, boolean isEmployee) throws IOException{ //Generate a unique birth certificate ID. No same ID distributed.
        Path Csvchoice;
        if(isEmployee){
            Csvchoice = csvEmployeeMoneyMarketcsv;
        }
        else{
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
   
   public static String RandomIDGeneratorDriversLicense(String userID, boolean isEmployee) throws IOException{
   Random rand = new Random();
   char firstchar = ReadCustomerinfolastnamechar(2, userID);
   String DriversID;
   do{
    if(firstchar == '\0')
    {
        return null;
    }
    firstchar = Character.toUpperCase(firstchar);
    StringBuilder string = new StringBuilder();
    string.append(firstchar);

    for(int i = 0; i<14; i++){
        string.append(rand.nextInt(10));
    }
    DriversID = string.toString();

   }while(DriversLicenseExists(DriversID, isEmployee));
    return DriversID;    
   }
      public static String RandomIDBirthCertificate(String userID) throws IOException{
   Random rand = new Random();
   boolean isEmployee_t = isEmployee(userID);
   String BC = "BC-";
   String birthCertificate;
   do{
    StringBuilder string = new StringBuilder();
    string.append(BC);

    for(int i = 0; i<14; i++){
        string.append(rand.nextInt(10));
    }
    birthCertificate = string.toString();

   }while(birthCertificateExists(birthCertificate, isEmployee_t));
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

        while (true) {
            System.out.print("Enter first name: ");
            firstName = scanner.nextLine();
            if (firstName.equalsIgnoreCase("exit")) {
            System.out.println("Exiting birth certificate verification.");
            return false;
            }   
            if (firstNameFromCSV != null && firstName.equalsIgnoreCase(firstNameFromCSV)) {
                break;
            }
            System.out.println("This is not your first name. Please try again.");
        }

        while (true) {
            System.out.print("Enter last name: ");
            lastName = scanner.nextLine();
            if (lastName.equalsIgnoreCase("exit")) {
            System.out.println("Exiting birth certificate verification.");
            return false;
            }
            if (lastNameFromCSV != null && lastName.equalsIgnoreCase(lastNameFromCSV)) {
                break;
            }
            System.out.println("Invalid last name. Please try again.");
        }

    LocalDate expectedDob = null;

    if (DOBfromcsv != null && !DOBfromcsv.isEmpty()) {
        DateTimeFormatter csvFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        expectedDob = LocalDate.parse(DOBfromcsv, csvFormatter);
}

        while (true) {

        System.out.print("Enter date of birth (YYYY-MM-DD) or type 'exit' to quit out of the process: ");
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("exit")) {
            System.out.println("Exiting birth certificate verification.");
            return false;
        }
        try {
            LocalDate dob = LocalDate.parse(input);

            if (dob.isAfter(LocalDate.now())) {
                System.out.println("Date cannot be in the future.");
                continue;
            }



        if (expectedDob != null && !dob.equals(expectedDob)) {
            System.out.println("Date of birth does not match our records. Please try again.");
            continue;
        }
            break;

        } catch (Exception e) {
            System.out.println("Invalid date format. Try again.");
        }
    }

        return true;
    
}



   public static moneyMarket pickAccount(List<moneyMarket> accounts) throws IOException{
    if (accounts == null || accounts.isEmpty()) {
        System.out.println("There is no accounts here.");
        return null;
    }

    Scanner input = new Scanner(System.in);
    while (true) {
        System.out.println("Select an account to view balance:");
        for (int i = 0; i < accounts.size(); i++) {
            moneyMarket account = accounts.get(i);
            System.out.printf("%d: ID=%s | Balance=$%.2f%n", i, account.getMoneyID(), account.getMoneyMarket());
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

   public static List<moneyMarket> loadallaccounts(String userID) throws IOException{
    List<moneyMarket> accounts = new ArrayList<>();
    boolean found = false; //lets use this to protect from errors like null pointers
    Path csvToUse = isEmployee(userID) ? csvEmployeeMoneyMarketcsv : csvPath;
    try (BufferedReader reader = Files.newBufferedReader(csvToUse)) {
        reader.readLine(); // skip header
        String line;

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",", -1);
            if(data[0].trim().equals(userID)){
                moneyMarket account = new moneyMarket();
                account.userID = data[0];
                account.balance = Double.parseDouble(data[2]);
                accounts.add(account);
                found = true;
            }
            
        }
        if(!found){
            System.out.println("No accounts found for this user.");
            return null;
        }
    }
    return accounts;
   }
   public static String RandomIDGenerator() throws IOException // make the randomIDGenerator a static so it doesn't belong to an object but an standard ID generator for MoneyMarket ids
   {
       Random rand = new Random(); // rand can generate random numbers
       String ID; // make ID String so we can easily manipulate it in CSV like reading or writing
                  // it.
       do {
           long number = (MIN) + (long) (rand.nextDouble() * (MAX - MIN + 1)); //the random generator that is in the range of min-max
           ID = String.valueOf(number); // convert number to String so ID can equal to that string.
       } while (MoneyIDExists(ID)); // Check if there is any MoneyMarket ID like it in the CSV file.
       return ID;
   }
   public static boolean MoneyIDExists(String MoneyID) throws IOException{ //MoneyID is used in the random generator so it wouldn't generate the same MoneyMarket ID as another persons MoneyMarket ID.
       Path[] csvFiles = new Path[] { csvPath, csvEmployeeMoneyMarketcsv };
       for (Path csv : csvFiles) {
           try (BufferedReader reader = Files.newBufferedReader(csv)) {
               reader.readLine(); // this line skips the header for example (userID,MoneyID,MoneyMarket)
               String line;
               while ((line = reader.readLine()) != null) {
                   String[] currentdata_to_col = line.split(",", -1);
                   if (currentdata_to_col.length > 1 && currentdata_to_col[1].equals(MoneyID)) {
                       return true;
                   }
               }
           }
       }
       return false; // return false if there is no MoneyMarket ID equal to another MoneyMarket ID
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
       public static String getPhonenumber(String userID) throws IOException{ // Grab phone number by searching through CSV
        try(BufferedReader readfile = Files.newBufferedReader(csvCustomerInfo)){
        String line;
        
        
        while((line = readfile.readLine())!=null){
            boolean valid = true;
            String[] dataline = line.split(",", -1);
            if(dataline.length > 0 && dataline[0].trim().equals(userID)){
                String phonenum = dataline.length > 6 ? dataline[6] : "";
                phonenum = phonenum.replaceAll("[^0-9]", "");
                if(phonenum.length() == 10){
                    

                    for(int i = 0; i < phonenum.length(); i++){
                        if(!Character.isDigit(phonenum.charAt(i))){
                            valid = false;
                            break;
                        }
                    }
                    if(valid){
                        return phonenum;
                    }
                }
            }
        }
        }
        return null;

       }
        public static String getSocialSecurity(String userID) throws IOException{ // Grab phone number by searching through CSV
        try(BufferedReader readfile = Files.newBufferedReader(csvCustomerInfo)){
        String line;
        while((line = readfile.readLine())!=null){
            boolean valid = true;
            String[] dataline = line.split(",", -1);
            if(dataline.length > 0 && dataline[0].trim().equals(userID)){
                String socialSecurity = dataline.length > 3 ? dataline[3] : ""; //social security is in the fourth column. 
                socialSecurity = socialSecurity.replaceAll("[^0-9]", "");
                if(socialSecurity.length() == 9){ //it has to be equal to 9 otherwise return a null to interrupt account creation

                    for(int i = 0; i < socialSecurity.length(); i++){ //just in case replaceall missed something.
                        if(!Character.isDigit(socialSecurity.charAt(i))){ //this is copied from phone number code.
                            valid = false;
                            break;//break out of here and return a null few lines down if there is a character inside a socialsecurity number
                        }
                    }
                    if(valid){ //valid helps us here by checking if characters are in the digits or not
                        return socialSecurity;
                    }
                }
            }
        }
        }
        return null;
       }
    //END

   public void setMoneyID(String MoneyID)
   {
       this.MoneyID = MoneyID;
   }
   public double depositMoneyMarket(double depositamt) { //how much you deposit.
       if (depositamt > 0) {
           return balance += depositamt;
       } else {
           System.out.println("Deposit has to be a positive.");
       }
       return balance;
   }
   public String getuserID() { //testing to see userID string
       return userID;
   }
   public String getMoneyID(){
   return MoneyID;
   }
   public LocalDate getDateCreated(){
   return datecreation; 
   }
   public static double getBalance(String userID)throws IOException{ //Use this when you don't want to create an account and just want to know the balance from the CSV
        List<moneyMarket> accountlist = loadallaccounts(userID);
        if (accountlist == null || accountlist.isEmpty())
        {
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
   // ************Withdraw system**************/
   public void withdrawMoneyMarket(double amt) throws IOException // withdraw system that records the amount. //right now this isn't use in the code at all.
   {
    Path currentCSV = isEmployee(userID) ? csvEmployeeMoneyMarketcsv : csvPath;
    LocalDate today = LocalDate.now();
    String currentMonth = today.getYear() + "-" + String.format("%02d", today.getMonthValue());
    Path temp = Files.createTempFile("tempmoney", ".csv");
    try(BufferedReader read = Files.newBufferedReader(csvPathFee); BufferedWriter writer = Files.newBufferedWriter(temp))
    {
    String line;
    while((line = read.readLine())!= null)
    {
        String[] data = line.split(",", -1);
        if(data[0].trim().equals(MoneyID))
        {
            String lastMinMonth = data.length > 1 ? data[1] : "";
            String lastMonthlyMonth = data.length > 2 ? data[2] : "";
            double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : balance;
            String lastYear = data.length > 4 ? data[4] : "";
            String lastInterest = data.length > 5 ? data[5] : "";
            double withdrawlimit = data.length > 6 ? Double.parseDouble(data[6]) : currentwithdraw;

            if (!currentMonth.equals(lastMonthlyMonth)) {
                currentwithdraw = 0;
            }
            boolean successfulWithdraw = false;
            if (amt <= 0)
            {
                System.out.println("Amount has to be greater than 0, please retry.");
            }
            else if (amt > balance)
            {
                System.out.println("You can't withdraw an amount greater than the moneymarket balance.");
            }
            else{
                if (currentwithdraw >= MAXWITHDRAW)
                {
                    double totalCost = amt + overlimitfee;
                    if (totalCost > balance)
                    {
                        System.out.println("Insufficient funds to cover the withdrawal plus overlimit fee.");
                    }
                    else
                    {
                        currentwithdraw++;
                        balance -= amt;
                        balance -= overlimitfee;
                        successfulWithdraw = true;
                    }
                }
                else{
                    currentwithdraw++;
                    balance -= amt;
                    successfulWithdraw = true;
                }
            }

            if (successfulWithdraw) {
                write
                writer.write(String.join(",",
                        MoneyID,
                        lastMinMonth,
                        lastMonthlyMonth,
                        String.format("%.2f", balance),
                        lastYear,
                        lastInterest,
                        String.valueOf(currentwithdraw)
                ));
                writer.newLine();
            } else {
                writer.write(line);
                writer.newLine();
            }
            continue;

        }
        writer.write(line);
        writer.newLine();

    }

    }
    Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
    
   }

 public double transfer(List<CheckingAccount.Account> possibleDestinations, Scanner scanner, boolean transfer, double value){ //we used list for checking account because there is multiple OR one checking account per user. USE VALUE ONLY IF IT'S OUTSIDE CHECKING ACCOUNT TRANSFER FOR EXAMPLE loans to savings.
    //transfer true from source -> savings
    if(transfer == true){
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
    }*

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
        writeMoneyCSV(userID,MoneyID, balance, isEmployee);
        applyInterest();
    }
// ************ FEES AND INTEREST METHODS ************
// This is the framework for all the fees such as monthly fee, minimum balance fee, and yearly fee.
// They'll be used in one method called updateFees
// MoneyID, LastMinMonth, LastMonthlyMonth, LowestBalance, LastYear, LastInterest

public double minBalanceFee() throws IOException { //Min month goal is to write in the csv a starting month for example "2026-03" if todays month is 2026-04 it'll check the lowest balance currently in
    LocalDate today = LocalDate.now();
    String currentMonth = today.getYear() + "-" + String.format("%02d", today.getMonthValue());

    Path temp = Files.createTempFile("temp", ".csv");
    boolean found = false;

    try (BufferedReader reader = Files.newBufferedReader(csvPathFee);
         BufferedWriter writer = Files.newBufferedWriter(temp)) {

        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",", -1);

            if (data[0].equals(MoneyID)) {

                // Retrieve previous CSV data safely
                String lastMinMonth = data.length > 1 ? data[1] : "";
                String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : balance;
                String lastYear = data.length > 4 ? data[4] : "";
                String lastInterest = data.length > 5 ? data[5] : "";
                double withdrawlimit = data.length > 6 ? Double.parseDouble(data[6]) : currentwithdraw; //withdraw limit is being added.

                //check if anything isn't null and check if lastminmonth isn't empty and make lastminmonth not equal to current month otherwise skip this.
                if (lastMinMonth != null && !lastMinMonth.isEmpty() && !lastMinMonth.equals(currentMonth)) {
                    if (balance < minimumbalance) {
                        balance -= minBalanceFee;
                        writeMoneyCSV(userID, MoneyID, balance, isEmployee);
                    }
                    lastMinMonth = currentMonth;
                    currentBalance = balance;
                } else {
                    //update current balance
                        currentBalance = balance;
                    
                }

                //write updated line
                writer.write(String.join(",",
                        MoneyID,
                        lastMinMonth != null ? lastMinMonth : "",
                        lastMonthlyMonth != null ? lastMonthlyMonth : "",
                        String.format("%.2f",currentBalance),
                        lastYear != null ? lastYear : "",
                        lastInterest != null ? lastInterest : "",
                        String.format("%.0f",withdrawlimit)
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
    YearMonth currentMonth = YearMonth.now();

    Path tempfile = Files.createTempFile("csv_temp", ".csv");
    boolean found = false;

    try (BufferedReader reader = Files.newBufferedReader(csvPathFee);
         BufferedWriter writer = Files.newBufferedWriter(tempfile)) {

        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            if (data[0].trim().equals(MoneyID)) {
                found = true;

                //load in csv data for other values.
                String lastMinMonth = data.length > 1 ? data[1] : "";
                String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : balance;
                String lastYear = data.length > 4 ? data[4] : "";
                String lastInterest = data.length > 5 ? data[5] : "";
                double withdrawlimit = data.length > 6 ? Double.parseDouble(data[6]) : currentwithdraw;

                YearMonth lastMonth;
                try{
                    lastMonth = (lastMonthlyMonth == null || lastMonthlyMonth.isEmpty()) ? currentMonth : YearMonth.parse(lastMonthlyMonth);
                }catch(Exception e){
                    lastMonth = currentMonth;
                }
                long monthsMissed = ChronoUnit.MONTHS.between(lastMonth, currentMonth);
                // Apply monthly fee if a month has passed
                if (monthsMissed > 0) {
                    balance -= monthlyFee * monthsMissed;
                    balance = currentBalance;
                    lastInterest = currentMonth.toString();
                }

                writer.write(String.join(",", //rewrite all data such as last min month and so on.
                        MoneyID,
                        lastMinMonth != null ? lastMinMonth : "",
                        lastMonthlyMonth,
                        String.format("%.2f",currentBalance),
                        lastYear != null ? lastYear : "",
                        lastInterest != null ? lastInterest : "",
                        String.format("%.0f",withdrawlimit))
                );
                writer.newLine();
                continue;
            }

            writer.write(line);
            writer.newLine();
        }
    }

    Files.move(tempfile, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
    return balance;
}

public double yearlyFee() throws IOException { //every year passing the user gets a fee.
    String currentYear = String.valueOf(LocalDate.now().getYear());

    Path temp = Files.createTempFile("temp", ".csv");

    try (BufferedReader reader = Files.newBufferedReader(csvPathFee);
         BufferedWriter writer = Files.newBufferedWriter(temp)) {

        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",", -1);

            if (data[0].equals(MoneyID)) {

                String lastMinMonth = data.length > 1 ? data[1] : "";
                String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : balance;
                String lastYear = data.length > 4 ? data[4] : "";
                String lastInterest = data.length > 5 ? data[5] : "";
                double withdrawlimit = data.length > 6 ? Double.parseDouble(data[6]) : currentwithdraw;
                
                if(lastInterest == null || lastInterest.isEmpty()){
                    lastInterest = currentYear.toString();
                }
                long YearsMissed = ChronoUnit.YEARS.between(Year.parse(lastYear.isEmpty() ? currentYear : lastYear), Year.parse(currentYear));
                // Apply yearly fee if year has changed
                if (YearsMissed > 0) {
                    balance -= yearlyFee;
                    writeMoneyCSV(userID, MoneyID, balance);
                }
                if(lastYear.isEmpty() ){
                    lastYear = currentYear;
                }
                writer.write(String.join(",",
                        MoneyID,
                        lastMinMonth != null ? lastMinMonth : "",
                        lastMonthlyMonth != null ? lastMonthlyMonth : "",
                        String.format("%.2f",currentBalance),
                        lastYear,
                        lastInterest != null ? lastInterest : "",
                        String.format("%.0f",withdrawlimit))
                );
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

public double applyInterest() throws IOException { //apply interest over months


    Path temp = Files.createTempFile("temp", ".csv");

    YearMonth currentMonth = YearMonth.now();

    try (BufferedReader reader = Files.newBufferedReader(csvPathFee);
         BufferedWriter writer = Files.newBufferedWriter(temp)) {

        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            
            if (data[COL_USERID].equals(MoneyID)) {
                

                String lastMinMonth = data.length > 1 ? data[1] : ""; 
                String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                double csvBalance = data.length > 3 ? Double.parseDouble(data[3]) : balance;
                String lastYear = data.length > 4 ? data[4] : "";
                String lastInterest = data.length > 5 ? data[5] : "";
                double withdrawlimit = data.length > 6 ? Double.parseDouble(data[6]) : currentwithdraw;
                if(lastInterest == null || lastInterest.isEmpty()){
                    lastInterest = currentMonth.toString();
                }
                //Calculate the Interest by number of months that was missed and power that by the monthly interest rate.
                YearMonth lastMonth;
                try{
                    lastMonth = (lastInterest == null || lastInterest.isEmpty()) ? currentMonth : YearMonth.parse(lastInterest);
                }catch(Exception e){
                    lastMonth = currentMonth;
                }
                long monthsMissed = ChronoUnit.MONTHS.between(lastMonth, currentMonth);
                if (monthsMissed > 0) {
                    double monthlyRate = interestamount / 12.0;
                    csvBalance = csvBalance * Math.pow(1+monthlyRate,monthsMissed);                
                    balance = csvBalance;
                               
                    lastInterest = currentMonth.toString();
                }

                writer.write(String.join(",",
                        MoneyID,
                        lastMinMonth,
                        lastMonthlyMonth,
                        String.format("%.2f", balance),
                        lastYear,
                        lastInterest,
                        String.format("%.0f", withdrawlimit)
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

       
}
