package by.smertex.interfaces;

import java.util.Map;

public interface ComponentManager {
    Object getComponent(Class<?> key);
    Map<Class<?>, Object> getComponentPool();
}
