import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CheckingAccount {

    //variables
    private double balance; 
    private double overdraftFee = 15;
    private double minBalanceFee = 100;
    public transactionHistory; 
    
    //deposit
    public void deposit(int amt){
        char acct = in.next().trim().toUpperCase().charAt(0); 
        System.out.print("Amount: "); 
        double amt = in.nextDouble(); 
        if (amt <= 0) { // if amount entered is valid (<= 0)
            System.out.println("Amount must be > 0."); 
            continue;
            } 
            else if (amt > 0) { //if amount entered is valid (>0)
                 balance += amt;
            }
            else {  //anything else entered that isn't valid (symbols,letters)
            System.out.println("Invalid account."); 
            continue; }
    }
    
    //withdrawals
        char acct = in.next().trim().toUpperCase().charAt(0); //C is variable used for checking account 
        System.out.print("Amount: "); 
        double amt = int.nextDouble(); 
        if (amt<= 0){
            System.out.println("Amount must be > 0"); 
        }
        else if (acct == 'C' ) {
        public void withdraw(int amt){
            if (amt > balance){
                if (hasOverdraftProtection == false){
                    balance -= overdraftFee;
                }
                else if (hasOverdraftProtection == true){
                    
                    //finish code
                }
                else{
                    left = amt - balance;
                    savings -= left;
                    balance = 0;
                }
            }
            else{
               balance -= amt;  
                System.out.print("Invalid Account."); 
            }
        }}




        
    //minimum balance
    public void checkMinimumBalance();
        if (balance < minimumBalance){
            savings -= minBalanceFee;
        }
    //transaction history: use 2d array (withdrawls, deposits, purchases, fees -> for each: date, amount, new balance)
    public transactionHistory();
    
    //connect to savings account
    
    //check if has savings account, if not charge fee
    public void loadSavingsStatus(csvFile csv) throws IOException { //reads csv file

        Map<String,String> record = csv.getRecord("AccountNumber", accountNumber); //checks for the row where the account number matches user's account

        if (record != null) { //checks if account actually exists 
            String value = record.get("HasSavings");// gets value from HasSavings column. check w people for the csv file if column in csv file is named hasSavings. 

            if (value != null && value.equalsIgnoreCase("true")) {// makes sure value exists. works for any capitalization or lower case letters if value is true.
                hasSavingsAccount = true; //makes class boolean true 
            } else { //anything other than true makes class false 
                hasSavingsAccount = false;
            }
        } else { //if no account is found then false
            hasSavingsAccount = false;
        }
    }
    private boolean hasSavingsAccount; 
    private double checkingBalance;
    private static final double NO_SAVINGS_FEE = 25.0;
    public void checkSavingsConnection() {
        if (!hasSavingsAccount) {// if it is false, user is charged 
            checkingBalance -= NO_SAVINGS_FEE;
            System.out.println("no existing savings account. a charge of $25 has been sent to your checking account");
        }

    }
    //create checking account
    //seaparate csv file for only checkings
    //more than one checking account -> unique checking account num separate from account num
    //2d array for csv file, nested loops (first for loop is row, nested loop is column)
    // declare dimensions to create array (x rows, x columns) but must be dynamic array to allow it to expand
    // don't hardcode length of array, use .length in loops? 
    //search algorithms
    //no interface
