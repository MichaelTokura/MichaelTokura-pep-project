package DAO;

import Model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    private Connection connection;

    public MessageDAO(Connection connection) {
        this.connection = connection;
    }

    public Message addMessage(Message message) throws SQLException {
        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, message.getPosted_by());
            statement.setString(2, message.getMessage_text());
            statement.setLong(3, message.getTime_posted_epoch());
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        message.setMessage_id(generatedId);
                        return message; // Return the message with the generated ID
                    }
                }
            }
        }
        return null; // Insert failed
    }


    public Message getMessageById(int message_id) throws SQLException {
        String sql = "SELECT * FROM message WHERE message_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, message_id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractMessageFromResultSet(resultSet);
                }
            }
        }
        return null;
    }

    public List<Message> getMessagesByUserId(int posted_by) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE posted_by = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, posted_by);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    messages.add(extractMessageFromResultSet(resultSet));
                }
            }
        }
        return messages;
    }


    public List<Message> getAllMessages() throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                messages.add(extractMessageFromResultSet(resultSet));
            }
        }
        return messages;
    }

    public void updateMessage(Message message) throws SQLException {
        String sql = "UPDATE message SET message_text = ?, time_posted_epoch = ? WHERE message_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, message.getMessage_text());
            statement.setLong(2, message.getTime_posted_epoch());
            statement.setInt(3, message.getMessage_id());
            statement.executeUpdate();
        }
    }

    public void deleteMessage(int message_id) throws SQLException {
        String sql = "DELETE FROM message WHERE message_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, message_id);
            statement.executeUpdate();
        }
    }

    private Message extractMessageFromResultSet(ResultSet resultSet) throws SQLException {
        int messageId = resultSet.getInt("message_id");
        int postedBy = resultSet.getInt("posted_by");
        String messageText = resultSet.getString("message_text");
        long timePostedEpoch = resultSet.getLong("time_posted_epoch");
        return new Message(messageId, postedBy, messageText, timePostedEpoch);
    }
}