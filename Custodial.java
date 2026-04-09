import java.time.LocalDate;
import java.time.temporal.ChronoUnit; //Class to store date//
import java.util.ArrayList; //Class to use operations with dates//
import java.util.Random;
import java.util.Scanner;

public class Custodial {
    private final String accountNumber;
    private int minorAge;
    private double balance;
    private final LocalDate accountCreationDate; // Set account creation date to current date in (year-month-day format)//
    private static ArrayList<String> accountNumbers = new ArrayList<>(); //Array to store account numbers to ensure no duplicates//
    Scanner scanner = new Scanner(System.in);

    public boolean checkDuplicateAccountNumber(String number) {
        for (String accountNum : accountNumbers) {
            if (accountNum != null && accountNum.equals(number)) {
                return true;
            }
        }
        return false;
    }
    private String generateRandom12DigitAccountNumber() {
        long min = 200000000000L;
        long max = 299999999999L;
        String formattedNumber = "";
        while(true){
            Random random = new Random();
        // Generate a random long within the range [min, max]
            long randomNumber = min + (long) (random.nextDouble() * (max - min + 1));
            formattedNumber = String.format("%012d", randomNumber);
            if (!checkDuplicateAccountNumber(formattedNumber)) {
                accountNumbers.add(formattedNumber); // Store the generated account number in the array
                break;
            }
        }
        // Format the long as a 12-digit string with leading zeros if needed
        return formattedNumber;
    }
    public Custodial(String accountNumber, LocalDate accountCreationDate, int minorAge, double balance) { //Constructor used for reading from CSV file, takes in all parameters as arguments//
        this.accountNumber = accountNumber;
        accountNumbers.add(accountNumber);
        this.accountCreationDate = accountCreationDate;
        this.minorAge = minorAge;
        this.balance = balance;
    }
    public Custodial() {
        accountNumber = generateRandom12DigitAccountNumber();
        System.out.print("Enter minor's age: ");
        minorAge = scanner.nextInt();
        accountCreationDate = LocalDate.now();
        if(minorAge >= 18) {
            throw new IllegalArgumentException("Can not create account. Ensure minor is below 18 years of age.");
        }
        System.out.print("Enter initial deposit amount: ");
        balance = scanner.nextDouble();
        if (balance < 100) {
            throw new IllegalArgumentException("Initial deposit must be at least $100.");
        }
        addToCSVData();
        System.out.println("Account created successfully. Account Number: " + accountNumber);
    }
    public Custodial(int age, double initialDeposit) {
        accountNumber = generateRandom12DigitAccountNumber();
        if (age > 18 || initialDeposit < 100) {
            throw new IllegalArgumentException("Can not create account. Ensure minor is below 18 years of age and at least $100 is deposited.");
        }
        else {
            minorAge = age;
            balance = initialDeposit;
        }
        accountCreationDate = LocalDate.now();
        addToCSVData();
        System.out.println("Account created successfully. Account Number: " + accountNumber);
        }
    public void custodialDeposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        System.out.println("Deposit successful. Current balance: $" + balance);
    }
    public void custodialWithdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds. Current balance: $" + balance);
        }
        balance -= amount;
        System.out.println("Withdrawal successful. Current balance: $" + balance);

    }
    public void displayAccountInfo() {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Minor's Age: " + minorAge);
        System.out.println("Current Balance: $" + balance);
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public int getMinorAge() {
        return minorAge;
    }
    public double getBalance() {
        return balance;
    }
    public void updateTime() {
        LocalDate currentDate = LocalDate.now();
        long yearsElapsed = ChronoUnit.YEARS.between(accountCreationDate, currentDate);
        minorAge += yearsElapsed;
        if (minorAge >= 18) {
            System.out.println("Minor has reached adulthood. Account can be transferred to a regular account.");
        } else {
            System.out.println("Minor is still underage. " + (18 - minorAge) + " years remaining until adulthood.");
    }
        applyInterest(yearsElapsed);
    }
    public LocalDate getAccountCreationDate() {
        return accountCreationDate;
    }
    private void applyInterest(long years) {
        double interestRate = 0.02; // 2% annual interest rate
        balance = balance * Math.pow(1 + interestRate/4, years*4); //Compound interest formula (A = P(1 + r/n)^(nt)), assuming the account is compounded quarterly//
        }
    private void addToCSVData() {
        CustodialWriter writer = new CustodialWriter();
        writer.addToCSV(this);
    }
}