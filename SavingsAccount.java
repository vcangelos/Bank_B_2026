//TODO USING SAVINGS.CSV IT'S A WIP

//Savings Account: is where you hold on to your money and in a while you can get interest for holding that money within a specific time period.
//this is used for the random generator.
import java.util.Random; //Random is used for the random ID generator  
import java.io.BufferedWriter; //This helps us write data to the CSV file.
import java.io.IOException; //catch errors if anything silly happens.
import java.nio.file.Files; //to make it easier to access the files read and write functions. Our HasSavings is a static so we need static methods to make the code work. Files work hand to hand with path objects instead of using Strings we could use that which makes it platform independent.
import java.util.ArrayList; //Array list is needed when we don't know the size of an array or when we resize an array if you see SavingsIDexists I used array list to capture all the columns and use it to compare with the current savings ID with the savings ID in the current array list. Something like this psuedocode currentsavingsID = currentarraylistsavings.
import java.nio.file.Path; //This function is used to find the file you want for example I'm using this to find my Savings.csv
import java.nio.file.StandardOpenOption; //we don't want to overwrite when we create a savings ID account we want to append.
import java.io.BufferedReader; //is used to read line by line
import java.util.Map; //find something specific like "userid": 12992


//TIME import TODO


public class SavingsAccount
{
    /***Variables***/
    private double savingsbalance; //TODO this is our savings balance which will start in a range if the customer first creates their account in about 100-300 $
    private double overDraftFee = 35; //TODO it'll start with 35$
    private double minBalanceFee = 15; //TODO minimum balance fee is 15 dollars.
    private double monthlyFee = 12; // TODO monthly fee is 12 dollars.
    private double yearlyFee = 48; // TODO 48 dollars.
    private static csvFile file; //csvFile equals to the path of the CSV file.
    private String userid; //current userID lets say they registered or they were recent the constructor will use that and make THIS field equal to that.
    /*Static Final variables */
    private static final Path csvPath = Path.of("Savings.csv"); //fine the path for the CSV file
    private static final long MAX = 199_999_999_999L; //This is the maximum for the savings number generator. //1000000000000 is Savings Account UNIQUE ID this is only for savings.
    private static final long MIN = 100_000_000_000L; //minimum for the random number generator
    private static final double minimumbalance = 100;
    private static final double maximumbalance = 300;

