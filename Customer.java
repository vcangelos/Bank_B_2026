
public class Customer {
  private String firstName;
  private String lastName;
  private int SSN;
  private String DOB;
  private String email;
  private int phoneNumber;
  private double minDeposit;


    public void Customer(String firstName, String lastName, int SSN, String DOB, String email, int phoneNumber, double minDeposit)
    {
      this.firstName = firstName;
      this.lastName = lastName;
      this.SSN = SSN;
      this.DOB = DOB;
      this.email = email;
      this.phoneNumber = phoneNumber;
      this.minDeposit = minDeposit;
    }
    //ok so input a username that we will pull from the csv
    public void getRecords(String email)
    {
      
    }
}


