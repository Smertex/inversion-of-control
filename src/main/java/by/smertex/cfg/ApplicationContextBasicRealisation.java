package by.smertex.cfg;

import by.smertex.annotation.ComponentScan;
import by.smertex.annotation.Dependent;
import by.smertex.annotation.NotSingleton;
import by.smertex.interfaces.ApplicationContext;
import by.smertex.interfaces.ComponentManager;
import by.smertex.interfaces.ConfigurationClassManager;

import java.util.Arrays;
import java.util.Map;


public class ApplicationContextBasicRealisation implements ApplicationContext {

    private ComponentManager componentManager;

    private ConfigurationClassManager configurationClassManager;

    public ApplicationContextBasicRealisation(Object configurationClass){
        validationConfigurationClass(configurationClass);
        initApplicationContext(configurationClass);
        initComponents();
        initDependency();
    }

    private void initApplicationContext(Object configurationClass){
        String path = configurationClass.getClass().getDeclaredAnnotation(ComponentScan.class).path();
        this.componentManager = new ComponentManagerBasicRealisation(new ClassFinderBasicRealisation(path));
        this.configurationClassManager = new ConfigurationClassManagerBasicRealisation(configurationClass);
    }

    private void initComponents(){
        Map<Class<?>, Object> components = componentManager.getComponentPool();
        components.keySet().stream()
                .filter(clazz -> clazz.getDeclaredAnnotation(NotSingleton.class) == null)
                .forEach(clazz -> components.put(clazz, createInstance(clazz)));
    }

    private void initDependency(){
        for(Object component: componentManager.getComponentPool().values())
            if(component != null) inject(component);
    }

    private Object createInstance(Class<?> clazz){
        return configurationClassManager.getConstructorMethod(clazz) != null ?
                invokeCfgMethod(configurationClassManager.getConfigurationClass(),
                        configurationClassManager.getConstructorMethod(clazz)) : createNewInstance(clazz);
    }


    @Override
    public void inject(Object component) {
        Arrays.stream(component.getClass().getDeclaredFields())
                .filter(field -> field.getDeclaredAnnotation(Dependent.class) != null)
                .forEach(field -> setDependency(component,
                                                          getComponent(field.getDeclaredAnnotation(Dependent.class).component()),
                                                          field));
    }

    private Object getNotSingletonComponent(Class<?> clazz){
        Object object = createInstance(clazz);
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
