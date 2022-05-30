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
        return null;
    }

    @Override
    public void createTable() {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps1 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `channel` (`id` int(11) NOT NULL, `guild_id` text NOT NULL, `channel_id` text NOT NULL, `type` text NOT NULL, `added_by` int(11) NOT NULL)");
            ps1.executeUpdate();

            var ps2 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `setting` (`id` int(11) NOT NULL, `guild_id` text NOT NULL, `name` text NOT NULL, `value` text NOT NULL, `timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(), `added_by` text NOT NULL)");
            ps2.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Failed to create database tables: ", exception);
        }
    }

    @Override
    public GuildConfiguration load(int id) {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("SELECT * FROM `setting` WHERE `id` = ?");
            ps.setInt(1, id);
            var resultSet = ps.executeQuery();

            return this.deserializeData(resultSet);
        } catch (SQLException exception) {
            logger.error("Cannot load guild configuration: ", exception);
        }
        return null;
    }

    @Override
    public Collection<GuildConfiguration> load() {
        var guildCount = this.count();
        List<GuildConfiguration> guildConfigurations = new ArrayList<>(guildCount - 1);
        for (int i = 0; i < guildCount; i++) {
            guildConfigurations.add(this.load(i));
        }

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

        return null;
    }

    @Override
    public GuildConfiguration deserializeData(ResultSet resultSet) throws SQLException {
        String guildID = resultSet.getString("guildID");
        String guildName = resultSet.getString("guildName");

        boolean commandsChannelsWhitelistMode = resultSet.getBoolean("commandsChannelsWhitelistMode");
        boolean welcomeImageEnable = resultSet.getBoolean("welcomeImageEnable");
        boolean leaveImageEnable = resultSet.getBoolean("leaveImageEnable");
        String welcomeImageMessage = resultSet.getString("welcomeImageMessage");
        String leaveImageMessage = resultSet.getString("leaveImageMessage");
        String welcomeChannel = resultSet.getString("welcomeChannel");
        String announcementsChannel = resultSet.getString("announcementsChannel");
        String djRole = resultSet.getString("djRole");

        List<String> commandChannels = this.getChannelValues(guildID, "commandChannels");
        List<String> memesChannels = this.getChannelValues(guildID, "memesChannels");
        List<String> autoVoiceChannels = this.getChannelValues(guildID, "autoVoiceChannels");
        List<String> botAdmins = this.getChannelValues(guildID, "botAdmins");

        return new GuildConfiguration(guildID, guildName, commandsChannelsWhitelistMode, commandChannels, memesChannels, autoVoiceChannels, welcomeImageEnable, leaveImageEnable, welcomeImageMessage, leaveImageMessage, welcomeChannel, announcementsChannel, botAdmins, djRole);
    }

    @Override
    public int count() {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("SELECT COUNT(DISTINCT `guild_id`) as `guildCount` FROM `setting`");
            return ps.executeQuery().getInt("guildCount");
        } catch (SQLException exception) {
            logger.error("Cannot count guilds: ", exception);
        }
        return 0;
    }

    private void saveSetting(String guildID, String name, String value, String editedBy) {
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("INSERT INTO `setting` (`guild_id`, `name`, `value`, `timestamp`, `added_by`) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, guildID);
            ps.setString(2, name);
            ps.setString(3, value);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(5, "system");
            ps.executeQuery();
        } catch (SQLException exception) {
            logger.error("Cannot save setting: ", exception);
        }
    }

    private List<String> getChannelValues(String guildID, String type) {
        List<String> arrayList = new ArrayList<>();
        try(Connection connection = rocketDiscord.getDatabaseConnection()) {
            var ps = connection.prepareStatement("SELECT `value` FROM `channel` WHERE `guild_id` = ? AND `type` = ?");
            ps.setString(1, guildID);
            ps.setString(2, type);
            var values = ps.executeQuery();
            while (values.next()) {
                arrayList.add(values.getString("value"));
            }
        } catch (SQLException exception) {
            logger.error("Cannot get channel values: ", exception);
        }
        return arrayList;
    }
}
