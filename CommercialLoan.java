import java.util.ArrayList;
import java.util.Scanner;
public class CommercialLoan{
    private final String loanNumber;
    private double loanAmount;
    private double interestRate;
    private int creditScore;
    private boolean anyPastLoans;
    private int loanTerm;
    private String loanPurpose;
    private String businessName;
    private String[] addressOfBusiness;
    private boolean proceedToApproval;
    public static ArrayList<CommercialLoan> commercialLoans = new ArrayList<>();
    
    public CommercialLoan() {
        this.loanNumber = randomLoanNumber();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter business name: ");
        this.businessName = scanner.nextLine();
        System.out.print("Enter loan amount requested: ");
        this.loanAmount = scanner.nextDouble();
        System.out.print("Enter term of loan in years: ");
        this.loanTerm = scanner.nextInt();
        while (true) { 
            System.out.print("Enter credit score: ");
            this.creditScore = scanner.nextInt();
            if (creditScore <= 850 && creditScore >= 300) {
                break;
            } else {
                System.out.println("Invalid credit score. Please enter a value between 300 and 850.");
            }
        }
        System.out.print("Have you had any past loans? (true/false): ");
        this.anyPastLoans = scanner.nextBoolean();
        System.out.print("Enter loan purpose: ");
        scanner.nextLine(); // Consume the newline
        this.loanPurpose = scanner.nextLine();
        System.out.print("Enter address of business: \n Street: ");
        String street = scanner.nextLine();
        System.out.print(" City: ");
        String city = scanner.nextLine();
        System.out.print(" State: ");
        String state = scanner.nextLine();
        System.out.print(" Zip Code: ");
        String zipCode = scanner.nextLine();
        this.addressOfBusiness = new String[]{street, city, state, zipCode};
        System.out.println("Loan application submitted for " + businessName);
        if (creditScore >= 680 && !anyPastLoans) {
            this.proceedToApproval = true;
            System.out.println("Loan application is approved based on credit score and past loan history. Proceeding to approval process.");
        } else {
            this.proceedToApproval = false;
            System.out.println("Loan application is not eligible for approval based on credit score and past loan history.");
        }
        saveLoanInformation();
        scanner.close();
    }
    public CommercialLoan(double loanAmount, double interestRate, int loanTerm) {
        this.loanNumber = randomLoanNumber();
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.loanTerm = loanTerm;
        
    }
    public CommercialLoan(String loanNumber, double loanAmount, double interestRate, int loanTerm) {
        this.loanNumber = loanNumber;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.loanTerm = loanTerm;
    }
    private static String randomLoanNumber() {
        long min = 800000000000L;
        long max = 899999999999L;
        long randomNumber = min + (long) (Math.random() * (max - min + 1));
        return String.valueOf(randomNumber);
    }
    public static CommercialLoan createLoan(double loanAmount, double interestRate, int loanTerm) {
        CommercialLoan newLoan = new CommercialLoan(loanAmount, interestRate, loanTerm);
        CommercialLoan.commercialLoans.add(newLoan);
        return newLoan;
    }
    public static CommercialLoan loadLoan(String loanNumber, double loanAmount, double interestRate, int loanTerm) {
        CommercialLoan newLoan = new CommercialLoan(loanNumber, loanAmount, interestRate, loanTerm);
        CommercialLoan.commercialLoans.add(newLoan);
        return newLoan;
    }

    public double calculateMonthlyPayment() {
        double monthlyInterestRate = interestRate / 12 / 100;
        int numberOfPayments = loanTerm * 12;
        return (loanAmount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, -numberOfPayments));
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }
    public String getLoanNumber() {
        return loanNumber;
    }
    public String[] getLoanAddress() {
        return addressOfBusiness;
    }
    public String getLoanPurpose() {
        return loanPurpose;
    }
    public String getBusinessName() {
        return businessName;
    }
    public int getCreditScore() {
        return creditScore;
    }
    public int getProceedToApproval() {
        return proceedToApproval ? 1 : 0;
    }
    public void makePayment(double paymentAmount) {
        double monthlyPayment = calculateMonthlyPayment();
        if (paymentAmount >= monthlyPayment) {
            loanAmount -= paymentAmount;
            System.out.println("Payment of $" + paymentAmount + " made. Remaining balance: $" + loanAmount);
        } else {
            System.out.println("Payment amount is less than the required monthly payment of $" + monthlyPayment);
        }
    }

    public void saveLoanInformation() {
        CommercialLoanWriter writer = new CommercialLoanWriter();
        try {
            if(proceedToApproval) {
                writer.addToCSV(this);
            }
            writer.saveLoanInformation(this);
        } catch (Exception e) {
            System.out.println("Error occurred saving loan information: " + e.getMessage());
        }
    }
}
