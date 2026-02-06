import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CheckingAccount {
    public getBalance(int AccountNum){
    //variables
    private double balance; 
    private double overdraftFee = 15;
    private double minBalanceFee = 100;
    public transactionHistory; 
    
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
            savings -= minBalanceFee;
        }
    //transaction history (withdrawls, deposits, purchases, fees -> for each: date, amount, new balance)
    public transactionHistory()
    //use 2d array
    
    //check if as savings account, if not charge fee
    //create checking account
    //seaparate csv file for only checkings
    //more than one checking account -> unique checking account num separate from account num
    
