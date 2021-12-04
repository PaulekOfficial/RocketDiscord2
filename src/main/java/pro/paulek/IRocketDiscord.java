package pro.paulek;

import net.dv8tion.jda.api.JDA;
import pro.paulek.commands.CommandManager;
import pro.paulek.data.Configuration;
import pro.paulek.data.DataModel;
import pro.paulek.database.Database;

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
}
