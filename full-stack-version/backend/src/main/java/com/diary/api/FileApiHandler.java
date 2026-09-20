package com.diary.api;

import com.diary.service.FileHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * FileApiHandler - REST API endpoints for File Handling
 * GET  /api/export/txt?userId=1  → TXT Backup download
 * GET  /api/export/csv?userId=1  → CSV Export download
 * POST /api/import?userId=1      → Import CSV
 * GET  /api/backups?userId=1     → List backup files
 */
public class FileApiHandler implements HttpHandler {

    private final FileHandler fileHandler = new FileHandler();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equals(method) && path.endsWith("/export/txt")) {
                handleExportTxt(exchange, query);
            } else if ("GET".equals(method) && path.endsWith("/export/csv")) {
                handleExportCsv(exchange, query);
            } else if ("POST".equals(method) && path.endsWith("/import")) {
                handleImport(exchange, query);
            } else if ("GET".equals(method) && path.endsWith("/backups")) {
                handleListBackups(exchange, query);
            } else {
                sendJson(exchange, 404, "{\"success\":false,\"message\":\"Not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Server error: " + e.getMessage() + "\"}");
        }
    }

    private void handleExportTxt(HttpExchange exchange, String query) throws Exception {
        int userId = getUserId(query);
        if (userId == -1) { sendJson(exchange, 400, "{\"success\":false,\"message\":\"Missing userId\"}"); return; }
        String content = fileHandler.exportToTxt(userId);
        sendFile(exchange, content, "text/plain", "diary_backup.txt");
    }

    private void handleExportCsv(HttpExchange exchange, String query) throws Exception {
        int userId = getUserId(query);
        if (userId == -1) { sendJson(exchange, 400, "{\"success\":false,\"message\":\"Missing userId\"}"); return; }
        String content = fileHandler.exportToCsv(userId);
        sendFile(exchange, content, "text/csv", "diary_export.csv");
    }

    private void handleImport(HttpExchange exchange, String query) throws Exception {
        int userId = getUserId(query);
        if (userId == -1) { sendJson(exchange, 400, "{\"success\":false,\"message\":\"Missing userId\"}"); return; }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (body.isEmpty()) { sendJson(exchange, 400, "{\"success\":false,\"message\":\"Empty CSV\"}"); return; }
        int count = fileHandler.importFromCsv(body, userId);
        sendJson(exchange, 200, "{\"success\":true,\"message\":\"Imported " + count + " entries\",\"count\":" + count + "}");
    }

    private void handleListBackups(HttpExchange exchange, String query) throws Exception {
        int userId = getUserId(query);
        if (userId == -1) { sendJson(exchange, 400, "{\"success\":false,\"message\":\"Missing userId\"}"); return; }
        String list = fileHandler.listBackups(userId);
        JSONObject res = new JSONObject();
        res.put("success", true);
        res.put("backups", list);
        sendJson(exchange, 200, res.toString());
    }

    private void sendFile(HttpExchange exchange, String content, String contentType, String filename) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", contentType + "; charset=utf-8");
        exchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private void sendJson(HttpExchange exchange, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private int getUserId(String query) {
        if (query == null) return -1;
        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if (kv.length == 2 && kv[0].equals("userId")) {
                try { return Integer.parseInt(kv[1]); } catch (NumberFormatException e) { return -1; }
            }
        }
        return -1;
    }
}
