import java.io.IOException;

public class SavingsDriver {
    public static void main(String[] args) throws IOException{
            SavingsAccount acc = SavingsAccount.OpenSavingsAccount("29399");
            acc.withdrawSavings(20, "Checking");
            acc.minBalanceFee();
            System.out.println("Savings " + acc.getSavings());
    }
}