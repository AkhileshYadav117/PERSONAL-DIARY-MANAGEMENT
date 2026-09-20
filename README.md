# 📓 Personal Diary Management System

[![Live Demo](https://img.shields.io/badge/Live%20Demo-personal--diary--uecw.onrender.com-brightgreen)](https://personal-diary-uecw.onrender.com)
[![Backend API](https://img.shields.io/badge/Backend%20API-diary--backend--24ef.onrender.com-blue)](https://diary-backend-24ef.onrender.com)
[![GitHub](https://img.shields.io/badge/GitHub-PERSONAL--DIARY--MANAGEMENT-black)](https://github.com/AkhileshYadav117/PERSONAL-DIARY-MANAGEMENT)

A full-stack Personal Diary Management System built as a **Java college mini-project**, extended into a modern web application.

## 🚀 Live Deployment

| Service | URL |
|---------|-----|
| 🌐 **Web App (Frontend)** | https://personal-diary-uecw.onrender.com |
| ⚙️ **REST API (Backend)** | https://diary-backend-24ef.onrender.com |
| 🗄️ **Database** | PostgreSQL on Render (Cloud) |

> ⚠️ **Note:** Free tier — first request may take 30-50 seconds to wake up the backend.

---

## 🌟 Project Overview

This project started as a **Core Java + Swing GUI desktop application** and evolved into a complete web application with:
- A **Flutter Web** frontend
- A **Core Java HTTP REST API** backend  
- A **PostgreSQL** database via JDBC
- **File Handling** (Backup, Export, Import)

---

## 🎯 Problem Statement

People need a secure, private, organized way to record daily thoughts, emotions, and events. This system provides a digital diary with full CRUD operations, mood tracking, category management, and data backup capabilities.

---

## ✨ Features

### User Account
- Register / Login / Logout
- Secure password hashing (BCrypt)
- Input validation

### Diary Management
- Create, View, Edit, Delete diary entries
- Rich entry detail view
- Favorite entries

### Organization
- **Mood Tracking** — Happy, Sad, Excited, Anxious, Calm, Angry, Grateful
- **Categories** — Personal, Work, Travel, Health, Family, Study, General
- **Search** — Search by title or content
- **Filter** — Filter by mood

### Calendar View
- Monthly calendar showing entry dates
- Click any date to view entries for that day
- Add entries directly from calendar

### File Handling (Core Java)
- **Backup** — Export diary to `.txt` file
- **Export** — Export diary to `.csv` (Excel compatible)
- **Import** — Import entries from `.csv`
- Files saved in `backups/` directory using Java `FileWriter`, `BufferedWriter`, `BufferedReader`

---

## 🏗️ Architecture

```
Flutter Web (Dart)
       ↓
HTTP / REST / JSON
       ↓
Core Java Backend (HttpServer)
       ↓
JDBC
       ↓
PostgreSQL Database

Core Java
       ↓
File Handling (FileWriter/BufferedWriter)
       ↓
Backup / Export / Import (.txt / .csv)
```

---

## 🛠️ Technologies Used

| Layer | Technology |
|---|---|
| Frontend | Flutter 3.x, Dart, table_calendar |
| Backend | Core Java 21, HttpServer |
| Database | PostgreSQL 16, JDBC |
| Build Tool | Apache Maven 3.9 |
| Security | BCrypt password hashing |
| File I/O | Java FileWriter, BufferedWriter, BufferedReader, PrintWriter |
| OOP | Classes, Interfaces, Encapsulation, Inheritance |

---

## 🗄️ Database Design

### users
| Column | Type | Description |
|---|---|---|
| id | SERIAL PRIMARY KEY | Auto-increment ID |
| name | VARCHAR(100) | User's full name |
| email | VARCHAR(150) UNIQUE | Login email |
| password_hash | TEXT | BCrypt hashed password |
| created_at | TIMESTAMP | Account creation time |

### diary_entries
| Column | Type | Description |
|---|---|---|
| id | SERIAL PRIMARY KEY | Auto-increment ID |
| user_id | INT (FK → users) | Owner user |
| title | VARCHAR(200) | Entry title |
| content | TEXT | Entry content |
| mood | VARCHAR(50) | Mood tag |
| category | VARCHAR(50) | Category tag |
| is_favorite | BOOLEAN | Favorite flag |
| entry_date | DATE | Entry date |
| created_at | TIMESTAMP | Created time |
| updated_at | TIMESTAMP | Last updated |

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/register | Register new user |
| POST | /api/login | Login user |
| GET | /api/entries?userId={id} | Get all entries |
| POST | /api/entries | Create new entry |
| PUT | /api/entries/{id} | Update entry |
| DELETE | /api/entries/{id}?userId={id} | Delete entry |
| GET | /api/export/txt?userId={id} | Download TXT backup |
| GET | /api/export/csv?userId={id} | Download CSV export |
| POST | /api/import?userId={id} | Import from CSV |
| GET | /api/backups?userId={id} | List backup files |

---

## 📁 Project Structure

```
PERSONAL DIARY MANAGEMENT/
├── src/                          ← Original Java GUI (Swing)
│   ├── Main.java
│   ├── DiaryEntry.java
│   ├── DiaryManager.java
│   ├── FileHandler.java
│   └── UI/
│       ├── MainFrame.java
│       ├── DashboardPanel.java
│       └── ...
│
└── full-stack-version/           ← Web Application
    ├── backend/                  ← Core Java REST API
    │   ├── pom.xml
    │   └── src/main/java/com/diary/
    │       ├── Main.java
    │       ├── api/              (AuthHandler, DiaryHandler, FileApiHandler)
    │       ├── dao/              (UserDAO, DiaryEntryDAO)
    │       ├── database/         (DatabaseConnection)
    │       ├── model/            (User, DiaryEntry)
    │       └── service/          (FileHandler)
    │
    └── frontend/                 ← Flutter Web
        ├── pubspec.yaml
        └── lib/
            ├── main.dart
            ├── models/           (diary_entry.dart)
            ├── services/         (api_service.dart)
            └── screens/
                ├── login_screen.dart
                ├── register_screen.dart
                ├── home_screen.dart
                ├── add_entry_screen.dart
                ├── entry_detail_screen.dart
                ├── calendar_screen.dart
                └── file_screen.dart
```

---

## ⚙️ Local Setup

### Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL 16+
- Flutter 3.x
- Git

### 1. Database Setup

```sql
CREATE DATABASE diary_db;
```

Run the schema:
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE diary_entries (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    mood VARCHAR(50) DEFAULT 'Happy',
    category VARCHAR(50) DEFAULT 'Personal',
    is_favorite BOOLEAN DEFAULT false,
    entry_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

### 2. Configure Database Credentials

Edit `full-stack-version/backend/src/main/java/com/diary/database/DatabaseConnection.java`:
```java
private static final String URL = "jdbc:postgresql://localhost:5432/diary_db";
private static final String USER = "your_postgres_user";
private static final String PASSWORD = "your_postgres_password";
```

### 3. Run Backend

```bash
cd full-stack-version/backend
mvn exec:java -Dexec.mainClass=com.diary.Main
```

Backend starts at: `http://localhost:8080`

### 4. Run Frontend

```bash
cd full-stack-version/frontend
flutter pub get
flutter run -d chrome
```

Frontend opens at: `http://localhost:PORT`

---

## 📄 File Handling

The application implements Java File I/O as a core college requirement:

| Feature | Java Class Used | Output |
|---|---|---|
| Backup | `FileWriter`, `BufferedWriter` | `.txt` file |
| Export | `PrintWriter`, `FileWriter` | `.csv` file |
| Import | `BufferedReader`, `StringReader` | Database entries |
| Log | `BufferedWriter` | Import log `.txt` |

All backup files are saved in `full-stack-version/backend/backups/`

---

## 🔒 Security

- Passwords hashed using **BCrypt** (never stored as plain text)
- **PreparedStatement** used for all database queries (SQL injection protection)
- Each user can only access their own diary entries
- No secrets or credentials committed to repository

---

## 🚀 Future Scope

- Deploy on cloud (Render)
- Email-based password reset
- Rich text editor for diary content
- Entry word count and reading time
- Data analytics dashboard
- Mobile app (Flutter Android/iOS)

---

