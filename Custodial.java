import java.util.Scanner;

public class Custodial {
    private final String accountNumber;
    private int minorAge;
    private double balance;
    Scanner scanner = new Scanner(System.in);

    public Custodial() {
        System.out.print("Enter account number: ");
        accountNumber = scanner.nextLine(); 
        System.out.print("Enter minor's age: ");
        minorAge = scanner.nextInt();
        if(minorAge > 18) {
            throw new IllegalArgumentException("Can not create account. Ensure minor is below 18 years of age.");
        }
        System.out.print("Enter initial deposit amount: ");
        balance = scanner.nextDouble();
        if (balance < 100) {
            throw new IllegalArgumentException("Initial deposit must be at least $100.");
        }
    }
    public Custodial(String accountNumber, int age, double initialDeposit) {
        this.accountNumber = accountNumber;
        if (age > 18 || initialDeposit < 100) {
            throw new IllegalArgumentException("Can not create account. Ensure minor is below 18 years of age and at least $100 is deposited.");
        }
        else {
            minorAge = age;
            balance = initialDeposit;
        }
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
    public boolean upgradeCustodial() {
        if (minorAge >= 18) {
            System.out.println("Account eligible for upgrade to regular account.");
            return true;
        } else {
            System.out.println("Account not eligible for upgrade. Minor is still under 18.");
            return false;
        }
    }
}
