package pro.paulek.data.sql.annotation;

import pro.paulek.data.sql.enums.GenerationType;

public @interface GeneratedValue {
    GenerationType strategy() default GenerationType.AUTO;
    String generator() default "";
}
