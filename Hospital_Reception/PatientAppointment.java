/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hospital_reception;

import com.mycompany.hospital_reception.UI.connectdb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;

public class PatientAppointment {
    // Attributes representing columns in the 'appointment' table
    private String appointment_id;
    private String patient_id;
    private String doctor_id;
    private Date appointment_date;
    private Time appointment_time;

    // Constructor to initialize a PatientAdmission object with given values
    public PatientAppointment(String appointment_id, String patient_id, String doctor_id, Date appointment_date, Time appointment_time) {
        this.appointment_id = appointment_id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.appointment_date = appointment_date;
        this.appointment_time = appointment_time;
    }

    // Insert - Method to insert a new appointment record into the database
    public String insertAppointment() {
        String sql = "INSERT INTO appointment (appointment_id, patient_id, doctor_id, appointment_date, appointment_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connectdb.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Setting parameters for the insert query
            pstmt.setString(1, appointment_id);
            pstmt.setString(2, patient_id);
            pstmt.setString(3, doctor_id);
            pstmt.setDate(4, appointment_date);
            pstmt.setTime(5, appointment_time);
            pstmt.executeUpdate(); // Execute the insert operation
            return "Appointment scheduled successfully.";
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {  // Check if the error is a duplicate entry
                return "Appointment ID has been duplicated. Please use a different ID.";
            } else {
                return "Schedule Failed: " + e.getMessage();
            }
        }
    }

    // Read - Method to retrieve an appointment record by its ID.
    public static PatientAppointment viewAppointment(String id) {
        String sql = "SELECT * FROM appointment WHERE appointment_id = ?";
        try (Connection conn = connectdb.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set patient_id parameter in the query
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            // Create a PatientAppointment object with the retrieved data if found
            if (rs.next()) {
                return new PatientAppointment(
                    rs.getString("appointment_id"),
                    rs.getString("patient_id"),
                    rs.getString("doctor_id"),
                    rs.getDate("appointment_date"),
                    rs.getTime("appointment_time")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Read Failed: " + e.getMessage());
            return null;
        }
    }

    // Update - Method to modify an existing appointment record in the database.
    public String updateAppointment() {
        String sql = "UPDATE appointment SET patient_id = ?, doctor_id = ?, appointment_date = ?, appointment_time = ? WHERE appointment_id = ?";
        try (Connection conn = connectdb.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Setting parameters for the insert query
            pstmt.setString(1, patient_id);
            pstmt.setString(2, doctor_id);
            pstmt.setDate(3, appointment_date);
            pstmt.setTime(4, appointment_time);
            pstmt.setString(5, appointment_id);
            int rows = pstmt.executeUpdate(); // Execute the update operation
            if (rows > 0) {
                return "Appointment updated successfully.";
            } else {
                return "Appointment ID not found.";
            }
        } catch (SQLException e) {
            return "Update Failed: " + e.getMessage();
        }
    }

    // Delete - Method to delete an admission record from the database by its ID.
    public String deleteAppointment() {
        String sql = "DELETE FROM appointment WHERE appointment_id = ?";
        try (Connection conn = connectdb.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set patient_id parameter in the query
            pstmt.setString(1, appointment_id);
            int rows = pstmt.executeUpdate(); // Execute the delete operation
            if (rows > 0) {
                return "Appointment deleted successfully.";
            } else {
                return "Appointment ID not found.";
            }
        } catch (SQLException e) {
            return "Delete Failed: " + e.getMessage();
        }
    }

    // Getters for each field to access private fields
    public String getAppointmentId() { return appointment_id; }
    public String getPatientId() { return patient_id; }
    public String getDoctorId() { return doctor_id; }
    public Date getAppointmentDate() { return appointment_date; }
    public Time getAppointmentTime() { return appointment_time; }
    
    // Setters for each field to modify private fields
    public void setPatientId(String patient_id) { this.patient_id = patient_id; }
    public void setDoctorId(String doctor_id) { this.doctor_id = doctor_id; }
    public void setAppointmentDate(Date appointmentDate) { this.appointment_date = appointmentDate; }
    public void setAppointmentTime(Time appointmentTime) { this.appointment_time = appointmentTime; }
}