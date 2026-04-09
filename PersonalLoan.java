import java.time.LocalDate;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class PersonalLoan {

        private double principal;
        private double interestRate;
        private int loanTerm; // months
        private String loanType;
        private String loanId;
        private LocalDate startDate;

        private int creditScore;
        private boolean approved;
        private CreditCard linkedCard;

        private double totalPaid;
        private double remainingBalance;
        private int paymentsMade;

        private boolean autoPayEnabled;

        public PersonalLoan(double principal, int loanTerm, String loanType, CreditCard card, boolean autoPayEnabled) {
                if (principal <= 0) {
                        throw new IllegalArgumentException("Principal must be greater than 0.");
                }
                if (loanTerm < 1 || loanTerm > 60) {
                        throw new IllegalArgumentException("Loan term must be between 1 and 60 months.");
                }
                if (loanType == null || loanType.trim().isEmpty()) {
                        throw new IllegalArgumentException("Loan type cannot be blank.");
                }
                if (card == null) {
                        throw new IllegalArgumentException("Credit card cannot be null.");
                }

                this.principal = roundMoney(principal);
                this.loanTerm = loanTerm;
                this.loanType = loanType;
                this.linkedCard = card;
                this.loanId = generateLoanId();
                this.startDate = LocalDate.now();
                this.autoPayEnabled = autoPayEnabled;

                this.creditScore = card.getCreditScore();
                evaluateLoan();

                this.totalPaid = 0.0;
                this.paymentsMade = 0;
                this.remainingBalance = approved ? roundMoney(calculateTotalRepayment()) : 0.0;
        }

        private String generateLoanId() {
                Random random = new Random();
                int number = 10000 + random.nextInt(90000);
                return "LN" + number;
        }

        private double roundMoney(double amount) {
                return Math.round(amount * 100.0) / 100.0;
        }

        private void evaluateLoan() {
                if (creditScore >= 750) {
                        approved = true;
                        interestRate = 0.08;
                } else if (creditScore >= 700) {
                        approved = true;
                        interestRate = 0.12;
                } else if (creditScore >= 650) {
                        approved = true;
                        interestRate = 0.16;
                } else if (creditScore >= 600) {
                        approved = true;
                        interestRate = 0.20;
                } else {
                        approved = false;
                        interestRate = 0.0;
                }
        }

        public double calculateMonthlyPayment() {
                if (!approved) return 0.0;
                double monthlyRate = interestRate / 12.0;
                if (monthlyRate == 0) return roundMoney(principal / loanTerm);
                return roundMoney((principal * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -loanTerm)));
        }

        public double calculateTotalRepayment() {
                if (!approved) return 0.0;
                return roundMoney(calculateMonthlyPayment() * loanTerm);
        }

        public double calculateTotalInterest() {
                if (!approved) return 0.0;
                return roundMoney(calculateTotalRepayment() - principal);
        }

        public boolean isPaidOff() {
                return remainingBalance <= 0.0;
        }

        public double getCurrentMonthlyDue() {
                if (!approved || isPaidOff()) return 0.0;
                return roundMoney(Math.min(calculateMonthlyPayment(), remainingBalance));
        }

        private boolean applyLoanPayment(double amount) {
                if (!approved) {
                        System.out.println("Loan is not approved.");
                        return false;
                }
                if (isPaidOff()) {
                        System.out.println("Loan is already fully paid off.");
                        return false;
                }
                if (amount <= 0) {
                        System.out.println("Payment must be greater than $0.");
                        return false;
                }

                double appliedAmount = roundMoney(Math.min(amount, remainingBalance));
                totalPaid = roundMoney(totalPaid + appliedAmount);
                remainingBalance = roundMoney(remainingBalance - appliedAmount);
                paymentsMade++;

                System.out.println("Loan payment applied: $" + String.format("%.2f", appliedAmount));
                System.out.println("Remaining loan balance: $" + String.format("%.2f", remainingBalance));
                return true;
        }

        public boolean payFromChecking(CheckingAccount.Account checking, double amount) {
                if (checking == null) {
                        System.out.println("Checking account is null.");
                        return false;
                }
                if (!checking.isActive) {
                        System.out.println("Checking account is inactive.");
                        return false;
                }
                if (amount <= 0) {
                        System.out.println("Payment amount must be greater than $0.");
                        return false;
                }
                if (checking.balance < amount) {
                        System.out.println("Not enough money in checking.");
                        return false;
                }

                checking.balance = roundMoney(checking.balance - amount);
                checking.addTransaction("Personal Loan Payment", amount);
                checking.updateFlags();

                return applyLoanPayment(amount);
        }

        public boolean payFromSavings(
                SavingsAccount savings, double amount) throws IOException {
                if (savings == null) {
                        System.out.println("Savings account is null.");
                        return false;
                }
                if (amount <= 0) {
                        System.out.println("Payment amount must be greater than $0.");
                        return false;
                }
                if (savings.getSavings() < amount) {
                        System.out.println("Not enough money in savings.");
                        return false;
                }

                boolean withdrew = savings.withdrawSavings(amount);
                if (!withdrew) return false;

                savings.update();
                return applyLoanPayment(amount);
        }

        public boolean payMonthlyFromChecking(CheckingAccount.Account checking) {
                return payFromChecking(checking, getCurrentMonthlyDue());
        }

        public boolean payMonthlyFromSavings(SavingsAccount savings) throws IOException {
                return payFromSavings(savings, getCurrentMonthlyDue());
        }

        public boolean payMonthlyFromCheckingAndSavings(CheckingAccount.Account checking, SavingsAccount savings) throws IOException {
                double paymentDue = getCurrentMonthlyDue();
                if (paymentDue <= 0) {
                        System.out.println("No payment due.");
                        return false;
                }

                double checkingAvailable = 0.0;
                if (checking != null && checking.isActive && checking.balance > 0) checkingAvailable = checking.balance;

                double savingsAvailable = (savings != null) ? savings.getSavings() : 0.0;

                if (checkingAvailable + savingsAvailable < paymentDue) {
                        System.out.println("Not enough money in checking and savings combined.");
                        return false;
                }

                double fromChecking = Math.min(checkingAvailable, paymentDue);
                double fromSavings = roundMoney(paymentDue - fromChecking);

                if (fromChecking > 0) {
                        checking.balance = roundMoney(checking.balance - fromChecking);
                        checking.addTransaction("Personal Loan Payment", fromChecking);
                        checking.updateFlags();
                }

                if (fromSavings > 0) {
                        boolean withdrew = savings.withdrawSavings(fromSavings);
                        if (!withdrew) return false;
                        savings.update();
                }

                boolean paid = applyLoanPayment(paymentDue);

                if (paid) {
                        System.out.println("Paid from checking: $" + String.format("%.2f", fromChecking));
                        System.out.println("Paid from savings: $" + String.format("%.2f", fromSavings));
                }

                return paid;
        }

        public boolean runAutoPay(CheckingAccount.Account checking, SavingsAccount savings) throws IOException {
                if (!autoPayEnabled) {
                        System.out.println("Autopay is turned OFF for this loan.");
                        return false;
                }
                System.out.println("Running autopay...");
                return payMonthlyFromCheckingAndSavings(checking, savings);
        }

        // ================= GETTERS & SETTERS =================
        public double getPrincipal() { return principal; }
        public double getInterestRate() { return interestRate; }
        public int getLoanTerm() { return loanTerm; }
        public String getLoanType() { return loanType; }
        public String getLoanId() { return loanId; }
        public LocalDate getStartDate() { return startDate; }
        public int getCreditScore() { return creditScore; }
        public boolean isApproved() { return approved; }
        public CreditCard getLinkedCard() { return linkedCard; }
        public double getTotalPaid() { return totalPaid; }
        public double getRemainingBalance() { return remainingBalance; }
        public int getPaymentsMade() { return paymentsMade; }
        public boolean isAutoPayEnabled() { return autoPayEnabled; }
        public void setLoanType(String loanType) { if (loanType != null && !loanType.trim().isEmpty()) this.loanType = loanType; }
        public void setLoanTerm(int loanTerm) { if (loanTerm >= 1 && loanTerm <= 60) this.loanTerm = loanTerm; }
        public void setPrincipal(double principal) { if (principal > 0) this.principal = roundMoney(principal); }
        public void setAutoPayEnabled(boolean autoPayEnabled) { this.autoPayEnabled = autoPayEnabled; }

        public void display() {
                System.out.println("\n========== PERSONAL LOAN SUMMARY ==========");
                System.out.println("Loan ID: " + loanId);
                System.out.println("Loan Type: " + loanType);
                System.out.println("Loan Amount: $" + String.format("%.2f", principal));
                System.out.println("Loan Term: " + loanTerm + " months");
                System.out.println("Start Date: " + startDate);
                System.out.println("Linked Card Type: " + linkedCard.getCreditCardType());
                System.out.println("Linked Card Number: " + linkedCard.getFormattedCardNumber());
                System.out.println("Credit Score Used: " + creditScore);
                System.out.println("Autopay Enabled: " + (autoPayEnabled ? "Yes" : "No"));

                if (approved) {
                        System.out.println("Loan Status: APPROVED");
                        System.out.println("Interest Rate: " + String.format("%.2f", interestRate * 100) + "%");
                        System.out.println("Monthly Payment: $" + String.format("%.2f", calculateMonthlyPayment()));
                        System.out.println("Total Repayment: $" + String.format("%.2f", calculateTotalRepayment()));
                        System.out.println("Total Interest Paid: $" + String.format("%.2f", calculateTotalInterest()));
                        System.out.println("Total Paid So Far: $" + String.format("%.2f", totalPaid));
                        System.out.println("Remaining Balance: $" + String.format("%.2f", remainingBalance));
                        System.out.println("Payments Made: " + paymentsMade);
                } else {
                        System.out.println("Loan Status: DENIED");
                        System.out.println("Reason: Credit score too low");
                }
        }

        public void displayLoanInfo() {
                display();
        }

        // ================= CSV SAVE FEATURE =================
        public void saveToCSV() {
                String fileName = "loans.csv";
                boolean fileExists = new File(fileName).exists();

                try (FileWriter writer = new FileWriter(fileName, true)) { // append mode
                        if (!fileExists) {
                                writer.append("LoanID,LoanType,Principal,RemainingBalance,PaymentsMade,InterestRate,Approved,AutoPayEnabled,StartDate\n");
                        }

                        writer.append(String.format("%s,%s,%.2f,%.2f,%d,%.2f,%s,%s,%s\n",
                                loanId,
                                loanType,
                                principal,
                                remainingBalance,
                                paymentsMade,
                                interestRate,
                                approved ? "Yes" : "No",
                                autoPayEnabled ? "Yes" : "No",
                                startDate.toString()
                        ));

                        writer.flush();
                        System.out.println("Loan saved to CSV successfully.");
                } catch (IOException e) {
                        System.out.println("Error saving loan to CSV: " + e.getMessage());
                }
        }
}