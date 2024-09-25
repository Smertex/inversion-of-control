package by.smertex.interfaces;

import by.smertex.annotation.ComponentScan;
import by.smertex.annotation.Configuration;
import by.smertex.exception.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface ApplicationContext {
    Object getComponent(Class<?> clazz);

    void inject(Object component);

    default void validationConfigurationClass(Object configurationClass){
        Class<?> clazz = configurationClass.getClass();
        if(clazz.getDeclaredAnnotation(Configuration.class) == null)
            throw new NotConfigurationClass(new RuntimeException());
        if(clazz.getDeclaredAnnotation(ComponentScan.class) == null)
            throw new ComponentScanNotFound(new RuntimeException());
    }

    default Object invokeCfgMethod(Object configurationClass, Method method){
        try {
            return method.invoke(configurationClass);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new CreateInstanceInCfgException(e);
        }
    }

    default Object createNewInstance(Class<?> clazz){
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new CreateInstanceBasicConstructorException(e);
        }
    }

    default void setDependency(Object component, Object instance, Field field){
        field.setAccessible(true);
        try {
            field.set(component, instance);
        } catch (IllegalAccessException e) {
            throw new DependencyInjectionException(e);
        }
    }
}
