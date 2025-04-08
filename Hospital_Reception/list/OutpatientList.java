package com.mycompany.hospital_reception.list;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OutpatientList extends Stage {

    private final ListView<String> outpatientListView = new ListView<>();
    private final Label lblStatus = new Label();

    // Connect to SQL database
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL",
            "root",
            "Jeffloh0329."
        );
    }

    public OutpatientList() {
        this.setTitle("Outpatient List");

        // Show outpatient button
        Button btShowOutpatients = new Button("Show Outpatients");

        // Set button action
        btShowOutpatients.setOnAction(e -> showOutpatients());

        // Layout format
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(15));
        vBox.getChildren().addAll(btShowOutpatients, lblStatus, outpatientListView);

        // Set up the scene
        Scene scene = new Scene(vBox, 450, 400);
        this.setScene(scene);
    }

    // display the list 
    private void showOutpatients() {
        ObservableList<String> outpatients = FXCollections.observableArrayList();
        
        // query join that show the patient and join the patient table 
        String sql = "SELECT o.outpatient_id, p.patient_firstName, p.patient_lastName, " +
                     "o.consultation_type " +
                     "FROM outpatient o " +
                     "JOIN patient p ON o.patient_id = p.patient_id";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            lblStatus.setText("Database Connected");

            // use while loop to loop all the name
            while (rs.next()) {
                String outpatientId = rs.getString("outpatient_id");  // Change to getString
                String firstName = rs.getString("patient_firstName");
                String lastName = rs.getString("patient_lastName");
                String consultationType = rs.getString("consultation_type");

                // format of listing the list (ID, First & Last Name and Consultation type
                String outpatientDetails = String.format("ID: %s | Patient: %s %s | Consultation Type: %s",
                                                         outpatientId, firstName, lastName, consultationType);
                outpatients.add(outpatientDetails);
            }

            // Display the outpatients in the ListView
            outpatientListView.setItems(outpatients);

        } catch (SQLException e) {
            lblStatus.setText("Database not connected");
            outpatientListView.setItems(FXCollections.observableArrayList("Error fetching outpatients: " + e.getMessage()));
        }
    }
}
