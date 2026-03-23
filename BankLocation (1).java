import java.util.HashMap;
import java.util.Map;

public class BankLocation {
    
    //Franchise-wide bank info
    private static String bankType = "Franchise Bank";
    private static double taxRate;
    private static double interestRate;
    private static String phoneNumber;
    private static String email;

    //Map for all locations that stores key-value pairs
    private static Map<String, BankLocation> 
        locations = new HashMap<>();
    //key: zip code, value: BankLocation object
    
    //Location specific info
    private String branchName;
    private String zipCode;
    private String routingNumber;

    //Overloaded constructor that creates one location
    public BankLocation(String branchName, String zipCode, String routingNumber) {
        if (branchName != null && !branchName.trim().isEmpty()) {
            this.branchName = branchName;
        } else { 
            this.branchName = "Unknown Name";
            System.out.println("Invalid branch name");
        }
        if (zipCode.matches("\\d{5}")) {
            this.zipCode = zipCode;
        } else {
            this.zipCode = "00000";
            System.out.println("Invalid zip code");
        }
        if (routingNumber.matches("\\d{9}")) {
            this.routingNumber = routingNumber;
        } else {
            this.routingNumber = "000000000";
            System.out.println("Invalid routing number");
        }

        //Stores the location in the map
        locations.put(this.zipCode, this);
    }
    
    //Instance variable getters
    public String getBranchName() {
        return branchName; 
    }
    
    public String getZipCode() {
        return zipCode; 
    }
    
    public String getRoutingNumber()  {
        return routingNumber; 
    }
    
    //Static getters & setters
    public static String getBankType() {
        return bankType; 
    }
    
    public static double getTaxRate() {
        return taxRate;
    }
    public static void setTaxRate(double rate) {
        if (rate >= 0 && rate <= 1) {
            taxRate = rate;
        } else {
            System.out.println("Invalid tax rate");
        }
    }
    
    public static double getInterestRate() {
        return interestRate; 
    }
    public static void setInterestRate(double rate) {
        if (rate >= 0 && rate <= 1) {
            interestRate = rate;
        } else {
            System.out.println("Invalid interest rate");
        }
    }
    
    public static String getPhoneNumber() {
        return phoneNumber; 
    }
    public static void setPhoneNumber(String phone) {
        if (phone != null && phone.matches("\\d{10}")) {
            phoneNumber = phone;
        } else {
            System.out.println("Invalid phone number");
        }
    }
    
    public static String getEmail() {
        return email; 
    }
    public static void setEmail(String em) {
        if (em != null && em.contains("@") && em.contains(".")) {
            email = em;
        } else {
            System.out.println("Invalid email");
        }
    }
    
    //Allows access to the map and retrieval of specific locations
    public static BankLocation getLocationByZip(String zip) {
        return locations.get(zip);
    }
    
    //Tester 
    public static void main(String[] args) {

        //Set franchise-wide information
        BankLocation.setTaxRate(0.07);
        BankLocation.setInterestRate(0.03);
        BankLocation.setPhoneNumber("8005551234");
        BankLocation.setEmail("contact@franchisebank.com");

        //Create bank locations
        BankLocation loc1 = new BankLocation("Bank of Old Bridge", "08857", "832954724");
        BankLocation loc2 = new BankLocation("Bank of Dallas", "75001", "723297259");
        BankLocation loc3 = new BankLocation("Bank of Los Angeles", "90001", "563934953");

        //Retrieve a location using the zip code
        BankLocation found = BankLocation.getLocationByZip("08857");

        //Print results
        System.out.println("Bank Type: " + BankLocation.getBankType());
        System.out.println("Tax Rate: " + BankLocation.getTaxRate());
        System.out.println("Interest Rate: " + BankLocation.getInterestRate());
        System.out.println("Phone: " + BankLocation.getPhoneNumber());
        System.out.println("Email: " + BankLocation.getEmail());

        System.out.println();
        
        if (found != null) {
            System.out.println("Branch found:");
            System.out.println("Name: " + found.getBranchName());
            System.out.println("Zip: " + found.getZipCode());
            System.out.println("Routing: " + found.getRoutingNumber());
        } else {
            System.out.println("Branch not found");
        }
    }
}   
    
    
    
    
    
    
    
    
    