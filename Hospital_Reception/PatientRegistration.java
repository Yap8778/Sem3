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

public class PatientRegistration {
    // Attributes representing columns in the 'patient' table
    private String patient_id;
    private String patient_firstName;
    private String patient_lastName;
    private int age;
    private String gender;
    private String address;
    private String contactNumber;
    
    // Constructor to initialize a PatientRegistration object with given values
    public PatientRegistration(String patient_id, String patient_firstName, String patient_lastName, 
                               int age, String gender, String address, String contactNumber) {
        this.patient_id = patient_id;
        this.patient_firstName = patient_firstName;
        this.patient_lastName = patient_lastName;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.contactNumber = contactNumber;
    }
    
    // Method to check if a patient is registered based on the patient_id
    public static boolean isPatientRegistered(String patient_id) {
        String sql = "SELECT patient_id FROM patient WHERE patient_id = ?";
        try (Connection conn = connectdb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patient_id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();  // Returns true if patient exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Assume patient is not registered if an error occurs
        }
    }
    
    // Insert - Method to add a new patient to the database
    public String insertPatient() {
        String sql = "INSERT INTO patient (patient_id, patient_firstName, patient_lastName, age, gender, address, contactNumber) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectdb.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Setting parameters for the insert query
            pstmt.setString(1, patient_id);
            pstmt.setString(2, patient_firstName);
            pstmt.setString(3, patient_lastName);
            pstmt.setInt(4, age);
            pstmt.setString(5, gender);
            pstmt.setString(6, address);
            pstmt.setString(7, contactNumber);
            pstmt.executeUpdate(); // Execute the insert operation
            return "Patient inserted successfully.";
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {  // Check if the error is a duplicate entry
                return "Patient ID has been duplicated. Please use a different ID.";
            } else {
                return "Insert Failed: " + e.getMessage();
            }
        }
    }

    // Read - Method to retrieve a patient's details by patient_id
    public static PatientRegistration viewPatient(String id) {
        String sql = "SELECT * FROM patient WHERE patient_id = ?";
        try (Connection conn = connectdb.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set patient_id parameter in the query
            pstmt.setString(1, id); 
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Create and return a PatientRegistration object if found
                return new PatientRegistration(
                        rs.getString("patient_id"),
                        rs.getString("patient_firstName"),
                        rs.getString("patient_lastName"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("contactNumber")
                );
            } else {
                System.out.println("Patient not found.");
            }
        } catch (SQLException e) {
            System.err.println("Read Failed: " + e.getMessage());
        }
        return null;
    }

    // Update - Method to edit an existing patient's information
    public String updatePatient() {
        String sql = "UPDATE patient SET patient_firstName = ?, patient_lastName = ?, age = ?, gender = ?, address = ?, contactNumber = ? WHERE patient_id = ?";
        try (Connection conn = connectdb.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Setting parameters for the update query
            pstmt.setString(1, patient_firstName);
            pstmt.setString(2, patient_lastName);
            pstmt.setInt(3, age);
            pstmt.setString(4, gender);
            pstmt.setString(5, address);
            pstmt.setString(6, contactNumber);
            pstmt.setString(7, patient_id);
            int rows = pstmt.executeUpdate(); // Execute the update operation
            if (rows > 0) {
                return "Patient updated successfully.";
            } else {
                return "Patient ID not found.";
            }
        } catch (SQLException e) {
            return "Update Failed: " + e.getMessage();
        }
    }

    // Delete - Method to remove a patient from the database by patient_id
    // It also deletes related records in the appointment and admission tables
    public String deletePatient() {
        String deleteAppointmentsSql = "DELETE FROM appointment WHERE patient_id = ?";
        String deleteAdmissionsSql = "DELETE FROM admission WHERE patient_id = ?";
        String deleteBedSql = "DELETE FROM bed WHERE patient_id = ?";
        String deleteInpatientSql = "DELETE FROM inpatient WHERE patient_id = ?";
        String deleteOutpatientSql = "DELETE FROM outpatient WHERE patient_id = ?";
        String deleteInsuranceClaimsSql = "DELETE FROM insurance_claims WHERE patient_id = ?";
        String deleteMedicalReportsSql = "DELETE FROM medical_reports WHERE patient_id = ?";
        String deletePatientSql = "DELETE FROM patient WHERE patient_id = ?";

        try (Connection conn = connectdb.getConnection()) {
            conn.setAutoCommit(false); // Start transaction to ensure all deletions successful

            // Delete appointment records if any from appointment table
            try (PreparedStatement pstmtAppointment = conn.prepareStatement(deleteAppointmentsSql)) {
                pstmtAppointment.setString(1, patient_id);
                pstmtAppointment.executeUpdate();
            }

            // Delete admission records if any from admission table
            try (PreparedStatement pstmtAdmission = conn.prepareStatement(deleteAdmissionsSql)) {
                pstmtAdmission.setString(1, patient_id);
                pstmtAdmission.executeUpdate();
            }
            
            try (PreparedStatement pstmtBed = conn.prepareStatement(deleteBedSql)) {
                pstmtBed.setString(1, patient_id);
                pstmtBed.executeUpdate();
            }
            
            try (PreparedStatement pstmtInpatient = conn.prepareStatement(deleteInpatientSql)) {
                pstmtInpatient.setString(1, patient_id);
                pstmtInpatient.executeUpdate();
            }

            try (PreparedStatement pstmtOutpatient = conn.prepareStatement(deleteOutpatientSql)) {
                pstmtOutpatient.setString(1, patient_id);
                pstmtOutpatient.executeUpdate();
            }
            
            try (PreparedStatement pstmtInsurance = conn.prepareStatement(deleteInsuranceClaimsSql)) {
                pstmtInsurance.setString(1, patient_id);
                pstmtInsurance.executeUpdate();
            }
            
            try (PreparedStatement pstmtMedicalReport = conn.prepareStatement(deleteMedicalReportsSql)) {
                pstmtMedicalReport.setString(1, patient_id);
                pstmtMedicalReport.executeUpdate();
            }
            
            // Delete from patient table
            try (PreparedStatement pstmtPatient = conn.prepareStatement(deletePatientSql)) {
                pstmtPatient.setString(1, patient_id);
                int rows = pstmtPatient.executeUpdate();
                if (rows > 0) {
                    conn.commit(); // Commit transaction if all deletions succeed
                    return "Patient deleted successfully.";
                } else {
                    conn.rollback(); // Rollback if patient ID not found
                    return "Patient ID not found.";
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Delete Failed: " + e.getMessage();
        }
    }

    // Getters for each field to access private fields
    public String getPatientId() { return patient_id; }
    public String getPatientFirstName() { return patient_firstName; }
    public String getPatientLastName() { return patient_lastName; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getContactNumber() { return contactNumber; }

    // Setters for each field to modify private fields
    public void setPatientFirstName(String patient_firstName) { this.patient_firstName = patient_firstName; }
    public void setPatientLastName(String patient_lastName) { this.patient_lastName = patient_lastName; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAddress(String address) { this.address = address; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}
