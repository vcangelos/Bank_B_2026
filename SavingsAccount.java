//Savings Account: is where you hold on to your money and in a while you can get interest for holding that money within a specific time period.
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
public class SavingsAccount
{
    //Variables
    private double savings; //this is our savings balance which will start in a range if the customer first creates their account in about 100-300 $
    private double overDraftFee = 35; //it'll start with 25$
    private double minBalanceFee = 30; //minimum balance fee is 30 dollars.
    private double monthlyFee = 12; //monthly fee is 12 dollars.
    private double yearlyFee = 48; //48 dollars.
    

    public SavingsAccount()
    {
        savings = 100.00; //make savings start at 100 to start with a default starting value.

    }

    public SavingsAccount(Customer c) //Work in progress.
    {
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
