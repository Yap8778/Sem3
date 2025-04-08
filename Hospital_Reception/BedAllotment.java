package com.mycompany.hospital_reception;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeff
 */
public class BedAllotment {
    public List<String> patientAllotBed = new ArrayList<>();
    
    // link to SQL
    public static void main (String[] args) 
            throws SQLException, ClassNotFoundException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("Driver loaded");
        
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL","root","Jeffloh0329.");
        System.out.println("Database Connected");
        connection.close();
    }
    
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL", 
            "root", 
            "Jeffloh0329."
        );
    }
    
    // list of patients without assigned beds
     //source:https://stackoverflow.com/questions/13395114/how-to-initialize-liststring-object-in-java
    public List<String> getAvailablePatientList() throws SQLException {
        List<String> patients = new ArrayList<>();
        //join to get patient name 
        String query = "SELECT inpatient.patient_id, patient.patient_firstName, patient.patient_lastName " +
                       "FROM inpatient " +
                       "JOIN patient ON inpatient.patient_id = patient.patient_id " +
                       "WHERE inpatient.bed_id IS NULL";
        //connect to sql
        try (Connection connection = connect();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query)) {
            while (result.next()) {
                String patientInfo = result.getString("patient_id") + " " + result.getString("patient_firstName") + " " + result.getString("patient_lastName");
                patients.add(patientInfo);
            }
        }
        return patients;
    }
    
    // Assign bed 
    public String bedAllot(String patient_id) throws SQLException {
        //query so will have bedid roomid and bed status = available then update 
        //source: https://www.w3schools.com/sql/sql_top.asp
        //SQL query to find an available bed. LIMIT 1 restricts the result to one bed
        String bedQuery = "SELECT bed_id, room_id FROM bed WHERE status = 'available' LIMIT 1";
         //? in sql represent parameter 
        //source: https://documentation.softwareag.com/webmethods/microservices_container/msc10-5/10-5_MSC_PIE_webhelp/index.html#page/pie-webhelp/to-accessing_databases_with_services_7.html
        //update bed and inpatient table
        String update = "UPDATE bed SET patient_id = ?, status = 'occupied' WHERE bed_id = ?";
        String updateInpatientQuery = "UPDATE inpatient SET room_id = ?, bed_id = ? WHERE patient_id = ?";

        try (Connection connection = connect();
             PreparedStatement findBedStmt = connection.prepareStatement(bedQuery);
             ResultSet results = findBedStmt.executeQuery()) {
            //loop the result until get empty bed and room
            if (results.next()) {
                int bed_id = results.getInt("bed_id");
                int room_id = results.getInt("room_id");

                try (PreparedStatement updateBedStmt = connection.prepareStatement(update)) {
                    updateBedStmt.setString(1, patient_id);
                    updateBedStmt.setInt(2, bed_id);
                    updateBedStmt.executeUpdate();
                }
                //update the table based on the roomid bedid and patientid
                try (PreparedStatement updateInpatientStmt = connection.prepareStatement(updateInpatientQuery)) {
                    updateInpatientStmt.setInt(1, room_id);
                    updateInpatientStmt.setInt(2, bed_id);
                    updateInpatientStmt.setString(3, patient_id);
                    updateInpatientStmt.executeUpdate();
                }
                
                patientAllotBed.add(patient_id); // Track this patient as assigned
                return "Assigned to Room " + room_id + ", Bed " + bed_id;
            } else {
                return "No room available.";
            }
        }
    }

    // list of patients assigned bed for update purpose
    public List<String> getAssignedPatients() throws SQLException {
        List<String> assignedPatients = new ArrayList<>();
        //query using join to get the patient name and use where to get already assigned bed patient
        String query = "SELECT patient.patient_id, patient.patient_firstName, patient.patient_lastName, inpatient.room_id, inpatient.bed_id " +
                       "FROM inpatient " +
                       "JOIN patient ON inpatient.patient_id = patient.patient_id " +
                       "WHERE inpatient.bed_id IS NOT NULL";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(query)) {
            //loop and get the detail
            while (result.next()) {
                String patientInfo = result.getString("patient_id") + " " +
                                     result.getString("patient_firstName") + " " +
                                     result.getString("patient_lastName") + " - Room: " +
                                     result.getInt("room_id") + ", Bed: " + result.getInt("bed_id");
                assignedPatients.add(patientInfo);
            }
        }
        return assignedPatients;
    }
    // reassign bed but the patient should have room already 
    public String updateBedAssignment(String patient_id, int new_bed_id) throws SQLException {
        String checkPatientQuery = "SELECT bed_id FROM inpatient WHERE patient_id = ? AND bed_id IS NOT NULL";
        String checkBedQuery = "SELECT bed_id, room_id FROM bed WHERE bed_id = ? AND status = 'available'";
        String updateBedQuery = "UPDATE bed SET patient_id = ?, status = 'occupied' WHERE bed_id = ?";
        String clearOldBedQuery = "UPDATE bed SET patient_id = NULL, status = 'available' WHERE bed_id = ?";
        String updateInpatientQuery = "UPDATE inpatient SET room_id = ?, bed_id = ? WHERE patient_id = ?";

        try (Connection connection = connect();
             PreparedStatement checkPatientStmt = connection.prepareStatement(checkPatientQuery)) {
            
            checkPatientStmt.setString(1, patient_id);
            try (ResultSet patientResult = checkPatientStmt.executeQuery()) {
                //if the patient dont have bed yet 
                if (!patientResult.next()) {
                    return "Patient " + patient_id + " does not have an assigned bed. Please assign a bed first.";
                }
                
                int oldBedId = patientResult.getInt("bed_id");

                // check the bed id is available or not
                try (PreparedStatement checkBedStmt = connection.prepareStatement(checkBedQuery)) {
                    checkBedStmt.setInt(1, new_bed_id);
                    try (ResultSet bedResult = checkBedStmt.executeQuery()) {
                        if (bedResult.next()) {
                            int room_id = bedResult.getInt("room_id");

                            // delete the recent bed id
                            try (PreparedStatement clearOldBedStmt = connection.prepareStatement(clearOldBedQuery)) {
                                clearOldBedStmt.setInt(1, oldBedId);
                                clearOldBedStmt.executeUpdate();
                            }

                            // update the newest bed id
                            try (PreparedStatement updateBedStmt = connection.prepareStatement(updateBedQuery)) {
                                updateBedStmt.setString(1, patient_id);
                                updateBedStmt.setInt(2, new_bed_id);
                                updateBedStmt.executeUpdate();
                            }

                            // update inpatient information
                            try (PreparedStatement updateInpatientStmt = connection.prepareStatement(updateInpatientQuery)) {
                                updateInpatientStmt.setInt(1, room_id);
                                updateInpatientStmt.setInt(2, new_bed_id);
                                updateInpatientStmt.setString(3, patient_id);
                                updateInpatientStmt.executeUpdate();
                            }

                            return "Patient " + patient_id + " reassigned to Room " + room_id + ", Bed " + new_bed_id;
                        } else {
                            return "Requested bed is not available.";
                        }
                    }
                }
            }
        }
    }
}
