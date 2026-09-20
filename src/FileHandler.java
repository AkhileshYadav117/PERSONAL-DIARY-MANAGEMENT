import java.io.*;
import java.util.ArrayList;

public class FileHandler {

    // Path to the diary file — relative path works from any computer
    private static final String FILE_PATH = "data/diary.txt";

    // ─────────────────────────────────────────────
    // 1. Ensure the file exists (called at startup)
    // ─────────────────────────────────────────────
    public static void ensureFileExists() {
        File file = new File(FILE_PATH);

        // Create the 'data' folder if it doesn't exist
        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        // Create diary.txt if it doesn't exist
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // 2. Read all entries from the file
    //    Returns: ArrayList of DiaryEntry objects
    // ─────────────────────────────────────────────
    public static ArrayList<DiaryEntry> readAllEntries() {
        ArrayList<DiaryEntry> entries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) continue;

                // Split the line by | delimiter
                String[] parts = line.split("\\|");

                // A valid line must have exactly 5 parts
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0]);
                    String date = parts[1];
                    String title = parts[2];
                    String content = parts[3];
                    String mood = parts[4];

                    entries.add(new DiaryEntry(id, date, title, content, mood));
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return entries;
    }

    // ─────────────────────────────────────────────
    // 3. Append one new entry to the file
    //    (Does NOT erase existing data)
    // ─────────────────────────────────────────────
    public static void appendEntry(DiaryEntry entry) {
        // 'true' means append mode — adds to end of file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(entry.toString());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // 4. Rewrite the ENTIRE file
    //    Used for: Update and Delete operations
    // ─────────────────────────────────────────────
    public static void rewriteAllEntries(ArrayList<DiaryEntry> entries) {
        // 'false' means overwrite mode — replaces entire file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (DiaryEntry entry : entries) {
                bw.write(entry.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error rewriting file: " + e.getMessage());
        }
    }
}
