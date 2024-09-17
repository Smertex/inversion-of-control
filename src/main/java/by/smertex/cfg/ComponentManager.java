package by.smertex.cfg;

import by.smertex.annotation.Component;
import by.smertex.annotation.Dependent;
import by.smertex.exception.InitComponentInstanceException;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentManager {
    private final Map<Class<?>, Object> componentsPool = new HashMap<>();

    public ComponentManager(String componentPath) {
        init(componentPath);
    }

    private void init(String componentPath) {
        List<Class<?>> classesInProject = ClassFinder.findComponents(componentPath);

        initComponents(classesInProject);
        dependencyInject();
    }

    private void initComponents(List<Class<?>> classesInProject){
        classesInProject.stream()
                .filter(clazz -> clazz.getDeclaredAnnotation(Component.class) != null)
                .forEach(this::addClassInstanceInPool);
    }

    private void addClassInstanceInPool(Class<?> clazz){
        try {
            var instance = clazz.getDeclaredConstructor().newInstance();
            componentsPool.put(clazz, instance);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new InitComponentInstanceException(e);
        }
    }

    private void dependencyInject(){
        for(Object object : componentsPool.values()){
            inject(object);
        }
    }

    private void inject(Object instance){
        Arrays.stream(instance.getClass().getDeclaredFields())
                .filter(field -> field.getDeclaredAnnotation(Dependent.class) != null)
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        field.set(instance, componentsPool.get(field.getDeclaredAnnotation(Dependent.class).component()));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public Object getComponent(Class<?> key){
        return componentsPool.get(key);
    }
}
