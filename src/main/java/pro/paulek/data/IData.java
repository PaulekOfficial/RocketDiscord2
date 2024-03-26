package pro.paulek.data;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Future;

//TODO add optionals, nullable
public interface IData<T, U> {

    /**
     * Load an object from database by param u
     * @param u
     * @return
     */
    Optional<T> load(U u);

    /**
     * Load an object from database by id
     * @param id
     * @return
     */
    Optional<T> load(int id);

    /**
     * Initialize table in database
     */
    Future<Boolean> createTable();

    /**
     * Load all objects from database
     */
    Optional<Collection<T>> load();

    /**
     * Save collection of object to database including changes
     * @param collection
     * @param ignoreNotChanged
     */
    Future<Boolean> saveAll(Collection<T> collection, boolean ignoreNotChanged);

    /**
     * Save object to database
     * @param t
     */
    Future<Boolean> save(T t);

    /**
     * Delete record by U
     * @param u
     */
    Future<Boolean> delete(U u);

    /**
     * Delete record by id
     * @param id
     */
    Future<Boolean> delete(int id);

    //TODO maybe this should be also an future, but it's not necessary the think is that counting all records in database can take a while
    /**
     * Count records or files stored in a database
     * @return
     */
    int count();
}
