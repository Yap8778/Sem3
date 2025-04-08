/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hospital_reception.UI;


import com.mycompany.hospital_reception.PatientAppointment;
import com.mycompany.hospital_reception.PatientRegistration;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;

public class AppointmentGUI {
    // Text fields for admission information
    private TextField appointmentIdField = new TextField();
    private TextField patientIdField = new TextField();
    private TextField doctorIdField = new TextField();
    private DatePicker appointmentDateField = new DatePicker(LocalDate.now());
    private TextField appointmentTimeField = new TextField();
   
    // Buttons for CRUD operations 
    private Button btnInsert = new Button("Schedule Appointment");
    private Button btnView = new Button("View Appointment");
    private Button btnUpdate = new Button("Update Appointment");
    private Button btnDelete = new Button("Delete Appointment");
    private Button btnClear = new Button("Clear Fields");
    
    // Method to set up the layout and return a VBox
    public VBox getLayout() {
        // Grid layout for fields and labels
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Set placeholders
        appointmentIdField.setPromptText("Appointment ID");
        patientIdField.setPromptText("Patient ID");
        doctorIdField.setPromptText("Doctor ID");
        appointmentTimeField.setPromptText("Time (HH:mm)");

        // Add labels and text fields to the grid
        grid.add(new Label("Appointment ID:"), 0, 0);
        grid.add(appointmentIdField, 1, 0);
        grid.add(new Label("Patient ID:"), 0, 1);
        grid.add(patientIdField, 1, 1);
        grid.add(new Label("Doctor ID:"), 0, 2);
        grid.add(doctorIdField, 1, 2);
        grid.add(new Label("Appointment Date:"), 0, 3);
        grid.add(appointmentDateField, 1, 3);
        grid.add(new Label("Appointment Time:"), 0, 4);
        grid.add(appointmentTimeField, 1, 4);
        
        // Arrange buttons in a single horizontal row
        HBox buttonBox = new HBox(10, btnInsert, btnView, btnUpdate, btnDelete, btnClear);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        // Set button actions for CRUD operations and clear function
        btnInsert.setOnAction(e -> insertAppointment());
        btnView.setOnAction(e -> viewAppointment());
        btnUpdate.setOnAction(e -> updateAppointment());
        btnDelete.setOnAction(e -> deleteAppointment());
        btnClear.setOnAction(e -> clearFields());

        VBox layout = new VBox(10, grid, buttonBox);
        return layout;
    }

    // Insert appointment method
    private void insertAppointment() {
        if (areFieldsFilled()) { // Check if all fields are filled
            try {
                // Check if patient ID exists in the database
                if (!PatientRegistration.isPatientRegistered(patientIdField.getText())) {
                    showAlert("Patient ID not registered. Please register first.");
                    return;
                }
                // Parse date and time fields
                LocalDate date = appointmentDateField.getValue();
                Date appointmentDate = Date.valueOf(date);
                LocalTime localTime = LocalTime.parse(appointmentTimeField.getText());
                Time appointmentTime = Time.valueOf(localTime);
                // Create a new PatientAppointment instance with the provided data
                PatientAppointment appointment = new PatientAppointment(
                        appointmentIdField.getText(),
                        patientIdField.getText(),
                        doctorIdField.getText(),
                        appointmentDate,
                        appointmentTime
                );
                // Insert admission into database
                showAlert(appointment.insertAppointment());
                clearFields();
            } catch (Exception e) {
                showAlert("Invalid input. Please check time format.");
            }
        } else {
            showAlert("Please fill in all required fields.");
        }
    }

    // View appointment method
    private void viewAppointment() {
        if (appointmentIdField.getText().isEmpty()) {
            // Prompt if no ID entered
            showAlert("Please enter an Appointment ID to view.");
            return;
        }
        // Retrieve admission record by ID from the database
        PatientAppointment appointment = PatientAppointment.viewAppointment(appointmentIdField.getText().trim());
        if (appointment != null) {
            patientIdField.setText(appointment.getPatientId());
            doctorIdField.setText(appointment.getDoctorId());
            appointmentDateField.setValue(appointment.getAppointmentDate().toLocalDate());
            appointmentTimeField.setText(appointment.getAppointmentTime().toString());
            showAlert("Appointment found.");
        } else {
            showAlert("Appointment not found.");
        }
    }

    // Update appointment method
    private void updateAppointment() {
        if (areFieldsFilled()) { 
            try {
                // Check if patient ID exists in the database
                if (!PatientRegistration.isPatientRegistered(patientIdField.getText())) {
                    showAlert("Patient ID not registered. Please register first.");
                    clearFields();
                    return;
                }
                // Parse date and time fields
                LocalDate date = appointmentDateField.getValue();
                Date appointmentDate = Date.valueOf(date);
                LocalTime localTime = LocalTime.parse(appointmentTimeField.getText());
                Time appointmentTime = Time.valueOf(localTime);
                
                PatientAppointment appointment = new PatientAppointment(
                        appointmentIdField.getText(),
                        patientIdField.getText(),
                        doctorIdField.getText(),
                        appointmentDate,
                        appointmentTime
                );
                // Update admission in database
                showAlert(appointment.updateAppointment());
                clearFields();
            } catch (Exception e) {
                showAlert("Invalid input. Please check time format.");
            }
        } else {
            showAlert("Please fill in all required fields.");
        }
    }

    // Delete appointment method
    private void deleteAppointment() {
        if (appointmentIdField.getText().isEmpty()) {
            showAlert("Please enter an Appointment ID to delete.");
            return;
        }
        PatientAppointment appointment = new PatientAppointment(
                appointmentIdField.getText(), 
                null, 
                null, 
                null, 
                null);
        showAlert(appointment.deleteAppointment());
        clearFields();
    }

    // Method to clear all text fields
    private void clearFields() {
        appointmentIdField.clear();
        patientIdField.clear();
        doctorIdField.clear();
        appointmentDateField.setValue(LocalDate.now());
        appointmentTimeField.clear();
    }

     // Method to check if all fields are filled
    private boolean areFieldsFilled() {
        return !appointmentIdField.getText().isEmpty() &&
               !patientIdField.getText().isEmpty() &&
               !doctorIdField.getText().isEmpty() &&
               appointmentDateField.getValue() != null &&
               !appointmentTimeField.getText().isEmpty();
    }
    
    // Method to display feedback in a popup window
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment Scheduling Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


