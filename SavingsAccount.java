//prototype
//The goal here is to create setters and getters with a constructor.
public class SavingsAccount
{
    //Variables
    private double savings; //this is our savings balance which will start in a range if the customer first creates their account in about 100-300
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
    public double withdrawSavings(double amt) //withdraw system that records the ammount.
    {   
        if(amt < savings)
        {
        return;
        }
    else{
        return savings -= amt;
        }   
    }

    
}
