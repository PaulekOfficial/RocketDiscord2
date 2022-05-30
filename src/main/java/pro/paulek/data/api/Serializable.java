package pro.paulek.data.api;

import java.sql.SQLException;
import java.util.Collection;

public interface Serializable<T, R> {

    /**
     * Used to serialize data
     * @param t
     * @return
     */
    R serializeData(T t);

    /**
     * Used to deserialize data
     * @param r
     * @return
     * @throws SQLException
     */
    T deserializeData(R r) throws SQLException;

}
