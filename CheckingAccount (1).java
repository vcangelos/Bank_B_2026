import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class CheckingAccount {

    //variables
    private final String userID;
    private final AccountRepository repo;
    private double balance; 
    private double overdraftFee = 35;
    private double minBalanceFee = 15;
    private double minBalance = 100;
    public transactionHistory; 
    //static File accounts = new File("xxx.csv");
    //static File checkingAccounts = new File ("checking_accounts.csv");
    //static File savingsAccounts = new File("saving_accounts.csv");
    //need csv for everything else? loans, mortgages, card, etc.? 
    
    
    public CheckingAccount(String id, AccountRepository repo) throws IOException {
        this.id = id;
        this.repo = repo;

        Map<String,String> data = repo.findById(id);
        this.balance = Integer.parseInt(data.get("checkingBalance"));
    }
    
    public double getBalance(){
        return balance;
    }    
    public String getID(){
        return userID;
    }
    public boolean getOverdraftProtection(){
        return hasOverdraftProtection;
    }

    
    //view accounts
    static void viewAccounts(){
       

    //deposit
    public void deposit(){
       
        System.out.print("Amount:$ "); 
        double amt = in.nextDouble(); 
        if (amt <= 0) { // if amount entered is valid (<= 0)
            System.out.println("Amount must be > 0."); 
        } 
        else if (amt > 0) { //if amount entered is valid (>0)
             balance += amt;
             System.out.println("Deposit successful.");
             System.out.println("New Balance: $" + balance); 
        }
        else {  //anything else entered that isn't valid (symbols,letters)
        System.out.println("Invalid account."); 
        } 
    } 
    
    //withdrawals
// make the overdraft fee $35 
   public void withdraw() {
    System.out.print("Amount: "); 
    double amt = int.nextDouble(); 
    if (amt <= 0){
        System.out.println("Amount must be > 0"); 
        return;
    }
    if (amt > balance ) {
        if (!hasOverdraftProtection) {
            balance -= 35;
            System.out.println("Overdraft fee applied"); 
            }
            else { 
                double left = amt - balance;
                savings -= left; 
                balance = 0; 
                System.out.print("Used savings for overdraft protection"); 
            }
            else{
             balace -= amt;
            }
            System.out.print("New balance: $ " + balance); 
        }
    }
    
    //minimum balance, possibly give one day to transfer funds from another account
    public void checkMinimumBalance();
        if (balance < minBalance){
            savings -= minBalanceFee;
        }
    //transaction history: use 2d array (withdrawls, deposits, purchases, fees -> for each: date, amount, new balance) 
    // don't need to connect with savings account
    //rows = single transaction, columns = transaction name, amount, date, balance after 
    public void transactionHistory() {
   // adds a row + columns required when a transaction is made 
   for (int row = 0; row < transactionHistory.length; row++); { 
       for (int column = 0; columns < transactionHistory.[row]; column++); { 
       }
   }
   //to find the transactions that satify a condition (ex: deposits only) 
       int count = 0; 
for (int r = 0; r < transactionHistory.length; r++) { 
    for (int c = 0; c < transactionHistory[r].length; c++) { 
        if (grades[r][c] >= ) count++; 
    } 
} 
System.out.println("# of " + __________ + " = " + count); 

   //prints out info for the transaction made 
    System.out.println("Date: " + | "Type: " + | "Amount:$ " + | "Balance After: " + );
    
    
    
    
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
    //YES interface
    //generate unique ids
    //certain length, each value is randomly assigned, no duplicants, has to be random, make sure checking account ids are not same as savings account ids, every id starts w/ 4
    //ids start with 4
