package com.diary.util;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

public class JsonUtil {

    // Read JSON body from HTTP request
    public static JSONObject readBody(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        String body = sb.toString().trim();
        if (body.isEmpty()) return new JSONObject();
        return new JSONObject(body);
    }

    // Create success response
    public static String success(String message) {
        return new JSONObject().put("success", true).put("message", message).toString();
    }

    // Create success response with data
    public static String success(String message, JSONObject data) {
        return new JSONObject().put("success", true).put("message", message).put("data", data).toString();
    }

    // Create error response
    public static String error(String message) {
        return new JSONObject().put("success", false).put("message", message).toString();
    }
}