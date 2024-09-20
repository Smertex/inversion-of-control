package by.smertex.interfaces;

import java.lang.reflect.Method;

public interface ConfigurationClassManager {
    Method getConstructorMethod(Class<?> key);

    Object getConfigurationClass();
}
