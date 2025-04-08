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

public class UnassignInpatientList extends Stage {

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
    //set UI 
    public UnassignInpatientList() {
        this.setTitle("Inpatients Awaiting Bed Assignment");

        // Show inpatients button
        Button btShowInpatients = new Button("Show Inpatients with Unassigned Beds");

        // Set button action
        btShowInpatients.setOnAction(e -> showUnassignedInpatients());

        // Layout format
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(15));
        vBox.getChildren().addAll(btShowInpatients, lblStatus, inpatientListView);

        // Set up the scene
        Scene scene = new Scene(vBox, 500, 450);
        this.setScene(scene);
    }

    // display
    private void showUnassignedInpatients() {
        ObservableList<String> inpatients = FXCollections.observableArrayList();
        
        // query show bed = null 
        String sql = "SELECT i.inpatient_id, i.patient_id,p.patient_firstName, p.patient_lastName, " +
                     "i.room_id " +
                     "FROM inpatient i " +
                     "JOIN patient p ON i.patient_id = p.patient_id " +
                     "WHERE i.bed_id IS NULL";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            lblStatus.setText("Database Connected");

            // loop all the list
            while (rs.next()) {
                String inpatientId = rs.getString("inpatient_id");
                String patientId = rs.getString("patient_id");
                String firstName = rs.getString("patient_firstName");
                String lastName = rs.getString("patient_lastName");
                String roomId = rs.getString("room_id");

                // Format each inpatient's details (with no assigned bed)
                String inpatientDetails = String.format("ID: %s | PatientID: %s Patient: %s %s | Room: %s | Bed: Unassigned",
                                                         inpatientId, patientId,firstName, lastName, roomId);
                inpatients.add(inpatientDetails);
            }

            // Display the inpatients in the ListView
            inpatientListView.setItems(inpatients);

        } catch (SQLException e) {
            lblStatus.setText("Database not connected");
            inpatientListView.setItems(FXCollections.observableArrayList("Error fetching inpatients: " + e.getMessage()));
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
