
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
import java.nio.file.StandardOpenOption; //we don't want to overwrite when we create a savings ID account we want to append.
import java.io.BufferedReader; //is used to read line by line
import java.time.ZoneId; //Ids such as "America/New York"
import java.time.ZonedDateTime; //the time for that local area.
import java.time.LocalTime; // just local time and not date.
import java.time.Instant; //global time this is very useful for local conversions.
import java.time.LocalDateTime; //convert to localdatetime for example New york eastern time.
import java.time.Duration; //we need duration to subtract both times to see how many hours it is if it is over or equal to 24 hours then send out a fee otherwise don't

public class SavingsAccount {
    /*** Variables ***/
    private double savingsbalance; // TODO this is our savings balance which will start in a range if the customer
                                   // first creates their account in about 100-300 $
    private double overDraftFee = 35; // TODO it'll start with 35$
    private double minBalanceFee = 15; // TODO minimum balance fee is 15 dollars.
    private double monthlyFee = 12; // TODO monthly fee is 12 dollars.
    private double yearlyFee = 48; // TODO 48 dollars.
    //private static csvFile file; // csvFile equals to the path of the CSV file.
    //private static csvFile FeeCheck; // savingsFee will equal to CSVpathFee
    private String userid; // current userID lets say they registered or they were recent the constructor
                           // will use that and make THIS field equal to that.
    private String employeeid;
    private String SavingsID;

    private boolean hasemployee;
    private boolean hasuser;

    /*SavingsFeeCheck variables */
    

    /* Static Final variables */
    private static final Path csvPath = Path.of("Savings.csv"); // fine the path for the CSV file
    private static final Path csvPathFee = Path.of("SavingsFeeCheck.csv"); // measuring 24 hour format.
    private static final long MAX = 199_999_999_999L; // This is the maximum for the savings number generator.
                                                      // //1000000000000 is Savings Account UNIQUE ID this is only for
                                                      // savings.
    private static final long MIN = 100_000_000_000L; // minimum for the random number generator
    private static final double minimumbalance = 100;
    private static final double maximumbalance = 300;
    private static final ZoneId EASTERN_ZONE = ZoneId.of("America/New_York");
    private static final LocalTime CUTOFF = LocalTime.of(17, 0); // 5 PM the Bank operates 9-5PM
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

    public static boolean userIDExists(String userid) throws IOException { //hasSavings or user ID
        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            reader.readLine(); //skip the header
            String line; //String line not initialized yet
            while ((line = reader.readLine()) != null) {
                String[] columnsplit = line.split(","); //split line into 3 columns instead of one huge string because we don't want that.
                ArrayList<String> rowdata = new ArrayList<>(List.of(columnsplit)); //rowdata uses columnsplit for the data for ex. column split is like {"12020", "1929394949", 100.00} array list uses it.
                if (!rowdata.isEmpty() && rowdata.get(0).trim().equals(userid.trim())) { //trim is useful for comparing data when white space exists what it does is removes those white spaces.

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
            if (line.isBlank()) return false; // skip empty lines
            String[] columnsplit = line.split(",", -1); // -1 preserves empty columns
            if (columnsplit.length == 0) continue; // skip malformed lines
            if (columnsplit[0].trim().equals(SavingsID.trim())) {
                return true; // userID found
            }
        }
    }
    return false; // not found
    }

    public static SavingsAccount createSavingsAccount(String userid, double savingsamount) throws IOException {//
        if (userIDExists(userid)) { // NEW WORKS I need to make employee CSV
            throw new IllegalArgumentException("The User Already has a Savings Account.");
        } else {
            // else(hasEmployee()){}
            String SavingsID = RandomIDGenerator();
            SavingsAccount account;
            if (savingsamount == 100) {
                account = new SavingsAccount();
                account.userid = userid;
                account.setSavingsID(SavingsID);
            } else if (savingsamount <= maximumbalance && savingsamount > minimumbalance) {
                account = new SavingsAccount(userid, savingsamount);
                account.setSavingsID(SavingsID);
            } 
            else {
                throw new IllegalArgumentException("The Saivngs amount has to be in the range of 100-300");
            }
            try (BufferedWriter bw = Files.newBufferedWriter(csvPath, StandardOpenOption.APPEND)) {
                bw.write(userid + "," + SavingsID + "," + savingsamount);
                bw.newLine(); // make a new line when written.
            }

            return account;
        }
    }

