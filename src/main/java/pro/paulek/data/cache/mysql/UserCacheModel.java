package pro.paulek.data.cache.mysql;

import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.cache.SQLDataModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class UserCacheModel implements SQLDataModel<User, String> {

    private final static Logger logger = LoggerFactory.getLogger(UserCacheModel.class);

    private final IRocketDiscord rocketDiscord;

    public UserCacheModel(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = rocketDiscord;
    }

    @Override
    public User load(String s) {
        try (Connection connection = rocketDiscord.getDatabaseConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `user_data` WHERE `discord_id`=? ORDER BY `timestamp` DESC LIMIT 1");
            preparedStatement.setString(1, s);

            ResultSet resultSet = preparedStatement.executeQuery();
            return this.deserializeData(resultSet);
        } catch (SQLException exception) {
            logger.error("Cannot get user by snowflake form user_data table", exception);
        }
        return null;
    }

    @Override
    public void createTable() {
        try (Connection connection = rocketDiscord.getDatabaseConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `user_data` ( `id` INT NOT NULL AUTO_INCREMENT , `discord_id` VARCHAR(64) NOT NULL , `username` MEDIUMTEXT NOT NULL , `avatar` TEXT NULL , `bot` BOOLEAN NULL , `system` BOOLEAN NULL , `mfa_enabled` BOOLEAN NULL , `banner` TEXT NULL , `accent_color` INT NULL , `locale` TINYTEXT NULL , `verified` BOOLEAN NULL , `email` TEXT NULL , `flags` INT NULL , `premium_type` INT NULL , `public_flags` INT NULL , `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , PRIMARY KEY (`id`))");
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Cannot create user_data table", exception);
        }
    }


    @Override
    public User load(int id) {
        try (Connection connection = rocketDiscord.getDatabaseConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `user_data` WHERE `id`=? ORDER BY `timestamp` DESC LIMIT 1");
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
        } catch (SQLException exception) {
            logger.error("Cannot get user by id form user_data table", exception);
        }
        return null;
    }

    @Override
    public void load() {
        try (Connection connection = rocketDiscord.getDatabaseConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `user_data` ORDER BY `timestamp` DESC");

            ResultSet resultSet = preparedStatement.executeQuery();
        } catch (SQLException exception) {
            logger.error("Cannot load users form user_data table", exception);
        }
    }

    @Override
    public void save(Collection<User> collection, boolean ignoreNotChanged) {
        for (User user : collection) {
            this.serializeData(user);
        }
    }

    @Override
    public void save(User user) {
        this.serializeData(user);
    }

    @Override
    public void delete(String s) {
        try (Connection connection = rocketDiscord.getDatabaseConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE * FROM `user_data` WHERE `discord_id`=? ORDER BY `timestamp` DESC LIMIT 1");
            preparedStatement.setString(1, s);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Cannot delete user by snowflake form user_data table", exception);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = rocketDiscord.getDatabaseConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE * FROM `user_data` WHERE `id`=? ORDER BY `timestamp` DESC LIMIT 1");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Cannot delete user by id form user_data table", exception);
        }
    }

    @Override
    public ResultSet serializeData(User user) {
        try (Connection connection = rocketDiscord.getDatabaseConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `user_data` SET `discord_id`=?, `username`=? , `avatar`=?, `bot`=?, `system`=?, `mfa_enabled`=?, `banner`=?, `accent_color`=?, `locale`=?, `verified`=?, `email`=?, `flags`=?, `premium_type`=?, `public_flags`=?");
            preparedStatement.setString(1, user.getId());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getAvatarUrl());
            preparedStatement.setBoolean(4, user.isBot());
            preparedStatement.setBoolean(5, user.isSystem());
            preparedStatement.setBoolean(6, false); //TODO get mfa info
            preparedStatement.setString(7, ""); //TODO get banner info
            preparedStatement.setInt(8, 0); //TODO get accent color info
            preparedStatement.setString(9, ""); //TODO get accent color info
            preparedStatement.setBoolean(10, false); //TODO get verification info
            preparedStatement.setString(11, ""); //TODO get email info
            preparedStatement.setInt(12, user.getFlagsRaw());
            preparedStatement.setInt(13, 0); //TODO get premium type
            preparedStatement.setInt(14, 0); //TODO get public flags

            return preparedStatement.executeQuery();
        } catch (SQLException exception) {
            logger.error("Cannot save user to user_data table", exception);
        }
        return null;
    }

    @Override
    public User deserializeData(ResultSet resultSet) throws SQLException {
        return null;
    }

    @Override
    public int count() {
        try (Connection connection = rocketDiscord.getDatabaseConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `user_data`(id)");
            ResultSet resultSet = preparedStatement.executeQuery();
            int i = 0;
            while (resultSet.next()) {
                i++;
            }
            return i;
        } catch (SQLException exception) {
            logger.error("Cannot delete user by id form user_data table", exception);
        }
        return 0;
    }
}
