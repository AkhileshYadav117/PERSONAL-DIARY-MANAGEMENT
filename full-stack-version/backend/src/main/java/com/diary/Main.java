package com.diary;

import com.diary.api.AuthHandler;
import com.diary.api.DiaryHandler;
import com.diary.api.FileApiHandler;
import com.diary.database.DatabaseConnection;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("==============================================");
        System.out.println("  Personal Diary Management System");
        System.out.println("  Core Java Backend Starting...");
        System.out.println("==============================================");

        System.out.println("[INFO] Connecting to PostgreSQL...");
        if (!DatabaseConnection.testConnection()) {
            System.out.println("[ERROR] Cannot connect to database! Exiting.");
            return;
        }
        System.out.println("[INFO] Database connected!");

        // Read PORT from environment (Render sets this automatically)
        int port = 8080;
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.isEmpty()) {
            port = Integer.parseInt(portEnv);
        }
        System.out.println("[INFO] Starting server on port: " + port);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Auth endpoints
        server.createContext("/api/register", new AuthHandler());
        server.createContext("/api/login", new AuthHandler());

        // Diary CRUD endpoints
        server.createContext("/api/entries", new DiaryHandler());

        // File Handling endpoints (Core Java File I/O)
        FileApiHandler fileApiHandler = new FileApiHandler();
        server.createContext("/api/export/txt", fileApiHandler);
        server.createContext("/api/export/csv", fileApiHandler);
        server.createContext("/api/import", fileApiHandler);
        server.createContext("/api/backups", fileApiHandler);

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("[INFO] Server started on http://localhost:8080");
        System.out.println("[INFO] Endpoints ready:");
        System.out.println("  POST   /api/register");
        System.out.println("  POST   /api/login");
        System.out.println("  GET    /api/entries?userId=1");
        System.out.println("  POST   /api/entries");
        System.out.println("  PUT    /api/entries/{id}");
        System.out.println("  DELETE /api/entries/{id}?userId=1");
        System.out.println("  GET    /api/export/txt?userId=1   [File Handling]");
        System.out.println("  GET    /api/export/csv?userId=1   [File Handling]");
        System.out.println("  POST   /api/import?userId=1       [File Handling]");
        System.out.println("  GET    /api/backups?userId=1      [File Handling]");
        System.out.println("==============================================");
        System.out.println("[INFO] Press Ctrl+C to stop the server");
    }
}