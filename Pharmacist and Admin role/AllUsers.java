// AllUsers Class = Superclass
public class AllUsers {
    // Using the 'protected' means that only the AllUsers, Admin and Pharmacist class can access the data
    protected String name;
    protected String staffID;
    protected String phoneNumber;
    protected String emailAddress;

    // Create a place to store the details
    public AllUsers(String name, String staffID, String phoneNumber, String emailAddress) {
        this.name = name;
        this.staffID = staffID;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    // Get the attributes and store them
    // 'return' will return the value and store it
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStaffID() { return staffID; }
    public void setStaffID(String staffID) { this.staffID = staffID; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    // Behaviours
    public void logIn() {
        System.out.println(name + " has logged in.");
    }

    public void logOut() {
        System.out.println(name + " has logged out.");
    }
}