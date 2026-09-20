package com.diary.api;

import com.diary.dao.DiaryEntryDAO;
import com.diary.model.DiaryEntry;
import com.diary.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DiaryHandler implements HttpHandler {

    private final DiaryEntryDAO dao = new DiaryEntryDAO();

    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        try {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            if (method.equals("OPTIONS")) { exchange.sendResponseHeaders(200, -1); return; }

            // GET /api/entries/search?userId=1&keyword=happy
            if (path.equals("/api/entries/search") && method.equals("GET")) {
                handleSearch(exchange, query); return;
            }

            // GET /api/entries?userId=1
            if (path.equals("/api/entries") && method.equals("GET")) {
                handleGetAll(exchange, query); return;
            }

            // POST /api/entries
            if (path.equals("/api/entries") && method.equals("POST")) {
                handleAdd(exchange); return;
            }

            // PUT /api/entries/{id}
            if (path.startsWith("/api/entries/") && method.equals("PUT")) {
                int id = Integer.parseInt(path.split("/")[3]);
                handleUpdate(exchange, id); return;
            }

            // DELETE /api/entries/{id}
            if (path.startsWith("/api/entries/") && method.equals("DELETE")) {
                int id = Integer.parseInt(path.split("/")[3]);
                handleDelete(exchange, id, query); return;
            }

            AuthHandler.sendResponse(exchange, 404, JsonUtil.error("Not found"));
        } catch (Exception e) {
            try { AuthHandler.sendResponse(exchange, 500, JsonUtil.error("Error: " + e.getMessage())); }
            catch (Exception ignored) {}
        }
    }

    private void handleGetAll(HttpExchange ex, String query) throws Exception {
        int userId = getUserId(query);
        List<DiaryEntry> entries = dao.getAllEntries(userId);
        JSONArray arr = new JSONArray();
        for (DiaryEntry e : entries) arr.put(entryToJson(e));
        JSONObject res = new JSONObject();
        res.put("success", true);
        res.put("entries", arr);
        res.put("count", entries.size());
        AuthHandler.sendResponse(ex, 200, res.toString());
    }

    private void handleAdd(HttpExchange ex) throws Exception {
        JSONObject body = JsonUtil.readBody(ex.getRequestBody());
        DiaryEntry entry = new DiaryEntry();
        entry.setUserId(body.optInt("userId", 0));
        entry.setTitle(body.optString("title", ""));
        entry.setContent(body.optString("content", ""));
        entry.setMood(body.optString("mood", "Happy"));
        entry.setCategory(body.optString("category", "General"));
        entry.setFavorite(body.optBoolean("isFavorite", false));

        if (entry.getTitle().isEmpty()) {
            AuthHandler.sendResponse(ex, 400, JsonUtil.error("Title is required"));
            return;
        }
        boolean added = dao.addEntry(entry);
        AuthHandler.sendResponse(ex, added ? 201 : 500,
            added ? JsonUtil.success("Entry added!") : JsonUtil.error("Failed to add entry"));
    }

    private void handleUpdate(HttpExchange ex, int id) throws Exception {
        JSONObject body = JsonUtil.readBody(ex.getRequestBody());
        DiaryEntry entry = new DiaryEntry();
        entry.setId(id);
        entry.setUserId(body.optInt("userId", 0));
        entry.setTitle(body.optString("title", ""));
        entry.setContent(body.optString("content", ""));
        entry.setMood(body.optString("mood", "Happy"));
        entry.setCategory(body.optString("category", "General"));
        entry.setFavorite(body.optBoolean("isFavorite", false));
        boolean updated = dao.updateEntry(entry);
        AuthHandler.sendResponse(ex, updated ? 200 : 404,
            updated ? JsonUtil.success("Entry updated!") : JsonUtil.error("Entry not found"));
    }

    private void handleDelete(HttpExchange ex, int id, String query) throws Exception {
        int userId = getUserId(query);
        boolean deleted = dao.deleteEntry(id, userId);
        AuthHandler.sendResponse(ex, deleted ? 200 : 404,
            deleted ? JsonUtil.success("Entry deleted!") : JsonUtil.error("Entry not found"));
    }

    private void handleSearch(HttpExchange ex, String query) throws Exception {
        int userId = getUserId(query);
        String keyword = getParam(query, "keyword");
        List<DiaryEntry> entries = dao.searchEntries(userId, keyword);
        JSONArray arr = new JSONArray();
        for (DiaryEntry e : entries) arr.put(entryToJson(e));
        JSONObject res = new JSONObject();
        res.put("success", true);
        res.put("entries", arr);
        AuthHandler.sendResponse(ex, 200, res.toString());
    }

    private JSONObject entryToJson(DiaryEntry e) {
        JSONObject j = new JSONObject();
        j.put("id", e.getId());
        j.put("userId", e.getUserId());
        j.put("title", e.getTitle());
        j.put("content", e.getContent());
        j.put("mood", e.getMood());
        j.put("category", e.getCategory());
        j.put("isFavorite", e.isFavorite());
        j.put("entryDate", e.getEntryDate() != null ? e.getEntryDate() : "");
        j.put("createdAt", e.getCreatedAt() != null ? e.getCreatedAt() : "");
        return j;
    }

    private int getUserId(String query) {
        return query != null ? Integer.parseInt(getParam(query, "userId")) : 0;
    }

    private String getParam(String query, String key) {
        if (query == null) return "";
        for (String p : query.split("&")) {
            String[] kv = p.split("=");
            if (kv.length == 2 && kv[0].equals(key)) return kv[1];
        }
        return "";
    }
}