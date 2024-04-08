package pro.paulek.data.sql;

public class SqlCondition {
    private final StringBuilder condition;

    private SqlCondition(String name, String value) {
        this.condition = new StringBuilder(name + " = '" + value + "'");
    }

    public static SqlCondition of(String name, String value) {
        return new SqlCondition(name, value);
    }

    public static SqlCondition of(String name, int value) {
        return new SqlCondition(name, String.valueOf(value));
    }

    public static SqlCondition of(String name, long value) {
        return new SqlCondition(name, String.valueOf(value));
    }

    public static SqlCondition of(String name, double value) {
        return new SqlCondition(name, String.valueOf(value));
    }

    public static SqlCondition of(String name, float value) {
        return new SqlCondition(name, String.valueOf(value));
    }

    public static SqlCondition of(String name, boolean value) {
        return new SqlCondition(name, String.valueOf(value));
    }

    public static SqlCondition of(String name, Object value) {
        return new SqlCondition(name, value.toString());
    }

    public SqlCondition and(String name, String value) {
        this.condition.append(" AND ").append(name).append(" = '").append(value).append("'");
        return this;
    }

    public SqlCondition and(String name, int value) {
        this.condition.append(" AND ").append(name).append(" = ").append(value);
        return this;
    }

    public SqlCondition and(String name, long value) {
        this.condition.append(" AND ").append(name).append(" = ").append(value);
        return this;
    }

    public SqlCondition and(String name, double value) {
        this.condition.append(" AND ").append(name).append(" = ").append(value);
        return this;
    }

    public SqlCondition and(String name, float value) {
        this.condition.append(" AND ").append(name).append(" = ").append(value);
        return this;
    }

    public SqlCondition and(String name, boolean value) {
        this.condition.append(" AND ").append(name).append(" = ").append(value);
        return this;
    }

    public SqlCondition and(String name, Object value) {
        this.condition.append(" AND ").append(name).append(" = ").append(value);
        return this;
    }

    public SqlCondition and(SqlCondition condition) {
        this.condition.append(" AND (").append(condition.toString()).append(")");
        return this;
    }

    public SqlCondition or(String name, String value) {
        this.condition.append(" OR ").append(name).append(" = '").append(value).append("'");
        return this;
    }

    public SqlCondition or(String name, int value) {
        this.condition.append(" OR ").append(name).append(" = ").append(value);
        return this;
    }

    public SqlCondition or(String name, long value) {
        this.condition.append(" OR ").append(name).append(" = ").append(value);
        return this;
    }

    public SqlCondition or(String name, double value) {
        this.condition.append(" OR ").append(name).append(" = ").append(value);
        return this;
    }

    public SqlCondition or(String name, float value) {
        this.condition.append(" OR ").append(name).append(" = ").append(value);
        return this;
    }

    public SqlCondition or(String name, boolean value) {
        this.condition.append(" OR ").append(name).append(" = ").append(value);
        return this;
    }

    public SqlCondition or(String name, Object value) {
        this.condition.append(" OR ").append(name).append(" = ").append(value);
        return this;
    }

    public SqlCondition or(SqlCondition condition) {
        this.condition.append(" OR (").append(condition.toString()).append(")");
        return this;
    }

    @Override
    public String toString() {
        return condition.toString();
    }
}