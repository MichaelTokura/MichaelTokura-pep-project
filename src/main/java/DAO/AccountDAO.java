package DAO;

import Model.Account;
import Util.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;



public class AccountDAO implements MAINDAO<Account> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDAO.class);

    private void handleSQLException(SQLException e, String sql, String errorMessage) {
        LOGGER.error("SQLException Details: {}", e.getMessage());
        LOGGER.error("SQL State: {}", e.getSQLState());
        LOGGER.error("Error Code: {}", e.getErrorCode());
        LOGGER.error("SQL: {}", sql);
        throw new ExceptionDAO(errorMessage, e);
    }







    @Override
    public Optional<Account> getById(int id) {

        String sql = "SELECT * FROM account WHERE account_id = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement state = conn.prepareStatement(sql)) {
            state.setInt(1, id);
            
            try (ResultSet result = state.executeQuery()) {
                if (result.next()) {
                    return Optional.of(new Account(
                        result.getInt("account_id"),
                        result.getString("username"),
                        result.getString("password")));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error: unable to retrieve account with id: " + id);
        }
        return Optional.empty();
    }


    @Override
    public List<Account> getAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account";
        Connection co = ConnectionUtil.getConnection();
        try (PreparedStatement ps = co.prepareStatement(sql)) {
            try (ResultSet resultset = ps.executeQuery()) {
                while (resultset.next()) {
                    Account account = new Account(
                        resultset.getInt("account_id"),
                        resultset.getString("username"),
                        resultset.getString("password"));
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error: unable to retrieve all accounts");
        }
        return accounts;
    }

    
    public Optional<Account> findAccountByUsername(String username) {

        String sql = "SELECT * FROM account WHERE username = ?";
        Connection conn = ConnectionUtil.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password")));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error: unable to find account with username: " + username);
        }
        return Optional.empty();
    }

   
    public Optional<Account> validateLogin(String username, String password) {
        String sql = "SELECT * FROM account WHERE username = ?";
        Connection conn = ConnectionUtil.getConnection();

        try (PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setString(1, username);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password"));

                    if (Objects.equals(password, account.getPassword())) {
                        return Optional.of(account);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error: unable to validate login for username: " + username);
        }
        return Optional.empty();
    }

   
    public boolean doesUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM account WHERE username = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } catch (SQLException e) {
                handleSQLException(e, sql, "Error: unable to check the existence of username: " + username);
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error: unable to establish connection");
        }
        return false;
    }

   
    @Override
    public Account insert(Account account) {
        String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedAccountId = generatedKeys.getInt(1);
                    return new Account(generatedAccountId, account.getUsername(), account.getPassword());
                } else {
                    throw new ExceptionDAO("Account Creation Failed: unable to obtain ID.");
                }
            }
        } catch (SQLException e) {
            throw new ExceptionDAO("Creating account failed due to SQL error", e);
        }
    }






   
    @Override
    public boolean update(Account account) {
        String sql = "UPDATE account SET username = ?, password = ? WHERE account_id = ?";
        Connection connect = ConnectionUtil.getConnection();
        try (PreparedStatement pStatement = connect.prepareStatement(sql)) {
            pStatement.setString(1, account.getUsername());
            pStatement.setString(2, account.getPassword());
            pStatement.setInt(3, account.getAccount_id());
            int impact = pStatement.executeUpdate();
            if (impact > 0) {
                return true;
            } else {
                throw new ExceptionDAO("Updating account failed, Account does not exist.");
            }
        } catch (SQLException e) {
            throw new ExceptionDAO("Updating account failed due to SQL error", e);
        }
    }

    
    @Override
    public boolean delete(Account account) {
        String sql = "DELETE FROM account WHERE account_id = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, account.getAccount_id());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new ExceptionDAO("Deleting account failed due to SQL error", e);
        }
    }
}