import java.io.IOException;

public class SavingsDriver {
    public static void main(String[] args) throws IOException{
            SavingsAccount acc = SavingsAccount.createSavingsAccount("29399", 100);
            //SavingsAccount acc = SavingsAccount.OpenSavingsAccount("29399");
            
            acc.updateFees();
            System.out.println("Savings " + acc.getSavings());
    }
}