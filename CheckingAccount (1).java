public class CheckingAccount {
    public getBalance(int AccountNum){
    //variables
    private double balance; 
    private double overdraftFee = 15;
    private double minBalanceFee = 100;
    public 
    //deposit
    public void deposit(int amt){
        balance += amt;
    }
    //withdrawals
    public boolean withdraw(int amt){
        if (amt > balance)
            if (hasOverdraftProtection == false){
                balance -= overdraftFee;
            }
            else{
                left = amt - balance;
                savings -= left;
                balance = 0
                
            }
        else{
           balance -= amt;  
        }    
        

    }

        
    //minimum balance
    public void checkMinimumBalance()
        if (balance < minimumBalance){
            savings -= minBalanceFee
        }
    //transaction history
    public transactHistory()