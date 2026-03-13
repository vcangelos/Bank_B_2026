import java.util.*;
import java.nio.file.*;
import java.io.*;

// Main driver class that runs the employee lookup program
public class EmployeeDriver {

    public static void main(String[] args) throws Exception {

        // Scanner allows us to read user input from the keyboard
        Scanner input = new Scanner(System.in);

        // 1. Open or create the CSV file that stores employee data
        // If employees.csv does not exist, it will be created with these headers
        csvFile employees = csvFile.openOrCreate(
                Path.of("employees.csv"),
                "employee_id",
                "name",
                "salary",
                "address",
                "position",
                "account_with_bank"
        );

        // NOTE:
        // I removed addRecord() because that method does not exist in csvFile.
        // The program now assumes employees already exist in the CSV.


        // 2. Ask the user for an employee ID to access the system
        System.out.println("Enter your employee ID to access the database:");
        int accessCode = input.nextInt();

        // nextInt() leaves a newline character in the scanner
        // so we clear it with nextLine()
        input.nextLine();


        // 3. Simple access check
        // Only these IDs can access the database
        if (accessCode == 123456 || accessCode == 123195 || accessCode == 123851) {

            System.out.println("Access Granted.");

            // Ask the user which employee they want to search for
            System.out.println("Enter employee name:");
            String nameInput = input.nextLine();


            // Search the CSV for a record where the "name" column matches nameInput
            Map<String, String> record = employees.getRecord("name", nameInput);


            // If a matching employee is found
            if (record != null) {

                // Create an Employee object using the values from the CSV record
                Employee emp = new Employee(

                        // Convert salary from String → int
                        Integer.parseInt(record.get("salary")),

                        // Address
                        record.get("address"),

                        // Job position
                        record.get("position"),

                        // Convert employee_id from String → int
                        Integer.parseInt(record.get("employee_id")),

                        // Convert text "true"/"false" → boolean
                        Boolean.parseBoolean(record.get("account_with_bank")),

                        // Employee name
                        record.get("name")
                );


                // Display employee information
                System.out.println("\n--- EMPLOYEE INFO ---");
                System.out.println("Name: " + emp.getName());
                System.out.println("Salary: $" + emp.getSalary());
                System.out.println("Position: " + emp.getPosition());
                System.out.println("Address: " + emp.getAddress());
                System.out.println("Has Bank Account? " + emp.getAccountWithBank());
                System.out.println("Employee ID: " + emp.getEmployeeID());

            } else {

                // If no employee with that name exists
                System.out.println("Employee not found.");
            }

        } else {

            // If access code does not match
            System.out.println("Access Denied.");
        }

        // Close scanner to prevent resource leaks
        input.close();
    }
}
