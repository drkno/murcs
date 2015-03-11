package sws.project.magic;

import javafx.scene.Node;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 */
public interface EditGenerator {
    Class[] supportedTypes();
    Node generate(Field field, Method getter, Method setter, Object from);
}
