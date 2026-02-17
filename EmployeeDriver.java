import java.util.Scanner;

public class EmployeeDriver {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        // Scan for employee ID
        System.out.println("Please enter your employee ID in order to access the database.");
        int keyboard = input.nextInt();
        input.nextLine(); // clear newline

        if (keyboard == 123456 || keyboard == 123195 || keyboard == 123821) {

            System.out.println("Access Granted. Which employee would you like to see?");
            String arh = input.nextLine();

            if (arh.equalsIgnoreCase("Alice")) {

                Employee emp1 = new Employee(
                    40000,
                    "123 Main St",
                    "Teller",
                    123456,
                    true,
                    "Alice Smith"
                );

                System.out.println("INFORMATION:");
                System.out.println("Name: " + emp1.getName());
                System.out.println("Salary: $" + emp1.getSalary());
                System.out.println("Position: " + emp1.getPosition());
                System.out.println("Address: " + emp1.getAddress());
                System.out.println("Do they have an account with the Bank? " + emp1.getAccountWithBank());
                System.out.println("EmployeeID: " + emp1.getEmployeeID());

            } else if (arh.equalsIgnoreCase("Bobby")) {

                Employee emp2 = new Employee(
                    63000,
                    "456 Secondary St",
                    "Loan Officer",
                    123195,
                    true,
                    "Bobby Johnson"
                );
                System.out.println("INFORMATION:");
                System.out.println("Name: " + emp2.getName());
                System.out.println("Salary: $" + emp2.getSalary());
                System.out.println("Position: " + emp2.getPosition());
                System.out.println("Address: " + emp2.getAddress());
                System.out.println("Do they have an account with the Bank? " + emp2.getAccountWithBank());
                System.out.println("EmployeeID: " + emp2.getEmployeeID());

            } else {
                System.out.println("Employee not found.");
            } 

        } else {
            System.out.println("Access Denied. Try again.");
        }
        
        input.close();
    }
}
