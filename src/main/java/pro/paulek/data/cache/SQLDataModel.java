package pro.paulek.data.cache;

import pro.paulek.data.api.Data;
import pro.paulek.data.api.Serializable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public interface SQLDataModel<T, U> extends Data<T, U>, Serializable<T, ResultSet> {

    /**
     * Serializes object to database, inserts values to database
     * @param t
     * @return
     */
    @Override
    ResultSet serializeData(T t);

    /**
     * Deserializes data from database to an object
     * @param resultSet
     * @return
     * @throws SQLException
     */
    @Override
    T deserializeData(ResultSet resultSet) throws SQLException;

    /**
     * Counts number of records stored in database
     * @return
     */
    int count();

}
