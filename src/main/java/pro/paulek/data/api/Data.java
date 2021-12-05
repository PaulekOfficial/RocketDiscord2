package pro.paulek.data.api;

import java.util.Collection;

public interface Data<T, U> {

    /**
     * Load an object from database by param u
     * @param u
     * @return
     */
    T load(U u);

    /**
     * Initialize table in database
     */
    void createTable();

    /**
     * Load an object from database by id
     * @param id
     * @return
     */
    T load(int id);

    /**
     * Load all objects from database
     */
    void load();

    /**
     * Save collection of object to database including changes
     * @param collection
     * @param ignoreNotChanged
     */
    void save(Collection<T> collection, boolean ignoreNotChanged);

    /**
     * Save object to database
     * @param t
     */
    void save(T t);

    /**
     * Delete record by U
     * @param u
     */
    void delete(U u);

    /**
     * Delete record by id
     * @param id
     */
    void delete(int id);

    /**
     * Count records or files stored in a database
     * @return
     */
    int count();

    //void validateLoadedData();

}
