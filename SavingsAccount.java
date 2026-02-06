//prototype
//The goal here is to create setters and getters with a constructor.
//Savings Account: is where you hold on to your money and in a while you can get interest for holding that money within a specific time period.
public class SavingsAccount
{
    //Variables
    private double savings; //this is our savings balance which will start in a range if the customer first creates their account in about 100-300 $
    private double overDraftFee = 25; //it'll start with 25$
    

    public SavingsAccount()
    {
        savings = 100.00; //make savings start in 100 when wanting to start with a default starting value.

    }

    public SavingsAccount(Customer c) //Work in progress.
    {
    //TODO    
    }
    
    public double getSavings() //this will return savings.
    { //Might be subject to change
        return savings;
    }
    public void setSavings(double savings) //This will set Savings the private field to the parameter savings.
    {
        this.savings = savings;
    }
    public double withdrawSavings(double amt, char choice) //withdraw system that records the amount.
    {
        if(choice == "Checking") //This is going to change the intent for this is if the user picks "Checking" then subtract savings from the amt that was placed and the checking account managers add that amt value otherwise if "Savings" we'll add the value from amt and the checking account people would just subtract on their part.
        {
            if(savings >= amt)
            {
            return savings -= amt; 
            }
            else{
                System.out.println("Amount is high.");
                return savings; //this is going to be here temporarily.
            } 
        }
        else if(choice == "Savings")
        {
            //TODO

        }
    }

    
}
