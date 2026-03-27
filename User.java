public class User {
    public String customerID;
    public String firstName;
    public String lastName;
    public String ssn;
    public String dob;
    public String email;
    public String phoneNumber;
    public String bankLocation;
    public String bankPhoneNumber;
    public boolean savingsAccount;
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

    public User(String customerID, String firstName, String lastName,
                String ssn, String dob, String email, String phoneNumber,
                String bankLocation, String bankPhoneNumber,
                boolean savingsAccount, double checkingAccount,
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
        this.bankPhoneNumber  = bankPhoneNumber;
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
    }

    public String toCsv() {
        return String.join(",",
                customerID, firstName, lastName, ssn, dob, email, phoneNumber,
                bankLocation, bankPhoneNumber,
                savingsAccount,
                String.valueOf(checkingAccount),
                creditCard,
                String.valueOf(creditCardLimit),
                String.valueOf(creditScore),
                String.valueOf(hasDebitCard),
                String.valueOf(cdBalance),
                String.valueOf(cdInterestRate),
                password,
                securityQuestion,
                securityAnswer
        );
    }

    public static User fromCsv(String line) {
        String[] p = line.split(",", 20);
        if (p.length < 20) return null;
        try {
            return new User(
                    p[0].trim(),
                    p[1].trim(),
                    p[2].trim(),
                    p[3].trim(),
                    p[4].trim(),
                    p[5].trim(),
                    p[6].trim(),
                    p[7].trim(),
                    p[8].trim(),
                    p[9].trim(),
                    Double.parseDouble(p[10].trim()),
                    p[11].trim(),
                    Double.parseDouble(p[12].trim()),
                    Integer.parseInt(p[13].trim()),
                    Boolean.parseBoolean(p[14].trim()),
                    Double.parseDouble(p[15].trim()),
                    Double.parseDouble(p[16].trim()),
                    p[17].trim(),
                    p[18].trim(),
                    p[19].trim()
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
