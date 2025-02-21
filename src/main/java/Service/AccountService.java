package Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.AccountDAO;
import DAO.ExceptionDAO;
import Model.Account;



public class AccountService {
    private AccountDAO accountDao;
    private static final Logger accountLogger = LoggerFactory.getLogger(AccountService.class);

    public AccountService() {
        accountDao = new AccountDAO();
    }

   
    public AccountService(AccountDAO accountDAO) {
        this.accountDao = accountDao;
    }

    


     
    public Optional<Account> getAccountById(int id) {
        accountLogger.info("Fetching account with ID: {}", id);
        try {
            Optional<Account> account = accountDao.getById(id);
            accountLogger.info("Fetched account: {}", account.orElse(null));
            return account;
        } catch (ExceptionDAO e) {
            throw new ExceptionService("Exception occurred while attempting to retrieve account", e);
        }
    }

   

    public List<Account> getAllAccounts() {
        accountLogger.info("Fetching all accounts");
        try {
            List<Account> accounts = accountDao.getAll();
            accountLogger.info("Fetched {} accounts", accounts.size());
            return accounts;
        } catch (ExceptionDAO e) {
            throw new ExceptionService("Exception occurred while attempting to retrieve accounts", e);
        }
    }

   
    public Optional<Account> findAccountByUsername(String username) {
        accountLogger.info("Finding account by username: {}", username);
        try {
            Optional<Account> account = accountDao.findAccountByUsername(username);
            accountLogger.info("Found account: {}", account.orElse(null));
            return account;
        } catch (ExceptionDAO e) {
            throw new ExceptionService("Exception occurred while finding account by username " + username, e);
        }
    }

    
    public Optional<Account> validateLogin(Account account) {
        accountLogger.info("Validating login");
        try {
            Optional<Account> validatedAccount = accountDao.validateLogin(account.getUsername(),
                    account.getPassword());
                    accountLogger.info("Login validation result: {}", validatedAccount.isPresent());
            return validatedAccount;
        } catch (ExceptionDAO e) {
            throw new ExceptionService("Exception occurred while validating login", e);
        }
    }

    
    public Account createAccount(Account account) {
        accountLogger.info("Creating account: {}", account);
        try {
            validateAccount(account);
            Optional<Account> searchedAccount = findAccountByUsername(account.getUsername());
            if (searchedAccount.isPresent()) {
                throw new ExceptionService("Account already exist");
            }
            Account createdAccount = accountDao.insert(account);
            accountLogger.info("Created account: {}", createdAccount);
            return createdAccount;
        } catch (ExceptionDAO e) {
            throw new ExceptionService("Exception occurred while creating account", e);
        }
    }

    
    public boolean updateAccount(Account account) {
        accountLogger.info("Updating account: {}", account);
        try {
            account.setPassword(account.password);
            boolean updated = accountDao.update(account);
            accountLogger.info("Updated account: {}. Update successful {}", account, updated);
            return updated;
        } catch (ExceptionDAO e) {
            throw new ExceptionService("Exception occurred while while updating account", e);
        }
    }

    
    public boolean deleteAccount(Account account) {
        accountLogger.info("Deleting account: {}", account);
        if (account.getAccount_id() == 0) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        try {
            boolean deleted = accountDao.delete(account);
            accountLogger.info("Deleted account: {} . Deletion successful {}", account, deleted);
            return deleted;
        } catch (ExceptionDAO e) {
            throw new ExceptionService("Exception occurred while while deleting account", e);
        }
    }


    private void validateAccount(Account account) {
        accountLogger.info("Validating account: {}", account);
        try {

            String username = account.getUsername().trim();
            String password = account.getPassword().trim();

            if (username.isEmpty()) {
                throw new ExceptionService("Username cannot be blank");
            }
            if (password.isEmpty()) {
                throw new ExceptionService("Password cannot be null");
            }

            if (password.length() < 4) {
                throw new ExceptionService("Password must be at least 4 characters long");
            }
            if (accountDao.doesUsernameExist(account.getUsername())) {
                throw new ExceptionService("The username must have unique values");
            }
        } catch (ExceptionDAO e) {
            throw new ExceptionService("Exception occurred while validating account", e);
        }
    }


    public boolean accountExists(int accountId) {
        accountLogger.info("Checking account existence with ID: {}", accountId);
        try {
            Optional<Account> account = accountDao.getById(accountId);
            boolean exists = account.isPresent();
            accountLogger.info("Account existence: {}", exists);
            return exists;
        } catch (ExceptionDAO e) {
            throw new ExceptionService("Exception occurred while checking account existence", e);
        }
    }
}