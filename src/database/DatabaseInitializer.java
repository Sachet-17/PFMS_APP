package database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                );
            """);

            // Create Transactions table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    userId INTEGER NOT NULL,
                    amount REAL NOT NULL,
                    category TEXT NOT NULL,
                    date TEXT NOT NULL,
                    type TEXT NOT NULL,
                    FOREIGN KEY (userId) REFERENCES Users(id)
                );
            """);

            // Create Budgets table with UNIQUE constraint
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Budgets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    userId INTEGER NOT NULL,
                    category TEXT NOT NULL,
                    amount REAL NOT NULL,
                    FOREIGN KEY (userId) REFERENCES Users(id),
                    UNIQUE (userId, category)
                );
            """);

            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
