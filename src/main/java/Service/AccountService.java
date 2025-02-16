package Service;

import DAO.AccountDAO;
import Model.Account;
import java.sql.SQLException;
import java.util.List;

public class AccountService {

    private AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account getAccountByUsername(String username) throws SQLException {
        return accountDAO.getAccountByUsername(username);
    }

    public Account getAccountById(int account_id) throws SQLException {
        return accountDAO.getAccountById(account_id);
    }

    public List<Account> getAllAccounts() throws SQLException {
        return accountDAO.getAllAccounts();
    }

    public Account addAccount(Account account) throws SQLException {
        // You can add business logic/validation here before adding to the database
        if (account.getUsername() == null || account.getUsername().isBlank()) {
          throw new IllegalArgumentException("Username cannot be blank."); // Example validation
        }

        if (account.getPassword() == null || account.getPassword().length() < 5) { // Example validation
            throw new IllegalArgumentException("Password must be at least 5 characters.");
        }
        return accountDAO.addAccount(account);
    }

    public void updateAccount(Account account) throws SQLException {
        // Add any necessary validation or business logic before updating
        accountDAO.updateAccount(account);
    }

    public void deleteAccount(int account_id) throws SQLException {
        // Add any necessary checks before deleting
        accountDAO.deleteAccount(account_id);
    }


    public boolean isUsernameTaken(String username) throws SQLException {
        Account account = accountDAO.getAccountByUsername(username);
        return account != null;
    }


    public Account login(String username, String password) throws SQLException {
        Account account = getAccountByUsername(username);
        if (account != null && account.getPassword().equals(password)) {
            return account; // Successful login
        }
        return null; // Login failed
    }
}