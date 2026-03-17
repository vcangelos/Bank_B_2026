// EmployeeDriver.java

// Import Scanner, Map, List, etc.
import java.util.*;

// Import classes for file paths
import java.nio.file.*;

// Import classes for file input/output
import java.io.*;

// Main class that runs the employee database program
public class EmployeeDriver {

    // The main method is where Java programs start running
    public static void main(String[] args) throws Exception {

        // Create a Scanner object to read user input from the keyboard
        Scanner input = new Scanner(System.in);


        // ---------------------------------------------------------
        // 1. OPEN OR CREATE THE CSV FILE
        // ---------------------------------------------------------

        // csvFile is a custom class used to manage CSV data
        // openOrCreate will:
        // - open the file if it already exists
        // - create a new one if it doesn't
        // The Path.of() method creates a file path object
        csvFile employees = csvFile.openOrCreate(

                // Path.of creates a file path for employees.csv
                Path.of("employees.csv"),

                // These strings define the column headers in the CSV
                "employee_id",
                "name",
                "salary",
                "address",
                "position",
                "account_with_bank"
        );


        // ---------------------------------------------------------
        // 2. ADD SAMPLE EMPLOYEES IF FILE IS EMPTY
        // ---------------------------------------------------------

        // getRecord searches the CSV for a row where
        // column "employee_id" equals "123111"
        if (employees.getRecord("employee_id", "123111") == null) {

            // If no record exists, add sample employees

            employees.addRecord(
                    "123111",          // employee_id
                    "Alice Smith",     // name
                    "40000",           // salary
                    "123 Main St",     // address
                    "Teller",          // job position
                    "true"             // whether they have a bank account
            );

            employees.addRecord(
                    "895195",
                    "Bobby Johnson",
                    "63000",
                    "456 Secondary St",
                    "Loan Officer",
                    "true"
            );
        }


        // ---------------------------------------------------------
        // 3. ASK USER FOR ACCESS CODE
        // ---------------------------------------------------------

        // Prompt user
        System.out.println("Enter your employee ID to access the database:");

        // nextInt reads an integer from user input
        int accessCode = input.nextInt();

        // nextInt does not remove the newline character
        // so we clear it with nextLine()
        input.nextLine();


        // ---------------------------------------------------------
        // 4. ACCESS CHECK
        // ---------------------------------------------------------

        // This checks if the entered code matches one of the allowed IDs
        if (accessCode == 123456 || accessCode == 123195 || accessCode == 123851) {

            // If correct code
            System.out.println("Access Granted.");


            // ---------------------------------------------------------
            // ASK WHICH EMPLOYEE TO SEARCH
            // ---------------------------------------------------------

            System.out.println("Enter employee name:");

            // Read the employee name
            String nameInput = input.nextLine();


            // ---------------------------------------------------------
            // SEARCH CSV DATABASE
            // ---------------------------------------------------------

            // getRecord searches the CSV for a row
            // where column "name" matches nameInput
            Map<String, String> record = employees.getRecord("name", nameInput);


            // ---------------------------------------------------------
            // IF EMPLOYEE EXISTS
            // ---------------------------------------------------------

            if (record != null) {

                // Create an Employee object using data from the CSV

                Employee emp = new Employee(

                        // Integer.parseInt converts String -> int
                        // CSV stores numbers as text so we convert them
                        Integer.parseInt(record.get("salary")),

                        // address stored as String
                        record.get("address"),

                        // job position
                        record.get("position"),

                        // convert employee_id String -> int
                        Integer.parseInt(record.get("employee_id")),

                        // Boolean.parseBoolean converts "true"/"false" text -> boolean
                        Boolean.parseBoolean(record.get("account_with_bank")),

                        // employee name
                        record.get("name")
                );


                // ---------------------------------------------------------
                // DISPLAY EMPLOYEE INFORMATION
                // ---------------------------------------------------------

                System.out.println("\n--- EMPLOYEE INFO ---");

                // Use getter methods from the Employee class

                System.out.println("Name: " + emp.getName());

                System.out.println("Salary: $" + emp.getSalary());

                System.out.println("Position: " + emp.getPosition());

                System.out.println("Address: " + emp.getAddress());

                System.out.println("Has Bank Account? " + emp.getAccountWithBank());

                System.out.println("Employee ID: " + emp.getEmployeeID());


            } else {

                // If no matching employee is found
                System.out.println("Employee not found.");
            }

        } else {

            // If access code is incorrect
            System.out.println("Access Denied.");
        }


        // ---------------------------------------------------------
        // CLOSE SCANNER
        // ---------------------------------------------------------

        // Closing Scanner prevents memory leaks
        input.close();
    }
}
