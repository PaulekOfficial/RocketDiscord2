package pro.paulek.data;

import java.sql.SQLException;

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
