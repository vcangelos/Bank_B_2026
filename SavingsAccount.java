//TODO USING SAVINGS.CSV IT'S A WIP

//Savings Account: is where you hold on to your money and in a while you can get interest for holding that money within a specific time period.
import java.util.*; //TODO 
public class SavingsAccount
{
    //Variables
    private double savings; //TODO this is our savings balance which will start in a range if the customer first creates their account in about 100-300 $
    private double overDraftFee = 35; //TODO it'll start with 35$
    private double minBalanceFee = 15; //TODO minimum balance fee is 15 dollars.
    private double monthlyFee = 12; // TODO monthly fee is 12 dollars.
    private double yearlyFee = 48; // TODO 48 dollars.
    private String userid; //current userID lets say they registered or they were recent the constructor will use that and make THIS field equal to that. 
    private boolean hassavings; //TODO the goal here is to use this in a column called "Has Savings" this will paste a true or false.

    public SavingsAccount() //when creating
    {
        savings = 100.00; //make savings start at 100 to start with a default starting value.

    }

    public SavingsAccount(String userid, int savingsamount) //Work in progress.
    {
        //TODO I need to create a method that checks in the CSV file, if the userID has a savings account or not if not then request back to them they don't have it. Request the User if he wants to create a savings account.
    }

    public static CheckSaving(String userid){ //TODO We'll check if an account has savings or not.


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
