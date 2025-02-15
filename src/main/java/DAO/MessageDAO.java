package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Model.Message;

public class MessageDAO {
    private Connection connection;

    public MessageDAO(Connection connection) {
        this.connection = connection;
    }

    public Message createMessage(int posted_by, String message_text, long time_posted_epoch) throws SQLException {
        String sql = "INSERT INTO messages (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, posted_by);
            stmt.setString(2, message_text);
            stmt.setLong(3, time_posted_epoch);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return null; // Message creation failed
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Message(generatedKeys.getInt(1), posted_by, message_text, time_posted_epoch);
                }
            }
        }
        return null;
    }

    public Message getMessageById(int message_id) throws SQLException {
        String sql = "SELECT * FROM messages WHERE message_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, message_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                );
            }
        }
        return null;
    }

    public List<Message> getAllMessages() throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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

    public boolean deleteMessage(int message_id) throws SQLException {
        String sql = "DELETE FROM messages WHERE message_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, message_id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateMessage(int message_id, String newText) throws SQLException {
        String sql = "UPDATE messages SET message_text = ? WHERE message_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newText);
            stmt.setInt(2, message_id);
            return stmt.executeUpdate() > 0;
        }
    }
}
