package com.mycompany.hospital_reception;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class Inpatient extends Stage {
    // UI design
    private TextField patientIdField = new TextField();
    private TextField inpatientIdField = new TextField();
    private Label admissionDetails = new Label();
    private String doctorId;

    public Inpatient() {
        this.setTitle("Inpatient Enrollment System");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Enter Inpatient ID:"), 0, 0);
        grid.add(inpatientIdField, 1, 0);

        grid.add(new Label("Enter Patient ID:"), 0, 1);
        grid.add(patientIdField, 1, 1);

        grid.add(new Label("Admission Details:"), 0, 2);
        grid.add(admissionDetails, 1, 2);
        //button
        Button loadButton = new Button("Load Admission Details");
        Button submitButton = new Button("Add to Inpatient");
        Button updateButton = new Button("Update Inpatient Record");
        Button deleteButton = new Button("Remove from Inpatient");

        grid.add(loadButton, 0, 3);
        grid.add(submitButton, 1, 3);
        grid.add(updateButton, 0, 4);
        grid.add(deleteButton, 1, 4);

        loadButton.setOnAction(event -> loadAdmissionDetails());
        submitButton.setOnAction(event -> showConfirmationDialog());
        updateButton.setOnAction(event -> showUpdateDialog()); // Defined below
        deleteButton.setOnAction(event -> showDeleteConfirmationDialog()); // Defined below

        this.setScene(new Scene(grid, 450, 250));
    }

    // load data from admission table from sql
    private void loadAdmissionDetails() {
        String patientId = patientIdField.getText();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "Jeffloh0329.");
            //get the data information
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM admission WHERE patient_id = ?")) {
            stmt.setString(1, patientId);
            ResultSet result = stmt.executeQuery();
            //loop until the the patientid is match from admission table 
            if (result.next()) {
                doctorId = result.getString("doctor_id");
                String date = result.getString("admission_date");
                String time = result.getString("admission_time");
                admissionDetails.setText("Doctor ID: " + doctorId + "\nDate: " + date + "\nTime: " + time);
            } else {
                admissionDetails.setText("No admission details found for Patient ID: " + patientId);
            }
        } catch (SQLException ex) {
            System.out.println("Database error occurred. Please try again.");
        }
    }

    // pop up message for confirm and error
    private void showConfirmationDialog() {
        String inpatientId = inpatientIdField.getText();
        String patientId = patientIdField.getText();

        if (doctorId == null) {
            loadAdmissionDetails();
        }

        if (doctorId == null) {
            showAlert("Error", "Patient ID " + patientId + " does not have an admission record. Please admit the patient first.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Validate inpatientId and patientId
            if (!patientExistsInAdmission(patientId)) {
                showAlert("Error", "Patient ID " + patientId + " does not exist in the admission table. Please admit the patient first.", Alert.AlertType.ERROR);
                return;
            }

            if (inpatientIdExists(inpatientId)) {
                showAlert("Error", "Inpatient ID " + inpatientId + " is already in use. Please use a different ID.", Alert.AlertType.ERROR);
                return;
            }
            if (patientIdExistsInInpatient(patientId)) {
                showAlert("Error", "Patient ID " + patientId + " is already admitted as an inpatient.", Alert.AlertType.ERROR);
                return;
            }
        } catch (SQLException ex) {
            showAlert("Error", "Database error occurred while checking IDs.", Alert.AlertType.ERROR);
            return;
        }

        // Proceed with confirmation dialog setup and display
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Inpatient Admission Confirmation");
        confirmationAlert.setHeaderText("Please confirm the following information:");
        confirmationAlert.setContentText("Inpatient ID: " + inpatientId + "\n" + "Patient ID: " + patientId + "\n" + "Doctor ID: " + doctorId);
        ButtonType submitButton = new ButtonType("Submit");
        ButtonType cancelButton = new ButtonType("Cancel");

        confirmationAlert.getButtonTypes().setAll(submitButton, cancelButton);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == submitButton) {
                addPatientToInpatient(inpatientId, patientId, doctorId);
                deleteAdmissionRecord(patientId);
                showAlert("Success", "Inpatient record saved and admission removed successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Cancelled", "Submission cancelled. You can edit the details.", Alert.AlertType.INFORMATION);
            }
        });
    }


    // is the patient exist on admission table if true then proceed
    private boolean patientExistsInAdmission(String patientId) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM admission WHERE patient_id = ?")) {
            stmt.setString(1, patientId);
            ResultSet result = stmt.executeQuery();
            return result.next() && result.getInt(1) > 0;
        }
    }

    // advoid repeated inpatient id
    private boolean inpatientIdExists(String inpatientId) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM inpatient WHERE inpatient_id = ?")) {
            stmt.setString(1, inpatientId);
            ResultSet result = stmt.executeQuery();
            //it means there is at least one record with the given inpatientId
            //if the inpatient id is repeated then true otherwise false 
            return result.next() && result.getInt(1) > 0;
        }
    }

    // advoid the patient already inside the inpatient table
    private boolean patientIdExistsInInpatient(String patientId) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM inpatient WHERE patient_id = ?")) {
            stmt.setString(1, patientId);
            ResultSet result = stmt.executeQuery();
            return result.next() && result.getInt(1) > 0;
        }
    }
    //add to inpatient table
    private void addPatientToInpatient(String inpatientId, String patientId, String doctorId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
            //query to insert data
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO inpatient (inpatient_id, patient_id, doctor_id, room_id, bed_id) VALUES (?, ?, ?, NULL, NULL)")) {
            stmt.setString(1, inpatientId);
            stmt.setString(2, patientId);
            stmt.setString(3, doctorId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Database error occurred. Please try again.");
        }
    }
    //after add to inpatient the admission table should delete the data
    private void deleteAdmissionRecord(String patientId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM admission WHERE patient_id = ?")) {
            stmt.setString(1, patientId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Database error occurred. Please try again.");
        }
    }

    // for update the doctor id
    private void showUpdateDialog() {
        String inpatientId = inpatientIdField.getText();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Inpatient Record");
        dialog.setHeaderText("Enter new Doctor ID for Inpatient ID: " + inpatientId);
        dialog.setContentText("New Doctor ID:");
        //source:https://www.javatpoint.com/java-8-optional
        //If a value is present, and the value matches the given predicate, return an Optional describing the value
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newDoctorId -> {
            try {
                if (doctorExists(newDoctorId)) {  // the doctor should exists in the doctor table
                    updateInpatientRecord(inpatientId, newDoctorId);
                    showAlert("Success", "Inpatient record updated successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Doctor ID " + newDoctorId + " does not exist.", Alert.AlertType.ERROR);
                }
            } catch (SQLException ex) {
                showAlert("Error", "Failed to update inpatient record. Please try again.", Alert.AlertType.ERROR);
            }
        });
    }
    //check the doctor is record in doctor table or not
    private boolean doctorExists(String doctorId) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM doctor WHERE doctor_id = ?")) {
            stmt.setString(1, doctorId);
            ResultSet result = stmt.executeQuery();
            return result.next() && result.getInt(1) > 0;
        }
    }
    //update the new doctor id
    private void updateInpatientRecord(String inpatientId, String newDoctorId) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("UPDATE inpatient SET doctor_id = ? WHERE inpatient_id = ?")) {
            stmt.setString(1, newDoctorId);
            stmt.setString(2, inpatientId);
            stmt.executeUpdate();
        }
    }

    // for delete dialog
    private void showDeleteConfirmationDialog() {
        String inpatientId = inpatientIdField.getText();
        String patientId = patientIdField.getText();

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Remove from Inpatient");
        confirmationAlert.setHeaderText("Are you sure you want to remove this inpatient record?");
        confirmationAlert.setContentText("Inpatient ID: " + inpatientId + "\nPatient ID: " + patientId);

        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel");
        confirmationAlert.getButtonTypes().setAll(deleteButton, cancelButton);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == deleteButton) {
                addAdmissionRecord(patientId, doctorId);  // Re-add to admission table
                deleteInpatientRecord(inpatientId);       // Delete from inpatient table
                showAlert("Success", "Inpatient record removed and re-added to admission table.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Cancelled", "Delete action cancelled.", Alert.AlertType.INFORMATION);
            }
        });
    }
    //add back to admission table after delete from inpatient
    private void addAdmissionRecord(String patientId, String doctorId) {
        //AdmissionID base on patientID
        String admissionID ="AD"+ patientId.substring(Math.max(patientId.length()-3, 0));
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO admission (admission_id,patient_id, doctor_id, admission_date, admission_time) VALUES (?,?, ?, NOW(), NOW())")) {
            
            stmt.setString(1, admissionID);
            stmt.setString(2, patientId);
            stmt.setString(3, doctorId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Database error occurred. Please try again.");
        }
    }
    //delete the inpatient record and reupdate to admission table
    private void deleteInpatientRecord(String inpatientId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM inpatient WHERE inpatient_id = ?")) {
            stmt.setString(1, inpatientId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Database error occurred. Please try again.");
        }
    }

    // Utility method to show alerts
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
