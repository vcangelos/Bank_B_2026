//SafetyDepositBox.loadBoxesFromCSV() - for codes to call 
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.*;
import java.io.IOException;

public class SafetyDepositBox {
    
    // Box properties
    private String boxID;
    private String ownerCustomerID;
    private String boxSize;
    private double annualFee;
    private String boxPassword;
    private boolean isRented;
    private LocalDateTime rentalStartDate;
    private LocalDateTime lastFeePaymentDate;
    private LocalDateTime lastAccessDate;
    private List<String> storedItems;
    private Set<String> authorizedUsers;
    
    // Constants
    private static final double SMALL_BOX_FEE = 50.00;
    private static final double MEDIUM_BOX_FEE = 60.00;
    private static final double LARGE_BOX_FEE = 80.00;
    
    private static final int SMALL_BOX_CAPACITY = 10;
    private static final int MEDIUM_BOX_CAPACITY = 20;
    private static final int LARGE_BOX_CAPACITY = 30;
    
    // CSV paths
    private static final Path BOX_CSV_PATH = Path.of("SafetyDepositBox.csv");
    private static final Path CUSTOMER_CSV_PATH = Path.of("customerInfo.csv");
    
    // Track all boxes
    private static Map<String, SafetyDepositBox> allBoxes = new HashMap<>();
    
    // Box counter
    private static int nextBoxNumber = 1;
    
    public SafetyDepositBox(String boxID, String ownerCustomerID, String boxSize, String boxPassword) {
        this.boxID = boxID;
        this.ownerCustomerID = ownerCustomerID;
        this.boxSize = boxSize;
        this.boxPassword = boxPassword;
        this.isRented = true;
        this.rentalStartDate = LocalDateTime.now();
        this.lastFeePaymentDate = LocalDateTime.now();
        this.lastAccessDate = LocalDateTime.now();
        this.storedItems = new ArrayList<>();
        this.authorizedUsers = new HashSet<>();
        this.authorizedUsers.add(ownerCustomerID);
        
        switch (boxSize.toLowerCase()) {
            case "small": this.annualFee = SMALL_BOX_FEE; break;
            case "medium": this.annualFee = MEDIUM_BOX_FEE; break;
            case "large": this.annualFee = LARGE_BOX_FEE; break;
            default: this.annualFee = MEDIUM_BOX_FEE;
        }
    }
    
    private static String generateBoxID() {
        String id = "SDB" + String.format("%03d", nextBoxNumber);
        nextBoxNumber++;
        return id;
    }
    
    // Load all boxes from CSV on startup
    public static void loadBoxesFromCSV() {
        try {
            if (!Files.exists(BOX_CSV_PATH)) {
                String header = "boxID,ownerCustomerID,boxSize,annualFee,boxPassword,rentalStartDate,lastFeePaymentDate,lastAccessDate,isRented,storedItems,authorizedUsers";
                Files.write(BOX_CSV_PATH, Arrays.asList(header));
                return;
            }
            
            List<String> lines = Files.readAllLines(BOX_CSV_PATH);
            
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] fields = line.split(",", -1);
                
                if (fields.length >= 11) {
                    String boxID = fields[0];
                    String ownerID = fields[1];
                    String boxSize = fields[2];
                    String password = fields[4];
                    
                    SafetyDepositBox box = new SafetyDepositBox(boxID, ownerID, boxSize, password);
                    
                    box.rentalStartDate = LocalDateTime.parse(fields[5]);
                    box.lastFeePaymentDate = LocalDateTime.parse(fields[6]);
                    box.lastAccessDate = LocalDateTime.parse(fields[7]);
                    box.isRented = Boolean.parseBoolean(fields[8]);
                    
                    if (!fields[9].isEmpty()) {
                        String[] items = fields[9].split("\\|");
                        box.storedItems = new ArrayList<>(Arrays.asList(items));
                    }
                    
                    if (!fields[10].isEmpty()) {
                        String[] users = fields[10].split("\\|");
                        box.authorizedUsers = new HashSet<>(Arrays.asList(users));
                    }
                    
                    allBoxes.put(boxID, box);
                    
                    String numPart = boxID.substring(3);
                    int boxNum = Integer.parseInt(numPart);
                    if (boxNum >= nextBoxNumber) {
                        nextBoxNumber = boxNum + 1;
                    }
                }
            }
            
