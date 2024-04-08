package pro.paulek.data.sql.annotation;

public @interface Table {
    String name();
    String catalog() default "";
    String schema() default "";
}
