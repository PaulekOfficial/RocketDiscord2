package pro.paulek.data;

import java.util.Optional;
import java.util.concurrent.Future;

public interface ICache<T, U> {
    /**
     * Initialise cache class
     */
    void init();

    /**
     * Get a value from cache
     *
     * @param u
     * @return
     */
    Optional<T> get(U u);

    /**
     * Adds object to cache
     *
     * @param u
     * @param t
     */
    boolean add(U u, T t);

    /**
     * Permanency deletes object by key
     *
     * @param u
     */
    Future<Boolean> deleteFromDatabase(U u);

    /**
     * Removes object from cache but can be still loaded from database
     *
     * @param u
     */
    boolean delete(U u);

    /**
     * Saves object to database if not null by keu U
     *
     * @param u
     */
    Future<Boolean> save(T t);
}
