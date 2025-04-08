/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
 
package com.mycompany.hospital_reception.UI;
 
import com.mycompany.hospital_reception.InsuranceClaims;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.*;
 
public class InsuranceClaimsFX extends Stage {
 
    // Creating an instance of the InsuranceClaims class to interact with the database
    private final InsuranceClaims insuranceClaims = new InsuranceClaims();
 
    // Constructor for the InsuranceClaimsFX window
    public InsuranceClaimsFX() {
        // Set the title of this window 
        this.setTitle("Insurance Claims");
 
        // Creating labels and text fields for each attribute we need for an insurance claim
        Label insuranceIdLabel = new Label("Insurance ID:");
        TextField insuranceIdField = new TextField();
 
        Label patientIdLabel = new Label("Patient ID:");
        TextField patientIdField = new TextField();
 
        Label providerNameLabel = new Label("Provider Name:");
        TextField providerNameField = new TextField();
 
        Label amountCoverageLabel = new Label("Amount Coverage:");
        TextField amountCoverageField = new TextField();
 
        Label claimStatusLabel = new Label("Claim Status:");
        TextField claimStatusField = new TextField();
 
        Label issueDateLabel = new Label("Issue Date (YYYY-MM-DD):");
        TextField issueDateField = new TextField();
 
        // Creating buttons for each operation: Add, View, Update, and Delete insurance claims
        Button addClaimButton = new Button("Add Claim");
        Button viewClaimsButton = new Button("View Claims");
        Button updateClaimButton = new Button("Update Claim");
        Button deleteClaimButton = new Button("Delete Claim");
 
        // Setting actions for each button. Each action calls the appropriate method.
        addClaimButton.setOnAction(e -> addInsuranceClaim(
                insuranceIdField.getText(),
                patientIdField.getText(),
                providerNameField.getText(),
                amountCoverageField.getText(),
                claimStatusField.getText(),
                issueDateField.getText())
        );
 
        viewClaimsButton.setOnAction(e -> viewInsuranceClaims());
 
        updateClaimButton.setOnAction(e -> updateInsuranceClaim(
                insuranceIdField.getText(),
                claimStatusField.getText(),
                amountCoverageField.getText())
        );
 
        deleteClaimButton.setOnAction(e -> deleteInsuranceClaim(insuranceIdField.getText()));
 
        // Setting up the layout with padding and spacing between rows and columns
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(15));
        layout.setHgap(10); // Horizontal gap between columns
        layout.setVgap(10); // Vertical gap between rows
 
        // Adding all labels, text fields, and buttons to the grid layout
        layout.add(insuranceIdLabel, 0, 0);
        layout.add(insuranceIdField, 1, 0);
        layout.add(patientIdLabel, 0, 1);
        layout.add(patientIdField, 1, 1);
        layout.add(providerNameLabel, 0, 2);
        layout.add(providerNameField, 1, 2);
        layout.add(amountCoverageLabel, 0, 3);
        layout.add(amountCoverageField, 1, 3);
        layout.add(claimStatusLabel, 0, 4);
        layout.add(claimStatusField, 1, 4);
        layout.add(issueDateLabel, 0, 5);
        layout.add(issueDateField, 1, 5);
        layout.add(addClaimButton, 0, 6);
        layout.add(viewClaimsButton, 1, 6);
        layout.add(updateClaimButton, 0, 7);
        layout.add(deleteClaimButton, 1, 7);
 
        // Setting up the scene with the layout, then setting this scene for the stage
        Scene scene = new Scene(layout, 500, 400);
        this.setScene(scene);
    }
 
    // Method to add an insurance claim with validation checks
    private void addInsuranceClaim(String insuranceId, String patientId, String providerName, String amountCoverage, String claimStatus, String issueDate) {
        try {
            // Check if insurance ID follows the format 'IN###'
            if (!insuranceId.matches("IN\\d{3}")) {
                showAlert("Error", "Insurance ID format is incorrect. It should be in 'IN###' format.");
                return;
            }
            // Check if patient ID follows the format 'P###'
            if (!patientId.matches("P\\d{3}")) {
                showAlert("Error", "Patient ID format is incorrect. It should be in 'P###' format.");
                return;
            }
            // Convert amountCoverage to double
            double coverage = Double.parseDouble(amountCoverage);
            // Call method to add claim to the database
            insuranceClaims.addInsuranceClaim(insuranceId, patientId, providerName, coverage, claimStatus, issueDate);
            showAlert("Success", "Insurance claim added successfully.");
        } catch (SQLException | NumberFormatException ex) {
            showAlert("Error", ex.getMessage()); // Show an alert if there is an error
        }
    }
 
    // Method to view all insurance claims and display them in an alert box
    private void viewInsuranceClaims() {
        try {
            // Get all claims from the database
            String claims = insuranceClaims.viewInsuranceClaims();
            showAlert("Insurance Claims", claims); // Display claims in an alert box
        } catch (SQLException ex) {
            showAlert("Error", ex.getMessage()); // Show an alert if there is an error
        }
    }
 
    // Method to update an insurance claim based on the insurance ID
    private void updateInsuranceClaim(String insuranceId, String claimStatus, String amountCoverage) {
        try {
            // Check if insurance ID follows the format 'IN###'
            if (!insuranceId.matches("IN\\d{3}")) {
                showAlert("Error", "Insurance ID format is incorrect. It should be in 'IN###' format.");
                return;
            }
            // Convert amountCoverage to double
            double coverage = Double.parseDouble(amountCoverage);
            // Call method to update the claim in the database
            insuranceClaims.updateInsuranceClaim(insuranceId, claimStatus, coverage);
            showAlert("Success", "Insurance claim updated successfully.");
        } catch (SQLException | NumberFormatException ex) {
            showAlert("Error", ex.getMessage()); // Show an alert if there is an error
        }
    }
 
    // Method to delete an insurance claim based on the insurance ID
    private void deleteInsuranceClaim(String insuranceId) {
        try {
            // Check if insurance ID follows the format 'IN###'
            if (!insuranceId.matches("IN\\d{3}")) {
                showAlert("Error", "Insurance ID format is incorrect. It should be in 'IN###' format.");
                return;
            }
            // Call method to delete the claim from the database
            insuranceClaims.deleteInsuranceClaim(insuranceId);
            showAlert("Success", "Insurance claim deleted successfully.");
        } catch (SQLException ex) {
            showAlert("Error", ex.getMessage()); // Show an alert if there is an error
        }
    }
 
    // Utility method to display an alert with a specified title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Create an alert of type INFORMATION
        alert.setTitle(title); // Set the title of the alert
        alert.setHeaderText(null); // No header text
        alert.setContentText(message); // Set the content text of the alert
        alert.showAndWait(); // Display the alert and wait for the user to close it
    }
}
