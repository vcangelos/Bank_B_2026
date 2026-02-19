//TODO USING SAVINGS.CSV IT'S A WIP

//Savings Account: is where you hold on to your money and in a while you can get interest for holding that money within a specific time period.
import java.util.*; //TODO 
import java.nio.file.Path; //This function is used to find the file you want for example I'm using this to find my Savings.csv

import java.util.Random //this is used for the random generator.
public class SavingsAccount
{
    /***Variables***/
    private double savings; //TODO this is our savings balance which will start in a range if the customer first creates their account in about 100-300 $
    private double overDraftFee = 35; //TODO it'll start with 35$
    private double minBalanceFee = 15; //TODO minimum balance fee is 15 dollars.
    private double monthlyFee = 12; // TODO monthly fee is 12 dollars.
    private double yearlyFee = 48; // TODO 48 dollars.
    private String userid; //current userID lets say they registered or they were recent the constructor will use that and make THIS field equal to that.
    Path csvPath = Path.of("Savings.csv"); //fine the path for the CSV file
    csvFile file = new csvFile(csvPath); //csvFile equals to the path of the CSV file.
    private long MAX = 199999999999L; //This is the maximum for the savings number generator. //1000000000000 is Savings Account UNIQUE ID this is only for savings.
    private long MIN = 100000000000L; //minimum for the random number generator
    //TODO long number = MIN + (long)(rand.nextDouble() * (MAX - MIN + 1));

    

    public SavingsAccount() //when creating
    {
        savings = 100.00; //make savings start at 100 to start with a default starting value.
    //TODO
    }

    public SavingsAccount(String userid, int savingsamount) //Work in progress.
    {
        //TODO I need to create a method that checks in the CSV file, if the userID has a savings account or not if not then request back to them they don't have it. Request the User if he wants to create a savings account.
    }



    //*******************************************CHECK IF THE USER HAS A SAVINGS ACCOUNT THE RETURN FUNCTION WILL BE THE FINAL TELLER use this inside createSavings or existingSavings */

    
    public static boolean hasSaving(String userid){ //TODO We'll check if an account has savings or not.
        boolean hassavings = false; //TODO the goal here is to use this in a column called "Has Savings" this will paste a true or false.

        Map<String,String> record = file.getRecord("userid", userid);
        if(record == null){
            return false;
        }

        hassavings = record.get("HasSavings") //hassavings contain the value of the map
        //Run inside the CSV and check if the value is 0 and if hasSavings is false both can't be wrong.

        return hassavings != null && hassaving.equalIgnoreCase("true") && rrecord.get("SavingsID") => MIN && record.get("SavingsID") <= MAX; //this is here TODO.
    }
    public SavingsAccount createSavingsAccount(){
        //TODO

    }
    

    public double getSavings() //this will return savings.
    { 
        return savings;
    }
    public void setSavings(double savings) //This will set Savings the private field to the parameter savings.
    {
        this.savings = savings;
    }
    public double depositSavings(double depositamt)
    {
        if(depositamt > 0)
        {
            return savings += depositamt;
        }
        else{

            System.out.println("Deposit has to be a positive.");
        }
        return savings;
    }

    public double withdrawSavings(double amt, String choice) //withdraw system that records the amount.
    {
        switch(choice) //This is going to change the intent for this is if the user picks "Checking" then subtract savings from the amt that was placed and the checking account managers add that amt value otherwise if "Savings" we'll add the value from amt and the checking account people would just subtract on their part.
        {   
            case "Checking": //"Checking" means that you're choosing to withdraw savings to checking
                if(amt > 0 && savings >= amt){//
                    savings -= amt; 
                }
                else{
                    System.out.println("Incorrect amount must be less than savings AND greater than 0");                     
                }
            break;
            case "Savings": //CHECKING to Savings would simply add amt with savings.
                    if(amt > 0)
                    {
                        savings += amt;
                    }
                    break;
            default:
                System.out.println("Invalid choose either Checking or Savings");
                break;
            }
            return savings;                
        }
    }
