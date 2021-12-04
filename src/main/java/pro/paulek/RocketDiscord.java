package pro.paulek;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class RocketDiscord {

    private static Logger logger = LoggerFactory.getLogger(RocketDiscord.class);

    private JDA jda;
    private JDABuilder jdaBuilder;
    private BotConfiguration configuration;

    public RocketDiscord(BotConfiguration configuration) {
        this.configuration = configuration;
    }

    public void prepareJDA() {
        jdaBuilder = JDABuilder.createDefault(configuration.getEndpoint());

        //Load listeners
        this.loadListeners();

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

    private void loadListeners() {
        jdaBuilder.addEventListeners(new RandomFunctionsListeners());
    }

}
