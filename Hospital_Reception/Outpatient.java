package com.mycompany.hospital_reception;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class Outpatient extends Stage {
    // UI design
    //TextField can get the input from users
    private TextField patientIdField = new TextField();
    private TextField outpatientIdField = new TextField();
    // Develop a drop-down selection for user
    private ComboBox<String> consultationTypeCombo = new ComboBox<>();
    private Label appointmentDetails = new Label();
    private String doctorId;

    public Outpatient() {
        this.setTitle("Outpatient Enrollment System");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Enter Outpatient ID:"), 0, 0);
        grid.add(outpatientIdField, 1, 0);

        grid.add(new Label("Enter Patient ID:"), 0, 1);
        grid.add(patientIdField, 1, 1);

        grid.add(new Label("Consultation Type:"), 0, 2);
        consultationTypeCombo.getItems().addAll("Online", "Face-to-face");
        grid.add(consultationTypeCombo, 1, 2);

        grid.add(new Label("Appointment Details:"), 0, 3);
        grid.add(appointmentDetails, 1, 3);

        // Buttons for CRUD operations
        Button loadButton = new Button("Load Appointment");
        Button submitButton = new Button("Add to Outpatient");
        Button updateButton = new Button("Update Outpatient Record");
        Button deleteButton = new Button("Remove from Outpatient");

        grid.add(loadButton, 0, 4);
        grid.add(submitButton, 1, 4);
        grid.add(updateButton, 0, 5);
        grid.add(deleteButton, 1, 5);

        loadButton.setOnAction(event -> loadAppointmentDetails());
        submitButton.setOnAction(event -> showConfirmationDialog());
        updateButton.setOnAction(event -> showUpdateDialog());
        deleteButton.setOnAction(event -> showDeleteConfirmationDialog());

        this.setScene(new Scene(grid, 450, 300));
        this.show();
    }

    // load the data from appointment table in sql
    private void loadAppointmentDetails() {
        String patientId = patientIdField.getText();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "Jeffloh0329.");
            //query to get patient id to load
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM appointment WHERE patient_id = ?")) {
            stmt.setString(1, patientId);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                doctorId = result.getString("doctor_id");
                String date = result.getString("appointment_date");
                String time = result.getString("appointment_time");
                appointmentDetails.setText("Doctor ID: " + doctorId + "\nDate: " + date + "\nTime: " + time);
            } else {
                appointmentDetails.setText("No appointment found for Patient ID: " + patientId);
            }
        } catch (SQLException ex) {
            System.out.println("Database error occurred. Please try again.");
        }
    }

        // show confirm and error dialog 
    private void showConfirmationDialog() {
        String outpatientId = outpatientIdField.getText();
        String patientId = patientIdField.getText();
        String consultationType = consultationTypeCombo.getValue();

        // Automatically load appointment details if doctorId is null
        if (doctorId == null) {
            loadAppointmentDetails(); // Attempt to load appointment details
        }

        // Check again if doctorId was loaded successfully
        if (doctorId == null) {
            showAlert("Error", "Patient ID " + patientId + " does not have an appointment. Please create an appointment first.", Alert.AlertType.ERROR);
            return;
        }

        try {
            if (!patientExistsInAppointment(patientId)) {
                showAlert("Error", "Patient ID " + patientId + " does not exist in the appointment table. Please create an appointment first.", Alert.AlertType.ERROR);
                return;
            }
            if (outpatientIdExists(outpatientId)) {
                showAlert("Error", "Outpatient ID " + outpatientId + " is already in use. Please use a different ID.", Alert.AlertType.ERROR);
                return;
            }
            if (patientIdExistsInOutpatient(patientId)) {
                showAlert("Error", "Patient ID " + patientId + " is already registered as an outpatient.", Alert.AlertType.ERROR);
                return;
            }
            if (!doctorExists(doctorId)) {
                showAlert("Error", "Doctor ID " + doctorId + " does not exist in the doctor table.", Alert.AlertType.ERROR);
                return;
            }
        } catch (SQLException ex) {
            showAlert("Error", "Database error occurred while checking IDs.", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Outpatient Summary");
        confirmationAlert.setHeaderText("Please confirm the following information:");
        confirmationAlert.setContentText("Outpatient ID: " + outpatientId + "\n" + "Patient ID: " + patientId + "\n" + "Doctor ID: " + doctorId + "\n" + "Consultation Type: " + consultationType);
        ButtonType submitButton = new ButtonType("Submit");
        ButtonType cancelButton = new ButtonType("Cancel");

        confirmationAlert.getButtonTypes().setAll(submitButton, cancelButton);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == submitButton) {
                insertOutpatientRecord(outpatientId, patientId, doctorId, consultationType);
                deleteAppointmentRecord(patientId);  // Deletes the appointment record after creating an outpatient entry
                showAlert("Success", "Outpatient record saved and appointment removed successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Cancelled", "Submission cancelled. You can edit the details.", Alert.AlertType.INFORMATION);
            }
        });
    }

    // update message
    private void showUpdateDialog() {
        String outpatientId = outpatientIdField.getText();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Outpatient Record");
        dialog.setHeaderText("Enter new Doctor ID for Outpatient ID: " + outpatientId);
        dialog.setContentText("New Doctor ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newDoctorId -> {
            try {
                if (doctorExists(newDoctorId)) {  // Ensure doctor exists in the doctor table
                    String newConsultationType = consultationTypeCombo.getValue();
                    updateOutpatientRecord(outpatientId, newDoctorId, newConsultationType);
                    showAlert("Success", "Outpatient record updated successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Doctor ID " + newDoctorId + " does not exist.", Alert.AlertType.ERROR);
                }
            } catch (SQLException ex) {
                showAlert("Error", "Failed to update outpatient record. Please try again.", Alert.AlertType.ERROR);
            }
        });
    }

    // delete message
    private void showDeleteConfirmationDialog() {
        String outpatientId = outpatientIdField.getText();
        String patientId = patientIdField.getText();

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Remove from Outpatient");
        confirmationAlert.setHeaderText("Are you sure you want to remove this outpatient record?");
        confirmationAlert.setContentText("Outpatient ID: " + outpatientId + "\nPatient ID: " + patientId);

        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel");
        confirmationAlert.getButtonTypes().setAll(deleteButton, cancelButton);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == deleteButton) {
                addAppointmentRecord(patientId, doctorId);  // Re-add to appointment table
                deleteOutpatientRecord(outpatientId);       // Delete from outpatient table
                showAlert("Success", "Outpatient record removed and re-added to appointment table.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Cancelled", "Delete action cancelled.", Alert.AlertType.INFORMATION);
            }
        });
    }

    // check the patient exists in appointment list or not
    private boolean patientExistsInAppointment(String patientId) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM appointment WHERE patient_id = ?")) {
            stmt.setString(1, patientId);
            ResultSet result = stmt.executeQuery();
            return result.next() && result.getInt(1) > 0;
        }
    }
    //advoid repeat outpatientid
    private boolean outpatientIdExists(String outpatientId) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM outpatient WHERE outpatient_id = ?")) {
            stmt.setString(1, outpatientId);
            ResultSet result = stmt.executeQuery();
            return result.next() && result.getInt(1) > 0;
        }
    }
    //advoid repeat patientid
    private boolean patientIdExistsInOutpatient(String patientId) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM outpatient WHERE patient_id = ?")) {
            stmt.setString(1, patientId);
            ResultSet result = stmt.executeQuery();
            return result.next() && result.getInt(1) > 0;
        }
    }
    //check the doctor have include inside doctor table
    private boolean doctorExists(String doctorId) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM doctor WHERE doctor_id = ?")) {
            stmt.setString(1, doctorId);
            ResultSet result = stmt.executeQuery();
            return result.next() && result.getInt(1) > 0;
        }
    }
    //insert the outpatient data to the table
    private void insertOutpatientRecord(String outpatientId, String patientId, String doctorId, String consultationType) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO outpatient (outpatient_id, patient_id, doctor_id, consultation_type) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, outpatientId);
            stmt.setString(2, patientId);
            stmt.setString(3, doctorId);
            stmt.setString(4, consultationType);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Database error occurred. Please try again.");
        }
    }
    // can update the doctor id and consultation type
    private void updateOutpatientRecord(String outpatientId, String newDoctorId, String newConsultationType) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("UPDATE outpatient SET doctor_id = ?, consultation_type = ? WHERE outpatient_id = ?")) {
            stmt.setString(1, newDoctorId);
            stmt.setString(2, newConsultationType);
            stmt.setString(3, outpatientId);
            stmt.executeUpdate();
        }
    }
    // Add back to appointment table when deleting from outpatient table
    private void addAppointmentRecord(String patientId, String doctorId) {
        // Generate the appointment ID based on the patient ID
        String appointmentID = "AP" + patientId.substring(Math.max(patientId.length() - 3, 0));  // Extract last 3 digits from patientId

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO appointment (appointment_id, patient_id, doctor_id, appointment_date, appointment_time) VALUES (?, ?, ?, NOW(), NOW())")) {

            stmt.setString(1, appointmentID);   // Set the dynamically generated appointment ID
            stmt.setString(2, patientId);       // Set the patient ID
            stmt.setString(3, doctorId);        // Set the doctor ID

            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Database error occurred. Please try again.");
            ex.printStackTrace();  // Optional: for more detailed error messages
        }
    }


    private void deleteAppointmentRecord(String patientId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM appointment WHERE patient_id = ?")) {
            stmt.setString(1, patientId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Database error occurred. Please try again.");
        }
    }
    //delete the data from outpatient table
    private void deleteOutpatientRecord(String outpatientId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM outpatient WHERE outpatient_id = ?")) {
            stmt.setString(1, outpatientId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Database error occurred. Please try again.");
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
