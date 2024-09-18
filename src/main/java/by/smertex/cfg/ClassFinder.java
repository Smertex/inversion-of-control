package by.smertex.cfg;

import by.smertex.exception.ComponentDirectoryIsEmpty;
import by.smertex.exception.LoadComponentException;
import by.smertex.interfaces.ClassFinderBasic;
import by.smertex.utils.ClassUtil;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ClassFinder implements ClassFinderBasic {
    private List<Class<?>> allClassInProject;

    public ClassFinder(String componentPath){
        init(componentPath);
    }

    private void init(String componentPath){
        allClassInProject = findClasses(componentPath);
    }

    @Override
     public List<Class<?>> findClasses(String componentPath) {
        List<String> objects = objectInDirectory(componentPath);
        List<Class<?>> allClassInDirectory = findClassInDirectory(objects, componentPath);
        allClassInDirectory.addAll(recursiveTraversal(objects, componentPath));

        return allClassInDirectory;
    }

    @Override
    public List<Class<?>> getClasses() {
        return allClassInProject;
    }

    private List<String> objectInDirectory(String componentPath){
        try(var stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(componentPath.replaceAll("[.]", "/"));
            var reader = new BufferedReader(new InputStreamReader(stream))){

            return reader.lines().collect(Collectors.toList());

        } catch (NullPointerException e) {
            throw new ComponentDirectoryIsEmpty(e);
        } catch (IOException e) {
            throw new LoadComponentException(e);
        }
    }

    private List<Class<?>> findClassInDirectory(List<String> objects, String componentPath){
         return objects.stream()
                 .filter(line -> line.endsWith(".class"))
                 .map(clazz -> ClassUtil.pathToClass(ClassFinderBasic.mergeClassPath(componentPath, clazz)))
                 .collect(Collectors.toList());
    }

    private List<Class<?>> recursiveTraversal(List<String> objects, String componentPath){
         return objects.stream()
                .filter(line -> !line.contains("."))
                .map(directory -> findClasses(ClassFinderBasic.pathMerging(componentPath, directory)))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
