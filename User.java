public class User {
    public String customerID;
    public String firstName;
    public String lastName;
    public String ssn;
    public String dob;
    public String email;
    public String phoneNumber;
    public String bankLocation;
    public String bankPhoneNumber;  // ADD THIS LINE
    public double savingsAccount;
    public double checkingAccount;
    public String creditCard;
    public double creditCardLimit;
    public int    creditScore;
    public boolean hasDebitCard;
    public double cdBalance;
    public double cdInterestRate;
    public String password;
    public String securityQuestion;
    public String securityAnswer;
    public boolean isLocked;  // ADD THIS LINE - tracks account lockout

    public User(String customerID, String firstName, String lastName,
                String ssn, String dob, String email, String phoneNumber,
                String bankLocation, String bankPhoneNumber,  // ADD THIS PARAMETER
                double savingsAccount, double checkingAccount,
                String creditCard, double creditCardLimit,
                int creditScore, boolean hasDebitCard,
                double cdBalance, double cdInterestRate,
                String password, String securityQuestion, String securityAnswer) {
        this.customerID       = customerID;
        this.firstName        = firstName;
        this.lastName         = lastName;
        this.ssn              = ssn;
        this.dob              = dob;
        this.email            = email;
        this.phoneNumber      = phoneNumber;
        this.bankLocation     = bankLocation;
        this.bankPhoneNumber  = bankPhoneNumber;  // ADD THIS LINE
        this.savingsAccount   = savingsAccount;
        this.checkingAccount  = checkingAccount;
        this.creditCard       = creditCard;
        this.creditCardLimit  = creditCardLimit;
        this.creditScore      = creditScore;
        this.hasDebitCard     = hasDebitCard;
        this.cdBalance        = cdBalance;
        this.cdInterestRate   = cdInterestRate;
        this.password         = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer   = securityAnswer;
        this.isLocked         = false;  // ADD THIS LINE - unlocked by default
    }

    public String toCsv() {
        return String.join(",",
                customerID, firstName, lastName, ssn, dob, email, phoneNumber,
                bankLocation, bankPhoneNumber,  // ADD bankPhoneNumber
                String.valueOf(savingsAccount),
                String.valueOf(checkingAccount),
                creditCard,
                String.valueOf(creditCardLimit),
                String.valueOf(creditScore),
                String.valueOf(hasDebitCard),
                String.valueOf(cdBalance),
                String.valueOf(cdInterestRate),
                password,
                securityQuestion,
                securityAnswer,
                String.valueOf(isLocked)  // ADD THIS LINE
        );
    }

    public static User fromCsv(String line) {
        String[] p = line.split(",", -1);  // Use -1 to keep all trailing commas
        if (p.length < 21) return null;
        try {
            User u = new User(
                    p[0].trim(),   // customerID
                    p[1].trim(),   // firstName
                    p[2].trim(),   // lastName
                    p[3].trim(),   // ssn
                    p[4].trim(),   // dob
                    p[5].trim(),   // email
                    p[6].trim(),   // phoneNumber
                    p[7].trim(),   // bankLocation
                    p[8].trim(),   // bankPhoneNumber (THIS WAS MISSING IN OLD PARSE)
                    Double.parseDouble(p[9].trim()),   // savingsAccount
                    Double.parseDouble(p[10].trim()),  // checkingAccount
                    p[11].trim(),  // creditCard
                    Double.parseDouble(p[12].trim()), // creditCardLimit
                    Integer.parseInt(p[13].trim()),   // creditScore
                    Boolean.parseBoolean(p[14].trim()), // hasDebitCard
                    Double.parseDouble(p[15].trim()), // cdBalance
                    Double.parseDouble(p[16].trim()), // cdInterestRate
                    p[17].trim(),  // password
                    p[18].trim(),  // securityQuestion
                    p[19].trim()   // securityAnswer
            );
            // if the isLocked column exists (index 20), read it
            if (p.length >= 21) u.isLocked = Boolean.parseBoolean(p[20].trim());
            return u;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

}