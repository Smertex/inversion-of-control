package by.smertex.utils;

import by.smertex.annotation.ComponentScan;
import by.smertex.annotation.Configuration;
import by.smertex.annotation.NotSingleton;
import by.smertex.exception.ComponentScanNotFound;
import by.smertex.exception.NotConfigurationClass;

import java.lang.reflect.Constructor;

public class ClassValidators {
    public static void validationConfigurationClass(Object configurationClass){
        var clazz = configurationClass.getClass();
        if(clazz.getDeclaredAnnotation(Configuration.class) == null)
            throw new NotConfigurationClass(new RuntimeException());
        if(clazz.getDeclaredAnnotation(ComponentScan.class) == null)
            throw new ComponentScanNotFound(new RuntimeException());
    }

    public static boolean validationSingletonClass(Class<?> clazz){
        return clazz.getDeclaredAnnotation(NotSingleton.class) == null;
    }

    public static Boolean hasNoOrdinaryConstructor(Class<?> clazz){
        for(Constructor<?> constructor: clazz.getConstructors())
            if(constructor.getParameterCount() > 0) return true;
        return false;
    }
}
