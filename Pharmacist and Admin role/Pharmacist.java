// Pharmacist class = subclass
// 'extends' means the child class has inherited the attribute of parent class
// Example: The Pharmacist class inherited the staffID from AllUsers class
public class Pharmacist extends AllUsers {
    // Using the 'protected' to ensure only the Pharmacist class user can access
    // Add two more attribute
    protected String department;
    protected String designation;

    public Pharmacist(String name, String staffID, String phoneNumber, String emailAddress, String department, String designation) {
        // The function of 'super' is to help us to call the attribute on the parent class (AllUsers)
        super(name, staffID, phoneNumber, emailAddress);
        this.department = department;
        this.designation = designation;
    }

    // Get the attributes and store them
    // 'return' will return the value and store it
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    // The behaviour only for Pharmacist
    public void viewVerifiedPatient() {
        System.out.println("Viewing verified patient records.");
    }

    public void verifyPrescription() {
        System.out.println("Verifying prescription.");
    }
}
