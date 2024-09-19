package by.smertex.interfaces;

public interface ApplicationContext {
    Object getComponent(Class<?> clazz);
    void inject(Object component);
}
