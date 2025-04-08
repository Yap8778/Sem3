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

public class ScheduleAppointmentList extends Stage {

    private final ListView<String> appointmentListView = new ListView<>();
    private final Label lblStatus = new Label();

    // Connect to SQL database
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL",
            "root",
            "Jeffloh0329."
        );
    }

    public ScheduleAppointmentList() {
        this.setTitle("Upcoming Appointment");

        // Show appointment button
        Button btShowAppointments = new Button("Show Appointments");

        // Set button action
        btShowAppointments.setOnAction(e -> showAppointments());

        // Layout format
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(15));
        vBox.getChildren().addAll(btShowAppointments, lblStatus, appointmentListView);

        // Set up the scene
        Scene scene = new Scene(vBox, 450, 400);
        this.setScene(scene);
    }

     // Display appointments in ListView
    private void showAppointments() {
        ObservableList<String> appointments = FXCollections.observableArrayList();
        
        // SQL query to fetch appointment data
        String sql = "SELECT a.appointment_id, p.patient_firstName, p.patient_lastName, " +
                     "d.doctor_firstName, d.doctor_lastName, a.appointment_date, a.appointment_time " +
                     "FROM appointment a " +
                     "JOIN patient p ON a.patient_id = p.patient_id " +
                     "LEFT JOIN doctor d ON a.doctor_id = d.doctor_id";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            lblStatus.setText("Database Connected");

            // Process the result set and build the appointment list
            while (rs.next()) {
                String appointmentId = rs.getString("appointment_id");
                String patientFirstName = rs.getString("patient_firstName");
                String patientLastName = rs.getString("patient_lastName");
                String doctorFirstName = rs.getString("doctor_firstName") != null ? rs.getString("doctor_firstName") : "N/A";
                String doctorLastName = rs.getString("doctor_lastName") != null ? rs.getString("doctor_lastName") : "N/A";
                String date = rs.getString("appointment_date");
                String time = rs.getString("appointment_time");

                // Format each appointment's details
                String appointmentDetails = String.format("ID: %s | Patient: %s %s | Doctor: %s %s | Date: %s | Time: %s",
                                                          appointmentId, patientFirstName, patientLastName,
                                                          doctorFirstName, doctorLastName, date, time);
                appointments.add(appointmentDetails);
            }

            // Display the appointments in the ListView
            appointmentListView.setItems(appointments);

        } catch (SQLException e) {
            lblStatus.setText("Database not connected");
            appointmentListView.setItems(FXCollections.observableArrayList("Error fetching appointments: " + e.getMessage()));
        }
    }
}
