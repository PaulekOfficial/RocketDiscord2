package pro.paulek.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {

    /**
     * Used to initialize database connection
     */
    public abstract void init();

    /**
     * Get valid connection to database
     *
     * @return sql connection
     * @throws SQLException
     */
    public abstract Connection getConnection() throws SQLException;

}
