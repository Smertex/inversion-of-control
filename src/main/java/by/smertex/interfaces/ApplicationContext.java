package by.smertex.interfaces;

import by.smertex.annotation.ComponentScan;
import by.smertex.annotation.Configuration;
import by.smertex.exception.ComponentScanNotFound;
import by.smertex.exception.NotConfigurationClass;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

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

    static Optional<Method> findMethodForCreateInstanceInCfg(Class<?> clazz, Object cfg){
        return Arrays.stream(cfg.getClass().getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(by.smertex.annotation.Constructor.class) != null)
                .filter(method -> method.getReturnType().equals(clazz))
                .findFirst();
    }
}
