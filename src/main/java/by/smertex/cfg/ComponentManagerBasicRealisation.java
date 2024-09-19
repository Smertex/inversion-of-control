package by.smertex.cfg;

import by.smertex.interfaces.ClassFinder;
import by.smertex.interfaces.ComponentManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentManagerBasicRealisation implements ComponentManager {
    private final Map<Class<?>, Object> componentsPool = new HashMap<>();
    private final ClassFinder classFinder;

    public ComponentManagerBasicRealisation(ClassFinder classFinder) {
        this.classFinder = classFinder;
        init();
    }

    private void init() {
        List<Class<?>> classesInProject = classFinder.getClasses();
        classesInProject.stream()
                .filter(ComponentManager::isComponentClass)
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
