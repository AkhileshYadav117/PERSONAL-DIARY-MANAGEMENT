import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DiaryManager {

    // ─────────────────────────────────────────────
    // Generate next unique ID
    // Rule: max existing ID + 1
    // ─────────────────────────────────────────────
    public static int generateNextId() {
        ArrayList<DiaryEntry> entries = FileHandler.readAllEntries();

        int maxId = 0;
        for (DiaryEntry entry : entries) {
            if (entry.getId() > maxId) {
                maxId = entry.getId();
            }
        }
        return maxId + 1;
    }

    // ─────────────────────────────────────────────
    // Get today's date automatically
    // Format: DD-MM-YYYY  e.g. 19-09-2026
    // ─────────────────────────────────────────────
    public static String getTodayDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return today.format(formatter);
    }

    // ─────────────────────────────────────────────
    // ADD a new diary entry
    // Returns: true if added, false if validation fails
    // ─────────────────────────────────────────────
    public static boolean addEntry(String title, String content, String mood) {

        // Validation — check for empty fields
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        if (mood == null || mood.trim().isEmpty()) {
            return false;
        }

        // Validation — prevent | character (breaks file format)
        if (title.contains("|") || content.contains("|") || mood.contains("|")) {
            return false;
        }

        // Generate ID and today's date automatically
        int id = generateNextId();
        String date = getTodayDate();

        // Create new entry object
        DiaryEntry entry = new DiaryEntry(id, date, title.trim(), content.trim(), mood.trim());

        // Save to file
        FileHandler.appendEntry(entry);
        return true;
    }

    // ─────────────────────────────────────────────
    // GET all diary entries
    // ─────────────────────────────────────────────
    public static ArrayList<DiaryEntry> getAllEntries() {
        return FileHandler.readAllEntries();
    }

    // ─────────────────────────────────────────────
    // SEARCH entries by keyword
    // Searches in: title and content (case-insensitive)
    // ─────────────────────────────────────────────
    public static ArrayList<DiaryEntry> searchEntries(String keyword) {
        ArrayList<DiaryEntry> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }

        String lowerKeyword = keyword.toLowerCase().trim();
        ArrayList<DiaryEntry> allEntries = FileHandler.readAllEntries();

        for (DiaryEntry entry : allEntries) {
            boolean titleMatch = entry.getTitle().toLowerCase().contains(lowerKeyword);
            boolean contentMatch = entry.getContent().toLowerCase().contains(lowerKeyword);
            boolean moodMatch = entry.getMood().toLowerCase().contains(lowerKeyword);

            if (titleMatch || contentMatch || moodMatch) {
                results.add(entry);
            }
        }

        return results;
    }

    // ─────────────────────────────────────────────
    // UPDATE an existing entry by ID
    // Returns: true if updated, false if ID not found
    // ─────────────────────────────────────────────
    public static boolean updateEntry(int id, String newTitle, String newContent, String newMood) {

        // Validation
        if (newTitle == null || newTitle.trim().isEmpty()) return false;
        if (newContent == null || newContent.trim().isEmpty()) return false;
        if (newMood == null || newMood.trim().isEmpty()) return false;
        if (newTitle.contains("|") || newContent.contains("|") || newMood.contains("|")) return false;

        ArrayList<DiaryEntry> entries = FileHandler.readAllEntries();
        boolean found = false;

        // Find the entry with matching ID and update it
        for (DiaryEntry entry : entries) {
            if (entry.getId() == id) {
                entry.setTitle(newTitle.trim());
                entry.setContent(newContent.trim());
                entry.setMood(newMood.trim());
                found = true;
                break;
            }
        }

        // If found, rewrite the entire file with updated data
        if (found) {
            FileHandler.rewriteAllEntries(entries);
        }

        return found;
    }

    // ─────────────────────────────────────────────
    // DELETE an entry by ID
    // Returns: true if deleted, false if ID not found
    // ─────────────────────────────────────────────
    public static boolean deleteEntry(int id) {
        ArrayList<DiaryEntry> entries = FileHandler.readAllEntries();
        ArrayList<DiaryEntry> updatedEntries = new ArrayList<>();
        boolean found = false;

        // Keep all entries EXCEPT the one with matching ID
        for (DiaryEntry entry : entries) {
            if (entry.getId() == id) {
                found = true; // skip this one — it's being deleted
            } else {
                updatedEntries.add(entry); // keep this one
            }
        }

        // Rewrite file without the deleted entry
        if (found) {
            FileHandler.rewriteAllEntries(updatedEntries);
        }

        return found;
    }

    // ─────────────────────────────────────────────
    // GET total count of entries
    // ─────────────────────────────────────────────
    public static int getTotalEntries() {
        return FileHandler.readAllEntries().size();
    }
}
