import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class moneyDriver {
    public static void main(String[] arg) throws IOException
    {
         
                        System.out.println("Please enter a user ID User!");
            Scanner newinput = new Scanner(System.in);
            List<User> users = new ArrayList<>();
            User alice = new User(
        "1001", "Alice", "Johnson", "123-45-6789", "01/01/1990",
        "alice@email.com", "555-123234", "NY", "New York", true, 1800.0,
        "Bank NY", 5000.0, 750, true, 0.0, 0.05,
        "extra1", "extra2", "extra3"
);        // Create CheckingAccount.Account objects
        CheckingAccount.Account acc1 = new CheckingAccount.Account("415631219101", 4250.75);
        CheckingAccount.Account acc2 = new CheckingAccount.Account("498891668177", 1800.00);
        CheckingAccount.Account acc3 = new CheckingAccount.Account("431246013015", 620.50);

        // Put them into a list to simulate "user accounts"
        List<CheckingAccount.Account> aliceAccounts = new ArrayList<>();
        aliceAccounts.add(acc1);
        aliceAccounts.add(acc2);
        aliceAccounts.add(acc3);

  
            String userid = newinput.nextLine();
           
            moneyMarket acc  = moneyMarket.createmoneyMarket(userid, 100);
            if(acc==null)
            {
            acc = moneyMarket.OpenmoneyMarket(userid);
            }
            if(acc !=null)
            {
            acc.update();
            System.out.println("Savings " + acc.getMoneyMarket());
            }
            newinput.close();
    }
    
}