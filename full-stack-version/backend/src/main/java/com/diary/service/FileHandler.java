package com.diary.service;

import com.diary.dao.DiaryEntryDAO;
import com.diary.model.DiaryEntry;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * FileHandler - Core Java File Handling (Mandatory College Feature)
 * Features: Backup (.txt), Export (.csv), Import (.csv), List Backups
 */
public class FileHandler {

    private static final String BACKUP_DIR = "backups";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private final DiaryEntryDAO entryDAO = new DiaryEntryDAO();

    public FileHandler() {
        try {
            Files.createDirectories(Paths.get(BACKUP_DIR));
            System.out.println("[FileHandler] Backup directory ready: " + BACKUP_DIR);
        } catch (IOException e) {
            System.err.println("[FileHandler] Could not create backup directory: " + e.getMessage());
        }
    }

    // ── EXPORT TO TXT (Backup) ────────────────────────────────────────────────
    public String exportToTxt(int userId) throws Exception {
        List<DiaryEntry> entries = entryDAO.getAllEntries(userId);
        StringBuilder sb = new StringBuilder();
        sb.append("=============================================================\n");
        sb.append("     PERSONAL DIARY MANAGEMENT SYSTEM - BACKUP\n");
        sb.append("=============================================================\n");
        sb.append("Backup Date  : ").append(LocalDateTime.now().format(FMT)).append("\n");
        sb.append("User ID      : ").append(userId).append("\n");
        sb.append("Total Entries: ").append(entries.size()).append("\n");
        sb.append("=============================================================\n\n");

        for (int i = 0; i < entries.size(); i++) {
            DiaryEntry e = entries.get(i);
            sb.append("ENTRY #").append(i + 1).append("\n");
            sb.append("ID       : ").append(e.getId()).append("\n");
            sb.append("Title    : ").append(e.getTitle()).append("\n");
            sb.append("Date     : ").append(e.getEntryDate()).append("\n");
            sb.append("Mood     : ").append(e.getMood()).append("\n");
            sb.append("Category : ").append(e.getCategory()).append("\n");
            sb.append("Favorite : ").append(e.isFavorite() ? "Yes" : "No").append("\n");
            sb.append("Content  :\n").append(e.getContent()).append("\n");
            sb.append("-------------------------------------------------------------\n\n");
        }

        // Write to local backup file
        String filePath = BACKUP_DIR + "/backup_user" + userId + "_" + LocalDateTime.now().format(FMT) + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(sb.toString());
        }
        System.out.println("[FileHandler] TXT Backup saved: " + filePath);
        return sb.toString();
    }

    // ── EXPORT TO CSV ────────────────────────────────────────────────────────
    public String exportToCsv(int userId) throws Exception {
        List<DiaryEntry> entries = entryDAO.getAllEntries(userId);
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Title,Date,Mood,Category,Favorite,Content\n");
        for (DiaryEntry e : entries) {
            sb.append(e.getId()).append(",");
            sb.append(escapeCsv(e.getTitle())).append(",");
            sb.append(e.getEntryDate()).append(",");
            sb.append(escapeCsv(e.getMood())).append(",");
            sb.append(escapeCsv(e.getCategory())).append(",");
            sb.append(e.isFavorite() ? "Yes" : "No").append(",");
            sb.append(escapeCsv(e.getContent())).append("\n");
        }

        String filePath = BACKUP_DIR + "/export_user" + userId + "_" + LocalDateTime.now().format(FMT) + ".csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.print(sb.toString());
        }
        System.out.println("[FileHandler] CSV Export saved: " + filePath);
        return sb.toString();
    }

    // ── IMPORT FROM CSV ──────────────────────────────────────────────────────
    public int importFromCsv(String csvContent, int userId) throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(csvContent));
        String line;
        int count = 0;
        boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if (firstLine) { firstLine = false; continue; }
            if (line.trim().isEmpty()) continue;
            String[] parts = parseCsvLine(line);
            if (parts.length < 6) continue;
            DiaryEntry entry = new DiaryEntry();
            entry.setUserId(userId);
            entry.setTitle(parts[1].trim());
            entry.setMood(parts[3].trim());
            entry.setCategory(parts[4].trim());
            entry.setFavorite(parts[5].trim().equalsIgnoreCase("Yes"));
            entry.setContent(parts.length > 6 ? parts[6].trim() : "");
            if (entryDAO.addEntry(entry)) count++;
        }
        reader.close();

        // Log import
        String logPath = BACKUP_DIR + "/import_log_user" + userId + "_" + LocalDateTime.now().format(FMT) + ".txt";
        try (BufferedWriter w = new BufferedWriter(new FileWriter(logPath))) {
            w.write("Import completed. Entries: " + count + "\nTime: " + LocalDateTime.now());
        }
        System.out.println("[FileHandler] Imported " + count + " entries.");
        return count;
    }

    // ── LIST BACKUPS ─────────────────────────────────────────────────────────
    public String listBackups(int userId) {
        File dir = new File(BACKUP_DIR);
        if (!dir.exists()) return "No backups found.";
        File[] files = dir.listFiles((d, name) -> name.contains("user" + userId) && (name.endsWith(".txt") || name.endsWith(".csv")));
        if (files == null || files.length == 0) return "No backups found.";
        StringBuilder sb = new StringBuilder();
        for (File f : files) {
            sb.append(f.getName()).append(" (").append(f.length() / 1024 + 1).append(" KB)\n");
        }
        return sb.toString();
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────
    private String escapeCsv(String v) {
        if (v == null) return "";
        v = v.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\n") || v.contains("\"")) v = "\"" + v + "\"";
        return v;
    }

    private String[] parseCsvLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
