package com.mycompany.hospital_reception;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.*;

public class PatientHospitalAdmission extends Stage {
    // Create a textfield to let users insert data
    private TextField admissionIdField = new TextField();
    private TextField patientIdField = new TextField();
    private TextField doctorIdField = new TextField();

    public PatientHospitalAdmission() {
        this.setTitle("Patient Hospital Admission");

        // Using GridPane can ensure the element locate in specific column or row
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        // Label just show a information to allow users to read
        grid.add(new Label("Inpatient/Outpatient ID (IP???/OP???):"), 0, 0);
        grid.add(admissionIdField, 1, 0);

        grid.add(new Label("Patient ID:"), 0, 1);
        grid.add(patientIdField, 1, 1);

        grid.add(new Label("Doctor ID:"), 0, 2);
        grid.add(doctorIdField, 1, 2);

        // Create Inpatient button and Outpatient button
        Button inpatientButton = new Button("Inpatient");
        Button outpatientButton = new Button("Outpatient");
        grid.add(inpatientButton, 0, 3);
        grid.add(outpatientButton, 1, 3);

        // Run this code when the Inpatient button get activate
        inpatientButton.setOnAction(event -> {
            String admissionId = admissionIdField.getText();
            String patientId = patientIdField.getText();
            String doctorId = doctorIdField.getText();

            // To check the Inpatient_id is inserted with following the format, which is start with "I"
            if (admissionId.startsWith("IP")) {
                // To make sure there will not have the same Inpatient_id
                // AlertType using the "ERROR" will show the users this insert is not work
                if (isPatientAssigned(patientId)) {
                    showAlert("Error", "Patient is already assigned as either an inpatient or outpatient. Cannot double assign.", Alert.AlertType.ERROR);
                } else {
                    updateInpatientRecord(patientId, admissionId, doctorId);
                }
            } else {
                showAlert("Error", "Invalid ID. Please enter a valid Inpatient ID (Example: IP001) and try again.", Alert.AlertType.ERROR);
                admissionIdField.clear();
            }
        });

        // Run this code when the Outpatient button get activate
        outpatientButton.setOnAction(event -> {
            String admissionId = admissionIdField.getText();
            String patientId = patientIdField.getText();
            String doctorId = doctorIdField.getText();

            // To check the Outpatient_id is inserted with following the format which is start with "O"
            if (admissionId.startsWith("OP")) {
                if (isPatientAssigned(patientId)) {
                    showAlert("Error", "Patient is already assigned as either an inpatient or outpatient. Cannot double assign.", Alert.AlertType.ERROR);
                } else {
                    updateOutpatientRecord(patientId, admissionId, doctorId);
                }
            } else {
                showAlert("Error", "Invalid ID. Please enter a valid Outpatient ID (Example: OP001) and try again.", Alert.AlertType.ERROR);
                admissionIdField.clear();
            }
        });

        this.setScene(new Scene(grid, 600, 200));
    }

     // To check the patient is successfully assigned to inpatient or outpatient
    private boolean isPatientAssigned(String patientId) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "Jeffloh0329.");
             PreparedStatement inpatientStmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM inpatient WHERE patient_id = ?");
             PreparedStatement outpatientStmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM outpatient WHERE patient_id = ?");
             PreparedStatement admissionStmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM admission WHERE patient_id = ?");
             PreparedStatement appointmentStmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM appointment WHERE patient_id = ?")) {

            // Check in inpatient table
            // If in the inpatient table can find the specific patient_id, it will return 1 which means true
            inpatientStmt.setString(1, patientId);
            ResultSet inpatientResult = inpatientStmt.executeQuery();
            inpatientResult.next();
            int inpatientCount = inpatientResult.getInt(1);

             // Check in outpatient table
            // If in the outpatient table can find the specific patient_id, it will return 1 which means true
            outpatientStmt.setString(1, patientId);
            ResultSet outpatientResult = outpatientStmt.executeQuery();
            outpatientResult.next();
            int outpatientCount = outpatientResult.getInt(1);

            // Check in admission table
            admissionStmt.setString(1, patientId);
            ResultSet admissionResult = admissionStmt.executeQuery();
            admissionResult.next();
            int admissionCount = admissionResult.getInt(1);

            // Check in appointment table
            appointmentStmt.setString(1, patientId);
            ResultSet appointmentResult = appointmentStmt.executeQuery();
            appointmentResult.next();
            int appointmentCount = appointmentResult.getInt(1);

            // Either one of them is one, it will return true value, otherwise it will happen error
            return (inpatientCount > 0 || outpatientCount > 0 || admissionCount > 0 || appointmentCount > 0);

        } catch (SQLException ex) {
            showAlert("Database Error", "Error checking patient assignment. Please try again.", Alert.AlertType.ERROR);
            ex.printStackTrace();
            return true; 
        }
    }
    // Update the patient details in the inpatient table based on the patient_id
    private void updateInpatientRecord(String patientId, String inpatientId, String doctorId) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement(
                     // "?" symbol is like a unknown data, it can be replace by anykind of data
                     "INSERT INTO inpatient (inpatient_id, patient_id, doctor_id) VALUES (?, ?, ?)")) {
            stmt.setString(1, inpatientId);
            stmt.setString(2, patientId);
            stmt.setString(3, doctorId);
            stmt.executeUpdate();

            // If successfully add specific patient_id into inpatient table, it will show the information otherwise it will show error 
            showAlert("Success", "Inpatient record added successfully.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Database Error", "Error adding inpatient record. Please try again.", Alert.AlertType.ERROR);
            ex.printStackTrace();
        }
    }

    // Update the patient details in the outpatient table based on the patient_id
    private void updateOutpatientRecord(String patientId, String outpatientId, String doctorId) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "Jeffloh0329.");
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO outpatient (outpatient_id, patient_id, doctor_id) VALUES (?, ?, ?)")) {
            stmt.setString(1, outpatientId);
            stmt.setString(2, patientId);
            stmt.setString(3, doctorId);
            stmt.executeUpdate();

            showAlert("Success", "Outpatient record added successfully.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Database Error", "Error adding outpatient record. Please try again.", Alert.AlertType.ERROR);
            ex.printStackTrace();
        }
    }

    // Create a frame to always show the information
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}