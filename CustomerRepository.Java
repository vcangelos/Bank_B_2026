import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CustomerRepository {

    private static final String FILE_NAME = "customers.csv";

    /**
     * Initialize CSV file with header if it does not exist.
     */
    public static void initializeCSV() throws IOException {

        File file = new File(FILE_NAME);

        if (!file.exists()) {

            try (FileWriter writer = new FileWriter(file)) {

                writer.write(
                    "customerID,firstName,lastName,ssn,dob,email,phoneNumber,bankLocation," +
                    "bankPhoneNumber,savingsAccount,checkingAccount,EmployeeAccount,creditCard,creditCardLimit," +
                    "creditScore,hasDebitCard,cdBalance,cdInterestRate\n"
                );
            }
        }
    }

    /**
     * Save a customer directly to CSV.
     */
    public static void saveCustomerToCSV(Customer customer) {

        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }

        try {
            // Ensure file exists
            initializeCSV();

            try (FileWriter writer = new FileWriter(FILE_NAME, true)) {

                String line =
                    escape(customer.getCustomerID()) + "," +
                    escape(customer.getFirstName()) + "," +
                    escape(customer.getLastName()) + "," +
                    escape(customer.getSSN()) + "," +
                    escape(customer.getDOB()) + "," +
                    escape(customer.getEmail()) + "," +
                    escape(customer.getPhoneNumber()) + "," +
                    escape(customer.getBankLocation()) + "," +
                    escape(customer.getBankPhoneNumber()) + "," +
                    escape(String.valueOf(customer.getSavingsAccount())) + "," +
                    escape(String.valueOf(customer.getCheckingAccount())) + "," +
                    escape(String.valueOf(customer.EmployeeAccount())) + "," +
                    escape(String.valueOf(customer.getCreditCard())) + "," +
                    escape(String.valueOf(customer.getCreditCardLimit())) + "," +
                    escape(String.valueOf(customer.getCreditScore())) + "," +
                    escape(String.valueOf(customer.hasDebitCard())) + "," +
                    escape(String.valueOf(customer.getCdBalance())) + "," +
                    escape(String.valueOf(customer.getCdInterestRate())) + "\n";

                writer.write(line);
            }

        } catch (IOException e) {
            System.out.println("Error saving customer to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Escape CSV values to handle commas, quotes, and line breaks.
     */
    private static String escape(String value) {

        if (value == null) {
            return "";
        }

        String escaped = value
            .replace("\"", "\"\"")   // escape quotes
            .replace("\n", " ")      // remove new lines
            .replace("\r", " ");     // remove carriage returns

        return "\"" + escaped + "\"";
    }
}
