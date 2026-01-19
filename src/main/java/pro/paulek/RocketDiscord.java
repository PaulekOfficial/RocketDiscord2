package pro.paulek;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.FileUpload;
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
import pro.paulek.data.DataModel;
import pro.paulek.data.ICache;
import pro.paulek.data.cache.DiscordMessageCache;
import pro.paulek.data.cache.GuildConfigurationICache;
import pro.paulek.data.cache.MusicPlayerCache;
import pro.paulek.database.Database;
import pro.paulek.database.MySQL;
import pro.paulek.database.SQLite;
import pro.paulek.listeners.WelcomeListener;
import pro.paulek.listeners.commands.MusicButtonListener;
import pro.paulek.listeners.commands.SlashCommandListener;
import pro.paulek.listeners.fun.MemesListeners;
import pro.paulek.listeners.fun.RandomFunctionsListeners;
import pro.paulek.listeners.modlog.LoggingListeners;
import pro.paulek.listeners.modlog.ModLogListener;
import pro.paulek.managers.MusicManager;
import pro.paulek.managers.RocketPlayerManager;
import pro.paulek.objects.Configuration;
import pro.paulek.objects.guild.DiscordMessage;
import pro.paulek.util.ImageGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class RocketDiscord implements IRocketDiscord {
    private final static Logger logger = LoggerFactory.getLogger(RocketDiscord.class);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private JDA jda;
    private JDABuilder jdaBuilder;
    private Database database;
    private DataModel dataModel;
    private GuildConfigurationICache guildConfigurationCache;
    private Configuration configuration;
    private CommandManager commandManager;
    private RocketPlayerManager audioPlayerManager;
    private ICache<MusicManager, String> musicManager;
    private ICache<DiscordMessage, String> discordMessageCache;

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

        //Initialize play manager
        audioPlayerManager = new RocketPlayerManager(this);
        audioPlayerManager.initialize();

        //Create jda builder
        logger.info("Initializing discord gateway...");
        jdaBuilder = JDABuilder.createDefault(configuration.getEndpoint());

        //Intends
        jdaBuilder.enableIntents(
                GatewayIntent.GUILD_MEMBERS,
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
        commandManager.addCommand(new PauseCommand(this));
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
        jdaBuilder.addEventListeners(new ModLogListener(this));
        jdaBuilder.addEventListeners(new SlashCommandListener(this));
        jdaBuilder.addEventListeners(new MemesListeners(this));
        jdaBuilder.addEventListeners(new WelcomeListener(this));
        jdaBuilder.addEventListeners(new MusicButtonListener(this));

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
        List<Command> existingCommands = jda.retrieveCommands().complete();

        commandManager.getCommandList().values().forEach(command -> {
            List<Command> matchingCommands = existingCommands.stream()
                    .filter(existingCommand -> existingCommand.getName().equalsIgnoreCase(command.getName()))
                    .toList();

            if (matchingCommands.size() > 1) {
                matchingCommands.forEach(matchingCommand -> jda.deleteCommandById(matchingCommand.getId()).queue());
                jda.upsertCommand(command.getCommandData()).queue();
                return;
            }

            if (!matchingCommands.isEmpty()) {
                Command matchingCommand = matchingCommands.get(0);
                if (!matchingCommand.getOptions().equals(command.getCommandData().getOptions()) || !matchingCommand.getDescription().equals(command.getCommandData().getDescription())) {
                    jda.deleteCommandById(matchingCommand.getId()).queue();
                    jda.upsertCommand(command.getCommandData()).queue();
                    return;
                }
            }

            jda.upsertCommand(command.getCommandData()).queue();
        });

        jda.updateCommands().queue();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
               jda.getGuilds().stream().filter(guild -> guild.getId().equalsIgnoreCase("740276300815663105")).forEach(guild -> {
                   if (!guild.getCategoriesByName("RocketDiscord Configuration", true).isEmpty()) {
                       return;
                   }

                   guild.createCategory("RocketDiscord Configuration")
                           .setPosition(0)
                           .addPermissionOverride(guild.getPublicRole(), 0, Permission.VIEW_CHANNEL.getRawValue()) // Deny VIEW_CHANNEL permission for @everyone role
//                           .addPermissionOverride(guild.getOwner().getRoles().getFirst(), 0, Permission.ADMINISTRATOR) // Grant all permissions to the guild owner
                           .queue(category -> {
                       guild.createTextChannel("initial-bot-setup")
                               .setParent(category)
                               .queue(textChannel -> {
                                   try {
                                       var image = ImageIO.read(Objects.requireNonNull(RocketDiscord.class.getClassLoader().getResource("welcome-text.jpg")));
                                       var tempFile = File.createTempFile("welcome-text", ".jpg");

                                       ImageIO.write(image, "png", tempFile);
                                       textChannel.sendFiles(FileUpload.fromData(tempFile)).queue();

                                       tempFile.delete();
                                   } catch (IOException e) {
                                       throw new RuntimeException(e);
                                   }

                                   String ownerMention = guild.getOwner().getAsMention();
                                   EmbedBuilder embedBuilder = new EmbedBuilder();
                                   embedBuilder.setAuthor("RocketDiscord", null, "https://cdn.discordapp.com/avatars/770759255459627010/5dd6b70a5adcc9fcc94d248972e58afe?size=1024");
                                   embedBuilder.setThumbnail("https://cdn.discordapp.com/attachments/602973050190954506/1197518975228182529/b527b167-626f-46d4-a0b1-e8cff035311c.jpg?ex=662e41f4&is=662cf074&hm=a7fb72ba3d109735d8f41f6ce6e143585ad6f5da6ed512ff6f49512d571bcf35&");
                                   embedBuilder.setDescription("Welcome to RocketDiscord! " + ownerMention + " This is a bot that can play music, moderate your server, and more! To get started, please follow the instructions below.");
                                   embedBuilder.addField("Version", "ALPHA", false);
                                   embedBuilder.setColor(Color.GREEN);
                                   embedBuilder.setTimestamp(Instant.now());

                                   textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
                               });
                   });
               });
            }
        }, 10 * 1000);
    }

    @Override
    public Database createDatabase() {
        dataModel = DataModel.getModelByName(configuration.getStorageType());
        if (dataModel.equals(DataModel.MYSQL)) {
            MySQL mySQL = new MySQL(configuration.getMysql());
            mySQL.init();
            return mySQL;
        }

        File databaseFile = new File("database.db");
        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException exception) {
                logger.error("Cannot create SQLite database file", exception);
            }
        }
        SQLite sqLite = new SQLite(databaseFile);
        sqLite.init();
        return sqLite;
    }

    @Override
    public Optional<User> getJDAUser(String discordID) {
        var user = jda.getUserById(discordID);
        if (user != null) {
            return Optional.of(user);
        }

        return Optional.empty();
    }

    @Override
    public Future<User.Profile> getUserProfile(String discordID) {
        CompletableFuture<User.Profile> completableFuture = new CompletableFuture<>();

        executorService.submit(() -> {
            var user = this.getJDAUser(discordID);
            if (user.isEmpty()) {
                return;
            }

            var restProfile = user.get().retrieveProfile();
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
    public Optional<DiscordMessage> getDiscordMessage(String id) {
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
    public Optional<MusicManager> getMusicManager(String guildID) {
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
