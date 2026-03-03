import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CustomerRepository {

    public static final String FILE_NAME = "customers.csv";

    /**
     * Initialize CSV file with header if it does not exist.
     */
    public static void initializeCSV() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            FileWriter writer = new FileWriter(file);
            writer.write(
                "customerID,firstName,lastName,SSN,DOB,email,phoneNumber,BankLocation," +
                "bankPhoneNumber,SavingsAccount,CheckingAccount,CreditCard,creditCardLimit," +
                "creditScore,hasDebitCard,cdBalance,cdInterestRate\n"
            );
            writer.close();
        }
    }

    /**
     * Save a customer directly to CSV.
     * This does NOT use ArrayList or memory storage.
     */
    public static void saveCustomerToCSV(Customer customer) throws IOException {

        // Open in append mode
        FileWriter writer = new FileWriter(FILE_NAME, true);

        writer.write(
            customer.customerID + "," +
            customer.firstName + "," +
            customer.lastName + "," +
            customer.SSN + "," +
            customer.DOB + "," +
            customer.email + "," +
            customer.phoneNumber + "," +
            customer.BankLocation + "," +
            customer.bankPhoneNumber + "," +
            customer.SavingsAccount + "," +
            customer.CheckingAccount + "," +
            customer.CreditCard + "," +
            customer.creditCardLimit + "," +
            customer.creditScore + "," +
            customer.hasDebitCard + "," +
            customer.cdBalance + "," +
            customer.cdInterestRate + "\n"
        );

        writer.close();
    }
}
