package by.smertex.interfaces;

import java.util.List;

public interface ApplicationContextBasic {
    Object getComponent(Class<?> clazz);
    void inject(Object component);
}
