import java.util.*;
import java.nio.file.*;
import java.io.*;

// -------------------------
// CSV Parsing Helper
// -------------------------
class csvParsing {
    public static List<String> parseLine(String line) {
        // Split a CSV line into columns
        return Arrays.asList(line.split(","));
    }
}

// -------------------------
// CSV Utility
// -------------------------
class csvFile {
    private final Path path;                    // CSV file path
    private final List<String> headers = new ArrayList<>(); // Column headers

    public csvFile(Path path) throws IOException {
        this.path = path;
        loadHeader();
    }

    // Open or create CSV
    public static csvFile openOrCreate(Path path, String... cols) throws IOException {
        if (Files.notExists(path)) {
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(String.join(",", cols));
                writer.newLine();
            }
        }
        return new csvFile(path);
    }

    // Load header line
    private void loadHeader() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine();
            if (line == null) throw new IllegalArgumentException("CSV is empty");
            headers.addAll(csvParsing.parseLine(line));
        }
    }

    // Get a record by column name and value
    public Map<String, String> getRecord(String column, String value) throws IOException {
        int colIndex = headers.indexOf(column);
        if (colIndex == -1) throw new IllegalArgumentException("Unknown column: " + column);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> row = csvParsing.parseLine(line);
                if (row.get(colIndex).equals(value)) {
                    Map<String, String> record = new HashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        record.put(headers.get(i), row.get(i));
                    }
                    return record;
                }
            }
        }
        return null; // Not found
    }

    // Add a record (optional, can add new employees)
    public void addRecord(String... values) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            writer.write(String.join(",", values));
            writer.newLine();
        }
    }
}

// -------------------------
// Employee Class
// -------------------------
class Employee {
    private int salary;
    private String address;
    private String position;
    private int employeeID;
    private boolean accountWithBank;
    private String name;

    public Employee(int salary, String address, String position,
                    int employeeID, boolean accountWithBank, String name) {
        this.salary = salary;
        this.address = address;
        this.position = position;
        this.employeeID = employeeID;
        this.accountWithBank = accountWithBank;
        this.name = name;
    }

    // Getters
    public int getSalary() { return salary; }
    public int getEmployeeID() { return employeeID; }
    public String getAddress() { return address; }
    public String getPosition() { return position; }
    public boolean getAccountWithBank() { return accountWithBank; }
    public String getName() { return name; }
}

// -------------------------
// Main Driver
// -------------------------
public class EmployeeDriver {
    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);

        // 1. Open or create CSV file
        csvFile employees = csvFile.openOrCreate(
                Path.of("employees.csv"),
                "employee_id",
                "name",
                "salary",
                "address",
                "position",
                "account_with_bank"
        );

        // 2. Add sample employees if not already there
        if (employees.getRecord("employee_id", "123111") == null) {
            employees.addRecord("123111","Alice Smith","40000","123 Main St","Teller","true");
            employees.addRecord("895195","Bobby Johnson","63000","456 Secondary St","Loan Officer","true");
        }

        // 3. Ask for access code
        System.out.println("Enter your employee ID to access the database:");
        int accessCode = input.nextInt();
        input.nextLine(); // clear newline

        // 4. Simple access check
        if (accessCode == 123456 || accessCode == 123195 || accessCode == 123851) {
            System.out.println("Access Granted.");

            // Ask which employee to look up
            System.out.println("Enter employee name:");
            String nameInput = input.nextLine();

            Map<String, String> record = employees.getRecord("name", nameInput);

            if (record != null) {
                // Build Employee object from CSV
                Employee emp = new Employee(
                        Integer.parseInt(record.get("salary")),
                        record.get("address"),
                        record.get("position"),
                        Integer.parseInt(record.get("employee_id")),
                        Boolean.parseBoolean(record.get("account_with_bank")),
                        record.get("name")
                );

                // Show employee info
                System.out.println("\n--- EMPLOYEE INFO ---");
                System.out.println("Name: " + emp.getName());
                System.out.println("Salary: $" + emp.getSalary());
                System.out.println("Position: " + emp.getPosition());
                System.out.println("Address: " + emp.getAddress());
                System.out.println("Has Bank Account? " + emp.getAccountWithBank());
                System.out.println("Employee ID: " + emp.getEmployeeID());

            } else {
                System.out.println("Employee not found.");
            }

        } else {
            System.out.println("Access Denied.");
        }

        input.close();
    }
}
