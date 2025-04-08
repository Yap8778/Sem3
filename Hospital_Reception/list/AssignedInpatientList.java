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

public class AssignedInpatientList extends Stage {

    private final ListView<String> inpatientListView = new ListView<>();
    private final Label lblStatus = new Label();

    // sql
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL",
            "root",
            "Jeffloh0329."
        );
    }
    //UI design
    public AssignedInpatientList() {
        this.setTitle("Occupied Beds - Inpatients");

        Button btShowInpatients = new Button("Show Inpatients with Assigned Beds");
        Button btUnassignBed = new Button("Unassign Bed");

        btShowInpatients.setOnAction(e -> showInpatients());
        btUnassignBed.setOnAction(e -> unassignBed());

        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(15));
        vBox.getChildren().addAll(btShowInpatients, btUnassignBed, lblStatus, inpatientListView);
        
        Scene scene = new Scene(vBox, 500, 450);
        this.setScene(scene);
    }

    // show the list
    private void showInpatients() {
        ObservableList<String> inpatients = FXCollections.observableArrayList();
        
        // query show bed is not null
        String sql = "SELECT i.inpatient_id,i.patient_id, p.patient_firstName, p.patient_lastName, " +
                     "i.room_id, i.bed_id " +
                     "FROM inpatient i " +
                     "JOIN patient p ON i.patient_id = p.patient_id " +
                     "WHERE i.bed_id IS NOT NULL";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            lblStatus.setText("Database Connected");

            // Process the result set and build the inpatient list
            while (rs.next()) {
                String inpatientId = rs.getString("inpatient_id");
                String patientId = rs.getString("patient_id");
                String firstName = rs.getString("patient_firstName");
                String lastName = rs.getString("patient_lastName");
                String roomId = rs.getString("room_id");
                String bedId = rs.getString("bed_id");

                // Format each inpatient's details
                String inpatientDetails = String.format("InpatientID: %s | PatientID: %s | Patient: %s %s | Room: %s | Bed: %s",
                                                         inpatientId, patientId,firstName, lastName, roomId, bedId);
                inpatients.add(inpatientDetails);
            }

            // Display the inpatients in the ListView
            inpatientListView.setItems(inpatients);

        } catch (SQLException e) {
            lblStatus.setText("Database not connected");
            inpatientListView.setItems(FXCollections.observableArrayList("Error fetching inpatients: " + e.getMessage()));
        }
    }

    // unassign the bed to the patient
    private void unassignBed() {
        // select item from the ListView
        String selectedItem = inpatientListView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            showAlert("No Selection", "Please select an inpatient to unassign the bed.", AlertType.WARNING);
            return;
        }

        // get the Id
        String inpatientId = selectedItem.split(" ")[1];  // Extracting the inpatient_id

        // update the unassign bed for the patient
        String sql = "UPDATE inpatient SET bed_id = NULL, room_id = NULL WHERE inpatient_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, inpatientId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Bed unassigned successfully for inpatient ID: " + inpatientId, AlertType.INFORMATION);
                showInpatients();  // Refresh the list to show the updated bed status
            } else {
                showAlert("Error", "Failed to unassign bed. Inpatient ID not found.", AlertType.ERROR);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error unassigning bed: " + e.getMessage(), AlertType.ERROR);
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
