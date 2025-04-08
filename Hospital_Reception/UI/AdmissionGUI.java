/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hospital_reception.UI;

import com.mycompany.hospital_reception.PatientAdmission;
import com.mycompany.hospital_reception.PatientRegistration;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Date;
import java.sql.Time;

public class AdmissionGUI {
    // Text fields for admission information
    private TextField admissionIdField = new TextField();
    private TextField patientIdField = new TextField();
    private TextField doctorIdField = new TextField();
    private DatePicker admissionDateField = new DatePicker(LocalDate.now());
    private TextField admissionTimeField = new TextField();
    
    // Buttons for CRUD operations 
    private Button btnInsert = new Button("Schedule Admission");
    private Button btnView = new Button("View Admission");
    private Button btnUpdate = new Button("Update Admission");
    private Button btnDelete = new Button("Delete Admission");
    private Button btnClear = new Button("Clear Fields");
    
    // Method to set up the layout and return a VBox
    public VBox getLayout() {
        // Grid layout for fields and labels
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Set placeholders
        admissionIdField.setPromptText("Admission ID");
        patientIdField.setPromptText("Patient ID");
        doctorIdField.setPromptText("Doctor ID");
        admissionTimeField.setPromptText("Time (HH:mm)");

        // Add labels and text fields to the grid
        grid.add(new Label("Admission ID:"), 0, 0);
        grid.add(admissionIdField, 1, 0);
        grid.add(new Label("Patient ID:"), 0, 1);
        grid.add(patientIdField, 1, 1);
        grid.add(new Label("Doctor ID:"), 0, 2);
        grid.add(doctorIdField, 1, 2);
        grid.add(new Label("Admission Date:"), 0, 3);
        grid.add(admissionDateField, 1, 3);
        grid.add(new Label("Admission Time:"), 0, 4);
        grid.add(admissionTimeField, 1, 4);
        
        // Arrange buttons in a single horizontal row
        HBox buttonBox = new HBox(10, btnInsert, btnView, btnUpdate, btnDelete, btnClear);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        // Set button actions for CRUD operations and clear function
        btnInsert.setOnAction(e -> insertAdmission());
        btnView.setOnAction(e -> viewAdmission());
        btnUpdate.setOnAction(e -> updateAdmission());
        btnDelete.setOnAction(e -> deleteAdmission());
        btnClear.setOnAction(e -> clearFields());

        VBox layout = new VBox(10, grid, buttonBox);
        return layout;
    }

    // Insert admission method
    private void insertAdmission() {
        if (areFieldsFilled()) { // Check if all fields are filled
            try {
                // Check if patient ID exists in the database
                if (!PatientRegistration.isPatientRegistered(patientIdField.getText())) {
                    showAlert("Patient ID not registered. Please register first.");
                    clearFields();
                    return;
                }
                // Parse date and time fields
                LocalDate date = admissionDateField.getValue();
                Date admissionDate = Date.valueOf(date);
                LocalTime localTime = LocalTime.parse(admissionTimeField.getText());
                Time admissionTime = Time.valueOf(localTime);
                // Create a new PatientAdmission instance with the provided data
                PatientAdmission admission = new PatientAdmission(
                    admissionIdField.getText(),
                    patientIdField.getText(),
                    doctorIdField.getText(),
                    admissionDate,
                    admissionTime
                );
                // Insert admission into database
                String feedback = admission.insertAdmission();
                showAlert(feedback);
                clearFields();
            } catch (Exception ex) {
                showAlert("Invalid input. Please check time format.");
            }
        } else {
            showAlert("Please fill in all required fields.");
        }
    }

    // View admission method
    private void viewAdmission() {
        if (admissionIdField.getText().isEmpty()) {
            // Prompt if no ID entered
            showAlert("Please enter an Admission ID to view.");
            return;
        }
        // Retrieve admission record by ID from the database
        PatientAdmission admission = PatientAdmission.viewAdmission(admissionIdField.getText().trim());
        if (admission != null) {
            patientIdField.setText(admission.getPatientId());
            doctorIdField.setText(admission.getDoctorId());
            admissionDateField.setValue(admission.getAdmissionDate().toLocalDate());
            admissionTimeField.setText(admission.getAdmissionTime().toString());
            showAlert("Admission found.");
        } else {
            showAlert("Admission not found.");
        }
    }

    // Update admission method
    private void updateAdmission() {
        if (areFieldsFilled()) {
            try {
                // Check if patient ID exists in the database
                if (!PatientRegistration.isPatientRegistered(patientIdField.getText())) {
                    showAlert("Patient ID not registered. Please register first.");
                    clearFields();
                    return;
                }
                // Parse date and time fields
                LocalDate date = admissionDateField.getValue();
                Date admissionDate = Date.valueOf(date);
                LocalTime localTime = LocalTime.parse(admissionTimeField.getText());
                Time admissionTime = Time.valueOf(localTime);

                PatientAdmission admission = new PatientAdmission(
                    admissionIdField.getText(),
                    patientIdField.getText(),
                    doctorIdField.getText(),
                    admissionDate,
                    admissionTime
                );
                // Update admission in database
                String feedback = admission.updateAdmission();
                showAlert(feedback);
                clearFields();
            } catch (Exception ex) {
                showAlert("Invalid input. Please check time format.");
            }
        } else {
            showAlert("Please fill in all required fields.");
        }
    }

    // Delete admission method
    private void deleteAdmission() {
        if (admissionIdField.getText().isEmpty()) {
            showAlert("Please enter an Admission ID to delete.");
            return;
        }

        PatientAdmission admission = new PatientAdmission(
                admissionIdField.getText(), 
                null, 
                null, 
                null, 
                null);
        String feedback = admission.deleteAdmission();
        showAlert(feedback);
        clearFields();
    }

    // Method to clear all text fields
    private void clearFields() {
        admissionIdField.clear();
        patientIdField.clear();
        doctorIdField.clear();
        admissionDateField.setValue(LocalDate.now());
        admissionTimeField.clear();
    }

    // Method to check if all fields are filled
    private boolean areFieldsFilled() {
        return !admissionIdField.getText().isEmpty() &&
               !patientIdField.getText().isEmpty() &&
               !doctorIdField.getText().isEmpty() &&
               admissionDateField.getValue() != null &&
               !admissionTimeField.getText().isEmpty();
    }
    
    // Method to display feedback in a popup window
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Admission Scheduling Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}





