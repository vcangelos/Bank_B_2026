import java.io.FileWriter;
import java.io.IOException; //In case of file errors//
import java.util.ArrayList;

public class CustodialWriter {
    private final String csvFile = "custodialAccounts.csv";
    public void addToCSV(Custodial account) {
        try(FileWriter appender = new FileWriter(csvFile, true)) {
            String dataString = account.getAccountNumber() + "," + account.getAccountCreationDate() + "," + account.getMinorAge() + "," + account.getBalance() + "\n";
            appender.write(dataString);
        }
        catch(IOException e) {
            System.out.println("Error occurred adding to file: " + e.getMessage());
        }
    }
    public void saveData(ArrayList<Custodial> accounts) {//still working on this method, it should overwrite the file with the new data from the accounts arraylist//
        try(FileWriter writer = new FileWriter(csvFile)) {
            for (Custodial account : accounts) {
                String dataString = account.getAccountNumber() + "," + account.getAccountCreationDate() + "," + account.getMinorAge() + "," + account.getBalance() + "\n";
                writer.write(dataString);
            }
        }
        catch(IOException e) {
            System.out.println("Error occurred saving data to file: " + e.getMessage());
        }
    }
}