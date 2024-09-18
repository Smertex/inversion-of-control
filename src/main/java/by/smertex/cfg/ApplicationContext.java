package by.smertex.cfg;

import by.smertex.annotation.ComponentScan;
import by.smertex.annotation.Constructor;
import by.smertex.annotation.Dependent;
import by.smertex.exception.InitComponentInstanceException;
import by.smertex.interfaces.ApplicationContextBasic;
import by.smertex.interfaces.ComponentManagerBasic;
import by.smertex.utils.ClassUtil;
import by.smertex.utils.ClassValidators;

import java.util.Arrays;

public class ApplicationContext implements ApplicationContextBasic {
    private Object configurationClass;
    private ComponentManagerBasic componentManagerBasic;

    public ApplicationContext(Object configurationClass){
        ClassValidators.validationConfigurationClass(configurationClass);
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

        components.keySet().stream()
                .filter(clazz -> !ClassValidators.hasNoOrdinaryConstructor(clazz))
                .filter(ClassValidators::validationSingletonClass)
                .forEach(clazz -> components.put(clazz, createInstanceFromBasicConstructor(clazz)));
        components.keySet().stream()
                .filter(ClassValidators::hasNoOrdinaryConstructor)
                .filter(ClassValidators::validationSingletonClass)
                .forEach(clazz -> components.put(clazz, creatingInstanceFromConfig(clazz)));
    }

    private void initDependency(){
        for(Object component: componentManagerBasic.getComponentPool().values()){
            if(component != null) inject(component);
        }
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
        var component = componentManagerBasic.getComponent(clazz);
        return component == null ?
                getNotSingletonComponent(clazz) : component;
    }
}
