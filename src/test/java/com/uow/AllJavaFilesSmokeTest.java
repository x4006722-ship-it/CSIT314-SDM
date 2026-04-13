package com.uow;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;

class AllJavaFilesSmokeTest {

    @TestFactory
    Stream<DynamicTest> allMainJavaClassesCanLoad() throws IOException {
        List<String> classNames = discoverMainClassNames();
        assertFalse(classNames.isEmpty(), "No classes discovered under src/main/java");

        return classNames.stream().map(className ->
                DynamicTest.dynamicTest("load " + className, () -> {
                    Class<?> clazz = Class.forName(className);
                    assertNotNull(clazz);
                }));
    }

    @TestFactory
    Stream<DynamicTest> allConcreteMainJavaClassesCanInstantiate() throws IOException {
        List<String> classNames = discoverMainClassNames();

        return classNames.stream().map(className ->
                DynamicTest.dynamicTest("instantiate " + className, () -> {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                        return;
                    }
                    Object instance = instantiateClass(clazz, new ArrayList<>());
                    assertNotNull(instance, "Could not instantiate " + className);
                }));
    }

    private static List<String> discoverMainClassNames() throws IOException {
        Path base = Paths.get("src", "main", "java");
        try (Stream<Path> pathStream = Files.walk(base)) {
            return pathStream
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(base::relativize)
                    .map(Path::toString)
                    .map(path -> path.replace('\\', '.').replace('/', '.'))
                    .map(path -> path.substring(0, path.length() - ".java".length()))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    private static Object instantiateClass(Class<?> clazz, List<Class<?>> visiting) throws Exception {
        if (visiting.contains(clazz)) {
            return null;
        }
        visiting.add(clazz);
        try {
            Constructor<?> constructor = chooseConstructor(clazz);
            if (constructor == null) {
                return null;
            }
            constructor.setAccessible(true);
            Object[] args = new Object[constructor.getParameterCount()];
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                args[i] = defaultValue(parameterTypes[i], visiting);
            }
            return constructor.newInstance(args);
        } finally {
            visiting.remove(clazz);
        }
    }

    private static Constructor<?> chooseConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            return null;
        }
        return List.of(constructors).stream()
                .min(Comparator.comparingInt(Constructor::getParameterCount))
                .orElse(null);
    }

    private static Object defaultValue(Class<?> type, List<Class<?>> visiting) throws Exception {
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return false;
            }
            if (type == char.class) {
                return '\0';
            }
            return 0;
        }
        if (type == String.class) {
            return "";
        }
        if (type == Boolean.class) {
            return Boolean.FALSE;
        }
        if (type == Integer.class) {
            return Integer.valueOf(0);
        }
        if (type == Long.class) {
            return Long.valueOf(0L);
        }
        if (type == Double.class) {
            return Double.valueOf(0.0);
        }
        if (type == Float.class) {
            return Float.valueOf(0.0f);
        }
        if (type == Short.class) {
            return Short.valueOf((short) 0);
        }
        if (type == Byte.class) {
            return Byte.valueOf((byte) 0);
        }
        if (type == Character.class) {
            return Character.valueOf('\0');
        }
        if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            return constants.length > 0 ? constants[0] : null;
        }
        if (type.isArray()) {
            return Array.newInstance(type.getComponentType(), 0);
        }
        if (List.class.isAssignableFrom(type)) {
            return List.of();
        }
        if (Set.class.isAssignableFrom(type)) {
            return Set.of();
        }
        if (Collection.class.isAssignableFrom(type)) {
            return List.of();
        }
        if (Map.class.isAssignableFrom(type)) {
            return Map.of();
        }
        if (type.getName().startsWith("com.uow.")) {
            return instantiateClass(type, visiting);
        }
        try {
            return Mockito.mock(type);
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
