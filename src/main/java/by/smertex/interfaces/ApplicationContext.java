package by.smertex.interfaces;

import by.smertex.annotation.ComponentScan;
import by.smertex.annotation.Configuration;
import by.smertex.annotation.NotSingleton;
import by.smertex.exception.ComponentScanNotFound;
import by.smertex.exception.InitComponentInstanceException;
import by.smertex.exception.NotConfigurationClass;

import java.lang.reflect.Constructor;

public interface ApplicationContext {
    Object getComponent(Class<?> clazz);
    void inject(Object component);

    static void validationConfigurationClass(Object configurationClass){
        Class<?> clazz = configurationClass.getClass();
        if(clazz.getDeclaredAnnotation(Configuration.class) == null)
            throw new NotConfigurationClass(new RuntimeException());
        if(clazz.getDeclaredAnnotation(ComponentScan.class) == null)
            throw new ComponentScanNotFound(new RuntimeException());
    }

    static boolean validationSingletonClass(Class<?> clazz){
        return clazz.getDeclaredAnnotation(NotSingleton.class) == null;
    }

    static Boolean hasNoOrdinaryConstructor(Class<?> clazz){
        for(Constructor<?> constructor: clazz.getConstructors())
            if(constructor.getParameterCount() > 0) return true;
        return false;
    }

    static void validationComponentInstance(Object object){
        if(object == null) throw new InitComponentInstanceException(new RuntimeException());
    }
}
