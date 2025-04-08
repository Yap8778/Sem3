/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
 
package com.mycompany.hospital_reception.UI;
 
import com.mycompany.hospital_reception.MedicalReports;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
 
import java.sql.SQLException;
 
public class MedicalReportsFX extends Stage {
    // Instance of MedicalReports class to access database functions
    private final MedicalReports medicalReports = new MedicalReports();
 
    // Constructor to set up the MedicalReportsFX window
    public MedicalReportsFX() {
        // Set the window title
        this.setTitle("Medical Reports");
 
        // Labels and TextFields for medical report details
        Label reportIdLabel = new Label("Medical Report ID:");
        TextField reportIdField = new TextField();
 
        Label patientIdLabel = new Label("Patient ID:");
        TextField patientIdField = new TextField();
 
        Label doctorIdLabel = new Label("Doctor ID:");
        TextField doctorIdField = new TextField();
 
        Label diagnosisLabel = new Label("Diagnosis:");
        TextField diagnosisField = new TextField();
 
        Label treatmentLabel = new Label("Treatment:");
        TextField treatmentField = new TextField();
 
        Label reportDateLabel = new Label("Report Date (YYYY-MM-DD):");
        TextField reportDateField = new TextField();
 
        // Buttons for performing CRUD operations
        Button addReportButton = new Button("Add Report"); // To add a new report
        Button viewReportButton = new Button("View Report"); // To view a report
        Button updateReportButton = new Button("Update Report"); // To update a report
        Button deleteReportButton = new Button("Delete Report"); // To delete a report
 
        // Setting actions for each button
        addReportButton.setOnAction(e -> addMedicalReport(
                reportIdField.getText(),
                patientIdField.getText(),
                doctorIdField.getText(),
                diagnosisField.getText(),
                treatmentField.getText(),
                reportDateField.getText()
        ));
 
        viewReportButton.setOnAction(e -> viewMedicalReport(
                reportIdField.getText(),
                patientIdField.getText()
        ));
 
        updateReportButton.setOnAction(e -> updateMedicalReport(
                reportIdField.getText(),
                diagnosisField.getText(),
                treatmentField.getText()
        ));
 
        deleteReportButton.setOnAction(e -> deleteMedicalReport(reportIdField.getText()));
 
        // Setting up the layout for the form
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(15)); // Padding around the grid
        layout.setHgap(10); // Horizontal gap between columns
        layout.setVgap(10); // Vertical gap between rows
 
        // Adding labels, text fields, and buttons to the grid layout
        layout.add(reportIdLabel, 0, 0);
        layout.add(reportIdField, 1, 0);
        layout.add(patientIdLabel, 0, 1);
        layout.add(patientIdField, 1, 1);
        layout.add(doctorIdLabel, 0, 2);
        layout.add(doctorIdField, 1, 2);
        layout.add(diagnosisLabel, 0, 3);
        layout.add(diagnosisField, 1, 3);
        layout.add(treatmentLabel, 0, 4);
        layout.add(treatmentField, 1, 4);
        layout.add(reportDateLabel, 0, 5);
        layout.add(reportDateField, 1, 5);
        layout.add(addReportButton, 0, 6);
        layout.add(viewReportButton, 1, 6);
        layout.add(updateReportButton, 0, 7);
        layout.add(deleteReportButton, 1, 7);
 
        // Setting up and displaying the scene
        Scene scene = new Scene(layout, 500, 400);
        this.setScene(scene);
    }
 
    // Method to add a new medical report
    private void addMedicalReport(String reportId, String patientId, String doctorId, String diagnosis, String treatment, String reportDate) {
        try {
            // Calls addMedicalReport method in MedicalReports class to insert new record
            medicalReports.addMedicalReport(reportId, patientId, doctorId, diagnosis, treatment, reportDate);
            showAlert("Success", "Medical report added successfully.");
        } catch (SQLException ex) {
            showAlert("Error", "Error adding medical report: " + ex.getMessage()); // Shows error if there’s an issue
        }
    }
 
    // Method to view a specific medical report by report ID or patient ID
    private void viewMedicalReport(String reportId, String patientId) {
        try {
            if (!reportId.isEmpty()) {
                // Retrieve report details by report ID
                String reportDetails = medicalReports.viewMedicalReportById(reportId, true); 
                showAlert("Medical Report Details", reportDetails);
            } else if (!patientId.isEmpty()) {
                // Retrieve report details by patient ID
                String reportDetails = medicalReports.viewMedicalReportById(patientId, false); 
                showAlert("Medical Report Details", reportDetails);
            } else {
                // Prompt to enter either Report ID or Patient ID
                showAlert("Error", "Please enter either a Medical Report ID or Patient ID to view the report.");
            }
        } catch (SQLException ex) {
            showAlert("Error", "Error viewing medical report: " + ex.getMessage());
        }
    }
 
    // Method to update an existing medical report by report ID
    private void updateMedicalReport(String reportId, String diagnosis, String treatment) {
        try {
            // Calls updateMedicalReport method to modify existing record in the database
            medicalReports.updateMedicalReport(reportId, diagnosis, treatment);
            showAlert("Success", "Medical report updated successfully.");
        } catch (SQLException ex) {
            showAlert("Error", "Error updating medical report: " + ex.getMessage());
        }
    }
 
    // Method to delete a medical report by report ID
    private void deleteMedicalReport(String reportId) {
        try {
            // Calls deleteMedicalReport method to remove record from the database
            medicalReports.deleteMedicalReport(reportId);
            showAlert("Success", "Medical report deleted successfully.");
        } catch (SQLException ex) {
            showAlert("Error", "Error deleting medical report: " + ex.getMessage());
        }
    }
 
    // Utility method to show alert messages to the user
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Creates an information alert
        alert.setTitle(title); // Sets title of the alert
        alert.setHeaderText(null); // Removes header text for simplicity
        alert.setContentText(message); // Sets message content
        alert.showAndWait(); // Shows the alert and waits for the user to close it
    }
}
