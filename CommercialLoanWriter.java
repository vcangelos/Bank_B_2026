import java.io.FileWriter;
import java.io.IOException; //In case of file errors//
import java.util.ArrayList;

public class CommercialLoanWriter {
    public void addToCSV(CommercialLoan loan) {
        String csvFile = "commercialLoans.csv";
        try(FileWriter appender = new FileWriter(csvFile, true)) {
            String dataString = loan.getLoanNumber() + "," + loan.getLoanAmount() + "," + loan.getInterestRate() + "," + loan.getLoanTerm() + "\n";
            appender.write(dataString);
        }
        catch(IOException e) {
            System.out.println("Error occurred adding to file: " + e.getMessage());
        }
    }
    public void saveToCSV(ArrayList<CommercialLoan> loans) {
        String csvFile = "commercialLoans.csv";
        try(FileWriter writer = new FileWriter(csvFile)) {
            for (CommercialLoan loan : loans) {
                String dataString = loan.getLoanNumber() + "," + loan.getLoanAmount() + "," + loan.getInterestRate() + "," + loan.getLoanTerm() + "\n";
                writer.write(dataString);
            }
        }
        catch(IOException e) {
            System.out.println("Error occurred writing to file: " + e.getMessage());
        }
    }
    public void saveLoanInformation(CommercialLoan loan) {
        String csvFile = "commercialLoanInformation.csv";
        try(FileWriter writer = new FileWriter(csvFile, true)){
            String dataString = loan.getLoanNumber() + "," + loan.getBusinessName() + "," + String.join(" ", loan.getLoanAddress()) + "," + loan.getLoanPurpose() + "," + loan.getCreditScore() + "," + loan.getProceedToApproval() + "\n";
            writer.write(dataString);
        }
        catch(IOException e) {
            System.out.println("Error occurred writing to file: " + e.getMessage());
        }
    }
}