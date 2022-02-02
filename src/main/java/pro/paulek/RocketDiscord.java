package pro.paulek;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import pro.paulek.commands.*;
import pro.paulek.commands.admin.DeleteMessagesCommand;
import pro.paulek.commands.music.*;
import pro.paulek.data.Configuration;
import pro.paulek.data.GuildConfiguration;
import pro.paulek.data.MusicPlayerCache;
import pro.paulek.data.api.Cache;
import pro.paulek.data.api.DataModel;
import pro.paulek.data.cache.GuildConfigurationCache;
import pro.paulek.database.Database;
import pro.paulek.database.MySQL;
import pro.paulek.database.SQLite;
import pro.paulek.listeners.LoggingListeners;
import pro.paulek.listeners.MemesListeners;
import pro.paulek.listeners.RandomFunctionsListeners;
import pro.paulek.listeners.SplashCommandListener;
import pro.paulek.objects.MusicManager;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class RocketDiscord implements IRocketDiscord {

    private final static Logger logger = LoggerFactory.getLogger(RocketDiscord.class);

    private JDA jda;
    private JDABuilder jdaBuilder;

    private Database database;
    private DataModel dataModel;

    private GuildConfigurationCache guildConfigurationCache;

    private Configuration configuration;
    private CommandManager commandManager;

    private AudioPlayerManager audioPlayerManager;

    private Cache<MusicManager, String> musicManager;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public RocketDiscord() {
    }

    @Override
    public void init() {
        //Load bot general configuration
        logger.info("Loading bot settings...");
        try {
            var inputStream = new FileInputStream(new File("settings.yml"));

            var yaml = new Yaml(new Constructor(Configuration.class));
            configuration = yaml.load(inputStream);
        } catch (FileNotFoundException exception) {
            logger.error("Cannot load settingsw.yml", exception);
        }

        //Load database configuration and connect to it
        logger.info("Initializing database...");
        this.database = createDatabase();

        //Load all caches
        guildConfigurationCache = new GuildConfigurationCache(this);
        guildConfigurationCache.init();

        //TODO remove this only for tests
        guildConfigurationCache.add("740276300815663105",
                new GuildConfiguration("740276300815663105", "INF INC. | PaulekLab", true,
                        Collections.singletonList("938543061192024064"), Collections.singletonList("938540794162335754"), Collections.emptyList(), "938538241953497088",
                        "938537678008377405", Collections.emptyList(), ""));

        //Create jda builder
        logger.info("Initializing discord gateway...");
        jdaBuilder = JDABuilder.createDefault(configuration.getEndpoint());

        //Set activity of this bot
        jdaBuilder.setActivity(Activity.streaming(configuration.getStatus(), "https://paulek.pro/rocketdiscord"));

        //Load commands
        logger.info("Initializing bot commands...");
        commandManager = new CommandManager(this);
        commandManager.addCommand(new HelpCommand(this));
        commandManager.addCommand(new PlayCommand(this));
        commandManager.addCommand(new VolumeCommand(this));
        commandManager.addCommand(new StopCommand(this));
        commandManager.addCommand(new LeaveCommand(this));
        commandManager.addCommand(new JoinCommand(this));
        commandManager.addCommand(new DeleteMessagesCommand(this));

        //Load listeners
        logger.info("Initializing bot listeners...");
        jdaBuilder.addEventListeners(commandManager);
        jdaBuilder.addEventListeners(new RandomFunctionsListeners());
        jdaBuilder.addEventListeners(new LoggingListeners());
        jdaBuilder.addEventListeners(new SplashCommandListener(this));
        jdaBuilder.addEventListeners(new MemesListeners(this));

        //Load music manager
        audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);

        //Load cache
        musicManager = new MusicPlayerCache(this);

        logger.info("Initialization complete");
    }

    @Override
    public void start() {
        //Starts JDA and connect to discord api
        logger.info("Starting JDA Websocket");
        try {
            jda = jdaBuilder.build();
        } catch (LoginException exception) {
            logger.error("Cannot build JDA application", exception);
            return;
        }

        this.registerSplashCommands();

        logger.info("Bot started!");
    }

    public void registerSplashCommands() {
        commandManager.getCommandList().values().forEach(command -> jda.upsertCommand(command.getCommandData()).queue());

        jda.updateCommands().queue();
    }

    @Override
    public Database createDatabase(){
        dataModel = DataModel.getModelByName(configuration.getStorageType());
        if(dataModel.equals(DataModel.MYSQL)) {
            MySQL mySQL = new MySQL(configuration.getMysql());
            mySQL.init();
            return mySQL;
        }

        File databaseFile = new File("database.db");
        if(!databaseFile.exists()){
            try {
                databaseFile.createNewFile();
            } catch (IOException exception){
                logger.error("Cannot create SQLite database file", exception);
            }
        }
        SQLite sqLite = new SQLite(databaseFile);
        sqLite.init();
        return sqLite;
    }

    @Override
    public User getJDAUser(String discordID) {
        return jda.getUserById(discordID);
    }

    @Override
    public Future<User.Profile> getUserProfile(String discordID) {
        CompletableFuture<User.Profile> completableFuture = new CompletableFuture<>();

        executorService.submit(() -> {
            var user = this.getJDAUser(discordID);
            if(user == null) {
                return;
            }
            var restProfile = user.retrieveProfile();
            restProfile.timeout(5, TimeUnit.SECONDS);
            completableFuture.complete(restProfile.complete());
        });

        return completableFuture;
    }

    @Override
    public Future<User.Profile> getUserProfile(User user) {
        CompletableFuture<User.Profile> completableFuture = new CompletableFuture<>();

        executorService.submit(() -> {
            var restProfile = user.retrieveProfile();
            restProfile.timeout(5, TimeUnit.SECONDS);
            completableFuture.complete(restProfile.complete());
        });

        return completableFuture;
    }

    @Override
    public AudioPlayerManager getAudioManager() {
        return audioPlayerManager;
    }

    @Override
    public Cache<MusicManager, String> getMusicManagers() {
        return musicManager;
    }

    @Override
    public MusicManager getMusicManager(String guildID) {
        return musicManager.get(guildID);
    }

    @Override
    public DataModel getDataModel() {
        return dataModel;
    }

    @Override
    public JDA getJda() {
        return jda;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public GuildConfigurationCache getGuildConfigurations() {
        return guildConfigurationCache;
    }

    @Override
    public Connection getDatabaseConnection() throws SQLException {
        return database.getConnection();
    }
}
