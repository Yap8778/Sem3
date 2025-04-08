package com.mycompany.hospital_reception.list;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PatientList extends Stage {

    private final ListView<String> patientListView = new ListView<>();
    private final Label lblStatus = new Label();

    // connect sql
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL",
            "root",
            "Jeffloh0329."
        );
    }

    // UI design
    public PatientList() {
        this.setTitle("Patient List");

        Button btShowPatients = new Button("Show Patient List");
        btShowPatients.setOnAction(e -> showPatientList());

        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(15));
        vBox.getChildren().addAll(btShowPatients, lblStatus, patientListView);

        Scene scene = new Scene(vBox, 1000, 450);  // Increased width to fit all information
        this.setScene(scene);
    }

    // Display patient list with inpatient/outpatient information
    private void showPatientList() {
        ObservableList<String> patients = FXCollections.observableArrayList();
        
        // Query to get details from patient, inpatient, and outpatient tables
        // used left join to check is that patient is in or out patient
        // case is use to determine the inpatient and outpatient
        String sql = "SELECT p.patient_id, p.patient_firstName, p.patient_lastName, p.age, p.gender, p.address, p.contactNumber, " +
                     "i.room_id, i.bed_id, o.consultation_type, " +
                     "CASE WHEN i.patient_id IS NOT NULL THEN 'Inpatient' " +
                     "     WHEN o.patient_id IS NOT NULL THEN 'Outpatient' " +
                     "     ELSE 'Not assigned' END AS status " +
                     "FROM patient p " +
                     "LEFT JOIN inpatient i ON p.patient_id = i.patient_id " +
                     "LEFT JOIN outpatient o ON p.patient_id = o.patient_id";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            lblStatus.setText("Database Connected");
            //loop all the data to get the detail
            while (rs.next()) {
                String patientId = rs.getString("patient_id");
                String patientFirstName = rs.getString("patient_firstName");
                String patientLastName = rs.getString("patient_lastName");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");
                String address = rs.getString("address");
                String contactNumber = rs.getString("contactNumber");

                // Determine patient status and room/bed assignments based on the join results
                String status = rs.getString("status");
                String roomStatus = "";
                String bedStatus = "";

                if ("Inpatient".equals(status)) {
                    // Inpatient: Check room and bed assignment
                    roomStatus = (rs.getString("room_id") != null) ? "Room: " + rs.getString("room_id") : "Room: Unassigned";
                    bedStatus = (rs.getString("bed_id") != null) ? "Bed: " + rs.getString("bed_id") : "Bed: Unassigned";
                    //+= used to append additional information to the existing status string
                    status += " | " + roomStatus + " | " + bedStatus;
                } else if ("Outpatient".equals(status)) {
                    // Outpatient: Show consultation type only
                    String consultationType = rs.getString("consultation_type");
                    status = "Outpatient | Consultation Type: " + (consultationType != null ? consultationType : "Not specified");
                } else {
                    // Not assigned: No room or bed assignment
                    status = "Not assigned";
                }

                // list out format
                String patientDetails = String.format("ID: %s | Name: %s %s | Age: %d | Gender: %s | Address: %s | Contact: %s | Status: %s",
                                                      patientId, patientFirstName, patientLastName, age, gender, address, contactNumber, status);
                patients.add(patientDetails);
            }

            patientListView.setItems(patients);

        } catch (SQLException e) {
            lblStatus.setText("Database not connected");
            patientListView.setItems(FXCollections.observableArrayList("Error fetching patient list: " + e.getMessage()));
        }
    }

    // Method to show alert dialogs
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
