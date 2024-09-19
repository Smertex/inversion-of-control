package by.smertex.cfg;

import by.smertex.annotation.ComponentScan;
import by.smertex.annotation.Constructor;
import by.smertex.annotation.Dependent;
import by.smertex.interfaces.ApplicationContext;
import by.smertex.interfaces.ComponentManager;
import by.smertex.utils.ClassUtil;
import by.smertex.utils.ClassValidators;

import java.util.Arrays;
import java.util.Map;

public class ApplicationContextBasicRealisation implements ApplicationContext {
    private Object configurationClass;
    private ComponentManager componentManager;

    public ApplicationContextBasicRealisation(Object configurationClass){
        ClassValidators.validationConfigurationClass(configurationClass);
        initApplicationContext(configurationClass);
        initComponents();
        initDependency();
    }

    private void initApplicationContext(Object configurationClass){
        String path = configurationClass.getClass().getDeclaredAnnotation(ComponentScan.class).path();
        componentManager = new ComponentManagerBasicRealisation(new ClassFinderBasicRealisation(path));
        this.configurationClass = configurationClass;
    }

    private void initComponents(){
        Map<Class<?>, Object> components = componentManager.getComponentPool();
        components.keySet().stream()
                .filter(ClassValidators::validationSingletonClass)
                .forEach(clazz -> components.put(clazz, !ClassValidators.hasNoOrdinaryConstructor(clazz) ?
                        createInstanceFromBasicConstructor(clazz) : creatingInstanceFromConfig(clazz)
                ));
    }

    private void initDependency(){
        for(Object component: componentManager.getComponentPool().values())
            if(component != null) inject(component);
    }

    private Object creatingInstanceFromConfig(Class<?> clazz){
        return Arrays.stream(configurationClass.getClass().getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(Constructor.class) != null)
                .filter(method -> method.getReturnType().equals(clazz))
                .map(method -> ClassUtil.invokeExceptionHandler(configurationClass, method))
                .peek(ClassValidators::validationComponentInstance)
                .findFirst()
                .get();
    }

    private Object createInstanceFromBasicConstructor(Class<?> clazz){
        return ClassUtil.createNewInstance(clazz);
    }

    @Override
    public void inject(Object component) {
        Arrays.stream(component.getClass().getDeclaredFields())
                .filter(field -> field.getDeclaredAnnotation(Dependent.class) != null)
                .forEach(field -> ClassUtil.setDependency(component,
                                                          getComponent(field.getDeclaredAnnotation(Dependent.class).component()),
                                                          field));
    }

    private Object getNotSingletonComponent(Class<?> clazz){
        Object object = !ClassValidators.hasNoOrdinaryConstructor(clazz) ?
                createInstanceFromBasicConstructor(clazz): creatingInstanceFromConfig(clazz);
        inject(object);
        return object;
    }

    @Override
    public Object getComponent(Class<?> clazz) {
        Object component = componentManager.getComponent(clazz);
        return component == null ?
                getNotSingletonComponent(clazz) : component;
    }
}
