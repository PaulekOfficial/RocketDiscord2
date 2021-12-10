package pro.paulek;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import pro.paulek.commands.CommandManager;
import pro.paulek.commands.HelpCommand;
import pro.paulek.data.Configuration;
import pro.paulek.data.api.DataModel;
import pro.paulek.database.Database;
import pro.paulek.database.MySQL;
import pro.paulek.database.SQLite;
import pro.paulek.listeners.LoggingListeners;
import pro.paulek.listeners.RandomFunctionsListeners;

import javax.security.auth.login.LoginException;
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

    private Configuration configuration;
    private CommandManager commandManager;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public RocketDiscord() {
    }

    @Override
    public void init() {
        //Load bot general configuration
        try {
            var inputStream = new FileInputStream(new File("settings.yml"));

            var yaml = new Yaml(new Constructor(Configuration.class));
            configuration = yaml.load(inputStream);
        } catch (FileNotFoundException exception) {
            logger.error("Cannot load settings.yml", exception);
        }

        //Load database configuration and connect to it
        this.database = createDatabase();

        //Create jda builder
        jdaBuilder = JDABuilder.createDefault(configuration.getEndpoint());

        //Load commands
        commandManager = new CommandManager(this);
        commandManager.addCommand(new HelpCommand());

        //Load listeners
        jdaBuilder.addEventListeners(commandManager);
        jdaBuilder.addEventListeners(new RandomFunctionsListeners());
        jdaBuilder.addEventListeners(new LoggingListeners());

        //Set activity of this bot
        jdaBuilder.setActivity(Activity.streaming(configuration.getStatus(), "https://paulek.pro/rocketdiscord"));
    }

    @Override
    public void start() {
        //Starts JDA and connect to discord api
        try {
            jdaBuilder.build();
        } catch (LoginException exception) {
            logger.error("Cannot build JDA application", exception);
            return;
        }
        logger.info("Bot started!");
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
    public Connection getDatabaseConnection() throws SQLException {
        return database.getConnection();
    }
}
