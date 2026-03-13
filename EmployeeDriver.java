import java.util.*;
import java.nio.file.*;
import java.io.*;

public class EmployeeDriver {

    public static void main(String[] args) throws Exception {

        Scanner input = new Scanner(System.in);

        // Open CSV file
        csvFile employees = csvFile.openOrCreate(
                Path.of("employees.csv"),
                "employee_id",
                "name",
                "salary",
                "address",
                "position",
                "account_with_bank"
        );

        // Ask for access code
        System.out.println("Enter your employee ID to access the database:");
        int accessCode = input.nextInt();
        input.nextLine();

        // Access check
        if (accessCode == 123456 || accessCode == 123195 || accessCode == 123851) {

            System.out.println("Access Granted.");

            System.out.println("Enter employee name:");
            String nameInput = input.nextLine();

            Map<String, String> record = employees.getRecord("name", nameInput);

            if (record != null) {

                Employee emp = new Employee(

                        Integer.parseInt(record.get("salary")),
                        record.get("address"),
                        record.get("position"),
                        Integer.parseInt(record.get("employee_id")),
                        Boolean.parseBoolean(record.get("account_with_bank")),
                        record.get("name")
                );

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
