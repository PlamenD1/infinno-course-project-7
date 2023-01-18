package org.example;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.InvocationHandler;
import org.example.Annotations.*;
import org.example.TestClasses.E;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registry {
    private final Pattern PSBC_PATTERN = Pattern.compile("\\$\\{(.*)}");
    Map<String, Object> stringToInstance = new HashMap<>();
    Map<Class<?>, Object> classToInstance = new HashMap<>();
    Map<Class<?>, Class<?>> interfaceToClass = new HashMap<>();

    public Registry() {}

    public Object getInstance(String key) throws Exception {
        Object o = stringToInstance.get(key);
        if (o == null)
            return null;

        handleFields(o, true);

        return o;
    }

    public <T> T getInstance(Class<T> c) throws Exception {
        return getInstance(c, true);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> c, boolean addDependencies) throws Exception {
        T o = (T) classToInstance.get(c);
        if (o == null) {
            Default _default = c.getAnnotation(Default.class);
            if (_default != null && _default.value() != null) {
                o = (T) getObject(_default.value());
            } else {
                if (c.isInterface())
                    throw new RegistryException("Cannot instantiate interface!");

                o = (T) getObject(c);

                if (addDependencies)
                    classToInstance.put(c, o);
            }
        }


        handleFields(o, addDependencies);

        if (o instanceof Initializer) {
            Initializer oInit = (Initializer) o;
            oInit.init();
        }

        return o;
    }

    public void decorateInstance(Object o) throws Exception {
        handleFields(o, false);
    }

    public void registerImplementation(Class<?> c, Class<?> subClass) throws Exception {
        if (interfaceToClass.get(c) != null)
            throw new RegistryException("This class is already registered!");

        interfaceToClass.put(c, subClass);
    }

    public void registerInstance(String key, Object instance) throws Exception {
        if (stringToInstance.get(key) != null)
            throw new RegistryException("This key is already registered!");

        stringToInstance.put(key, instance);
    }

    public void registerInstance(Class<?> c, Object instance) throws Exception {
        if (classToInstance.get(c) != null)
            throw new RegistryException("This class is already registered!");

        classToInstance.put(c, instance);
    }

    public void registerInstance(Object instance) throws Exception {
        if (classToInstance.get(instance.getClass()) != null)
            throw new RegistryException("The class of this object is already registered!");

        classToInstance.put(instance.getClass(), instance);
    }

    public Object getObject(Class<?> clazz) throws Exception {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> c : constructors) {
            c.setAccessible(true);
            Inject inject = c.getAnnotation(Inject.class);
            if (inject == null)
                continue;

            Parameter[] params = c.getParameters();
            Object[] paramInstances = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                if (setIfPrimitiveWrapperOrString(params[i], paramInstances, i))
                    continue;

                paramInstances[i] = getParamInstance(params, i);
            }

            return c.newInstance(paramInstances);
        }

        return clazz.getConstructor().newInstance();
    }

    private Object getParamInstance(Parameter[] params, int i) throws Exception {
        Object paramInstance = null;
        NamedParameter name = params[i].getAnnotation(NamedParameter.class);
        if (name != null && name.value() != null) {
            paramInstance = getInstance(name.value());
        }

        if (paramInstance == null) {
            paramInstance = getInstance(params[i].getType());
        }

        if (paramInstance == null)
            throw new RegistryException("Cannot instantiate parameter for constructor!");

        return paramInstance;
    }

    public void handleFields(Object o, boolean addDependencies) throws Exception {
        Field[] fields = o.getClass().getFields();
        for (Field f : fields) {
            f.setAccessible(true);

            Inject inject = f.getAnnotation(Inject.class);
            if (inject == null)
                continue;

            Lazy lazy = f.getAnnotation(Lazy.class);
            if (lazy != null) {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(f.getType());
                enhancer.setCallback(new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object oField = getInstance(f.getType());
                        f.set(o, oField);
                        return method.invoke(oField, args);
                    }
                });
                Object proxy = enhancer.create();

                f.set(o, proxy);
                continue;
            }

            Named named = f.getAnnotation(Named.class);

            if (named != null) {
                f.set(o, stringToInstance.get(f.getName()));
            }

            if (!f.getType().isPrimitive()) {
                if (f.get(o) != null) {
                    continue;
                }

                f.set(o, classToInstance.get(f.getType()));

                if (f.get(o) != null) {
                    continue;
                }

                if (!f.getType().isInterface()) {
                    f.set(o, getInstance(f.getType(), addDependencies));
                    continue;
                }

                Class<?> clazz = interfaceToClass.get(f.getType());
                if (clazz != null) {
                    f.set(o, getInstance(clazz, addDependencies));
                    continue;
                }
            }

            Value value = f.getAnnotation(Value.class);
            if (value == null || value.value() == null) {
                continue;
            }

            Matcher matcher = PSBC_PATTERN.matcher(value.value());
            if (matcher.find()) {
                String propName = matcher.group(1);

                PropertySourcesPlaceholderConfigurer pspc = getInstance(PropertySourcesPlaceholderConfigurer.class);
                if (pspc.processProperty(f, o, propName)) {
                    continue;
                }
            }

            Default _default = f.getType().getAnnotation(Default.class);
            if (_default == null && !f.getType().isPrimitive())
                throw new RegistryException("Missing default class!");

            if (_default != null) {
                Class<?> defaultClass = _default.value();
                if (defaultClass == null)
                    throw new RegistryException("Missing default class!");

                f.set(o, getInstance(defaultClass, addDependencies));
            }
        }
    }

    boolean setIfPrimitiveWrapperOrString(Parameter o, Object[] paramInstances, int index) {
            Object inst = stringToInstance.get(o.getClass().getSimpleName());

            if (inst == null)
                return false;

            paramInstances[index] = inst;
            return true;
    }
}