package org.example;

import org.example.Annotations.Default;
import org.example.Annotations.Inject;
import org.example.Annotations.Named;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Registry {
    Map<String, Object> stringToInstance = new HashMap<>();
    Map<Class<?>, Object> classToInstance = new HashMap<>();
    Map<Class<?>, Class<?>> classToClass = new HashMap<>();
    public Object getInstance(String key) throws Exception {
        Object o = stringToInstance.get(key);
        if (o == null)
            return null;
        setObjectFields(o);

        return o;
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> c) throws Exception {
        T o = (T) classToInstance.get(c);
        if (o == null) {
            Default _default = c.getAnnotation(Default.class);
            if (_default != null && _default.value() != null) {
                o = (T) getObject(_default.value());
            } else {
                if (c.isInterface())
                    throw new RegistryException();

                o = (T) getObject(c);
                classToInstance.put(c, o);
            }
        }

        setObjectFields(o);

        return o;
    }

    public void decorateInstance(Object o) throws Exception {
        setObjectFields(o);
    }

    public void registerImplementation(Class<?> c, Class<?> subClass) throws Exception {
        if (classToClass.get(c) != null)
            throw new RegistryException();

        classToClass.put(c, subClass);
    }

    public void registerInstance(String key, Object instance) throws Exception {
        if (stringToInstance.get(key) != null)
            throw new RegistryException();

        stringToInstance.put(key, instance);
    }

    public void registerInstance(Class<?> c, Object instance) throws Exception {
        if (classToInstance.get(c) != null)
            throw new RegistryException();

        classToInstance.put(c, instance);
    }

    public void registerInstance(Object instance) throws Exception {
        if (classToInstance.get(instance.getClass()) != null)
            throw new RegistryException();

        classToInstance.put(instance.getClass(), instance);
    }

    public Object getObject(Class<?> clazz) throws Exception {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> c : constructors) {
            c.setAccessible(true);
            Inject inject = c.getAnnotation(Inject.class);
            if (inject == null)
                continue;

            Class<?>[] paramClasses = c.getParameterTypes();
            Object[] params = new Object[paramClasses.length];
            for (int i = 0; i < paramClasses.length; i++) {
                if (isWrapperOrString(paramClasses[i]))
                    continue;

                params[i] = getInstance(paramClasses[i]);
            }

            return c.newInstance(params);
        }

        return clazz.getConstructor().newInstance();
    }

    public void setObjectFields(Object o) throws Exception {
        Field[] fields = o.getClass().getFields();
        for (Field f : fields) {
            f.setAccessible(true);
            Inject inject = f.getAnnotation(Inject.class);
            if (inject == null)
                continue;

            Named named = f.getAnnotation(Named.class);

            if (named != null) {
                f.set(o, stringToInstance.get(f.getName()));
            }

            if (f.get(o) != null)
                continue;

            f.set(o, classToInstance.get(f.getType()));

            if (f.get(o) != null)
                continue;

            if (f.getType().isInterface() && f.get(o) == null) {
                Class<?> clazz = classToClass.get(f.getType());
                if (clazz != null) {
                    f.set(o, getInstance(clazz));
                    continue;
                }

                Default _default = f.getType().getAnnotation(Default.class);
                if (_default == null)
                    throw new RegistryException();

                Class<?> defaultClass = _default.value();
                if (defaultClass == null)
                    throw new RegistryException();

                f.set(o, getInstance(defaultClass));
            } else f.set(o, getInstance(f.getType()));
        }
    }

    boolean isWrapperOrString(Class<?> clazz) {
        return clazz == Integer.class ||
               clazz == Double.class ||
               clazz == Float.class ||
               clazz == Boolean.class ||
               clazz == Byte.class ||
               clazz == String.class;
    }
}