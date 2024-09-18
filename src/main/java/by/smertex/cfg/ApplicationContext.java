package by.smertex.cfg;

import by.smertex.annotation.ComponentScan;
import by.smertex.annotation.Constructor;
import by.smertex.annotation.Dependent;
import by.smertex.annotation.NotSingleton;
import by.smertex.exception.InitComponentInstanceException;
import by.smertex.interfaces.ApplicationContextBasic;
import by.smertex.interfaces.ComponentManagerBasic;
import by.smertex.utils.ClassUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class ApplicationContext implements ApplicationContextBasic {
    private Object configurationClass;
    private ComponentManagerBasic componentManagerBasic;

    public ApplicationContext(Object configurationClass){
        ClassUtil.validationConfigurationClass(configurationClass);
        initApplicationContext(configurationClass);
        initComponents();
        initDependency();
    }

    private void initApplicationContext(Object configurationClass){
        String path = configurationClass.getClass().getDeclaredAnnotation(ComponentScan.class).path();
        componentManagerBasic = new ComponentManager(new ClassFinder(path));
        this.configurationClass = configurationClass;
    }

    private void initComponents(){
        var components = componentManagerBasic.getComponentPool();
        Set<Class<?>> objectKeysToCreateInConfig = new HashSet<>();

        for(Class<?> clazz: components.keySet()){
            if(!ClassUtil.hasNoOrdinaryConstructor(clazz))
                components.put(clazz, createInstanceFromBasicConstructor(clazz));
            else objectKeysToCreateInConfig.add(clazz);
        }
        for(Class<?> clazz: objectKeysToCreateInConfig) components.put(clazz, creatingInstanceFromConfig(clazz));
    }

    private Object creatingInstanceFromConfig(Class<?> clazz){
        var object = Arrays.stream(configurationClass.getClass().getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(Constructor.class) != null)
                .filter(method -> method.getReturnType().equals(clazz))
                .map(method -> ClassUtil.invokeExceptionHandler(configurationClass, method))
                .findFirst();

        if(object.isEmpty()) throw new InitComponentInstanceException(new RuntimeException());

        return object.get();
    }

    private Object createInstanceFromBasicConstructor(Class<?> clazz){
        if(clazz.getDeclaredAnnotation(NotSingleton.class) != null) return null;
        return ClassUtil.createNewInstance(clazz);
    }

    private Object getNotSingletonComponent(Class<?> clazz){
        Object object;
        if(!ClassUtil.hasNoOrdinaryConstructor(clazz))
            object = createInstanceFromBasicConstructor(clazz);
        else object = createInstanceFromBasicConstructor(clazz);

        inject(object);
        return object;
    }

    private void initDependency(){
        for(Object component: componentManagerBasic.getComponentPool().values()){
            if(component != null) inject(component);
        }
    }

    @Override
    public void inject(Object component) {
        Arrays.stream(component.getClass().getDeclaredFields())
                .filter(field -> field.getDeclaredAnnotation(Dependent.class) != null)
                .forEach(field -> ClassUtil.setDependency(component,
                                                          getComponent(field.getDeclaredAnnotation(Dependent.class).component()),
                                                          field));
    }

    @Override
    public Object getComponent(Class<?> clazz) {
        var component = componentManagerBasic.getComponent(clazz);
        if(component == null) return getNotSingletonComponent(clazz);
        return componentManagerBasic.getComponent(clazz);
    }

    public ComponentManagerBasic getComponentManagerBasic(){
        return componentManagerBasic;
    }
}
