package by.smertex.cfg;

import by.smertex.annotation.Constructor;
import by.smertex.interfaces.ConfigurationClassManager;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationClassManagerBasicRealisation implements ConfigurationClassManager {
    private final Object configurationClass;

    private final Map<Class<?>, Method> constructorMethods = new HashMap<>();

    public ConfigurationClassManagerBasicRealisation(Object configurationClass){
        this.configurationClass = configurationClass;
        readConfigurationClass();
    }

    private void readConfigurationClass(){
        Arrays.stream(configurationClass.getClass().getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(Constructor.class) != null)
                .forEach(method -> {
                    if(constructorMethods.containsKey(method.getReturnType())) ConfigurationClassManager.configurationRepeatingMethod();
                    constructorMethods.put(method.getReturnType(), method);
                });
    }

    @Override
    public Method getConstructorMethod(Class<?> key) {
        return constructorMethods.get(key);
    }

    @Override
    public Object getConfigurationClass() {
        return configurationClass;
    }
}
