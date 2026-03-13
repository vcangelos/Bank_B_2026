import java.io.IOException;

public class SavingsDriver {
    public static void main(String[] args) throws IOException{
            SavingsAccount acc = SavingsAccount.createSavingsAccount("29399", 100.00);
            acc.withdrawSavings(20, "Checking");
            acc.minBalanceFee();
            System.out.println("Savings " + acc.getSavings());
    }
}