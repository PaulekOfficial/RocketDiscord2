package pro.paulek;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import pro.paulek.commands.CommandManager;
import pro.paulek.commands.HelpCommand;
import pro.paulek.commands.admin.DeleteMessagesCommand;
import pro.paulek.commands.fun.Rule34Command;
import pro.paulek.commands.music.*;
import pro.paulek.data.cache.DiscordMessageCache;
import pro.paulek.objects.Configuration;
import pro.paulek.data.cache.MusicPlayerCache;
import pro.paulek.data.ICache;
import pro.paulek.data.DataModel;
import pro.paulek.data.cache.GuildConfigurationICache;
import pro.paulek.database.Database;
import pro.paulek.database.MySQL;
import pro.paulek.database.SQLite;
import pro.paulek.listeners.*;
import pro.paulek.listeners.commands.SplashCommandListener;
import pro.paulek.listeners.fun.MemesListeners;
import pro.paulek.listeners.fun.RandomFunctionsListeners;
import pro.paulek.listeners.modlog.LoggingListeners;
import pro.paulek.objects.MusicManager;
import pro.paulek.objects.guild.DiscordMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.*;

public class RocketDiscord implements IRocketDiscord {
    private final static Logger logger = LoggerFactory.getLogger(RocketDiscord.class);

    private JDA jda;
    private JDABuilder jdaBuilder;

    private Database database;
    private DataModel dataModel;

    private GuildConfigurationICache guildConfigurationCache;

    private Configuration configuration;
    private CommandManager commandManager;

    private AudioPlayerManager audioPlayerManager;

    private ICache<MusicManager, String> musicManager;
    private ICache<DiscordMessage, String> discordMessageCache;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public RocketDiscord() {
    }

    @Override
    public void init() {
        //Load bot general configuration
        logger.info("Loading bot settings...");
        try {
            var inputStream = new FileInputStream(new File("settings.yml"));

            var yaml = new Yaml(new Constructor(new LoaderOptions()));
            configuration = yaml.loadAs(inputStream, Configuration.class);
        } catch (FileNotFoundException exception) {
            logger.error("Cannot load settings.yml", exception);
        }

        //Load database configuration and connect to it
        logger.info("Initializing database...");
        this.database = createDatabase();

        //Load all caches
        guildConfigurationCache = new GuildConfigurationICache(this);
        guildConfigurationCache.init();

        discordMessageCache = new DiscordMessageCache(this);
        discordMessageCache.init();

        //Create jda builder
        logger.info("Initializing discord gateway...");
        jdaBuilder = JDABuilder.createDefault(configuration.getEndpoint());

        //Intends
        jdaBuilder.enableIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGE_TYPING,
                GatewayIntent.MESSAGE_CONTENT);

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
        commandManager.addCommand(new SkipCommand(this));
        commandManager.addCommand(new DeleteMessagesCommand(this));
        commandManager.addCommand(new QueueCommand(this));
        commandManager.addCommand(new RepeatCommand(this));
        commandManager.addCommand(new Rule34Command(this));

        //Load listeners
        logger.info("Initializing bot listeners...");
        jdaBuilder.addEventListeners(commandManager);
        jdaBuilder.addEventListeners(new RandomFunctionsListeners());
        jdaBuilder.addEventListeners(new LoggingListeners(this));
        jdaBuilder.addEventListeners(new SplashCommandListener(this));
        jdaBuilder.addEventListeners(new MemesListeners(this));
        jdaBuilder.addEventListeners(new WelcomeListener(this));

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
        } catch (Exception exception) {
            logger.error("Cannot build JDA application", exception);
            return;
        }

        this.registerSplashCommands();

        logger.info("Bot started!");
    }

    public void registerSplashCommands() {

        // For debug only
//        jda.retrieveCommands().complete().forEach(cmd -> {
//            jda.deleteCommandById(cmd.getId()).complete();
//        });

        var commands = jda.retrieveCommands().complete();
        commandManager.getCommandList().values().forEach(command -> {
            boolean match = false;
            for (Command cmd : commands) {
                if (cmd.getName().equalsIgnoreCase(command.getName())) {
                    match = true;
                    return;
                }
            }

            if (!match) jda.upsertCommand(command.getCommandData()).queue();
        });
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
    public ICache<DiscordMessage, String> getDiscordMessages() {
        return this.discordMessageCache;
    }

    @Override
    public DiscordMessage getDiscordMessage(String id) {
        return this.discordMessageCache.get(id);
    }

    @Override
    public AudioPlayerManager getAudioManager() {
        return audioPlayerManager;
    }

    @Override
    public ICache<MusicManager, String> getMusicManagers() {
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
    public GuildConfigurationICache getGuildConfigurations() {
        return guildConfigurationCache;
    }

    @Override
    public Connection getDatabaseConnection() throws SQLException {
        return database.getConnection();
    }
}
