/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hospital_reception.UI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectdb {
    private static final String URL = "jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL"; // Correct database URL
    private static final String USER = "root"; // Replace with your database username
    private static final String PASSWORD = "Jeffloh0329."; // Replace with your database password

    // Method to establish and return a connection
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Explicitly load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please add it to your library path.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        }
        return connection;
    }

    // Optional: Method to close the connection
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed successfully.");
            } catch (SQLException e) {
                System.err.println("Failed to close the connection: " + e.getMessage());
            }
        }
    }

    // Main method to test the connection
    public static void main(String[] args) {
        // Test the connection
        Connection connection = getConnection();
        if (connection != null) {
            System.out.println("Connection to database was successful!");
            closeConnection(connection);
        } else {
            System.err.println("Connection to database failed.");
        }
    }
}