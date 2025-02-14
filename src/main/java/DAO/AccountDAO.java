package DAO;

import java.sql.*;

import Model.Account;

public class AccountDAO {
    private Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    public Account createAccount(String username, String password) throws SQLException {
        String sql = "INSERT INTO Account (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Account(generatedKeys.getInt(1), username, password);
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
            }
        }
    }

    public Account getAccountByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Account WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    public Account getAccountById(int accountId) throws SQLException {
        String sql = "SELECT * FROM Account WHERE account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        }
        return null;
    }
}

