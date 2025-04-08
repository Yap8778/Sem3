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
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class AdmissionList extends Stage {

    private final ListView<String> admissionListView = new ListView<>();
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
    public AdmissionList() {
        this.setTitle("Upcoming Admissions");

        Button btShowAdmissions = new Button("Show Admissions");

        btShowAdmissions.setOnAction(e -> showAdmissions());

        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(15));
        vBox.getChildren().addAll(btShowAdmissions, lblStatus, admissionListView);

        Scene scene = new Scene(vBox, 600, 450);
        this.setScene(scene);
    }

    // show list
    private void showAdmissions() {
        ObservableList<String> admissions = FXCollections.observableArrayList();
        
        // query
        String sql = "SELECT a.admission_id, p.patient_firstName, p.patient_lastName, " +
             "d.doctor_firstName, d.doctor_lastName, a.admission_date, a.admission_time " +
             "FROM admission a " +
             "JOIN patient p ON a.patient_id = p.patient_id " +
             "LEFT JOIN doctor d ON a.doctor_id = d.doctor_id";


        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            lblStatus.setText("Database Connected");

            // use string to get the result as my data type is set varchar
            while (rs.next()) {
                String admissionId = rs.getString("admission_id");
                String patientFirstName = rs.getString("patient_firstName");
                String patientLastName = rs.getString("patient_lastName");
                String doctorFirstName = rs.getString("doctor_firstName");
                String doctorLastName = rs.getString("doctor_lastName");
                Date admissionDate = rs.getDate("admission_date");
                Time admissionTime = rs.getTime("admission_time");

                // list out format
                String admissionDetails = String.format("ID: %s | Patient: %s %s | Doctor: %s %s | Date: %s | Time: %s",
                                                         admissionId, patientFirstName, patientLastName, 
                                                         doctorFirstName, doctorLastName, admissionDate, admissionTime);
                admissions.add(admissionDetails);
            }

            admissionListView.setItems(admissions);

        } catch (SQLException e) {
            lblStatus.setText("Database not connected");
            admissionListView.setItems(FXCollections.observableArrayList("Error fetching admissions: " + e.getMessage()));
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
