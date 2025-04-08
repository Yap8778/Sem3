/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hospital_reception.UI;

import com.mycompany.hospital_reception.PatientRegistration;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

public class RegistrationGUI {
    
    // Text fields for patient information
    private TextField txtID = new TextField();
    private TextField txtFirstName = new TextField();
    private TextField txtLastName = new TextField();
    private TextField txtAge = new TextField();
    private ComboBox<String> cmbGender = new ComboBox<>();
    private TextField txtAddress = new TextField();
    private TextField txtContact = new TextField();
    
    // Buttons for CRUD operations 
    private Button btnInsert = new Button("Register Patient");
    private Button btnView = new Button("View Patient");
    private Button btnUpdate = new Button("Update Patient");
    private Button btnDelete = new Button("Delete Patient");
    private Button btnClear = new Button("Clear Fields");
    
    // Method to set up the layout and return a VBox
    public VBox getLayout() {
        // Grid layout for fields and labels
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Set placeholders and add gender dropdown options
        txtID.setPromptText("Patient ID");
        txtFirstName.setPromptText("First Name");
        txtLastName.setPromptText("Last Name");
        txtAge.setPromptText("Age");
        cmbGender.getItems().addAll("Male", "Female");
        cmbGender.setPromptText("Gender");
        txtAddress.setPromptText("Address");
        txtContact.setPromptText("Contact Number");

        // Add labels and text fields to the grid
        grid.add(new Label("Patient ID:"), 0, 0);
        grid.add(txtID, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(txtFirstName, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(txtLastName, 1, 2);
        grid.add(new Label("Age:"), 0, 3);
        grid.add(txtAge, 1, 3);
        grid.add(new Label("Gender:"), 0, 4);
        grid.add(cmbGender, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(txtAddress, 1, 5);
        grid.add(new Label("Contact Number:"), 0, 6);
        grid.add(txtContact, 1, 6);

        // Arrange buttons in a single horizontal row
        HBox buttonBox = new HBox(10, btnInsert, btnView, btnUpdate, btnDelete, btnClear);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        // Set button actions for CRUD operations and clear function
        btnInsert.setOnAction(e -> insertPatient());
        btnView.setOnAction(e -> viewPatient());
        btnUpdate.setOnAction(e -> {
            updatePatient();
            clearFields();  // Auto-clear fields after update
        });
        btnDelete.setOnAction(e -> {
            deletePatient();
            clearFields();  // Auto-clear fields after delete
        });
        btnClear.setOnAction(e -> clearFields());

        // Add grid and button box to the main layout
        VBox layout = new VBox(10, grid, buttonBox);
        return layout;
    }

    // Insert patient method
    private void insertPatient() {
        if (areFieldsFilled()) { // Check if all fields are filled
            try {
                int age = Integer.parseInt(txtAge.getText());
                // Create a new PatientRegistration instance with the provided data
                PatientRegistration patient = new PatientRegistration(
                        txtID.getText(),
                        txtFirstName.getText(),
                        txtLastName.getText(),
                        age,
                        cmbGender.getValue(),
                        txtAddress.getText(),
                        txtContact.getText()
                );
                // Insert patient into database
                String feedback = patient.insertPatient(); 
                showAlert(feedback);
            } catch (NumberFormatException e) {
                // Handle non-numeric age input
                showAlert("Age must be a valid number.");
            }
        } else {
            // Prompt if fields are missing
            showAlert("Please fill in all required fields.");
        }
    }

    // View patient method
    private void viewPatient() {
        if (txtID.getText().isEmpty()) {
            // Prompt if no ID entered
            showAlert("Please enter a Patient ID to view.");
            return;
        }
        // Retrieve registration record by ID from the database  
        PatientRegistration patient = PatientRegistration.viewPatient(txtID.getText().trim());
        if (patient != null) {
            // Add fields with patient details if found
            txtFirstName.setText(patient.getPatientFirstName());
            txtLastName.setText(patient.getPatientLastName());
            txtAge.setText(String.valueOf(patient.getAge()));
            cmbGender.setValue(patient.getGender());
            txtAddress.setText(patient.getAddress());
            txtContact.setText(patient.getContactNumber());
            showAlert("Patient found.");
        } else {
            showAlert("Patient not found.");
        }
    }

    // Update patient method
    private void updatePatient() {
        if (areFieldsFilled()) { // Check if all fields are filled
            try {
                int age = Integer.parseInt(txtAge.getText());
                PatientRegistration patient = new PatientRegistration(
                        txtID.getText(),
                        txtFirstName.getText(),
                        txtLastName.getText(),
                        age,
                        cmbGender.getValue(),
                        txtAddress.getText(),
                        txtContact.getText()
                );
                String feedback = patient.updatePatient();
                showAlert(feedback);
            } catch (NumberFormatException e) {
                // Handle non-numeric age input
                showAlert("Age must be a valid number.");
            }
        } else {
            // Prompt if fields are missing
            showAlert("Please fill in all required fields.");
        }
    }

    // Delete patient method
    private void deletePatient() {
        if (txtID.getText().isEmpty()) {
            // Prompt if no ID entered
            showAlert("Please enter a Patient ID to delete.");
            return;
        }
        PatientRegistration patient = new PatientRegistration(
                txtID.getText(), 
                null, 
                null, 
                0, 
                null, 
                null, 
                null);
        String feedback = patient.deletePatient();
        showAlert(feedback);
    }

    // Method to clear all text fields
    private void clearFields() {
        txtID.clear();
        txtFirstName.clear();
        txtLastName.clear();
        txtAge.clear();
        cmbGender.setValue(null);
        txtAddress.clear();
        txtContact.clear();
    }

    // Method to check if all fields are filled
    private boolean areFieldsFilled() {
        return !txtID.getText().isEmpty() && 
               !txtFirstName.getText().isEmpty() && 
               !txtLastName.getText().isEmpty() &&
               !txtAge.getText().isEmpty() && 
               cmbGender.getValue() != null && 
               !txtAddress.getText().isEmpty() &&
               !txtContact.getText().isEmpty();
    }

    // Method to display feedback in a popup window
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getButtonTypes().setAll(ButtonType.CLOSE);
        alert.showAndWait();
    }
}






