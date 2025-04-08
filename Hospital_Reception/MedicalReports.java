/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package com.mycompany.hospital_reception;
 
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class MedicalReports {
 
    // Database connection details for connecting to the 'receptionsystem' database
    private static final String URL = "jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String USER = "root";
    private static final String PASSWORD = "Jeffloh0329.";
 
    // Method to check if a patient with the given ID exists in the database
    private boolean checkPatientExists(String patient_id) throws SQLException {
        String sql = "SELECT 1 FROM patient WHERE patient_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patient_id); // Set the patient_id parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Return true if a record is found, false otherwise
            }
        }
    }
 
    // Method to check if a doctor with the given ID exists in the database
    private boolean checkDoctorExists(String doctor_id) throws SQLException {
        String sql = "SELECT 1 FROM doctor WHERE doctor_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctor_id); // Set the doctor_id parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Return true if a record is found, false otherwise
            }
        }
    }
 
    // Method to check if a specific medical report ID already exists in the database
    private boolean checkMedicalReportIdExists(String medical_report_id) throws SQLException {
        String sql = "SELECT 1 FROM medical_reports WHERE medical_report_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medical_report_id); // Set the medical_report_id parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Return true if a record is found, false otherwise
            }
        }
    }
 
    // Method to add a new medical report, only if the patient and doctor exist, and the report ID is unique
    public void addMedicalReport(String medical_report_id, String patient_id, String doctor_id, String diagnosis, String treatment, String report_date) throws SQLException {
        if (!checkPatientExists(patient_id)) {
            throw new SQLException("Error: Patient ID " + patient_id + " does not exist.");
        }
        if (!checkDoctorExists(doctor_id)) {
            throw new SQLException("Error: Doctor ID " + doctor_id + " does not exist.");
        }
        if (checkMedicalReportIdExists(medical_report_id)) {
            throw new SQLException("Error: Medical report ID " + medical_report_id + " already exists.");
        }
 
        String sql = "INSERT INTO medical_reports (medical_report_id, patient_id, doctor_id, diagnosis, treatment, report_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medical_report_id); // Set the medical report ID
            pstmt.setString(2, patient_id); // Set the patient ID
            pstmt.setString(3, doctor_id); // Set the doctor ID
            pstmt.setString(4, diagnosis); // Set the diagnosis
            pstmt.setString(5, treatment); // Set the treatment
            pstmt.setDate(6, Date.valueOf(report_date)); // Convert and set the report date
            pstmt.executeUpdate(); // Execute the insert operation
            System.out.println("Medical report added successfully.");
        }
    }
 
    // Method to view a specific medical report based on either medical_report_id or patient_id
    public String viewMedicalReportById(String id, boolean isMedicalReportId) throws SQLException {
        StringBuilder reportDetails = new StringBuilder(); // To store report details
        String sql = "SELECT mr.medical_report_id, mr.patient_id, p.patient_firstName, p.patient_lastName, " +
                     "mr.doctor_id, d.doctor_firstName, d.doctor_lastName, mr.diagnosis, mr.treatment, mr.report_date " +
                     "FROM medical_reports mr " +
                     "JOIN patient p ON mr.patient_id = p.patient_id " +
                     "JOIN doctor d ON mr.doctor_id = d.doctor_id " +
                     "WHERE " + (isMedicalReportId ? "mr.medical_report_id = ?" : "mr.patient_id = ?");
 
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id); // Set the parameter based on ID type
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) { // Check if result set is empty
                    return "No medical report found for the provided ID.";
                }
 
                // Append each report's details to the StringBuilder
                while (rs.next()) {
                    reportDetails.append("Report ID: ").append(rs.getString("medical_report_id")).append("\n")
                            .append("Patient ID: ").append(rs.getString("patient_id")).append("\n")
                            .append("Patient Name: ").append(rs.getString("patient_firstName")).append(" ").append(rs.getString("patient_lastName")).append("\n")
                            .append("Doctor ID: ").append(rs.getString("doctor_id")).append("\n")
                            .append("Doctor Name: ").append(rs.getString("doctor_firstName")).append(" ").append(rs.getString("doctor_lastName")).append("\n")
                            .append("Diagnosis: ").append(rs.getString("diagnosis")).append("\n")
                            .append("Treatment: ").append(rs.getString("treatment")).append("\n")
                            .append("Report Date: ").append(rs.getDate("report_date")).append("\n")
                            .append("------\n");
                }
            }
        }
        return reportDetails.toString(); // Return the report details as a string
    }
 
    // Method to update an existing medical report by its ID
    public void updateMedicalReport(String medical_report_id, String diagnosis, String treatment) throws SQLException {
        if (!checkMedicalReportIdExists(medical_report_id)) {
            throw new SQLException("Error: Medical report with ID " + medical_report_id + " not found.");
        }
 
        String sql = "UPDATE medical_reports SET diagnosis = ?, treatment = ? WHERE medical_report_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, diagnosis); // Set the new diagnosis
            pstmt.setString(2, treatment); // Set the new treatment
            pstmt.setString(3, medical_report_id); // Set the medical report ID
            int rowsAffected = pstmt.executeUpdate(); // Execute the update
            if (rowsAffected > 0) {
                System.out.println("Medical report updated successfully.");
            } else {
                System.out.println("Error: Medical report with ID " + medical_report_id + " not found.");
            }
        }
    }
 
    // Method to delete a medical report by its ID
    public void deleteMedicalReport(String medical_report_id) throws SQLException {
        String sql = "DELETE FROM medical_reports WHERE medical_report_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medical_report_id); // Set the medical report ID
            int rowsAffected = pstmt.executeUpdate(); // Execute the delete
            if (rowsAffected > 0) {
                System.out.println("Medical report deleted successfully.");
            } else {
                System.out.println("Error: Medical report with ID " + medical_report_id + " not found.");
            }
        }
    }
}
