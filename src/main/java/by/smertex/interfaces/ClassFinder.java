package by.smertex.interfaces;

import java.util.List;

public interface ClassFinder {
    List<Class<?>> findClasses(String componentPath);

    List<Class<?>> getClasses();

    default Class<?> pathToClass(String componentPath){
        try {
            return Class.forName(componentPath);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    default String pathMerging(String rootPath, String appendableObject){
        return rootPath + "." + appendableObject;
    }

    default String mergeClassPath(String rootPath, String appendableObject){
        return pathMerging(rootPath, appendableObject).replaceAll(".class", "");
    }
}
