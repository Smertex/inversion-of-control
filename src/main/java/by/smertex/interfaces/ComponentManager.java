package by.smertex.interfaces;

import by.smertex.annotation.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;

public interface ComponentManager {
    Object getComponent(Class<?> key);

    Map<Class<?>, Object> getComponentPool();

    default boolean isComponentClass(Class<?> clazz){
        return Arrays.stream(clazz.getDeclaredAnnotations())
                .anyMatch(this::isComponentAnnotation);
    }

    default boolean isComponentAnnotation(Annotation annotation){
        return annotation.annotationType().equals(Component.class) ||
               annotation.annotationType().getDeclaredAnnotation(Component.class) != null;
    }
}
