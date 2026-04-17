import java.io.*;
import java.util.*;

public class UserDatabase {
    private static final String FILE   = "customerInfo.csv";
    private static final String HEADER = "customerID,firstName,lastName,SSN,DOB,email," +
            "phoneNumber,BankLocation,bankPhoneNumber,SavingsAccount,CheckingAccount," +
            "CreditCard,creditCardLimit,creditScore,hasDebitCard,cdBalance,cdInterestRate," +
            "password,securityQuestion,securityAnswer,isLocked";

    private Map<String, User> usersByEmail = new LinkedHashMap<>();

    public UserDatabase() {
        load();
    }

    private void load() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || firstLine) { firstLine = false; continue; }
                User u = User.fromCsv(line);
                if (u != null) usersByEmail.put(u.email.toLowerCase(), u);
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not read customerInfo.csv");
        }
    }

    private void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            pw.println(HEADER);
            for (User u : usersByEmail.values()) {
                pw.println(u.toCsv());
            }
        } catch (IOException e) {
            System.out.println("Error: Could not save to customerInfo.csv");
        }
    }

    public boolean emailExists(String email) {
        return usersByEmail.containsKey(email.toLowerCase());
    }

    public User getByEmail(String email) {
        return usersByEmail.get(email.toLowerCase());
    }

    public void addUser(User u) {
        usersByEmail.put(u.email.toLowerCase(), u);
        save();
    }

    public void updateUser(User u) {
        usersByEmail.values().removeIf(x -> x.customerID.equals(u.customerID));
        usersByEmail.put(u.email.toLowerCase(), u);
        save();
    }

    public String generateAccountId() {
        Random rand = new Random();
        Set<String> existing = new HashSet<>();
        for (User u : usersByEmail.values()) existing.add(u.customerID);
        String id;
        do {
            id = String.format("%09d", rand.nextInt(1_000_000_000));
        } while (existing.contains(id));
        return id;
    }
}