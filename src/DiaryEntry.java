public class DiaryEntry {

    // Fields — private means only this class can access them directly
    private int id;
    private String date;
    private String title;
    private String content;
    private String mood;

    // Constructor — called when we create a new DiaryEntry object
    public DiaryEntry(int id, String date, String title, String content, String mood) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.content = content;
        this.mood = mood;
    }

    // Getters — used to READ the private fields
    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getMood() {
        return mood;
    }

    // Setters — used to UPDATE the private fields
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    // toString — converts one entry to the file format line
    // Example: 1|19-09-2026|College Day|Learned Java.|Happy
    @Override
    public String toString() {
        return id + "|" + date + "|" + title + "|" + content + "|" + mood;
    }
}
