// Main class for testing
public class Testing {
    public static void main(String[] args) {
        // Create an Admin object
        Admin admin = new Admin("Alice", "A001", "123-456-7890", "alice@example.com", Admin.ACTIVE);
        admin.logIn();  // Output: Alice has logged in.
        admin.viewRegistration();  // Output: Alice is viewing patient registration.
        admin.confirmRegistration();  // Output: Alice has confirmed the patient registration.
        admin.logOut();  // Output: Alice has logged out.

        // Create a Pharmacist object
        Pharmacist pharmacist = new Pharmacist("Bob", "P002", "987-654-3210", "bob@example.com", "Pharmacy Dept", "Chief Pharmacist");
        pharmacist.logIn();  // Output: Bob has logged in.
        pharmacist.viewVerifiedPatient();  // Output: Bob is viewing verified patient records.
        pharmacist.verifyPrescription();  // Output: Bob is verifying a prescription.
        pharmacist.logOut();  // Output: Bob has logged out.
    }
}