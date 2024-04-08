package pro.paulek.data.sql.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.data.sql.SqlBuilderBase;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SqlInsertBuilder extends SqlBuilderBase {
    private static final Logger logger = LoggerFactory.getLogger(SqlInsertBuilder.class);

    private String table;
    private Connection connection;
    private Class<?> type;
    private Object record;
    private final Map<String, Object> values = new HashMap<>();
    private final List<String> columns = new ArrayList<>();
    private final Map<String, String> columnFieldNames = new HashMap<>();

    public SqlInsertBuilder table(String table) {
        this.table = table;
        return this;
    }

    public SqlInsertBuilder type(Class<?> clazz) {
        this.type = clazz;
        return this;
    }

    public SqlInsertBuilder connection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public SqlInsertBuilder record(Object object) {
        this.record = object;
        this.type = object.getClass();
        return this;
    }

    public SqlInsertBuilder value(String name, String value) {
        values.put(name, value);
        return this;
    }

    public SqlInsertBuilder value(String name, int value) {
        values.put(name, value);
        return this;
    }

    public SqlInsertBuilder value(String name, long value) {
        values.put(name, value);
        return this;
    }

    public SqlInsertBuilder value(String name, double value) {
        values.put(name, value);
        return this;
    }

    public SqlInsertBuilder value(String name, float value) {
        values.put(name, value);
        return this;
    }

    public SqlInsertBuilder value(String name, boolean value) {
        values.put(name, value);
        return this;
    }

    public SqlInsertBuilder value(String name, Object value) {
        values.put(name, value);
        return this;
    }

    @Override
    protected Optional<Collection<String>> mapColumns(ResultSet resultSet) {
        List<String> columns = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String column = resultSet.getString("COLUMN_NAME");
                columns.add(column);
            }

            return Optional.of(columns);
        } catch (SQLException e) {
            logger.error("An error occurred while mapping the columns", e);
        }

        return Optional.empty();
    }

    public String buildSQL() {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(table).append(" (");

        // Append column names
        for (String column : values.keySet()) {
            if (values.get(column) == null) {
                continue;
            }

            sql.append(column).append(", ");
        }

        // Remove trailing comma and space
        sql.setLength(sql.length() - 2);

        sql.append(") VALUES (");

        // Append placeholders for values
        for (int i = 0; i < values.size(); i++) {
            sql.append("?, ");
        }

        // Remove trailing comma and space
        sql.setLength(sql.length() - 2);

        sql.append(")");

        return sql.toString();
    }

    private Optional<Collection<String>> fetchColumnNames() {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?")) {
            ps.setString(1, table);
            ResultSet rs = ps.executeQuery();

            return mapColumns(rs);
        } catch (SQLException e) {
            logger.error("An error occurred while fetching the column names", e);
        }

        return Optional.empty();
    }

    private Optional<?> mapRecord() {
        if (record == null) {
            return Optional.empty();
        }

        for (String column : columns) {
            var mapOptional = mapClassField(type, column);
            mapOptional.ifPresent(fieldName -> {
                try {
                    Field field = type.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    values.put(column, field.get(record));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    logger.error("An error occurred while mapping the record", e);
                }
            });
        }

        return Optional.of(record);
    }

    public Optional<?> execute() {
        if (columns.isEmpty()) {
            var columnsOptional = fetchColumnNames();
            columnsOptional.ifPresent(columns::addAll);
        }

        if (columnFieldNames.isEmpty()) {
            var columnFieldNamesOptional = mapClassFields(columns, type);
            columnFieldNamesOptional.ifPresent(columnFieldNames::putAll);
        }

        if (values.isEmpty()) {
            var recordOptional = mapRecord();
            recordOptional.ifPresent(r -> {});
        }

        String sql = this.buildSQL();
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {
            // Set the values
            int index = 1;
            for (Object value : values.values()) {
                if (value instanceof Enum<?>) {
                    value = ((Enum<?>) value).name();
                    ps.setString(index, (String) value);
                    index++;
                    continue;
                }

                switch (value.getClass().getSimpleName()) {
                    case "Integer":
                        ps.setInt(index, (Integer) value);
                        break;
                    case "Long":
                        ps.setLong(index, (Long) value);
                        break;
                    case "Double":
                        ps.setDouble(index, (Double) value);
                        break;
                    case "Float":
                        ps.setFloat(index, (Float) value);
                        break;
                    case "Boolean":
                        ps.setBoolean(index, (Boolean) value);
                        break;
                    case "String":
                        ps.setString(index, (String) value);
                        break;
                    default:
                        ps.setObject(index, value);
                        break;
                }
                index++;
            }

            // Execute the statement
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting record failed, no rows affected.");
            }

            return Optional.of(affectedRows);
        } catch (SQLException e) {
            logger.error("An error occurred while executing the SQL query", e);
        }

        return Optional.empty();
    }

    @Override
    protected void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error("An error occurred while closing the connection", e);
        }
    }
}
