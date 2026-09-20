package com.diary.dao;

import com.diary.database.DatabaseConnection;
import com.diary.model.DiaryEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO = Data Access Object
// All database operations for diary_entries table
public class DiaryEntryDAO {

    // INSERT - Add new entry
    public boolean addEntry(DiaryEntry entry) throws SQLException {
        String sql = "INSERT INTO diary_entries (user_id, title, content, mood, category, is_favorite, entry_date) VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, entry.getUserId());
        stmt.setString(2, entry.getTitle());
        stmt.setString(3, entry.getContent());
        stmt.setString(4, entry.getMood());
        stmt.setString(5, entry.getCategory());
        stmt.setBoolean(6, entry.isFavorite());
        int rows = stmt.executeUpdate();
        return rows > 0;
    }

    // SELECT ALL - Get all entries for a user
    public List<DiaryEntry> getAllEntries(int userId) throws SQLException {
        List<DiaryEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM diary_entries WHERE user_id = ? ORDER BY created_at DESC";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            DiaryEntry entry = new DiaryEntry();
            entry.setId(rs.getInt("id"));
            entry.setUserId(rs.getInt("user_id"));
            entry.setTitle(rs.getString("title"));
            entry.setContent(rs.getString("content"));
            entry.setMood(rs.getString("mood"));
            entry.setCategory(rs.getString("category"));
            entry.setFavorite(rs.getBoolean("is_favorite"));
            entry.setEntryDate(rs.getString("entry_date"));
            entry.setCreatedAt(rs.getString("created_at"));
            entries.add(entry);
        }
        return entries;
    }

    // SELECT ONE - Get entry by id
    public DiaryEntry getEntryById(int id, int userId) throws SQLException {
        String sql = "SELECT * FROM diary_entries WHERE id = ? AND user_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.setInt(2, userId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            DiaryEntry entry = new DiaryEntry();
            entry.setId(rs.getInt("id"));
            entry.setUserId(rs.getInt("user_id"));
            entry.setTitle(rs.getString("title"));
            entry.setContent(rs.getString("content"));
            entry.setMood(rs.getString("mood"));
            entry.setCategory(rs.getString("category"));
            entry.setFavorite(rs.getBoolean("is_favorite"));
            entry.setEntryDate(rs.getString("entry_date"));
            return entry;
        }
        return null;
    }

    // UPDATE - Edit entry
    public boolean updateEntry(DiaryEntry entry) throws SQLException {
        String sql = "UPDATE diary_entries SET title=?, content=?, mood=?, category=?, is_favorite=?, updated_at=CURRENT_TIMESTAMP WHERE id=? AND user_id=?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, entry.getTitle());
        stmt.setString(2, entry.getContent());
        stmt.setString(3, entry.getMood());
        stmt.setString(4, entry.getCategory());
        stmt.setBoolean(5, entry.isFavorite());
        stmt.setInt(6, entry.getId());
        stmt.setInt(7, entry.getUserId());
        return stmt.executeUpdate() > 0;
    }

    // DELETE - Remove entry
    public boolean deleteEntry(int id, int userId) throws SQLException {
        String sql = "DELETE FROM diary_entries WHERE id = ? AND user_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.setInt(2, userId);
        return stmt.executeUpdate() > 0;
    }

    // SEARCH - Search by keyword
    public List<DiaryEntry> searchEntries(int userId, String keyword) throws SQLException {
        List<DiaryEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM diary_entries WHERE user_id=? AND (LOWER(title) LIKE ? OR LOWER(content) LIKE ? OR LOWER(mood) LIKE ?)";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        String kw = "%" + keyword.toLowerCase() + "%";
        stmt.setInt(1, userId);
        stmt.setString(2, kw);
        stmt.setString(3, kw);
        stmt.setString(4, kw);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            DiaryEntry entry = new DiaryEntry();
            entry.setId(rs.getInt("id"));
            entry.setTitle(rs.getString("title"));
            entry.setContent(rs.getString("content"));
            entry.setMood(rs.getString("mood"));
            entry.setCategory(rs.getString("category"));
            entries.add(entry);
        }
        return entries;
    }
}