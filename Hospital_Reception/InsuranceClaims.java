/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
 
package com.mycompany.hospital_reception;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class InsuranceClaims {
    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String USER = "root";
    private static final String PASSWORD = "Jeffloh0329.";
 
    // Method to check if a patient with the given ID exists in the database
    private boolean checkPatientExists(String patient_id) throws SQLException {
        // SQL query to check for patient_id in the "patient" table
        String sql = "SELECT 1 FROM patient WHERE patient_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patient_id); // Setting the patient_id in the query
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if a patient with this ID exists, otherwise false
            }
        }
    }
 
    // Method to check if an insurance ID already exists in the database
    private boolean checkInsuranceIdExists(String insurance_id) throws SQLException {
        // SQL query to check for insurance_id in the "insurance_claims" table
        String sql = "SELECT 1 FROM insurance_claims WHERE insurance_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, insurance_id); // Setting the insurance_id in the query
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if this insurance ID exists, otherwise false
            }
        }
    }
 
    // Method to add a new insurance claim, only if the patient exists and insurance ID does not already exist
    public void addInsuranceClaim(String insurance_id, String patient_id, String insurance_provider_name, double amount_coverage, String claim_status, String date_issue) throws SQLException {
        // First, check if the insurance ID already exists in the database
        if (checkInsuranceIdExists(insurance_id)) {
            throw new SQLException("Insurance ID " + insurance_id + " already exists.");
        }
 
        // Check if the patient with patient_id exists in the database
        if (!checkPatientExists(patient_id)) {
            throw new SQLException("Patient ID " + patient_id + " does not exist. Cannot add insurance claim.");
        }
 
        // If both checks pass, add the new insurance claim
        String sql = "INSERT INTO insurance_claims (insurance_id, patient_id, insurance_provider_name, amount_coverage, claim_status, date_issue) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, insurance_id); // Set the insurance_id for the claim
            pstmt.setString(2, patient_id); // Set the patient_id for the claim
            pstmt.setString(3, insurance_provider_name); // Set the insurance provider name
            pstmt.setDouble(4, amount_coverage); // Set the coverage amount
            pstmt.setString(5, claim_status); // Set the claim status
            pstmt.setString(6, date_issue); // Set the issue date for the claim
            pstmt.executeUpdate(); // Execute the query to insert the new claim
        }
    }
 
    // Method to view all insurance claims in the database
    public String viewInsuranceClaims() throws SQLException {
        StringBuilder claims = new StringBuilder("Insurance Claims:\n"); // StringBuilder to hold the output
        String sql = "SELECT * FROM insurance_claims";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // If there are no records, return a message saying so
            if (!rs.isBeforeFirst()) { // Check if result set is empty
                return "No insurance claims found in the database.\n";
            }
            // Loop through each record in the result set
            while (rs.next()) {
                claims.append("Claim ID: ").append(rs.getString("insurance_id"))
                      .append(", Patient ID: ").append(rs.getString("patient_id"))
                      .append(", Provider Name: ").append(rs.getString("insurance_provider_name"))
                      .append(", Amount Coverage: ").append(rs.getDouble("amount_coverage"))
                      .append(", Claim Status: ").append(rs.getString("claim_status"))
                      .append(", Issue Date: ").append(rs.getDate("date_issue"))
                      .append("\n"); // Append each field to the output
            }
        }
        return claims.toString(); // Return all claims as a single string
    }
 
    // Method to update an existing insurance claim by insurance ID
    public void updateInsuranceClaim(String insurance_id, String claim_status, double amount_coverage) throws SQLException {
        String sql = "UPDATE insurance_claims SET claim_status = ?, amount_coverage = ? WHERE insurance_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, claim_status); // Set the new claim status
            pstmt.setDouble(2, amount_coverage); // Set the new amount coverage
            pstmt.setString(3, insurance_id); // Specify which insurance claim to update by ID
            int rowsAffected = pstmt.executeUpdate(); // Execute the update
            if (rowsAffected == 0) { // If no rows were affected, the insurance ID was not found
                throw new SQLException("Insurance claim with ID " + insurance_id + " not found.");
            }
        }
    }
 
    // Method to delete an insurance claim by insurance ID
    public void deleteInsuranceClaim(String insurance_id) throws SQLException {
        String sql = "DELETE FROM insurance_claims WHERE insurance_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, insurance_id); // Specify the insurance claim to delete by ID
            int rowsAffected = pstmt.executeUpdate(); // Execute the delete
            if (rowsAffected == 0) { // If no rows were affected, the insurance ID was not found
                throw new SQLException("Insurance claim with ID " + insurance_id + " not found.");
            }
        }
    }
}

