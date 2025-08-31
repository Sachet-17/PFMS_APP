package managers;

import database.DatabaseConnection;
import models.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {

    public void addTransaction(int userId, Transaction transaction) {
        String query = "INSERT INTO Transactions (userId, amount, category, date, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setDouble(2, transaction.getAmount());
            stmt.setString(3, transaction.getDescription());
            stmt.setString(4, transaction.getDate());
            stmt.setString(5, transaction.getType());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
        }
    }

    public List<Transaction> getTransactions(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM Transactions WHERE userId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("type")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching transactions: " + e.getMessage());
        }
        return transactions;
    }

    public void deleteTransaction(int transactionId) {
        String query = "DELETE FROM Transactions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, transactionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting transaction: " + e.getMessage());
        }
    }
}
