package pro.paulek;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.commands.CommandManager;
import pro.paulek.commands.HelpCommand;
import pro.paulek.listeners.LoggingListeners;
import pro.paulek.listeners.RandomFunctionsListeners;

import javax.security.auth.login.LoginException;

public class RocketDiscord {

    private final static Logger logger = LoggerFactory.getLogger(RocketDiscord.class);

    private JDA jda;
    private JDABuilder jdaBuilder;
    private BotConfiguration configuration;

    private CommandManager commandManager;

    public RocketDiscord(BotConfiguration configuration) {
        this.configuration = configuration;
    }

    public void prepareJDA() {
        jdaBuilder = JDABuilder.createDefault(configuration.getEndpoint());

        //Load commands
        commandManager = new CommandManager(this);
        commandManager.addCommand(new HelpCommand());

        //Load listeners
        jdaBuilder.addEventListeners(commandManager);
        jdaBuilder.addEventListeners(new RandomFunctionsListeners());
        jdaBuilder.addEventListeners(new LoggingListeners());

        jdaBuilder.setActivity(Activity.streaming(configuration.getStatus(), "https://paulek.pro/rocketdiscord"));
    }

    public void start() {
        try {
            jdaBuilder.build();
        } catch (LoginException exception) {
            logger.error("Cannot build JDA application", exception);
            return;
        }
        logger.info("Bot started!");
    }

    public JDA getJda() {
        return jda;
    }

    public BotConfiguration getConfiguration() {
        return configuration;
    }
}
