package pro.paulek.data.api;

import org.slf4j.Logger;
import pro.paulek.IRocketDiscord;

public interface Cache<T, U>{

    /**
     * Initialise cache class
     */
    void init(IRocketDiscord rocketDiscord, Logger logger);

    /**
     * Get a value from cache
     * @param u
     * @return
     */
    T get(U u);

    /**
     * Add a object by key
     * @param u
     * @param t
     */
    void add(U u,T t);

    /**
     * Permanency deletes object by key
     * @param u
     */
    void delete(U u);

    /**
     * Removes object from cache but can be still loaded from database
     * @param u
     */
    void remove(U u);

    /**
     * Saves object by key to cache, and to database sometimes
     * @param u
     * @param t
     */
    void save(U u, T t);

    /**
     * Saves object to database if not null by keu U
     * @param u
     */
    void save(U u);

}
