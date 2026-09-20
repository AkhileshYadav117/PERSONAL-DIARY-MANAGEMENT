package com.diary.model;

public class DiaryEntry {
    private int id;
    private int userId;
    private String title;
    private String content;
    private String mood;
    private String category;
    private boolean isFavorite;
    private String entryDate;
    private String createdAt;
    private String updatedAt;

    public DiaryEntry() {}

    public DiaryEntry(int userId, String title, String content, String mood, String category) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.mood = mood;
        this.category = category;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public String getEntryDate() { return entryDate; }
    public void setEntryDate(String entryDate) { this.entryDate = entryDate; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "DiaryEntry{id=" + id + ", title=" + title + ", mood=" + mood + "}";
    }
}