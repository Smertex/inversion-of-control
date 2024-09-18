package by.smertex.interfaces;

import by.smertex.exception.LoadComponentException;

import java.util.List;

public interface ClassFinderBasic {
    List<Class<?>> findClasses(String componentPath);
    List<Class<?>> getClasses();

    static String pathMerging(String rootPath, String appendableObject){
        return rootPath + "." + appendableObject;
    }

    static String mergeClassPath(String rootPath, String appendableObject){
        return pathMerging(rootPath, appendableObject).replaceAll(".class", "");
    }
}
