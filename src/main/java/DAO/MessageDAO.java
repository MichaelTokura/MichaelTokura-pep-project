package DAO;

import java.sql.*;
import java.util.*;

import Model.Message;

public class MessageDAO {
    private Connection connection;

    public MessageDAO(Connection connection) {
        this.connection = connection;
    }

    public Message createMessage(int postedBy, String messageText, long timePosted) throws SQLException {
        String sql = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?);";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, postedBy);
            stmt.setString(2, messageText);
            stmt.setLong(3, timePosted);
            
            int affectedRows = stmt.executeUpdate();
    
            if (affectedRows == 0) {
                throw new SQLException("Creating message failed, no rows affected.");
            }
    
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Message(generatedKeys.getInt(1), postedBy, messageText, timePosted);
                } else {
                    throw new SQLException("Creating message failed, no ID obtained.");
                }
            }
        }
    }
    public List<Message> getAllMessages() throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM Message";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                messages.add(new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                ));
            }
        }
        return messages;
    }

    public Message getMessageById(int messageId) throws SQLException {
        String sql = "SELECT * FROM Message WHERE message_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                    );
                }
            }
        }
        return null;
    }
}
