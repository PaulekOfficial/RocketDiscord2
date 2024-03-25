package pro.paulek.data.cache.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.ISQLDataModel;
import pro.paulek.data.cache.GuildConfigurationICache;
import pro.paulek.objects.guild.DiscordMessage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class DiscordMessageMySQLModel implements ISQLDataModel<DiscordMessage, String> {
    private final static Logger logger = LoggerFactory.getLogger(DiscordMessageMySQLModel.class);
    private final IRocketDiscord rocketDiscord;

    public DiscordMessageMySQLModel(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    @Override
    public DiscordMessage load(String id) {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("SELECT * FROM messages WHERE message_id = ?");
            ps.setString(1, id);
            var rs = ps.executeQuery();

            if(rs.next()) {
                return this.deserializeData(rs);
            }
        } catch (SQLException exception) {
            logger.error("Cannot load discord message: ", exception);
        }

        return null;
    }

    @Override
    public void createTable() {

    }

    @Override
    public DiscordMessage load(int id) {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("SELECT * FROM messages WHERE id = ?");
            ps.setInt(1, id);
            var rs = ps.executeQuery();

            if(rs.next()) {
                return this.deserializeData(rs);
            }
        } catch (SQLException exception) {
            logger.error("Cannot load discord message: ", exception);
        }

        return null;
    }

    @Override
    public Collection<DiscordMessage> load() {
        var collection = new ArrayList<DiscordMessage>();
        try (Connection connection = this.rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("SELECT * FROM messages");
            var rs = ps.executeQuery();

            while(rs.next()) {
                collection.add(this.deserializeData(rs));
            }
        } catch (SQLException exception) {
            logger.error("Cannot load discord messages: ", exception);
        }

        return collection;
    }

    @Override
    public void saveAll(Collection<DiscordMessage> collection, boolean ignoreNotChanged) {
       collection.forEach(this::save);
    }

    @Override
    public void save(DiscordMessage discordMessage) {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("INSERT INTO messages (author_name, author_id, message_id, content, action, created_at) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, discordMessage.getAuthorName());
            ps.setString(2, discordMessage.getAuthorID());
            ps.setString(3, discordMessage.getMessageID());
            ps.setString(4, discordMessage.getContent());
            ps.setString(5, discordMessage.getAction().name());
            ps.setTimestamp(6, Timestamp.from(discordMessage.getCreatedAt()));
            ps.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Cannot load discord message: ", exception);
        }
    }

    @Override
    public void delete(String id) {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("DELETE FROM messages WHERE message_id = ?");
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Cannot delete discord message: ", exception);
        }
    }

    @Override
    public void delete(int id) {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("DELETE FROM messages WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Cannot delete discord message: ", exception);
        }
    }

    //TODO check if it is important, or if it is not used overall to delete
    @Override
    public ResultSet serializeData(DiscordMessage discordMessage) {
        return null;
    }

    @Override
    public DiscordMessage deserializeData(ResultSet resultSet) throws SQLException {
        return new DiscordMessage(
                resultSet.getInt("id"),
                resultSet.getString("author_name"),
                resultSet.getString("author_id"),
                resultSet.getString("message_id"),
                resultSet.getString("content"),
                DiscordMessage.MessageAction.valueOf(resultSet.getString("action")),
                resultSet.getTimestamp("created_at").toInstant()
        );
    }

    @Override
    public int count() {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("SELECT COUNT(*) FROM messages");
            var rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException exception) {
            logger.error("Cannot load discord message: ", exception);
        }

        return 0;
    }
}
