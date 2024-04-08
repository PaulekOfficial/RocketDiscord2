package pro.paulek.data.sql.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.data.sql.enums.OrderType;
import pro.paulek.data.sql.SqlBuilderBase;
import pro.paulek.data.sql.SqlCondition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;

public class SqlSelectBuilder extends SqlBuilderBase {
    private static final Logger logger = LoggerFactory.getLogger(SqlSelectBuilder.class);

    private String table;
    private final List<String> columns;
    private final List<String> orderByColumns;
    private final List<OrderType> orderByDirections;
    private final List<SqlCondition> conditions;
    private Integer limit;
    private Connection connection;
    private Class<?> type;
    private final Map<String, String> columnFieldNames;
    private boolean closeConnection;

    public SqlSelectBuilder() {
        this.columns = new ArrayList<>();
        this.orderByColumns = new ArrayList<>();
        this.orderByDirections = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.columnFieldNames = new HashMap<>();
    }

    public SqlSelectBuilder table(String table) {
        this.table = table;
        return this;
    }

    public SqlSelectBuilder connection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public SqlSelectBuilder closeConnection(boolean closeConnection) {
        this.closeConnection = closeConnection;
        return this;
    }

    public SqlSelectBuilder type(Class<?> clazz) {
        this.type = clazz;
        return this;
    }

    public SqlSelectBuilder mapType(String column, String fieldName) {
        this.columnFieldNames.put(column, fieldName);
        return this;
    }

    public SqlSelectBuilder columns(String... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    public SqlSelectBuilder allColumns() {
        this.columns.clear();
        this.columns.add("*");
        return this;
    }

    public SqlSelectBuilder condition(SqlCondition condition) {
        this.conditions.add(condition); // Add the condition to the conditions list
        return this;
    }

    public SqlSelectBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public SqlSelectBuilder sortBy(String column, OrderType orderType) {
        this.orderByColumns.add(column);
        this.orderByDirections.add(orderType);
        return this;
    }

    public String buildSQL() {
        StringBuilder query = new StringBuilder("SELECT ");
        if (columns.isEmpty()) {
            query.append("*");
        } else {
            for (String column : columns) {
                query.append(column).append(", ");
            }
            query.setLength(query.length() - 2);
        }
        query.append(" FROM ").append(table);
        if (!conditions.isEmpty()) {
            query.append(" WHERE ");
            for (SqlCondition condition : conditions) {
                query.append(condition.toString()).append(" AND ");
            }
            query.setLength(query.length() - 5);
        }
        if (!orderByColumns.isEmpty()) {
            query.append(" ORDER BY ");
            for (int i = 0; i < orderByColumns.size(); i++) {
                query.append(orderByColumns.get(i));
                if (orderByDirections.get(i) == OrderType.DESCENDING) {
                    query.append(" DESC");
                } else {
                    query.append(" ASC");
                }
                if (i < orderByColumns.size() - 1) {
                    query.append(", ");
                }
            }
        }
        if (limit != null) {
            query.append(" LIMIT ").append(limit);
        }
        return query.toString();
    }

    public Optional<?> execute() {
        String sql = this.buildSQL();
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return Optional.empty();
            }

            if (this.columns.isEmpty()) {
                var columns = this.mapColumns(rs);
                columns.ifPresent(this.columns::addAll);
            }

            if (this.columnFieldNames.isEmpty()) {
                var fields = this.mapClassFields(columns, type);
                fields.ifPresent(this.columnFieldNames::putAll);
            }

            Object instance = type.getDeclaredConstructor().newInstance();
            for (String column : this.columns) {
                if (!columnFieldNames.containsKey(column)) {
                    continue;
                }

                Field field = type.getDeclaredField(columnFieldNames.get(column));
                field.setAccessible(true);
                Object value;

                switch (field.getType().getSimpleName()) {
                    case "int":
                    case "Integer":
                        value = rs.getInt(column);
                        break;
                    case "long":
                    case "Long":
                        value = rs.getLong(column);
                        break;
                    case "double":
                    case "Double":
                        value = rs.getDouble(column);
                        break;
                    case "float":
                    case "Float":
                        value = rs.getFloat(column);
                        break;
                    case "boolean":
                    case "Boolean":
                        value = rs.getBoolean(column);
                        break;
                    case "String":
                        value = rs.getString(column);
                        break;
                    case "Instant":
                        value = rs.getTimestamp(column).toInstant();
                        break;
                    case "LocalDateTime":
                        value = rs.getTimestamp(column).toLocalDateTime();
                        break;
                    case "LocalDate":
                        value = rs.getDate(column).toLocalDate();
                        break;
                    case "ZonedDateTime":
                        value = rs.getTimestamp(column).toInstant().atZone(ZoneId.systemDefault());
                        break;
                    default:
                        value = rs.getObject(column);
                        break;
                }

                if (field.getType().isEnum()) {
                    String enumValue = rs.getString(column);
                    value = Enum.valueOf((Class<Enum>) field.getType(), enumValue);
                }

                field.set(instance, value);
                field.setAccessible(false);
            }

            this.closeConnection();
            return Optional.of(instance);
        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException | NoSuchFieldException e) {
            logger.error("An error occurred while executing the SQL query", e);
        }

        this.closeConnection();
        return Optional.empty();
    }

    @Override
    protected void closeConnection() {
        if (this.closeConnection) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("An error occurred while closing the connection", e);
            }
        }
    }
}
