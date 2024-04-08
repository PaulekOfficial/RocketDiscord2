package pro.paulek.data.sql;

import pro.paulek.data.sql.builder.SqlInsertBuilder;
import pro.paulek.data.sql.builder.SqlSelectBuilder;

public class SqlBuilder {
    public SqlSelectBuilder select() {
        return new SqlSelectBuilder();
    }
    public SqlInsertBuilder insert() {
        return new SqlInsertBuilder();
    }
}
