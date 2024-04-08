package pro.paulek.data.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SqlBuilderBase {
    private static final Logger logger = LoggerFactory.getLogger(SqlBuilderBase.class);

    protected abstract String buildSQL();

    protected abstract Optional<?> execute();

    protected abstract void closeConnection();

    protected Optional<Map<String, String>> mapClassFields(Collection<String> columns, Class<?> type) {
        Map<String, String> columnFieldNames = new HashMap<>();
        for (String column : columns) {
            var mapOptional = mapClassField(type, column);
            mapOptional.ifPresent(fieldName -> columnFieldNames.put(column, fieldName));
        }

        if (columnFieldNames.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(columnFieldNames);
    }

    protected Optional<String> mapClassField(Class<?> type, String columnName) {
        try {
            String camelCaseName = Arrays.stream(columnName.split("_"))
                    .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                    .collect(Collectors.joining());

            Field field;
            try {
                field = type.getDeclaredField(camelCaseName);
            } catch (NoSuchFieldException e) {
                field = null;
                for (Field f : type.getDeclaredFields()) {
                    String snakeCaseName = f.getName().replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
                    if (snakeCaseName.equals(columnName)) {
                        field = f;
                        break;
                    }
                }
            }

            if (field != null) {
                return Optional.of(field.getName());
            } else {
                throw new NoSuchFieldException("No suitable field found for column: " + columnName);
            }
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while mapping the class fields", e);
        }

        return Optional.empty();
    }

    protected Optional<Collection<String>> mapColumns(ResultSet resultSet) {
        List<String> columns = new ArrayList<>();
        try {
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                String column = resultSet.getMetaData().getColumnName(i);
                columns.add(column);
            }

            return Optional.of(columns);
        } catch (SQLException e) {
            logger.error("An error occurred while mapping the columns", e);
        }

        return Optional.empty();
    }
}
