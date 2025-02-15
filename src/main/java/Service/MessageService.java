package Service;

import java.sql.SQLException;
import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public Message createMessage(int postedBy, String messageText, long timePostedEpoch) throws SQLException {
        if (messageText == null || messageText.isBlank() || messageText.length() >= 255) {
            return null; // Invalid message text
        }

        return messageDAO.createMessage(postedBy, messageText, timePostedEpoch);
    }

    public List<Message> getAllMessages() throws SQLException {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int messageId) throws SQLException {
        return messageDAO.getMessageById(messageId);
    }

    public boolean deleteMessage(int messageId) throws SQLException {
        return messageDAO.deleteMessage(messageId);
    }

    public boolean updateMessage(int messageId, String newMessageText) throws SQLException {
        if (newMessageText == null || newMessageText.isBlank() || newMessageText.length() >= 255) {
            return false; // Invalid message text
        }

        Message existingMessage = messageDAO.getMessageById(messageId);
        if (existingMessage == null) {
            return false; // Message does not exist
        }

        return messageDAO.updateMessage(messageId, newMessageText);
    }
}
