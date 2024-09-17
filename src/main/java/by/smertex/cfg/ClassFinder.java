package by.smertex.cfg;

import by.smertex.exception.ComponentDirectoryIsEmpty;
import by.smertex.exception.LoadComponentException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassFinder {
     public static List<Class<?>> findComponents(String componentPath) {
         List<String> paths = objectInDirectory(componentPath);
         List<Class<?>> allClassInDirectory = paths.stream()
                 .filter(line -> line.endsWith(".class"))
                 .map(clazz -> pathToClass(mergeClassPath(componentPath, clazz)))
                 .collect(Collectors.toList());

        paths.stream()
                .filter(line -> !line.contains("."))
                .forEach(directory -> allClassInDirectory.addAll(findComponents(pathMerging(componentPath, directory))));

        return allClassInDirectory;
    }

    private static List<String> objectInDirectory(String componentPath){
        try(var stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(componentPath.replaceAll("[.]", "/"));
            var reader = new BufferedReader(new InputStreamReader(stream))){

            return reader.lines().collect(Collectors.toList());

        } catch (NullPointerException e) {
            throw new ComponentDirectoryIsEmpty(e);
        }
        catch (IOException e) {
            throw new LoadComponentException(e);
        }
    }

    private static String pathMerging(String rootPath, String appendableObject){
        return rootPath + "." + appendableObject;
    }

    private static String mergeClassPath(String rootPath, String appendableObject){
        return pathMerging(rootPath, appendableObject).replaceAll(".class", "");
    }

    private static Class<?> pathToClass(String componentPath){
        try {
            return Class.forName(componentPath);
        } catch (ClassNotFoundException e) {
            throw new LoadComponentException(e);
        }
    }

    private ClassFinder(){

    }
}
