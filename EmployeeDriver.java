// EmployeeDriver.java

import java.util.*;
import java.nio.file.*;
import java.io.*;

public class EmployeeDriver {

    // Path to the employees CSV file
    private static final Path EMPLOYEES_CSV = Path.of("employees.csv");

    // CSV header
    private static final String HEADER = "employee_id,name,salary,address,position,account_with_bank";

    public static void main(String[] args) throws Exception {

        Scanner input = new Scanner(System.in);

        // ---------------------------------------------------------
        // 1. OPEN OR CREATE employees.csv
        // ---------------------------------------------------------

        if (!Files.exists(EMPLOYEES_CSV)) {
            try (BufferedWriter bw = Files.newBufferedWriter(EMPLOYEES_CSV, StandardOpenOption.CREATE)) {
                bw.write(HEADER);
                bw.newLine();
            }
            System.out.println("Created new employees.csv");
        }

        // ---------------------------------------------------------
        // 2. SEED SAMPLE EMPLOYEES IF NOT ALREADY PRESENT
        // ---------------------------------------------------------

        if (findRecord("employee_id", "323111") == null) {
            addRecord("323111", "Alice Smith",   "40000", "123 Main St",       "Teller",       "true");
            addRecord("395195", "Bobby Johnson", "63000", "456 Secondary St",  "Loan Officer", "true");
            System.out.println("Sample employees added to database.");
        }

        // ---------------------------------------------------------
        // 3. ASK USER FOR ACCESS CODE
        // ---------------------------------------------------------

        System.out.println("Enter your employee ID to access the database:");
        String accessCodeInput = input.nextLine().trim();

        // ---------------------------------------------------------
        // 4. ACCESS CHECK
        // Valid IDs: real employee IDs from employees.csv + manager admin code
        // ---------------------------------------------------------

        boolean accessGranted = accessCodeInput.equals("323111")
                || accessCodeInput.equals("395195")
                || accessCodeInput.equals("100000");

        if (accessGranted) {

            System.out.println("Access Granted.");

            // ---------------------------------------------------------
            // 5. SEARCH FOR AN EMPLOYEE BY NAME
            // ---------------------------------------------------------

            System.out.println("Enter employee name to search:");
            String nameInput = input.nextLine().trim();

            Map<String, String> record = findRecord("name", nameInput);

            if (record != null) {

                Employee emp = new Employee(
                        Integer.parseInt(record.get("salary")),
                        record.get("address"),
                        record.get("position"),
                        Integer.parseInt(record.get("employee_id")),
                        Boolean.parseBoolean(record.get("account_with_bank")),
                        record.get("name")
                );

                // ---------------------------------------------------------
                // 6. DISPLAY EMPLOYEE INFO
                // ---------------------------------------------------------

                System.out.println("\n--- EMPLOYEE INFO ---");
                System.out.println("Name:             " + emp.getName());
                System.out.println("Employee ID:      " + emp.getEmployeeID());
                System.out.println("Position:         " + emp.getPosition());
                System.out.println("Salary:           $" + emp.getSalary());
                System.out.println("Address:          " + emp.getAddress());
                System.out.println("Has Bank Account: " + emp.getAccountWithBank());

                // ---------------------------------------------------------
                // 7. IF EMPLOYEE BANKS WITH US, SHOW THEIR ACCOUNT DETAILS
                // ---------------------------------------------------------

                if (emp.getAccountWithBank()) {
                    System.out.println("\nLoading bank account details...");

                    List<BankingCSV.User> bankingUsers = new ArrayList<>();

                    EmployeeAccount empAccount = new EmployeeAccount(
                            String.valueOf(emp.getEmployeeID()),
                            emp.getName(),
                            bankingUsers
                    );

                    empAccount.showAccounts();
                }

            } else {
                System.out.println("Employee not found in database.");
            }

        } else {
            System.out.println("Access Denied. Invalid employee ID.");
        }

        input.close();
    }

    // ---------------------------------------------------------
    // HELPER: findRecord
    // Searches employees.csv for a row where columnName equals value.
    // Returns a Map of column -> value, or null if not found.
    // ---------------------------------------------------------

    private static Map<String, String> findRecord(String columnName, String value) throws IOException {
        if (!Files.exists(EMPLOYEES_CSV)) return null;

        try (BufferedReader br = Files.newBufferedReader(EMPLOYEES_CSV)) {
            String headerLine = br.readLine();
            if (headerLine == null) return null;

            String[] headers = headerLine.split(",", -1);

            // Find which index matches the requested column name
            int searchIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase(columnName)) {
                    searchIndex = i;
                    break;
                }
            }
            if (searchIndex == -1) return null;

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);

                if (fields.length > searchIndex
                        && fields[searchIndex].trim().equalsIgnoreCase(value.trim())) {

                    Map<String, String> row = new LinkedHashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        row.put(headers[i].trim(), i < fields.length ? fields[i].trim() : "");
                    }
                    return row;
                }
            }
        }
        return null;
    }

    // ---------------------------------------------------------
    // HELPER: addRecord
    // Appends a new row to employees.csv.
    // ---------------------------------------------------------

    private static void addRecord(String... values) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(EMPLOYEES_CSV, StandardOpenOption.APPEND)) {
            bw.write(String.join(",", values));
            bw.newLine();
        }
    }
}
