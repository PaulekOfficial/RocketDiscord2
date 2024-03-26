package pro.paulek.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ISQLDataModel<T, U> extends IData<T, U>, ISerializable<T, ResultSet> {

//    /**
//     * Serializes object to database, inserts values to database
//     * @param t
//     * @return
//     */
//    @Override
//    ResultSet serializeData(T t);

    /**
     * Deserializes data from database to an object
     *
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
