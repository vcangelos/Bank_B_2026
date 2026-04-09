import java.util.Scanner;
import java.util.regex.Pattern;

public class BankApp {
    private final Scanner sc;
    private final UserDatabase db;
    private User currentUser = null;

    //regex patterns for different inputs we have to take
    private static final Pattern DOB_PATTERN   = Pattern.compile("^(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])/(19|20)\\d{2}$");
    private static final Pattern SSN_PATTERN   = Pattern.compile("^\\d{3}-\\d{2}-\\d{4}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\(?\\d{3}\\)?[\\s.\\-]?\\d{3}[\\s.\\-]?\\d{4}$");


    //we feed in on scanner for the bank project to use
    //helps avoid multiple instances of a scanner
    public BankApp(Scanner sc) {
        this.sc = sc;
        this.db = new UserDatabase();
    }

    public void run() {
        welcomeScreen();
    }

    //neat way to clear the screen
    private void clearScreen() {
        //ansi escape code forcing the console to be entirely cleared
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void printLine() {
        System.out.println("─────────────────────────────────────────");
    }

    //prints a header for what screen we are on, also giving a title for what that page is
    private void printHeader(String title) {
        clearScreen();
        printLine();
        System.out.println("BANK  |  " + title);
        printLine();
    }

    private String prompt(String label) {
        System.out.print("  " + label + ": ");
        return sc.nextLine().trim();
    }

    private void pause() {
        System.out.print("\n  Press ENTER to continue...");
        sc.nextLine();
    }

    //validate if a given input matches a regex patter
    private String promptValidated(String label, Pattern pattern, String errorMsg) {
        while (true) {
            String input = prompt(label);
            if (input.equals("0")) return "0"; //for weird regex edge cases
            if (pattern.matcher(input).matches()) return input;
            System.out.println("  Invalid format: " + errorMsg);
        }
    }

    private String promptDob(String label) {
        return promptValidated(label, DOB_PATTERN, "Use MM/DD/YYYY (e.g. 04/25/2001)");
    }

    private String promptSsn(String label) {
        return promptValidated(label, SSN_PATTERN, "Use XXX-XX-XXXX (e.g. 123-45-6789)");
    }

    private String promptEmail(String label) {
        while (true) {
            String input = prompt(label);
            if (input.equals("0")) return "0";
            if (!EMAIL_PATTERN.matcher(input).matches()) {
                System.out.println("  Invalid format: Enter a valid email (e.g. name@example.com)");
                continue;
            }
            return input;
        }
    }

    private String promptPhone(String label) {
        return promptValidated(label, PHONE_PATTERN, "Use a 10-digit number (e.g. 732-555-0100)");
    }

    private String promptPassword(String label) {
        while (true) {
            String input = prompt(label);
            if (input.equals("0")) return "0";
            if (input.length() < 8) {
                System.out.println("  Invalid: Password must be at least 8 characters.");
                continue;
            }
            if (!input.matches(".*[A-Z].*")) {
                System.out.println("  Invalid: Password must contain at least one uppercase letter.");
                continue;
            }
            if (!input.matches(".*[0-9].*")) {
                System.out.println("  Invalid: Password must contain at least one number.");
                continue;
            }
            return input;
        }
    }

    private double promptDeposit(String label, double minimum) {
        while (true) {
            String raw = prompt(label);
            if (raw.equals("0")) return -1;
            try {
                double amount = Double.parseDouble(raw.replace("$", "").replace(",", ""));
                if (amount < minimum) {
                    System.out.printf("  Invalid: Minimum deposit is $%.2f.%n", minimum);
                } else {
                    return amount;
                }
            } catch (NumberFormatException e) {
                System.out.println("  Invalid: Enter a numeric amount (e.g. 100 or 100.00)");
            }
        }
    }

    private String promptSsnLast4(String label) {
        while (true) {
            String input = prompt(label);
            if (input.equals("0")) return "0";
            if (input.matches("\\d{4}")) return input;
            System.out.println("  Invalid: Enter exactly 4 digits (e.g. 6789)");
        }
    }

    //just a plain prompt for anything, requiring it to not be blank
    private String promptNonEmpty(String label) {
        while (true) {
            String input = prompt(label);
            if (input.equals("0")) return "0";
            if (!input.isEmpty()) return input;
            System.out.println("  Invalid: This field cannot be blank.");
        }
    }

    //screen that opens on first start
    private void welcomeScreen() {
        while (true) {
            printHeader("Welcome");
            System.out.println("  [1] Log In");
            System.out.println("  [2] Open an Account");
            System.out.println("  [3] Exit");
            printLine();
            String choice = prompt("Select");
            //neat shorthand with arrow notation. just eliminates the need for a break line.
            switch (choice) {
                case "1" -> loginScreen();
                case "2" -> signupScreen();
                case "3" -> { System.out.println("\n  Goodbye.\n"); return; }
                default  -> System.out.println("  Invalid option. Try again.");
            }
        }
    }

    // ── Login Screen ──────────────────────────────────────────────────────
    private void loginScreen() {
        while (true) {
            printHeader("Log In");
            System.out.println("  Enter [0] at any field to go back.\n");
            String email = promptEmail("Email");
            if (email.equals("0")) return;

            String password = prompt("Password");
            if (password.equals("0")) return;

            User u = db.getByEmail(email);
            if (u == null || !u.password.equals(password)) {
                System.out.println("\n  Incorrect email or password.");
                System.out.println("  [1] Try again  [2] Forgot Password  [0] Back");
                String c = prompt("Select");
                if (c.equals("2")) forgotPasswordScreen();
                else if (c.equals("0")) return;
            } else {
                currentUser = u;
                dashboardScreen();
                currentUser = null;
                return;
            }
        }
    }

    // ── Forgot Password ───────────────────────────────────────────────────
    private void forgotPasswordScreen() {
        printHeader("Account Recovery");
        System.out.println("  Enter [0] at any field to cancel.\n");
        System.out.println("  You must verify your identity with your DOB, SSN, and security question.\n");

        String email = promptEmail("Registered Email");
        if (email.equals("0")) return;

        User u = db.getByEmail(email);
        if (u == null) {
            System.out.println("\n  No account found with that email.");
            pause();
            return;
        }

        String dob = promptDob("Date of Birth (MM/DD/YYYY)");
        if (dob.equals("0")) return;

        String ssn = promptSsnLast4("Last 4 digits of SSN");
        if (ssn.equals("0")) return;

        if (!u.dob.equals(dob) || !u.ssn.endsWith(ssn)) {
            System.out.println("\n  Verification failed. DOB or SSN did not match.");
            pause();
            return;
        }

        System.out.println("\n  Security Question: " + u.securityQuestion);
        String answer = promptNonEmpty("Your Answer");
        if (answer.equals("0")) return;

        if (!answer.equalsIgnoreCase(u.securityAnswer)) {
            System.out.println("\n  Verification failed. Security answer did not match.");
            pause();
            return;
        }

        System.out.println("\n  Identity verified.");
        System.out.println("  Password must be 8+ characters, include an uppercase letter and a number.\n");

        String newPass = promptPassword("New Password");
        if (newPass.equals("0")) return;

        while (true) {
            String confirm = prompt("Confirm New Password");
            if (confirm.equals("0")) return;
            if (confirm.equals(newPass)) break;
            System.out.println("  Passwords do not match. Try again.");
        }

        u.password = newPass;
        db.updateUser(u);
        System.out.println("\n  Password updated. You may now log in.");
        pause();
    }

    //screen for when sign up
    private void signupScreen() {
        printHeader("Open an Account — Step 1: Personal Info");
        System.out.println("  Enter [0] at any field to cancel.\n");

        String firstName = prompt("First Name");
        if (firstName.equals("0")) return;

        String lastName = prompt("Last Name");
        if (lastName.equals("0")) return;

        String dob = promptDob("Date of Birth (MM/DD/YYYY)");
        if (dob.equals("0")) return;

        String ssn = promptSsn("Social Security Number (XXX-XX-XXXX)");
        if (ssn.equals("0")) return;

        String email;
        while (true) {
            email = promptEmail("Email Address");
            if (email.equals("0")) return;
            if (db.emailExists(email)) {
                System.out.println("  An account with that email already exists.");
            } else break;
        }

        String phone = promptPhone("Cell Phone Number");
        if (phone.equals("0")) return;

        // Step 2: Branch Selection
        printHeader("Open an Account — Step 2: Select Your Branch");
        System.out.println("  Choose your nearest branch location:\n");
        System.out.println("  [1] New York, NY        — (212) 555-0101");
        System.out.println("  [2] New Jersey, NJ      — (732) 555-0202");
        System.out.println("  [3] Philadelphia, PA    — (215) 555-0303");
        System.out.println("  [4] Los Angeles, CA     — (310) 555-0404");
        System.out.println("  [5] Chicago, IL         — (312) 555-0505");
        System.out.println("  [6] Diddy's Bank, NY    — (312) 908-0506");
        System.out.println();

        String bankLocation = "";
        String bankPhoneNumber = "";
        while (true) {
            String c = prompt("Select Branch");
            switch (c) {
                case "1" -> { bankLocation = "New York, NY";     bankPhoneNumber = "(212) 555-0101"; }
                case "2" -> { bankLocation = "New Jersey, NJ";   bankPhoneNumber = "(732) 555-0202"; }
                case "3" -> { bankLocation = "Philadelphia, PA"; bankPhoneNumber = "(215) 555-0303"; }
                case "4" -> { bankLocation = "Los Angeles, CA";  bankPhoneNumber = "(310) 555-0404"; }
                case "5" -> { bankLocation = "Chicago, IL";      bankPhoneNumber = "(312) 555-0505"; }
                case "0" -> { return; }
                default  -> { System.out.println("  Invalid: Enter 1 through 6."); continue; }
            }
            break;
        }

        // Step 3: Credentials
        printHeader("Open an Account — Step 3: Set Password");
        System.out.println("  Password must be 8+ characters, include an uppercase letter and a number.\n");

        String password = promptPassword("Create Password");
        if (password.equals("0")) return;

        while (true) {
            String confirm = prompt("Confirm Password");
            if (confirm.equals("0")) return;
            if (confirm.equals(password)) break;
            System.out.println("  Passwords do not match. Try again.");
        }

        // prompt security question
        printHeader("Open an Account — Step 4: Security Question");
        System.out.println("  This will be used to verify your identity during account recovery.\n");

        String securityQuestion = promptNonEmpty("Write your security question");
        if (securityQuestion.equals("0")) return;

        String securityAnswer = promptNonEmpty("Your answer");
        if (securityAnswer.equals("0")) return;

        // opening deposit
        printHeader("Open an Account — Step 5: Opening Deposit");
        System.out.println("  Account Types:");
        System.out.println("  [1] Checking");
        System.out.println("  [2] Savings");
        System.out.println("  [3] Money Market");
        System.out.println("  [4] Certificate of Deposit (CD)");
        System.out.println();

        String accountType = "";
        while (true) {
            String c = prompt("Select Account Type");
            switch (c) {
                case "1" -> accountType = "Checking";
                case "2" -> accountType = "Savings";
                case "3" -> accountType = "Money Market";
                case "4" -> accountType = "Certificate of Deposit";
                case "0" -> { return; }
                default  -> { System.out.println("  Invalid: Enter 1, 2, 3, or 4."); continue; }
            }
            break;
        }

        double deposit = promptDeposit("Opening Deposit Amount (min $25.00)", 25.0);
        if (deposit == -1) return;

        String accountId = db.generateAccountId();
        User newUser = new User(
                accountId,
                firstName,
                lastName,
                ssn,
                dob,
                email,
                phone,
                bankLocation,
                bankPhoneNumber,
                accountType.equals("Savings") ? deposit : 0.0,
                accountType.equals("Checking") ? deposit : 0.0,
                "",
                0.0,
                0,
                false,
                accountType.equals("Certificate of Deposit") ? deposit : 0.0,
                0.0,
                password,
                securityQuestion,
                securityAnswer
        );
        db.addUser(newUser);

        printHeader("Account Created!");
        printLine();
        System.out.printf("  Welcome, %s %s!%n", firstName, lastName);
        System.out.println("  Your Account ID:  " + accountId);
        System.out.printf("  Branch Location:  %s%n", bankLocation);
        System.out.printf("  Account Type:     %s%n", accountType);
        System.out.printf("  Opening Balance:  $%.2f%n", deposit);
        printLine();
        System.out.println("  Your account is now active. Please log in.");
        pause();
    }

    // ── Dashboard ─────────────────────────────────────────────────────────
    private void dashboardScreen() {
        while (true) {
            printHeader("Dashboard");
            System.out.printf("  Welcome, %s %s%n", currentUser.firstName, currentUser.lastName);
            System.out.println("  Account ID: " + currentUser.customerID);
            System.out.println("  Branch:     " + currentUser.bankLocation);
            printLine();
            System.out.println("  Select an account or service:");
            System.out.println("  [1] Checking");
            System.out.println("  [2] Savings");
            System.out.println("  [3] Loans");
            System.out.println("  [4] Investments");
            System.out.println("  [5] Account Settings");
            System.out.println("  [0] Log Out");
            printLine();

            String choice = prompt("Select");
            switch (choice) {
                case "1" -> launchModule("Checking");
                case "2" -> launchModule("Savings");
                case "3" -> launchModule("Loans");
                case "4" -> launchModule("Investments");
                case "5" -> settingsScreen();
                case "0" -> { System.out.println("\n  Logged out."); pause(); return; }
                default  -> System.out.println("  Invalid option.");
            }
        }
    }

    // ok from here just run the code that deals with the
    private void launchModule(String moduleName) {
        printHeader(moduleName);
        System.out.println("  Launching " + moduleName + " module...");
        System.out.println("  (Connect your team's " + moduleName + " program here.)");
        pause();
    }

    // modifty account stufff menu
    private void settingsScreen() {
        printHeader("Account Settings");
        System.out.println("  Verification required to make changes.\n");

        String ssn = promptSsnLast4("Last 4 digits of SSN");
        if (ssn.equals("0")) return;

        String dob = promptDob("Date of Birth (MM/DD/YYYY)");
        if (dob.equals("0")) return;

        if (!currentUser.ssn.endsWith(ssn) || !currentUser.dob.equals(dob)) {
            System.out.println("\n  Verification failed.");
            pause();
            return;
        }

        System.out.println("\n  Verified. What would you like to update?");
        System.out.println("  [1] Change Email");
        System.out.println("  [2] Change Password");
        System.out.println("  [3] Change Security Question");
        System.out.println("  [0] Cancel");

        String choice = prompt("Select");
        switch (choice) {
            case "1" -> changeEmail();
            case "2" -> changePassword();
            case "3" -> changeSecurityQuestion();
            case "0" -> {}
            default  -> System.out.println("  Invalid option.");
        }
        pause();
    }

    private void changeEmail() {
        while (true) {
            String newEmail = promptEmail("New Email Address");
            if (newEmail.equals("0")) return;
            if (db.emailExists(newEmail) && !newEmail.equalsIgnoreCase(currentUser.email)) {
                System.out.println("  That email is already in use.");
            } else {
                currentUser.email = newEmail;
                db.updateUser(currentUser);
                System.out.println("  Email updated successfully.");
                return;
            }
        }
    }

    private void changePassword() {
        System.out.println("  Password must be 8+ characters, include an uppercase letter and a number.\n");
        while (true) {
            String newPass = promptPassword("New Password");
            if (newPass.equals("0")) return;
            String confirm = prompt("Confirm New Password");
            if (confirm.equals("0")) return;
            if (!newPass.equals(confirm)) {
                System.out.println("  Passwords do not match. Try again.");
            } else {
                currentUser.password = newPass;
                db.updateUser(currentUser);
                System.out.println("  Password updated successfully.");
                return;
            }
        }
    }

    private void changeSecurityQuestion() {
        System.out.println();
        String newQuestion = promptNonEmpty("New Security Question");
        if (newQuestion.equals("0")) return;
        String newAnswer = promptNonEmpty("New Answer");
        if (newAnswer.equals("0")) return;
        currentUser.securityQuestion = newQuestion;
        currentUser.securityAnswer   = newAnswer;
        db.updateUser(currentUser);
        System.out.println("  Security question updated successfully.");
    }
}