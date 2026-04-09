public class Employee {
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
