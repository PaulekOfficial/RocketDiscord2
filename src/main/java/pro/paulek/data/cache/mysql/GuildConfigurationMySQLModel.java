package pro.paulek.data.cache.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.GuildConfiguration;
import pro.paulek.data.cache.GuildConfigurationCache;
import pro.paulek.data.cache.SQLDataModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class GuildConfigurationMySQLModel implements SQLDataModel<GuildConfiguration, String> {

    private final static Logger logger = LoggerFactory.getLogger(GuildConfigurationCache.class);

    private final IRocketDiscord rocketDiscord;

    public GuildConfigurationMySQLModel(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    @Override
    public GuildConfiguration load(String s) {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("SELECT * FROM `setting` WHERE `guild_id` = ?");
            ps.setString(1, s);
            var resultSet = ps.executeQuery();

            return this.deserializeData(resultSet);
        } catch (SQLException exception) {
            logger.error("Cannot load guild configuration: ", exception);
        }
        return null;
    }

    @Override
    public void createTable() {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps1 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `channel` (`id` int(11) NOT NULL AUTO_INCREMENT, `guild_id` text NOT NULL, `channel_id` text NOT NULL, `type` text NOT NULL, `added_by` text NOT NULL, PRIMARY KEY (`id`))");
            ps1.executeUpdate();

            var ps2 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `setting` (`id` int(11) NOT NULL AUTO_INCREMENT, `guild_id` text NOT NULL, `name` text NOT NULL, `value` text NOT NULL, `timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(), `added_by` text NOT NULL, PRIMARY KEY (`id`))");
            ps2.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Failed to create database tables: ", exception);
        }
    }

    @Override
    public GuildConfiguration load(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<GuildConfiguration> load() {
        List<String> guilds = new ArrayList<>();
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("SELECT DISTINCT `guild_id` FROM `setting`");
            var rs = ps.executeQuery();

            while (rs.next()) {
                var guildID = rs.getString("guild_id");
                guilds.add(guildID);
            }
        } catch (SQLException exception) {
            logger.error("Cannot load guilds: ", exception);
        }

        List<GuildConfiguration> guildConfigurations = new ArrayList<>();
         guilds.forEach(guild -> {
             var settings = this.load(guild);
             if (settings != null) {
                 guildConfigurations.add(settings);
             }
         });

        return guildConfigurations;
    }

    @Override
    public void save(Collection<GuildConfiguration> collection, boolean ignoreNotChanged) {
        collection.forEach(this::serializeData);
    }

    @Override
    public void save(GuildConfiguration guildConfiguration) {
        this.serializeData(guildConfiguration);
    }

    @Override
    public void delete(String s) {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("DELETE * FROM `setting` WHERE `guild_id` = ?");
            ps.setString(1, s);
            ps.executeUpdate();

            var ps2 = connection.prepareStatement("DELETE * FROM `channel` WHERE `guild_id` = ?");
            ps2.setString(1, s);
            ps2.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Cannot count guilds: ", exception);
        }
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSet serializeData(GuildConfiguration guildConfiguration) {
        var guidID = guildConfiguration.getGuildID();

        try (Connection connection = this.rocketDiscord.getDatabaseConnection()){
            this.saveSetting(connection, guidID, "guildID", guidID, "system");
            this.saveSetting(connection, guidID, "guildName", guildConfiguration.getGuildName(), "system");
            this.saveSetting(connection, guidID, "commandsChannelsWhitelistMode", String.valueOf(guildConfiguration.isCommandsChannelsWhitelistMode()), "system");
            this.saveSetting(connection, guidID, "welcomeImageEnable", String.valueOf(guildConfiguration.isWelcomeImageEnable()), "system");
            this.saveSetting(connection, guidID, "leaveImageEnable", String.valueOf(guildConfiguration.isLeaveImageEnable()), "system");
            this.saveSetting(connection, guidID, "welcomeImageMessage", guildConfiguration.getWelcomeImageMessage(), "system");
            this.saveSetting(connection, guidID, "leaveImageMessage", guildConfiguration.getLeaveImageMessage(), "system");
            this.saveSetting(connection, guidID, "welcomeChannel", guildConfiguration.getWelcomeChannel(), "system");
            this.saveSetting(connection, guidID, "announcementsChannel", guildConfiguration.getAnnouncementsChannel(), "system");
            this.saveSetting(connection, guidID, "djRole", guildConfiguration.getDjRole(), "system");

            guildConfiguration.getCommandChannels().forEach(channel -> {
                this.saveChannelValues(connection, guidID, channel, "commandChannels", "system");
            });
            guildConfiguration.getMemesChannels().forEach(channel -> {
                this.saveChannelValues(connection, guidID, channel, "memesChannels", "system");
            });
            guildConfiguration.getAutoVoiceChannels().forEach(channel -> {
                this.saveChannelValues(connection, guidID, channel, "autoVoiceChannels", "system");
            });
            guildConfiguration.getBotAdmins().forEach(channel -> {
                this.saveChannelValues(connection, guidID, channel, "botAdmins", "system");
            });
        } catch (SQLException exception) {
            logger.error("Cannot save guild settings: ", exception);
        }

        return null;
    }

    @Override
    public GuildConfiguration deserializeData(ResultSet resultSet) throws SQLException {
        Map<String, Object> values = new HashMap<>(resultSet.getFetchSize());
        while (resultSet.next()) {
            var name = resultSet.getString("name");
            var value = resultSet.getObject("value");
            values.put(name, value);
        }

        try (Connection connection = this.rocketDiscord.getDatabaseConnection()){

        String guildID = (String) values.get("guildID");
        String guildName = (String) values.get("guildName");

        boolean commandsChannelsWhitelistMode = values.get("commandsChannelsWhitelistMode") != null && Boolean.parseBoolean((String) values.get("commandsChannelsWhitelistMode"));
        boolean welcomeImageEnable = values.get("welcomeImageEnable") != null && Boolean.parseBoolean((String) values.get("welcomeImageEnable"));
        boolean leaveImageEnable = values.get("leaveImageEnable") != null && Boolean.parseBoolean((String) values.get("leaveImageEnable"));
        String welcomeImageMessage = values.get("welcomeImageMessage") != null ? (String) values.get("welcomeImageMessage") : "";
        String leaveImageMessage = values.get("leaveImageMessage") != null ? (String) values.get("leaveImageMessage") : "";
        String welcomeChannel = values.get("welcomeChannel") != null ? (String) values.get("welcomeChannel") : "";
        String announcementsChannel = values.get("announcementsChannel") != null ? (String) values.get("announcementsChannel") : "";
        String djRole = values.get("djRole") != null ? (String) values.get("djRole") : "";

        List<String> commandChannels = this.getChannelValues(connection, guildID, "commandChannels");
        List<String> memesChannels = this.getChannelValues(connection, guildID, "memesChannels");
        List<String> autoVoiceChannels = this.getChannelValues(connection, guildID, "autoVoiceChannels");
        List<String> botAdmins = this.getChannelValues(connection, guildID, "botAdmins");

        return new GuildConfiguration(guildID, guildName, commandsChannelsWhitelistMode, commandChannels, memesChannels, autoVoiceChannels, welcomeImageEnable, leaveImageEnable, welcomeImageMessage, leaveImageMessage, welcomeChannel, announcementsChannel, botAdmins, djRole);
        } catch (SQLException exception) {
            logger.error("Cannot load guild settings: ", exception);
        }
        return null;
    }

    @Override
    public int count() {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("SELECT COUNT(DISTINCT `guild_id`) as `guildCount` FROM `setting`");
            var rs = ps.executeQuery();
            rs.next();
            return  rs.getInt("guildCount");
        } catch (SQLException exception) {
            logger.error("Cannot count guilds: ", exception);
        }
        return 0;
    }

    private void saveSetting(Connection connection, String guildID, String name, String value, String editedBy) {
        try {
            var ps = connection.prepareStatement("INSERT INTO `setting` (`guild_id`, `name`, `value`, `timestamp`, `added_by`) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, guildID);
            ps.setString(2, name);
            ps.setString(3, value);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(5, editedBy);
            ps.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Cannot save setting: ", exception);
        }
    }

    private void saveChannelValues(Connection connection, String guildID, String channelID, String type, String addedBy) {
        try {
            var ps = connection.prepareStatement("INSERT INTO `channel` (`guild_id`, `channel_id`, `type`, `added_by`) VALUES (?, ?, ?, ?)");
            ps.setString(1, guildID);
            ps.setString(2, channelID);
            ps.setString(3, type);
            ps.setString(4, addedBy);
            ps.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Cannot save channel values: ", exception);
        }
    }

    private List<String> getChannelValues(Connection connection, String guildID, String type) {
        List<String> arrayList = new ArrayList<>();
        try {
            var ps = connection.prepareStatement("SELECT `channel_id` FROM `channel` WHERE `guild_id` = ? AND `type` = ?");
            ps.setString(1, guildID);
            ps.setString(2, type);
            var values = ps.executeQuery();
            while (values.next()) {
                arrayList.add(values.getString("channel_id"));
            }
        } catch (SQLException exception) {
            logger.error("Cannot get channel values: ", exception);
        }
        return arrayList;
    }
}
