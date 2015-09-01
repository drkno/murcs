package sws.murcs.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that determines if a field is searchable using search functionality.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Searchable {

    /**
     * Priority to search this field with.
     * @return priority of the search field.
     */
    SearchPriority value() default SearchPriority.Medium;

    /**
     * Field name to identify this field as.
     * @return the field name
     */
    String fieldName() default "";
}