    //TODO long number = MIN + (long)(rand.nextDouble() * (MAX - MIN + 1));
    //try catch exceptions if the csvfile fails to load
    static{
        try{
            file = new csvFile(csvPath);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    

    public SavingsAccount() //when creating
    {
        savingsbalance = minimumbalance; //make savings start at 100 to start with a default starting value.
    }

    public SavingsAccount(String userid, double savingsamount) //Work in progress.
    {
        savingsbalance = savingsamount;//TODO I need to create a method that checks in the CSV file, if the userID has a savings account or not if not then request back to them they don't have it. Request the User if he wants to create a savings account.
    }



    /*CHECK IF THE USER HAS A SAVINGS ACCOUNT THE RETURN FUNCTION WILL BE THE FINAL SIGNAL, use this inside createSavings or existingSavings */

    
    public static boolean hasSaving(String userid){ //we use Hassavings to return true or false values if the user has an savings account or not.
        try{
            Map<String,String> record = file.getRecord("userid", userid);
            if(record == null){
                return false;
            }
            String savingsID = record.get("SavingsID");
        return savingsID != null && !savingsID.isEmpty();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public SavingsAccount createSavingsAccount(String userid, double savingsamount) throws IOException{ //Create Savings Account:Creates an account if it doesn't exist; the user gets 2 options 
        if(hasSaving(userid)){
            throw new IllegalArgumentException("The User Already has a Savings Account.");
        }
        String SavingsID = RandomIDGenerator();
        SavingsAccount account;
        if(savingsamount == 100)
        {
            account = new SavingsAccount();
            account.userid = userid;
        }
        else if(savingsamount <= maximumbalance && savingsamount > minimumbalance){
            account = new SavingsAccount(userid, savingsamount);
        }
        else{
            throw new IllegalArgumentException("The Saivngs amount has to be in the range of 100-300");
        }

        try(BufferedWriter bw = Files.newBufferedWriter(csvPath, StandardOpenOption.APPEND)){
            bw.write(userid + "," + SavingsID + "," + savingsamount);
            bw.newLine(); //make a new line when written.
        }
        return account;
    }

    public static String RandomIDGenerator() throws IOException // make the randomIDGenerator a static so it doesn't belong to an object but an stabdard ID generator for savings ids
    {
        Random rand = new Random(); //rand can generate random numbers
        String ID; //make ID String so we can easily manipulate it in CSV like reading or writing it.
        do{
            long number = (MIN)+ (long)(rand.nextDouble()*(MAX - MIN + 1)); //to simplify this this is just saying make the number within the minimum and maximum.
            ID = String.valueOf(number); //convert number to String so ID can equal to that string.
        }
        while(savingsIDExists(ID)); //Check if there is any Savings ID like it in the CSV file.

        return ID;
    }
    
    public static boolean savingsIDExists(String SavingsID) throws IOException //This is different to hasSavings savingsIDEXIST checks if there is a user with the same savings ID as another persons this code tries rerolling Savings ID until satisfied.
    {
        try(BufferedReader reader = Files.newBufferedReader(csvPath)){
            reader.readLine(); //this line skips the header for example (userid,SavingsID,savings)
            String line;
            while((line = reader.readLine()) != null){
                    ArrayList<String> currentdata_to_col = csvParsing.parseLine(line); //make numbers the column instead and changes the data in line to rows.
                if(currentdata_to_col.size() > 2 && currentdata_to_col.get(2).equals(SavingsID))
                {
                    return true;
                }
            }
        }
    return false; //return false if there is no savings ID equal to another savings ID
}

//*******SETTERS AND GETTERS*******/
    public double getSavings() //this will return savings.
    { 
        return savingsbalance;
    }
    public void setSavings(double savings) //This will set Savings the private field to the parameter savings.
    {
        savingsbalance = savings;
    }
    public double depositSavings(double depositamt)
    {
        if(depositamt > 0)
        {
            return savingsbalance += depositamt;
        }
        else{

            System.out.println("Deposit has to be a positive.");
        }
        return savingsbalance;
    }
//************Withdraw system**************/
    public double withdrawSavings(double amt, String choice) //withdraw system that records the amount.
    {
        switch(choice) //This is going to change the intent for this is if the user picks "Checking" then subtract savings from the amt that was placed and the checking account managers add that amt value otherwise if "Savings" we'll add the value from amt and the checking account people would just subtract on their part.
        {   
            case "Checking": //"Checking" means that you're choosing to withdraw savings to checking
                if(amt > 0 && savingsbalance >= amt){//
                    savingsbalance -= amt; 
                }
                else{
                    System.out.println("Incorrect amount must be less than savings AND greater than 0");                     
                }
            break;
            case "Savings": //CHECKING to Savings would simply add amt with savings.
                    if(amt > 0)
                    {
                        savingsbalance += amt;
                    }
                    break;
            default:
                System.out.println("Invalid choose either Checking or Savings");
                break;
            }
            return savingsbalance;                
        }

    //FEES
    public double minBalanceFee(double savingsbalance, String userid)throws IOException{  //If the user is below 100 then create or check if 24 hours passed.
        if(savingsbalance < 100)
        { //Gotta implement a 24 hour system
            //TODO implement a time system that checks the time during runtime and compare it with the CSV if 24 hours passed then pass that fee to Checking
            //TODO write something if there is nothing
            Map<String,String> saving_fee_file = file.getRecord("userid", userid);
            if(saving_fee_file == null)
            {
                //GOAL HERE IS TO CREATE AN TIMESTAMP IF THE USER IS LESS THAN 100 MAKE SURE TO RECORD TIME -Younes Ziani

            }
        }
        
        return 0.00;
    }

    public double overDraftFee_(){
        //TODO.
        return 0.00;
    }
    

    }
