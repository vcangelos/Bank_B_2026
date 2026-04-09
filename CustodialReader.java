import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class CustodialReader{
    private final String csvFile = "custodialAccounts.csv";
    public ArrayList<Custodial> readCSV() {
        String line;
        String csvSplitBy = ",";
        ArrayList<Custodial> accounts = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) { //Check if file is there before anything else//
            while ((line = br.readLine()) != null) { //If line isnt empty, split it by commas and print the account info//
                String[] accountData = line.split(csvSplitBy);
                System.out.println("Account Number: " + accountData[0] + ", Creation Date: " + accountData[1] + ", User Age: " + accountData[2] + ", Balance: $" + accountData[3]);
                Custodial account = new Custodial(accountData[0], LocalDate.parse(accountData[1]), Integer.parseInt(accountData[2]), Double.parseDouble(accountData[3]));
                accounts.add(account);
        } }
        catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return accounts;
    }
}