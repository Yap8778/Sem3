// Admin class = subclass
// 'extends' means the child class has inherited the attribute of parent class
// Example: The Admin class inherited the name from AllUsers class
public class Admin extends AllUsers {
    // The 'final' means that if a value has been given a value, the others input cannot change its value
    // It will keep staying the value that have been give previously
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    protected String status;

    public Admin(String name, String staffID, String phoneNumber, String emailAddress, String status) {
        // The function of 'super' is to help us to call the attribute on the parent class (AllUsers)
        super(name, staffID, phoneNumber, emailAddress);
        this.status = status;
    }

    // Get the attributes and store them
    // 'return' will return the value and store it
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // The behaviour only for Admin
    public void viewRegistration() {
        System.out.println("Viewing patient registration.");
    }

    public void confirmRegistration() {
        System.out.println("Confirming patient registration.");
    }
}