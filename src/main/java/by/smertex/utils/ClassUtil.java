package by.smertex.utils;

import by.smertex.annotation.ComponentScan;
import by.smertex.annotation.Configuration;
import by.smertex.exception.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassUtil {
    public static Class<?> pathToClass(String componentPath){
        try {
            return Class.forName(componentPath);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean hasNoOrdinaryConstructor(Class<?> clazz){
        for(Constructor<?> constructor: clazz.getConstructors())
            if(constructor.getParameterCount() > 0) return true;
        return false;
    }

    public static Object invokeExceptionHandler(Object object, Method method){
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new CreateInstanceInCfgException(e);
        }
    }

    public static void validationConfigurationClass(Object configurationClass){
        var clazz = configurationClass.getClass();
        if(clazz.getDeclaredAnnotation(Configuration.class) == null)
            throw new NotConfigurationClass(new RuntimeException());
        if(clazz.getDeclaredAnnotation(ComponentScan.class) == null)
            throw new ComponentScanNotFound(new RuntimeException());
    }

    public static Object createNewInstance(Class<?> clazz){
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new CreateInstanceBasicConstructorException(e);
        }
    }

    public static void setDependency(Object component, Object instance, Field field){
        field.setAccessible(true);
        try {
            field.set(component, instance);
        } catch (IllegalAccessException e) {
            throw new DependencyInjectionException(e);
        }
    }
}
