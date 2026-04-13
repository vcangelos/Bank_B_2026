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
import java.time.LocalDate;

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
    private boolean isLogged;
    private boolean isEmployee = false;
    private boolean hasMoney;
    private LocalDate datecreation;
    
    

    /*transaction data*/
    private final List<String> transactionHistory = new ArrayList<>();

    /* Static Final variables */
    private static final Path csvPath = Path.of("Money.csv"); // fine the path for the CSV file
    private static final Path csvPathFee = Path.of("MoneyFee.csv"); // measuring monthly
    private static final Path csvCustomerInfo = Path.of("customerinfo.csv");
    private static final Path csvEmployeecsv= Path.of("employeecards.csv");
    private static final Path csvEmployeeMoneyMarketcsv= Path.of("EmployeeMoneyMarket.csv");

    private static final long MAX = 599_999_999_999L; // This is the maximum for the MoneyMarket number generator 3000000000000 is MoneyMarket Account UNIQUE ID this is only for MoneyMarket.
                                                     
                                               
    private static final long MIN = 500_000_000_000L; // minimum for the random number generator
    private static final double minimumbalance = 2500;
    private static final double maximumbalance = 5000;
    private static final double accountclosure = 5;
    private static final double MAXWITHDRAW = 6;
    private final double interestamount = 0.005; //interest is 5% 


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
    public static boolean employeeIDExists(String userID) throws IOException { //checkifMoneyIDexist
       try (BufferedReader reader = Files.newBufferedReader(csvEmployeeMoneyMarketcsv)) {
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
    public static boolean MoneyMarketIDExistsfeefile(String MoneyID) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(csvPathFee)) {
       reader.readLine(); // skip header
       String line;
       while ((line = reader.readLine()) != null) {
           String[] columnsplit = line.split(",");
           if (columnsplit.length == 0) continue; // skip malformed lines
           if (columnsplit[0].trim().equals(MoneyID.trim())) {
               return true; // userID found
           }
       }
   }
   return false; // not found
   }

   //write to csv

   public static void writeMoneyCSV(String userID, String MoneyID, double newbalance, String driversLicense) throws IOException //make a automatic writing system.
   {
        Path temp = Files.createTempFile("temp", ".csv");
        try(BufferedReader read = Files.newBufferedReader(csvPath); BufferedWriter writetemp = Files.newBufferedWriter(temp)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",");
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
        Files.move(temp, csvPath, StandardCopyOption.REPLACE_EXISTING);
   }
   public static void writeEmployeeMoneyMarketCSV(String userID, String MoneyID, double newbalance) throws IOException //make a automatic writing system.
   {
        Path temp = Files.createTempFile("temp", ".csv");
        try(BufferedReader read = Files.newBufferedReader(csvEmployeeMoneyMarketcsv); BufferedWriter writetemp = Files.newBufferedWriter(temp)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",");
                if(datacur[0].trim().equals(userID) && datacur[1].equals(MoneyID)){
                    
                    datacur[2] = String.valueOf(newbalance);
                    writetemp.write(String.join(",", datacur));
                }
                else{
                    writetemp.write(line);
                }
                writetemp.newLine();
            }
        }
        Files.move(temp, csvEmployeeMoneyMarketcsv, StandardCopyOption.REPLACE_EXISTING);
   }

   //under here is a customerinfo read for last name in customer info.
   public static char ReadCustomerinfo(int column, String userID) throws IOException
     {    
        try(BufferedReader read = Files.newBufferedReader(csvCustomerInfo)){
            String line;
            while((line = read.readLine()) != null){
                String[] datacur = line.split(",", -1);
                if(datacur[0].trim().equals(userID)){
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
   public static moneyMarket createmoneyMarket(String userID, double MoneyMarketamount) throws IOException {//
    boolean isEmployee_t = isEmployee(userID);
           
           String MoneyID = RandomIDGenerator();
           String phoneNum = getPhonenumber(userID);
           String socialSecurity = getSocialSecurity(userID);
           String DL = RandomIDGeneratorDriversLicense(userID); //DL is drivers license by the way.
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
            LocalDate today = LocalDate.now(); //date creation for the account.
            String date = today.toString();
              
              
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
                
           }
           Path currentCSV = isEmployee(userID) ? csvEmployeeMoneyMarketcsv : csvPath;
           try (BufferedWriter bw = Files.newBufferedWriter(currentCSV, StandardOpenOption.APPEND)) {
                
               bw.write(userID + "," + MoneyID + "," + account.balance + "," + DL + "," + date);
               bw.newLine(); // make a new line when written.
           }
           writeCustomerinfo(account.hasMoney, account.getuserID());
           return account;
   }
   public static void closeMoneyMarketAccount(List<moneyMarket> acc, String moneyID) throws IOException //early account closure 5-50 it rises depending on the day
   {
        moneyMarket currentaccount = null;
        for(int i = 0; i < acc.size(); i++){

            if(acc.get(i).getMoneyID().equals(moneyID)){
                currentaccount = acc.get(i);
                LocalDate today;
                long daysOpen = java.time.temporal.ChronoUnit.DAYS.between(datecreation, today);

                if (daysOpen < 30) {
                    fee = 50;
                } 
                else if (daysOpen < 180) {
                fee = 20;
                }


                currentaccount.datecreation //make date creation subtract todays date so we can see if it's an enclosure reach and depending on the variable
                break;
            }

        }

    
   }
   public static List<moneyMarket> OpenmoneyMarket(String userID) throws IOException{
    if(userIDExists(userID))
    {
        List<moneyMarket> acc = new ArrayList<>();

        try(BufferedReader readlines = Files.newBufferedReader(csvPath)){
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
                    account.balance = Double.parseDouble(currentdata[2]);
                    account.driversLicense = currentdata[3]
                    account.datecreation = LocalDate.parse(currentdata[4]);
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
    else
    {
        System.out.println("Account doesn't exist, create it.");
        
    }
    return null;
   }

    public static boolean DriversLicenseExists(String DriversID) throws IOException{ //Generate a unique drivers ID. No same ID distributed.
    try (BufferedReader reader = Files.newBufferedReader(csvPath)) {            
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
   public static String RandomIDGeneratorDriversLicense(String userID) throws IOException{
   Random rand = new Random();
   char firstchar = ReadCustomerinfo(2, userID);
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

   }while(DriversLicenseExists(DriversID));
    return DriversID;    
   }
   public static List<moneyMarket> loadallaccounts(String userID) throws IOException{
    List<moneyMarket> accounts = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
        reader.readLine(); // skip header
        String line;

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",", -1);
            if(data[0].trim().equals(userID)){
                moneyMarket account = new moneyMarket();
                account.userID = data[0];
                account.balance = Double.parseDouble(data[2]);
                accounts.add(account);
            }
            
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
   try (BufferedReader reader = Files.newBufferedReader(csvPath)) {            
           reader.readLine(); // this line skips the header for example (userID,MoneyID,MoneyMarket)
           String line;
           while ((line = reader.readLine()) != null) { // as line doesn't equal to NULL (end of file) continue.
               String[] currentdata_to_col = line.split(",", -1);
               if (currentdata_to_col.length > 0 && currentdata_to_col[1].equals(MoneyID)) {
                   return true;
               }
           }
       }                                                                        
       return false; // return false if there is no MoneyMarket ID equal to another MoneyMarket ID
   }
   
   //START Both getMoneyMarket() and setMoneyMarket() are used for debugging.
       public double getMoneyMarket()
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
                System.out.println(java.util.Arrays.toString(dataline));
                String socialSecurity = dataline.length > 3 ? dataline[3] : ""; //social security is in the fourth column. 
                socialSecurity = socialSecurity.replaceAll("[^0-9]", "");
                if(socialSecurity.length() == 9){ //it has to be equal to 9 otherwise return a null to interrupt account creation

                    for(int i = 0; i < socialSecurity.length(); i++){ //just in case replaceall missed something.
                        if(!Character.isDigit(socialSecurity.charAt(i))){
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
   // ************Withdraw system**************/
   public void withdrawMoneyMarket(double amt) throws IOException // withdraw system that records the amount. //right now this isn't use in the code at all.
   {
    LocalDate today = LocalDate.now();
    String currentMonth = today.getYear() + "-" + String.format("%02d", today.getMonthValue());
    Path temp = Files.createTempFile("tempmoney", ".csv");
    try(BufferedReader read = Files.newBufferedReader(csvPathFee); BufferedWriter writer = Files.newBufferedWriter(temp))
    {
    String line;
    while((line = read.readLine())!= null)
    {
        String[] data = line.split(",");
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
            if(amt <= 0)
            {
                System.out.println("Amount has to be greater than 0, please retry.");
            }
            else if(amt > balance)
            {
                System.out.printf("You can't withdraw an amount greater than the moneymarket balance.");
            }
            else{
                
                    if(currentwithdraw >= MAXWITHDRAW)
                    {   currentwithdraw++;
                        balance -= amt;
                        balance -= overlimitfee;
                    }
                    else{
                    currentwithdraw++;
                    balance -= amt;
                    }
            }
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
                continue;

        }
        writer.write(line);
        writer.newLine();

    }

    }
    Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
    
   }

 /*public double transfer(List<CheckingAccount.Account> possibleDestinations, Scanner scanner, boolean transfer, double value){ //we used list for checking account because there is multiple OR one checking account per user. USE VALUE ONLY IF IT'S OUTSIDE CHECKING ACCOUNT TRANSFER FOR EXAMPLE loans to savings.
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
    }*/

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
        moneyMarket.writeEmployeeMoneyMarketCSV(userID, MoneyID, balance);
        
        }
        else{
        moneyMarket.writeMoneyCSV(userID, MoneyID, balance, driversLicense);
        }
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
            String[] data = line.split(",");

            if (data[0].equals(MoneyID)) {
                found = true;

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
                        writeMoneyCSV(userID, MoneyID, balance, driversLicense);
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

        //if nothing was found.
        if (!found) {
            writer.write(String.join(",",
                    MoneyID,
                    currentMonth,
                    "",
                    String.format("%.2f",balance),
                    "",
                    "",
                    ""
            ));
            writer.newLine();
        }
    }

    //replace old CSV with updated temp file.
    Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
    return balance;
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
            if (data[0].trim().equals(MoneyID)) {
                found = true;

                //load in csv data for other values.
                String lastMinMonth = data.length > 1 ? data[1] : "";
                String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : balance;
                String lastYear = data.length > 4 ? data[4] : "";
                String lastInterest = data.length > 5 ? data[5] : "";
                double withdrawlimit = data.length > 6 ? Double.parseDouble(data[6]) : currentwithdraw;

                // Apply monthly fee if a month has passed
                if (!lastMonthlyMonth.isEmpty() && !currentMonth.equals(lastMonthlyMonth)) {
                    balance -= monthlyFee;
                    writeMoneyCSV(userID, MoneyID, balance, driversLicense);
                }
                lastMonthlyMonth = currentMonth;

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

        // First time entry
        if (!found) {
            writer.write(String.join(",",
                    MoneyID,
                    "",
                    currentMonth,
                    String.format("%.2f",balance),
                    "",
                    "",
                    ""
            ));
            writer.newLine();
        }
    }

    Files.move(tempfile, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
    return balance;
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

            if (data[0].equals(MoneyID)) {
                found = true;

                String lastMinMonth = data.length > 1 ? data[1] : "";
                String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : balance;
                String lastYear = data.length > 4 ? data[4] : "";
                String lastInterest = data.length > 5 ? data[5] : "";
                double withdrawlimit = data.length > 6 ? Double.parseDouble(data[6]) : currentwithdraw;

                // Apply yearly fee if year has changed
                if (!lastYear.isEmpty() && !lastYear.equals(currentYear)) {
                    balance -= yearlyFee;
                    writeMoneyCSV(userID, MoneyID, balance, driversLicense);
                }
                lastYear = currentYear;

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

        if (!found) {
            writer.write(String.join(",",
                    MoneyID,
                    "",
                    "",
                    String.format("%.2f",balance),
                    currentYear,
                    "",
                    ""
            ));
            writer.newLine();
        }
    }

    Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
    return balance;
}

public double applyInterest() throws IOException { //apply interest over months
    LocalDate today = LocalDate.now(); //grab todays date so we can compare it with last month
    String interest = today.getYear() + "-" + String.format("%02d", today.getMonthValue());

    Path temp = Files.createTempFile("temp", ".csv");
    boolean found = false; //boolean logic so we can either create or update an existing data such as interest balance

    try (BufferedReader reader = Files.newBufferedReader(csvPathFee);
         BufferedWriter writer = Files.newBufferedWriter(temp)) {

        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");

            if (data[0].equals(MoneyID)) {
                

                String lastMinMonth = data.length > 1 ? data[1] : ""; 
                String lastMonthlyMonth = data.length > 2 ? data[2] : "";
                double currentBalance = data.length > 3 ? Double.parseDouble(data[3]) : balance;
                String lastYear = data.length > 4 ? data[4] : "";
                String lastInterest = data.length > 5 ? data[5] : "";
                double withdrawlimit = data.length > 6 ? Double.parseDouble(data[6]) : currentwithdraw;

                //Interest is applied via month and MoneyMarket is written to MoneyMarket csv so it can be written overtime
                if (!lastInterest.isEmpty() && !lastInterest.equals(interest)) {
                    double monthlyRate = interestamount / 12.0; //interest amount.
                    balance += balance * monthlyRate;
                    currentBalance = balance;
                    System.out.println("p");
                    writeMoneyCSV(userID, MoneyID, currentBalance, driversLicense);
                }
                lastInterest = interest;

                writer.write(String.join(",",
                        MoneyID,
                        lastMinMonth != null ? lastMinMonth : "",
                        lastMonthlyMonth != null ? lastMonthlyMonth : "",
                        String.format("%.2f",currentBalance),
                        lastYear != null ? lastYear : "",
                        lastInterest,
                        String.format("%.0f",withdrawlimit))

                );
                found = true;
                writer.newLine();
                continue;
            }

            writer.write(line);
            writer.newLine();
        }

        // First time entry
        if (!found) {
            writer.write(String.join(",",
                    MoneyID,
                    "",
                    "",
                    String.format("%.2f",balance),
                    "",
                    interest,
                    ""
            ));
            writer.newLine();
        }
    }

    Files.move(temp, csvPathFee, StandardCopyOption.REPLACE_EXISTING);
    return balance;
}

       
}
