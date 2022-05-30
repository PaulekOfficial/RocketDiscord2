package pro.paulek;

import org.apache.log4j.*;
import org.apache.logging.log4j.core.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        //Init log4j
        BasicConfigurator.configure();

        File logFile = new File("latest.log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException exception) {
                logger.error("Cannot create latest.log file", exception);
                return;
            }
        }
        var rootLogger = org.apache.log4j.Logger.getRootLogger();
        try {
            rootLogger.addAppender(new FileAppender(new PatternLayout("%r [%t] %p %c %x - %m%n"), logFile.getAbsolutePath(), true));
        } catch (IOException exception) {
            logger.error("Cannot create latest.log file appender for log4j", exception);
            return;
        }


        //Load settings.yml file if not exists
        File settingsFile = new File("settings.yml");
        if (!settingsFile.exists()) {
            logger.info("settings.yml does not exists, creating a new one...");
            var is = ClassLoader.getSystemResourceAsStream("settings.yml");
            assert is != null;
            var reader = new InputStreamReader(is);

            try {
                var created = settingsFile.createNewFile();
                if (created) {
                    logger.info("Created empty file...");
                }
            } catch (IOException exception) {
                logger.error("Cannot create settings.yml file", exception);
            }

            try(var os = new FileWriter(settingsFile)){
                IOUtils.copy(reader, os);
            } catch (FileNotFoundException exception) {
                logger.error("Could not find settings.yml in order to copy from pattern", exception);
            } catch (IOException exception) {
                logger.error("Could not copy settings.yml ", exception);
            }
            logger.info("File created successfully!");
        }

        //Load RocketDiscord
        logger.info("Loading RocketDiscord instance...");
        var rocket = new RocketDiscord();
        rocket.init();
        rocket.start();
    }
}
