package by.smertex.interfaces;

import by.smertex.exception.ConfigurationRepeatingMethodException;

import java.lang.reflect.Method;

public interface ConfigurationClassManager {
    Method getConstructorMethod(Class<?> key);

    Object getConfigurationClass();

    default void configurationRepeatingMethod(){
        throw new ConfigurationRepeatingMethodException(new RuntimeException());
    }
}
