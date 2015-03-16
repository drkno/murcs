package sws.project.magic.easyedit;

import sws.project.magic.easyedit.fxml.BasicPaneGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An interface for the editable annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Editable {
    /**
     * The type of the generator to use for this field
     * @return The type of generator to use on this field
     */
    Class<?> value() default BasicPaneGenerator.class;

    /**
     * The name for the getter for the field. This
     * is optional and if left blank it will default to
     * getFieldName
     * @return The name of the getter method
     */
    String getterName() default "";
    /**
     * The name for the setter for the field. This
     * is optional and if left blank it will default to
     * setFieldName
     *
     * The reflection expects this method to have one
     * argument, namely the new value of the field
     * @return The name of the getter method
     */
    String setterName() default "";

    /**
     * The name for the validator of the field. This doesn't need
     * to be specified. The method should take one argument, namely
     * that the value to validate, which should be of the same type
     * as the field itself. The method itself should be public
     *
     * @return The name of the validator method
     */
    String validatorName() default "";

    /**
     * An optional argument specifying an argument to
     * pass on to the generator.
     * @return The argument
     */
    String argument() default "";

    /**
     * The friendly name of the field. This is usually
     * used as the title of the field on the edit pane.
     *
     * If left blank, this will be generated as follows:
     * myLongFieldName goes to My Long Field Name
     * @return the friendly name of the field
     */
    String friendlyName() default "";

    /**
     * Indicates how high up the pane the field should show
     * @return The position of the pane
     */
    int sort() default 0;
}
