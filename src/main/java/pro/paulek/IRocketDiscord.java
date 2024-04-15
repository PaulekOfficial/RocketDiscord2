package pro.paulek;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import pro.paulek.commands.CommandManager;
import pro.paulek.data.DataModel;
import pro.paulek.data.ICache;
import pro.paulek.data.cache.GuildConfigurationICache;
import pro.paulek.database.Database;
import pro.paulek.objects.Configuration;
import pro.paulek.managers.MusicManager;
import pro.paulek.objects.guild.DiscordMessage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.Future;

public interface IRocketDiscord {

    /**
     * Initializes all application functions, after this state everything
     * is ready to lift off
     */
    void init();

    /**
     * Starts JDA and connects application to discord endpoint
     */
    void start();

    /**
     * Creates database according to #DataModel
     * @return
     */
    Database createDatabase();

    /**
     * Get current application storage method
     * @return
     */
    DataModel getDataModel();

    /**
     * Returns JDA api main class
     * @return
     */
    JDA getJda();

    /**
     * Returns the general bot configuration
     * @return
     */
    Configuration getConfiguration();

    /**
     * Return command manager that holds all the commands
     * @return
     */
    CommandManager getCommandManager();

    /**
     * Returns database connection to chosen database type
     * @return
     * @throws SQLException
     */
    Connection getDatabaseConnection() throws SQLException;

    /**
     * Gets user data from discord id
     * @param discordID
     * @return
     */
    Optional<User> getJDAUser(String discordID);

    /**
     * Gets user profile from discord id
     * @param discordID
     * @return
     */
    Future<User.Profile> getUserProfile(String discordID);

    /**
     * Gets user profile from discord user
     * @param user
     * @return
     */
    Future<User.Profile> getUserProfile(User user);

    /**
     * Gets music manager for guilds
     * @return
     */
    ICache<MusicManager, String> getMusicManagers();


    /**
     * Gets discord messages cache
     * @return
     */
    ICache<DiscordMessage, String> getDiscordMessages();

    /**
     * Gets discord message by id
     * @param id
     * @return
     */
    Optional<DiscordMessage> getDiscordMessage(String id);

    /**
     * Gets music manager for guild
     * @return
     */
    Optional<MusicManager> getMusicManager(String guildID);

    /**
     * Get audio manager
     * @return
     */
    AudioPlayerManager getAudioManager();

    /**
     * Get guild configurations
     * @return
     */
    GuildConfigurationICache getGuildConfigurations();
}
