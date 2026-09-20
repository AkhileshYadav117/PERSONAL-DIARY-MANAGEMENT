package com.diary.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// JDBC: Java Database Connectivity
// This class connects Core Java to PostgreSQL
// Uses environment variables for secure deployment (Render/Cloud)
public class DatabaseConnection {

    // Environment variables for deployment (fallback to local for development)
    private static final String URL      = System.getenv("DB_URL")      != null
        ? System.getenv("DB_URL")
        : "jdbc:postgresql://localhost:5432/diary_db";
    private static final String USER     = System.getenv("DB_USER")     != null
        ? System.getenv("DB_USER")
        : "postgres";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null
        ? System.getenv("DB_PASSWORD")
        : "Akhilesh@112";

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