package by.smertex.cfg;

import by.smertex.annotation.Component;
import by.smertex.annotation.Dependent;
import by.smertex.exception.InitComponentInstanceException;
import by.smertex.interfaces.ClassFinderBasic;
import by.smertex.interfaces.ComponentManagerBasic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentManager implements ComponentManagerBasic {
    private final Map<Class<?>, Object> componentsPool = new HashMap<>();
    private final ClassFinderBasic classFinder;

    public ComponentManager(ClassFinderBasic classFinder) {
        this.classFinder = classFinder;
        init();
    }

    private void init() {
        List<Class<?>> classesInProject = classFinder.getClasses();
        classesInProject.stream()
                .filter(clazz -> clazz.getDeclaredAnnotation(Component.class) != null)
                .forEach(clazz -> componentsPool.put(clazz, null));
    }

    @Override
    public Object getComponent(Class<?> key){
        return componentsPool.get(key);
    }

    @Override
    public Map<Class<?>, Object> getComponentPool() {
        return componentsPool;
    }


}
