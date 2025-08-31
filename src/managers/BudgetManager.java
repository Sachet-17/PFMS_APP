package managers;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BudgetManager {

    public void setBudget(int userId, String category, double amount) {
        String query = """
            INSERT INTO Budgets (userId, category, amount)
            VALUES (?, ?, ?)
            ON CONFLICT(userId, category)
            DO UPDATE SET amount = excluded.amount
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, category);
            stmt.setDouble(3, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error setting budget: " + e.getMessage());
        }
    }

    public Map<String, Double> getBudgets(int userId) {
        Map<String, Double> budgets = new HashMap<>();
        String query = "SELECT category, amount FROM Budgets WHERE userId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                budgets.put(rs.getString("category"), rs.getDouble("amount"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving budgets: " + e.getMessage());
        }
        return budgets;
    }
}
