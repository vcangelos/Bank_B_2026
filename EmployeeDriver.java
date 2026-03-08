// EmployeeDriver.java
import java.util.*;
import java.nio.file.*;
import java.io.*;

// Assuming csvParsing and csvFile are in the same package or imported

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
