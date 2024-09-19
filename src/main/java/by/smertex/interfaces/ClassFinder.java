package by.smertex.interfaces;

import java.util.List;

public interface ClassFinder {
    List<Class<?>> findClasses(String componentPath);

    List<Class<?>> getClasses();

    static Class<?> pathToClass(String componentPath){
        try {
            return Class.forName(componentPath);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static String pathMerging(String rootPath, String appendableObject){
        return rootPath + "." + appendableObject;
    }

    static String mergeClassPath(String rootPath, String appendableObject){
        return pathMerging(rootPath, appendableObject).replaceAll(".class", "");
    }
}
