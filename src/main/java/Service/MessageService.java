package Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.MessageDAO;
import DAO.ExceptionDAO;
import Model.Account;
import Model.Message;
import io.javalin.http.NotFoundResponse;



public class MessageService {
    private MessageDAO messageDao;
    private static final Logger serviceLogger = LoggerFactory.getLogger(MessageService.class);
    private static final String DB_ACCESS_ERROR_MSG = "Error: unable to access database";

    public MessageService() {
        messageDao = new MessageDAO();
    }

    
    public MessageService(MessageDAO messageDao) {
        this.messageDao = messageDao;
    }

    
    public Optional<Message> getMessageById(int id) {
        serviceLogger.info("Searching for message with ID: {} ", id);
        try {
            Optional<Message> message = messageDao.getById(id);
            if (!message.isPresent()) {
                throw new ExceptionService("Unable to find message");
            }
            serviceLogger.info("Obtained message: {}", message.orElse(null));
            return message;
        } catch (ExceptionDAO e) {
            throw new ExceptionService(DB_ACCESS_ERROR_MSG, e);
        }
    }

   
    public List<Message> getAllMessages() {
        serviceLogger.info("Locating all messages");
        try {
            List<Message> messages = messageDao.getAll();
            serviceLogger.info("Fetched {} messages", messages.size());
            return messages;
        } catch (ExceptionDAO e) {
            throw new ExceptionService(DB_ACCESS_ERROR_MSG, e);
        }
    }

   
    public List<Message> getMessagesByAccountId(int accountId) {
        serviceLogger.info("Obtaining messages posted by AccountID: {}", accountId);
        try {
            List<Message> messages = messageDao.getMessagesByAccountId(accountId);
            serviceLogger.info("Fetched {} messages", messages.size());
            return messages;
        } catch (ExceptionDAO e) {
            throw new ExceptionService(DB_ACCESS_ERROR_MSG, e);
        }
    }

    
    public Message createMessage(Message message, Optional<Account> account) {
        serviceLogger.info("Creating message: {}", message);

        if (!account.isPresent()) {
            throw new ExceptionService("Unable to post message without an account");
        }

        validateMessage(message);

        checkAccountPermission(account.get(), message.getPosted_by());
        try {
            Message createdMessage = messageDao.insert(message);
            serviceLogger.info("Message successfully created: {}", createdMessage);
            return createdMessage;
        } catch (ExceptionDAO e) {
            throw new ExceptionService(DB_ACCESS_ERROR_MSG, e);
        }
    }

    
    public Message updateMessage(Message message) {
        serviceLogger.info("Updating message: {}", message.getMessage_id());

        Optional<Message> retrievedMessage = this.getMessageById(message.getMessage_id());

        if (!retrievedMessage.isPresent()) {
            throw new ExceptionService("Unable to find Message");
        }

        retrievedMessage.get().setMessage_text(message.getMessage_text());

        validateMessage(retrievedMessage.get());

        try {
            messageDao.update(retrievedMessage.get());
            serviceLogger.info("Updated message: {}", message);
            return retrievedMessage.get();
        } catch (ExceptionDAO e) {
            throw new ExceptionService(DB_ACCESS_ERROR_MSG, e);
        }
    }

    
    public void deleteMessage(Message message) {
        serviceLogger.info("Deleting message: {}", message);
        try {
            boolean hasDeletedMessage = messageDao.delete(message);
            if (hasDeletedMessage) {
                serviceLogger.info("Deleted message {}", message);
            } else {
                throw new NotFoundResponse("Unable to find Message to delete");
            }
        } catch (ExceptionDAO e) {
            throw new ExceptionService(DB_ACCESS_ERROR_MSG, e);
        }
    }

    
    private void validateMessage(Message message) {
        serviceLogger.info("Validating message: {}", message);
        if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()) {
            throw new ExceptionService("Message text cannot be null or empty");
        }
        if (message.getMessage_text().length() > 254) {
            throw new ExceptionService("Message text cannot exceed 254 characters");
        }
    }

    
    private void checkAccountPermission(Account account, int postedBy) {
        serviceLogger.info("Checking account permissions for messages");
        if (account.getAccount_id() != postedBy) {
            throw new ExceptionService("Message cant be modified due to lack of account Authorization");
        }
    }
}