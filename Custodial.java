import java.nio.file.Path;
import java.nio.file.Paths; //Class of date)//
import java.time.LocalDate; //Class of date format)//
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Scanner;

public class Custodial {
    Path custodialFile = Paths.get("custodialAccounts.csv");
    private final String accountNumber = generateRandom12DigitAccountNumber();
    private int minorAge;
    private double balance;
    private final LocalDate accountCreationDate; // Set account creation date to current date in (year-month-day format)//
    Scanner scanner = new Scanner(System.in);

    private String generateRandom12DigitAccountNumber() {
        long min = 100000000000L;
        long max = 999999999999L;

        Random random = new Random();
        // Generate a random long within the range [min, max]
        long randomNumber = min + (long) (random.nextDouble() * (max - min + 1));
        
        // Format the long as a 12-digit string with leading zeros if needed
        return String.format("%012d", randomNumber);
    }
    public Custodial() {
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
        addToCSV();
    }
    public Custodial(int age, double initialDeposit) {
        if (age > 18 || initialDeposit < 100) {
            throw new IllegalArgumentException("Can not create account. Ensure minor is below 18 years of age and at least $100 is deposited.");
        }
        else {
            minorAge = age;
            balance = initialDeposit;
        }
        accountCreationDate = LocalDate.now();
        addToCSV();
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
    private void applyInterest(long years) {
        double interestRate = 0.02; // 2% annual interest rate
        balance = balance * Math.pow(1 + interestRate/4, years*4); //Compound interest formula (A = P(1 + r/n)^(nt)), assuming the account is compounded quarterly//
        }
    private void addToCSV() {
            try {
                String csvLine = accountNumber + "," + minorAge + "," + balance + "," + accountCreationDate;
                java.nio.file.Files.write(custodialFile, (csvLine + System.lineSeparator()).getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                System.out.println("Account information added to CSV file.");
            } catch (java.io.IOException e) {
                System.out.println("An error occurred while writing to the CSV file: " + e.getMessage());
    }
    }
}
