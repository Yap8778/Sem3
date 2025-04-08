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

public class PatientAdmission {
    // Attributes representing columns in the 'admission' table
    private String admission_id;
    private String patient_id;
    private String doctor_id;
    private Date admission_date;
    private Time admission_time;

    // Constructor to initialize a PatientAdmission object with given values
    public PatientAdmission(String admission_id, String patient_id, String doctor_id, Date admission_date, Time admission_time) {
        this.admission_id = admission_id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.admission_date = admission_date;
        this.admission_time = admission_time;
    }

    // Insert - Method to insert a new admission record into the database
    public String insertAdmission() {
        String sql = "INSERT INTO admission (admission_id, patient_id, doctor_id, admission_date, admission_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connectdb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Setting parameters for the insert query
            pstmt.setString(1, admission_id);
            pstmt.setString(2, patient_id);
            pstmt.setString(3, doctor_id);
            pstmt.setDate(4, admission_date);
            pstmt.setTime(5, admission_time);
            pstmt.executeUpdate(); // Execute the insert operation
            return "Admission scheduled successfully.";
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {  // Check if the error is a duplicate entry
                return "Admission ID has been duplicated. Please use a different ID.";
            } else {
                return "Schedule Failed: " + e.getMessage();
            }
        }
    }

    // Read - Method to retrieve an admission record by its ID.
    public static PatientAdmission viewAdmission(String id) {
        String sql = "SELECT * FROM admission WHERE admission_id = ?";
        try (Connection conn = connectdb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set patient_id parameter in the query
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            // Create a PatientAdmission object with the retrieved data if found
            if (rs.next()) {
                return new PatientAdmission(
                        rs.getString("admission_id"),
                        rs.getString("patient_id"),
                        rs.getString("doctor_id"),
                        rs.getDate("admission_date"),
                        rs.getTime("admission_time")
                );
            } else {
                System.out.println("Admission not found.");
            }
        } catch (SQLException e) {
            System.err.println("Read Failed: " + e.getMessage());
        }
        return null;
    }

    // Update - Method to modify an existing admission record in the database.
    public String updateAdmission() {
        String sql = "UPDATE admission SET patient_id = ?, doctor_id = ?, admission_date = ?, admission_time = ? WHERE admission_id = ?";
        try (Connection conn = connectdb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Setting parameters for the insert query
            pstmt.setString(1, patient_id);
            pstmt.setString(2, doctor_id);
            pstmt.setDate(3, admission_date);
            pstmt.setTime(4, admission_time);
            pstmt.setString(5, admission_id);

            int rows = pstmt.executeUpdate(); // Execute the update operation
            if (rows > 0) {
                return "Admission updated successfully.";
            } else {
                return "Admission ID not found.";
            }
        } catch (SQLException e) {
            return "Update Failed: " + e.getMessage();
        }
    }

    // Delete - Method to delete an admission record from the database by its ID.
    public String deleteAdmission() {
        String sql = "DELETE FROM admission WHERE admission_id = ?";
        try (Connection conn = connectdb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set patient_id parameter in the query
            pstmt.setString(1, admission_id);
            int rows = pstmt.executeUpdate(); // Execute the delete operation
            if (rows > 0) {
                return "Admission deleted successfully.";
            } else {
                return "Admission ID not found.";
            }
        } catch (SQLException e) {
            return "Delete Failed: " + e.getMessage();
        }
    }

    // Getters for each field to access private fields
    public String getAdmissionId() { return admission_id; }
    public String getPatientId() { return patient_id; }
    public String getDoctorId() { return doctor_id; }
    public Date getAdmissionDate() { return admission_date; }
    public Time getAdmissionTime() { return admission_time; }

    // Setters for each field to modify private fields
    public void setPatientId(String patient_id) { this.patient_id = patient_id; }
    public void setDoctorId(String doctor_id) { this.doctor_id = doctor_id; }
    public void setAdmissionDate(Date admissionDate) { this.admission_date = admissionDate; }
    public void setAdmissionTime(Time admissionTime) { this.admission_time = admissionTime; }
}