            System.out.println("Loaded " + allBoxes.size() + " safety deposit boxes from CSV");
            
        } catch (IOException e) {
            System.out.println("Warning: Could not load boxes from CSV");
        }
    }
    
    // Save all boxes to CSV
    private static void saveAllBoxesToCSV() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("boxID,ownerCustomerID,boxSize,annualFee,boxPassword,rentalStartDate,lastFeePaymentDate,lastAccessDate,isRented,storedItems,authorizedUsers");
            
            for (SafetyDepositBox box : allBoxes.values()) {
                String itemsStr = String.join("|", box.storedItems);
                String usersStr = String.join("|", box.authorizedUsers);
                
                String line = String.format("%s,%s,%s,%.2f,%s,%s,%s,%s,%s,%s,%s",
                    box.boxID,
                    box.ownerCustomerID,
                    box.boxSize,
                    box.annualFee,
                    box.boxPassword,
                    box.rentalStartDate,
                    box.lastFeePaymentDate,
                    box.lastAccessDate,
                    box.isRented,
                    itemsStr,
                    usersStr
                );
                
                lines.add(line);
            }
            
            Files.write(BOX_CSV_PATH, lines);
            
        } catch (IOException e) {
            System.out.println("Error: Could not save to CSV");
        }
    }
    
    // Rent a box - no banking dependency, just creates the box
    public static SafetyDepositBox rentBox(Scanner scanner, String customerID) {
        try {
            csvFile customerFile = new csvFile(CUSTOMER_CSV_PATH);
            Map<String, String> customerRecord = customerFile.getRecord("customerID", customerID);
            
            if (customerRecord == null) {
                System.out.println("Error: Customer not found");
                return null;
            }
            
            String firstName = customerRecord.get("firstName");
            String lastName = customerRecord.get("lastName");
            
            System.out.println("\n========================================");
            System.out.println("   Safety Deposit Box Rental");
            System.out.println("========================================");
            System.out.println("Customer: " + firstName + " " + lastName);
            
            System.out.println("\nAvailable Box Sizes:");
            System.out.println("1. Small  (5\" x 5\" x 21.5\")  - $" + SMALL_BOX_FEE + "/year");
            System.out.println("2. Medium (3\" x 10\" x 21.5\") - $" + MEDIUM_BOX_FEE + "/year");
            System.out.println("3. Large  (5\" x 10\" x 21.5\") - $" + LARGE_BOX_FEE + "/year");
            
            System.out.print("\nSelect box size (1-3): ");
            int choice = scanner.nextInt();
            
            String boxSize;
            double fee;
            
            switch (choice) {
                case 1: boxSize = "Small"; fee = SMALL_BOX_FEE; break;
                case 2: boxSize = "Medium"; fee = MEDIUM_BOX_FEE; break;
                case 3: boxSize = "Large"; fee = LARGE_BOX_FEE; break;
                default:
                    System.out.println("Invalid selection");
                    return null;
            }
            
            System.out.print("\nCreate a password for your box: ");
            String boxPassword = scanner.next();
            
            System.out.print("Confirm password: ");
            String confirmPassword = scanner.next();
            
            if (!boxPassword.equals(confirmPassword)) {
                System.out.println("Error: Passwords do not match");
                return null;
            }
            
            String boxID = generateBoxID();
            SafetyDepositBox newBox = new SafetyDepositBox(boxID, customerID, boxSize, boxPassword);
            
            allBoxes.put(boxID, newBox);
            saveAllBoxesToCSV();
            
            System.out.println("\n✓ Safety Deposit Box rented successfully!");
            System.out.println("  Box ID: " + boxID);
            System.out.println("  Size: " + boxSize);
            System.out.println("  Annual Fee: $" + fee + "/year");
            System.out.println("\n⚠ IMPORTANT: Remember your box password!");
            
            return newBox;
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    
    // Access box
    public static SafetyDepositBox accessBox(Scanner scanner, String customerID) {
        System.out.println("\n=== Access Safety Deposit Box ===");
        
        List<SafetyDepositBox> customerBoxes = new ArrayList<>();
        for (SafetyDepositBox box : allBoxes.values()) {
            if (box.authorizedUsers.contains(customerID)) {
                customerBoxes.add(box);
            }
        }
        
        if (customerBoxes.isEmpty()) {
            System.out.println("You don't have any safety deposit boxes");
            return null;
        }
        
        System.out.println("\nYour boxes:");
        for (SafetyDepositBox box : customerBoxes) {
            System.out.println("  " + box.boxID + " (" + box.boxSize + ")");
        }
        
        System.out.print("\nEnter Box ID: ");
        String boxID = scanner.next();
        
        SafetyDepositBox box = allBoxes.get(boxID);
        
        if (box == null) {
            System.out.println("Error: Box not found");
            return null;
        }
        
        if (!box.authorizedUsers.contains(customerID)) {
            System.out.println("Error: You don't have access to this box");
            return null;
        }
        
        System.out.print("Enter box password: ");
        String enteredPassword = scanner.next();
        
        if (!box.boxPassword.equals(enteredPassword)) {
            System.out.println("Error: Incorrect password");
            return null;
        }
        
        System.out.println("✓ Access granted");
        box.lastAccessDate = LocalDateTime.now();
        saveAllBoxesToCSV();
        
        return box;
    }
    
    // Deposit items
    public boolean depositItems(Scanner scanner, String customerID) {
        int capacity = getBoxCapacity();
        int available = capacity - storedItems.size();
        
        if (available == 0) {
            System.out.println("Error: Box is full (capacity: " + capacity + ")");
            return false;
        }
        
        System.out.println("\nBox capacity: " + storedItems.size() + "/" + capacity);
        System.out.println("Available space: " + available + " items");
        
        System.out.print("\nHow many items do you want to deposit? ");
        int numItems = scanner.nextInt();
        scanner.nextLine(); // consume leftover newline
        
        if (numItems > available) {
            System.out.println("Error: Not enough space. Maximum: " + available);
            return false;
        }
        
        for (int i = 0; i < numItems; i++) {
            System.out.print("Item " + (i + 1) + " name: ");
            String itemName = scanner.nextLine();
            storedItems.add(itemName);
            System.out.println("  ✓ Added: " + itemName);
        }
        
        lastAccessDate = LocalDateTime.now();
        saveAllBoxesToCSV();
        
        System.out.println("\n✓ " + numItems + " item(s) deposited successfully");
        return true;
    }
    
    // Withdraw items - fixed scanner bug
    public boolean withdrawItems(Scanner scanner, String customerID) {
        if (storedItems.isEmpty()) {
            System.out.println("Box is empty");
            return false;
        }
        
        System.out.println("\nCurrent contents:");
        for (int i = 0; i < storedItems.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + storedItems.get(i));
        }
        
        System.out.print("\nEnter item name to withdraw: ");
        String itemName = scanner.nextLine();
        if (itemName.isEmpty()) {
            itemName = scanner.nextLine(); // handle leftover newline
        }
        
        if (storedItems.contains(itemName)) {
            storedItems.remove(itemName);
            lastAccessDate = LocalDateTime.now();
            saveAllBoxesToCSV();
            System.out.println("✓ Item removed: " + itemName);
            return true;
        } else {
            System.out.println("Error: Item not found");
            return false;
        }
    }
    
    // View contents
    public void viewContents(String customerID) {
        int capacity = getBoxCapacity();
        
        System.out.println("\n=== Box Contents ===");
        System.out.println("Box ID: " + boxID);
        System.out.println("Size: " + boxSize);
        System.out.println("Capacity: " + storedItems.size() + "/" + capacity);
        
        if (storedItems.isEmpty()) {
            System.out.println("\nBox is empty");
        } else {
            System.out.println("\nStored items:");
            for (int i = 0; i < storedItems.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + storedItems.get(i));
            }
        }
        
        lastAccessDate = LocalDateTime.now();
        saveAllBoxesToCSV();
    }
    
    // Grant access
    public boolean grantAccess(Scanner scanner, String customerID) {
        if (!customerID.equals(ownerCustomerID)) {
            System.out.println("Error: Only the owner can grant access");
            return false;
        }
        
        System.out.print("\nEnter customer ID to grant access: ");
        String otherCustomerID = scanner.next();
        
        try {
            csvFile customerFile = new csvFile(CUSTOMER_CSV_PATH);
            Map<String, String> customerRecord = customerFile.getRecord("customerID", otherCustomerID);
            
            if (customerRecord == null) {
                System.out.println("Error: Customer not found");
                return false;
            }
            
            if (authorizedUsers.contains(otherCustomerID)) {
                System.out.println("This customer already has access");
                return false;
            }
            
            authorizedUsers.add(otherCustomerID);
            saveAllBoxesToCSV();
            
            String firstName = customerRecord.get("firstName");
            String lastName = customerRecord.get("lastName");
            
            System.out.println("✓ Access granted to: " + firstName + " " + lastName);
            return true;
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
    
    private int getBoxCapacity() {
        switch (boxSize.toLowerCase()) {
            case "small": return SMALL_BOX_CAPACITY;
            case "medium": return MEDIUM_BOX_CAPACITY;
            case "large": return LARGE_BOX_CAPACITY;
            default: return MEDIUM_BOX_CAPACITY;
        }
    }
    
    public void displayBoxInfo() {
        System.out.println("\n=== Safety Deposit Box Information ===");
        System.out.println("Box ID: " + boxID);
        System.out.println("Owner: " + ownerCustomerID);
        System.out.println("Size: " + boxSize);
        System.out.println("Annual Fee: $" + annualFee);
        System.out.println("Rental Start: " + rentalStartDate.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        ));
        System.out.println("Last Access: " + lastAccessDate.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        ));
        System.out.println("Items Stored: " + storedItems.size() + "/" + getBoxCapacity());
        System.out.println("Authorized Users: " + authorizedUsers.size());
        System.out.println("Status: " + (isRented ? "Active" : "Closed"));
    }
    
    // Main menu
    public static void manageBox(Scanner scanner, String customerID) {
        SafetyDepositBox box = accessBox(scanner, customerID);
        
        if (box == null) return;
        
        boolean continueManaging = true;
        
        while (continueManaging) {
            System.out.println("\n=== Safety Deposit Box Menu ===");
            System.out.println("1. Deposit items");
            System.out.println("2. Withdraw items");
            System.out.println("3. View contents");
            System.out.println("4. Grant access to another person");
            System.out.println("5. View box information");
            System.out.println("6. Exit");
            
            System.out.print("\nSelect option: ");
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1: box.depositItems(scanner, customerID); break;
                case 2: box.withdrawItems(scanner, customerID); break;
                case 3: box.viewContents(customerID); break;
                case 4: box.grantAccess(scanner, customerID); break;
                case 5: box.displayBoxInfo(); break;
                case 6: continueManaging = false; break;
                default: System.out.println("Invalid option");
            }
        }
    }
}
