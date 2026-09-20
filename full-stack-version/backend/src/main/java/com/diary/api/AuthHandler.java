package com.diary.api;

import com.diary.dao.UserDAO;
import com.diary.model.User;
import com.diary.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AuthHandler implements HttpHandler {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        try {
            // CORS headers - allow Flutter to connect
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            if (method.equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            if (path.equals("/api/register") && method.equals("POST")) {
                handleRegister(exchange);
            } else if (path.equals("/api/login") && method.equals("POST")) {
                handleLogin(exchange);
            } else {
                sendResponse(exchange, 404, JsonUtil.error("Endpoint not found"));
            }
        } catch (Exception e) {
            try { sendResponse(exchange, 500, JsonUtil.error("Server error: " + e.getMessage())); }
            catch (Exception ignored) {}
        }
    }

    private void handleRegister(HttpExchange exchange) throws Exception {
        JSONObject body = JsonUtil.readBody(exchange.getRequestBody());
        String name = body.optString("name", "").trim();
        String email = body.optString("email", "").trim();
        String password = body.optString("password", "").trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            sendResponse(exchange, 400, JsonUtil.error("Name, email, and password are required"));
            return;
        }
        if (password.length() < 6) {
            sendResponse(exchange, 400, JsonUtil.error("Password must be at least 6 characters"));
            return;
        }
        if (userDAO.emailExists(email)) {
            sendResponse(exchange, 400, JsonUtil.error("Email already registered"));
            return;
        }

        // Hash password using BCrypt - NEVER store plain text!
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        boolean created = userDAO.registerUser(name, email, hashedPassword);

        if (created) {
            sendResponse(exchange, 201, JsonUtil.success("Registration successful!"));
        } else {
            sendResponse(exchange, 500, JsonUtil.error("Registration failed"));
        }
    }

    private void handleLogin(HttpExchange exchange) throws Exception {
        JSONObject body = JsonUtil.readBody(exchange.getRequestBody());
        String email = body.optString("email", "").trim();
        String password = body.optString("password", "").trim();

        if (email.isEmpty() || password.isEmpty()) {
            sendResponse(exchange, 400, JsonUtil.error("Email and password required"));
            return;
        }

        User user = userDAO.findByEmail(email);
        if (user == null) {
            sendResponse(exchange, 401, JsonUtil.error("Invalid email or password"));
            return;
        }

        // BCrypt password check
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            sendResponse(exchange, 401, JsonUtil.error("Invalid email or password"));
            return;
        }

        JSONObject userData = new JSONObject();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());

        sendResponse(exchange, 200, JsonUtil.success("Login successful!", userData));
    }

    static void sendResponse(HttpExchange exchange, int status, String body) throws Exception {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}