package Service;

import DAO.MessageDAO;
import Model.Message;
import java.sql.SQLException;
import java.util.List;

public class MessageService {

    private MessageDAO messageDAO;

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public Message addMessage(Message message) throws SQLException {
        // Add business logic/validation here
        if (message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
            throw new IllegalArgumentException("Message text cannot be blank and must be under 255 characters.");
        }
        if (message.getPosted_by() <= 0) {
            throw new IllegalArgumentException("Invalid user ID."); // Example: User ID validation
        }
        if (message.getTime_posted_epoch() <= 0) {
            throw new IllegalArgumentException("Invalid timestamp.");
        }
        return messageDAO.addMessage(message);
    }

    public Message getMessageById(int message_id) throws SQLException {
        return messageDAO.getMessageById(message_id);
    }

    public List<Message> getMessagesByUserId(int posted_by) throws SQLException {
        return messageDAO.getMessagesByUserId(posted_by);
    }

    public List<Message> getAllMessages() throws SQLException {
        return messageDAO.getAllMessages();
    }

    public Message updateMessage(Message message) throws SQLException {
        // Add validation/business logic before updating
         if (message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
            throw new IllegalArgumentException("Message text cannot be blank and must be under 255 characters.");
        }
        messageDAO.updateMessage(message);
    }

    public void deleteMessage(int message_id) throws SQLException {
        messageDAO.deleteMessage(message_id);
    }

    // Example of a more complex business logic operation (if needed)
    // public List<Message> getMessagesByUserAndDateRange(int userId, long startTime, long endTime) throws SQLException { ... }
}