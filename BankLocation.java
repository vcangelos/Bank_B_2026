
import java.util.HashMap;
import java.util.Map;

public class BankLocation {
    
    //Franchise bank info
    private static String bankType = "Franchise Bank";
    private static double taxRate;
    private static double interestRate;
    private String phoneNumber;
    private String email;

    //Map for all locations that stores key-value pairs
    private static Map<String, BankLocation> 
        locations = new HashMap<>();
    //key-zip code, value-BankLocation object
    
    //Location specific info
    private String branchName;
    private String zipCode;
    private String routingNumber;

    
}