package pro.paulek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        BotConfiguration configuration = null;
        try {
            var inputStream = new FileInputStream(new File("settings.yml"));

            var yaml = new Yaml(new Constructor(BotConfiguration.class));
            configuration = yaml.load(inputStream);
        } catch (FileNotFoundException exception) {
            logger.error("Cannot load settings.yml", exception);
        }

        var rocket = new RocketDiscord(configuration);
        rocket.prepareJDA();
        rocket.start();
    }
}
