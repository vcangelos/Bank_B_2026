import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CommercialLoanReader {
    private final String csvFile = "commercialLoans.csv";
    private String line;
    private final String csvSplitBy = ",";
    public ArrayList<CommercialLoan> readCSV() {
        ArrayList<CommercialLoan> commercialLoans = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] loanData = line.split(csvSplitBy);
                String loanNumber = loanData[0];
                double loanAmount = Double.parseDouble(loanData[1]);
                double interestRate = Double.parseDouble(loanData[2]);
                int loanTerm = Integer.parseInt(loanData[3]);
                CommercialLoan.loadLoan(loanNumber, loanAmount, interestRate, loanTerm);
            }
        } catch (IOException e) {
            System.out.println("Error occurred reading file: " + e.getMessage());
        }
        return commercialLoans;
    }
    public CommercialLoan findLoan(String loanNumber) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] loanData = line.split(csvSplitBy);
                if (loanData[0].equals(loanNumber)) {
                    double loanAmount = Double.parseDouble(loanData[1]);
                    double interestRate = Double.parseDouble(loanData[2]);
                    int loanTerm = Integer.parseInt(loanData[3]);
                    return new CommercialLoan(loanNumber, loanAmount, interestRate, loanTerm);
                }
            }
        } catch (IOException e) {
            System.out.println("Error occurred reading file: " + e.getMessage());
        }
        return null;
    }
}