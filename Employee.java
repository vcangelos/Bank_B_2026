public class Employee {

    //instance variables
    private int salary;
    private String address;
    private String position;
    private int employeeID;
    private boolean accountWithBank;
    private String name;

    // Default constructor
    public Employee() {
        salary = 0;
        address = "";
        position = "";
        employeeID = 0;
        accountWithBank = false;
        name = "";
    }

    // overloaded constructor
    public Employee(int salary, String address, String position, int employeeID, boolean accountWithBank, String name) {
        this.salary = salary;
        this.address = address;
        this.position = position;
        this.employeeID = employeeID;
        this.accountWithBank = accountWithBank;
        this.name = name;
    }

    // getter methods
    public int getSalary() {
        return salary;
    }

    public String getAddress() {
        return address;
    }

    public String getPosition() {
        return position;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public boolean getAccountWithBank() {
        return accountWithBank;
    }

    public String getName() {
        return name;
    }

    // setter methods
    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public void setAccountWithBank(boolean accountWithBank) {
        this.accountWithBank = accountWithBank;
    }

    public void setName(String name) {
        this.name = name;
    }
}