    public void OpenSavingsAccount()
    {

    }

    // public SavingsAccount createEmployeeSavingsAccount() //Main menu will make a
    // boolean such as
    //{

        // TODO.
    //}

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
                ArrayList<String> currentdata_to_col = csvParsing.parseLine(line);
                if (currentdata_to_col.size() > 2 && currentdata_to_col.get(1).equals(SavingsID)) {
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
    public double withdrawSavings(double amt, String choice) // withdraw system that records the amount.
    {
        switch (choice) // This is going to change the intent for this is if the user picks "Checking"
                        // then subtract savings from the amt that was placed and the checking account
                        // managers add that amt value otherwise if "Savings" we'll add the value from
                        // amt and the checking account people would just subtract on their part.
        {
            case "Checking": // "Checking" means that you're choosing to withdraw savings to checking
                if (amt > 0 && savingsbalance >= amt) {//
                    savingsbalance -= amt;
                } else {
                    System.out.println("Incorrect amount must be less than savings AND greater than 0");
                }
                break;
            case "Savings": // CHECKING to Savings would simply add amt with savings.
                if (amt > 0) {
                    savingsbalance += amt;
                }
                break;
            default:
                System.out.println("Invalid choose either Checking or Savings");
                break;
        }
        return savingsbalance;
    }

    // FEES
    public double minBalanceFee() throws IOException { // If the user is below 100
        
        if (savingsbalance < 100) {
            Instant now = Instant.now();
            System.out.println("Checkpoint!");
            ZonedDateTime eastern = now.atZone(EASTERN_ZONE);
            boolean isDaylight = EASTERN_ZONE.getRules().isDaylightSavings(eastern.toInstant());
            boolean hasPaid = savingsbalance >= 100;                                                                     
            if(!SavingsIDExistsfeefile(getSavingsID())) {
                System.out.println("Test");
                String date = eastern.toLocalDate().toString(); // translate the date for example 3/3/2026 into a string  instead of a object.
                // GOAL HERE IS TO CREATE AN TIMESTAMP IF THE USER IS LESS THAN 100 MAKE SURE TO
                // RECORD TIME if there isn't anything written when they started. -Younes Ziani
                try (BufferedWriter bw = Files.newBufferedWriter(csvPathFee, StandardOpenOption.APPEND)) {
                    bw.write(this.SavingsID + "," + date + "," + eastern.withNano(0).toLocalDateTime() + "," + savingsbalance + "," + hasPaid + "," + isDaylight);            
                    bw.newLine(); // make a new line when written.
                } 
                catch (IOException e) {
                            e.printStackTrace(); // print the error.
                }
            }
            else{
                try(BufferedReader readcsvfee = Files.newBufferedReader(csvPathFee)){
                    readcsvfee.readLine();
                    String data;
                    while((data = readcsvfee.readLine()) !=null){
                        String[] currentdata = data.split(",");
                        if(currentdata != null && currentdata[0].trim().equals(SavingsID)){ //make currentdata current line in the CSV formated something like this SavingsID,date,timeThen,savings,hasPaid,Daylight equal to userID so we can see if that account 
                        
                            LocalDateTime csvtime = LocalDateTime.parse(currentdata[2]);
                            ZonedDateTime previous_time = csvtime.atZone(EASTERN_ZONE);
                            long hours = Duration.between(previous_time.toInstant(), now).toHours(); 
                            if(hours >= 24){
                            this.savingsbalance -= minBalanceFee;
                            }
                        }
                    }
                }
            }
        }        
        else{
            Instant now = Instant.now();
            ZonedDateTime eastern = now.atZone(EASTERN_ZONE);
            boolean isDaylight = EASTERN_ZONE.getRules().isDaylightSavings(eastern.toInstant());
            boolean hasPaid = savingsbalance >= 100;
        }
   
    return 0.00;
    }

    public double overDraftFee() {
        // TODO.
        return 0.00;
    }

}