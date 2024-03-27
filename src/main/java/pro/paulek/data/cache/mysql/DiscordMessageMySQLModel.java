package pro.paulek.data.cache.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.ISQLDataModel;
import pro.paulek.objects.guild.DiscordMessage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DiscordMessageMySQLModel implements ISQLDataModel<DiscordMessage, String> {
    private final static Logger logger = LoggerFactory.getLogger(DiscordMessageMySQLModel.class);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final IRocketDiscord rocketDiscord;

    public DiscordMessageMySQLModel(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    @Override
    public Optional<DiscordMessage> load(String id) {
        try(Connection connection = rocketDiscord.getDatabaseConnection();
            var ps = connection.prepareStatement("SELECT * FROM messages WHERE message_id = ?")) {
            ps.setString(1, id);
            var rs = ps.executeQuery();

            if(rs.next()) {
                var data = this.deserializeData(rs);
                if (data == null) {
                    return Optional.empty();
                }

                return Optional.of(data);
            }
        } catch (SQLException exception) {
            logger.error("Cannot load discord message: ", exception);
        }

        return Optional.empty();
    }

    @Override
    public Future<Boolean> createTable() {
        return executorService.submit(() -> {
            try(Connection connection = rocketDiscord.getDatabaseConnection();
                var ps = connection.prepareStatement("create table if not exists message" +
                        "(" +
                        "    id int auto_increment" +
                        "    primary key," +
                        "    author_name varchar(80)     null," +
                        "    author_id   varchar(30)     null," +
                        "    message_id  varchar(30) not null," +
                        "    content     text             null," +
                        "    action      varchar(15)  not null," +
                        "    created_at  timestamp default current_timestamp() not null)")) {

                return ps.executeUpdate() > 0;
            } catch (SQLException exception) {
                logger.error("Cannot create discord message table: ", exception);
            }

            return false;
        });
    }

    @Override
    public Optional<DiscordMessage> load(int id) {
        try(Connection connection = rocketDiscord.getDatabaseConnection();
            var ps = connection.prepareStatement("SELECT * FROM message WHERE id = ?")) {
            ps.setInt(1, id);
            var rs = ps.executeQuery();

            if(rs.next()) {
                var data = this.deserializeData(rs);
                if (data == null) {
                    return Optional.empty();
                }

                return Optional.of(data);
            }
        } catch (SQLException exception) {
            logger.error("Cannot load discord message: ", exception);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Collection<DiscordMessage>> load() {
        var collection = new ArrayList<DiscordMessage>();
        try (Connection connection = this.rocketDiscord.getDatabaseConnection();
            var ps = connection.prepareStatement("SELECT * FROM message")) {
            var rs = ps.executeQuery();

            while(rs.next()) {
                collection.add(this.deserializeData(rs));
            }
        } catch (SQLException exception) {
            logger.error("Cannot load discord messages: ", exception);

            return Optional.empty();
        }

        return Optional.of(collection);
    }

    @Override
    public Future<Boolean> saveAll(Collection<DiscordMessage> collection, boolean ignoreNotChanged) {
        return executorService.submit(() -> {
            for (var discordMessage : collection) {
                var saveStatus = this.save(discordMessage);
                if (saveStatus.isDone() && !saveStatus.get()) {
                    return false;
                }
            }

            return true;
        });
    }

    @Override
    public Future<Boolean> save(DiscordMessage discordMessage) {
        return executorService.submit(() -> {
            try(Connection connection = rocketDiscord.getDatabaseConnection();
                var ps = connection.prepareStatement("INSERT INTO message (author_name, author_id, message_id, content, action, created_at) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, discordMessage.getAuthorName());
                ps.setString(2, discordMessage.getAuthorID());
                ps.setString(3, discordMessage.getMessageID());
                ps.setString(4, discordMessage.getContent());
                ps.setString(5, discordMessage.getAction().name());
                ps.setTimestamp(6, Timestamp.from(discordMessage.getCreatedAt()));

                return ps.executeUpdate() > 0;
            } catch (SQLException exception) {
                logger.error("Cannot load discord message: ", exception);
            }

            return false;
        });
    }

    @Override
    public Future<Boolean> delete(String id) {
        return executorService.submit(() -> {
            try (Connection connection = rocketDiscord.getDatabaseConnection();
                var ps = connection.prepareStatement("DELETE FROM message WHERE message_id = ?")) {
                ps.setString(1, id);

                return ps.executeUpdate() > 0;
            } catch (SQLException exception) {
                logger.error("Cannot delete discord message: ", exception);
            }

            return false;
        });
    }

    @Override
    public Future<Boolean> delete(int id) {
        return executorService.submit(() -> {
            try (Connection connection = rocketDiscord.getDatabaseConnection();
                var ps = connection.prepareStatement("DELETE FROM message WHERE id = ?")) {
                ps.setInt(1, id);

                return ps.executeUpdate() > 0;
            } catch (SQLException exception) {
                logger.error("Cannot delete discord message: ", exception);
            }

            return false;
        });
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
        try(Connection connection = rocketDiscord.getDatabaseConnection();
            var ps = connection.prepareStatement("SELECT COUNT(*) FROM message")) {
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
