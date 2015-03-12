package sws.project.magic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Editable {
    Class<?> value();

    String getterName() default "";
    String setterName() default "";

    String argument() default "";
    String friendlyName() default "";
}
