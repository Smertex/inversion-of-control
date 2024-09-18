package by.smertex.interfaces;

import java.util.Map;

public interface ComponentManagerBasic {
    Object getComponent(Class<?> key);
    Map<Class<?>, Object> getComponentPool();
}
