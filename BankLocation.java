
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
    //key-zip code, value-BankLocation object
    
    //Location specific info
    private String branchName;
    private String zipCode;
    private String routingNumber;

    //Overloaded constructor that creates one location
    public BankLocation(String branchName, String zipCode, String routingNumber) {
        this.branchName;
        this.zipCode;
        this.routingNumber;
        //Stores the location in the map
        locations.put(zipCode, this);
    }
    
    //Instance variable getters
    public String getBranchName() 
        { return branchName; }
    
    public String getZipCode()
        { return zipCode; }
    
    public String getRoutingNumber() 
        { return routingNumber; }
    
    //Static getters & setters
    public static String getBankType() 
        { return bankType; }

    public static void setTaxRate(double rate)
        { taxRate = rate; }
    public static double getTaxRate() 
        { return taxRate; }

    public static void setInterestRate(double rate) 
        { interestRate = rate; }
    public static double getInterestRate()
        { return interestRate; }

    public static void setPhoneNumber(String phone) 
        { phoneNumber = phone; }
    public static String getPhoneNumber() 
        { return phoneNumber; }

    public static void setEmail(String em) 
        { email = em; }
    public static String getEmail() 
        { return email; }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
    