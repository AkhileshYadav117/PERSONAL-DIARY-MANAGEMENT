package com.diary.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// JDBC: Java Database Connectivity
// This class connects Core Java to PostgreSQL
public class DatabaseConnection {

    private static final String URL      = "jdbc:postgresql://localhost:5432/diary_db";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "Akhilesh@112";

    private static Connection connection = null;

    // Get database connection (Singleton pattern)
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("[DB] Connecting to PostgreSQL...");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Connected to diary_db successfully!");
        }
        return connection;
    }

    // Test connection
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
            return false;
        }
    }

    // Close connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }
}