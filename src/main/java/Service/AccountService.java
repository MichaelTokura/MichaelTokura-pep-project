package Service;

import java.sql.SQLException;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account registerAccount(String username, String password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.length() <= 4) {
            return null; // Invalid username or password
        }

        if (accountDAO.getAccountByUsername(username) != null) {
            return null; // Username already exists
        }

        return accountDAO.createAccount(username, password);
    }

    public Account login(String username, String password) throws SQLException {
        Account account = accountDAO.getAccountByUsername(username);
        if (account != null && account.getPassword().equals(password)) {
            return account;
        }
        return null; // Invalid credentials
    }

    public Account getAccountById(int accountId) throws SQLException {
        return accountDAO.getAccountById(accountId);
    }
}
