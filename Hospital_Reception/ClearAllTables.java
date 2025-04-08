package com.mycompany.hospital_reception;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClearAllTables {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/receptionsystem?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String USER = "root";
    private static final String PASSWORD = "Jeffloh0329.";

    public static void main(String[] args) {
        clearAllTables();
    }

    public static void clearAllTables() {
        // 定义需要清空数据的表名称
        String[] tables = {
            "admission", "appointment", "bed", "doctor", "inpatient",
            "insurance_claims", "medical_reports", "outpatient", "patient", "room"
        };

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            // 禁用外键检查，避免删除数据时发生外键约束冲突
            try (PreparedStatement disableFKChecks = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 0")) {
                disableFKChecks.execute();
            }

            // 遍历每个表并清空数据
            for (String table : tables) {
                try (PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE " + table)) {
                    stmt.execute();
                    System.out.println("Cleared data from table: " + table);
                } catch (SQLException e) {
                    System.out.println("Error clearing data from table: " + table);
                    e.printStackTrace();
                }
            }

            // 重新启用外键检查
            try (PreparedStatement enableFKChecks = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 1")) {
                enableFKChecks.execute();
            }

            System.out.println("All specified tables have been cleared.");
        } catch (SQLException e) {
            System.out.println("Database connection error.");
            e.printStackTrace();
        }
    }
}