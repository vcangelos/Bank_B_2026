# vcangelos-Bank_B_2026
This is the repository for the 2026 AP Computer Science A Bank Agile Management Project. All student files will be pushed and committed here. 

This project utilizes all aspects of Agile Management and the usage of a Trello Workboard.
# XYZ Bank (AP Java Bank Project)

XYZ Bank is a console-based banking simulation built in Java for an AP Computer Science A–style project.  
Users can create accounts, log in, deposit, withdraw, transfer funds, and view transaction history.

This project demonstrates core AP CSA skills: classes & objects, constructors, encapsulation, arrays/ArrayLists, input validation, and basic program design.

---

## Features
- Create a new customer profile
- Open checking and/or savings accounts
- Deposit and withdraw with validation (no negative deposits, no overdrafts)
- Transfer money between accounts
- View balances and recent transactions
- Menu-driven console interface

---

## Demo (Sample Run)
Welcome to XYZ Bank!

1. Create Customer
2. Log In
3. Exit

1
Enter name: Jordan Lee
Set a 4-digit PIN: 1234
Customer created!

1. Open Account
2. Deposit
3. Withdraw
4. Transfer
5. Transaction History
6. Log Out

1
Open (1) Checking or (2) Savings? 1
Checking account opened!


---

## Project Structure
XYZBank/
├── src/
│ ├── app/
│ │ └── BankApp.java # main menu + input loop
│ ├── model/
│ │ ├── Customer.java # customer profile + accounts
│ │ ├── BankAccount.java # base account class
│ │ ├── CheckingAccount.java # checking rules
│ │ ├── SavingsAccount.java # savings rules
│ │ └── Transaction.java # transaction records
│ └── service/
│ └── BankService.java # business logic (deposit/withdraw/transfer)
└── README.md


---

## Getting Started

### Requirements
- Java 17+ (or the version your class uses)
- VS Code + Java Extension Pack, IntelliJ, or Eclipse

### Run the Program
**Option A: Command Line**
```bash
cd XYZBank
javac -d out src/app/BankApp.java src/model/*.java src/service/*.java
java -cp out app.BankApp

Option B: IDE
Open the XYZBank folder in your IDE

Run BankApp.java

Design Notes (AP CSA Concepts)
------------------------------
This project emphasizes:
Encapsulation: private fields + public getters/setters
Constructors: initialize customer/accounts properly
Inheritance: CheckingAccount and SavingsAccount extend BankAccount
Polymorphism: store multiple account types in a single list
ArrayList usage for customers and transaction history
Input validation for PINs, amounts, and menu choices

Rules / Constraints
-------------------
Deposits must be > 0
Withdrawals must be > 0 and ≤ available balance
Transfers must be valid and cannot exceed available balance
PIN must be exactly 4 digits
No file storage in the base version (optional extension below)

Extensions (Optional)
---------------------
If you want to take it further:
Save customers/accounts to a file (CSV or text)
Add interest for savings accounts
Add a monthly fee for checking accounts
Add login lockout after 3 failed PIN attempts
Add unit tests (JUnit)

Team Roles (if group project)
-----------------------------
Scrum Master: manages task board + sprint check-ins
Developer(s): implement features and write tests
QA/Tester: run test cases and track bugs
Documentation Lead: keeps README and user guide updated

Credits
--------
Created by: (Team Name)
Course: AP Computer Science A
Instructor: (V. Cangelosi)
Date: (Jan-Feb, 2026)
Length of Project: 6 Weeks
