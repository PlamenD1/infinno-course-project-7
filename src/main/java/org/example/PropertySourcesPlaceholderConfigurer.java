package org.example;

import org.example.Annotations.Inject;
import org.example.Annotations.Named;
import org.example.TestClasses.E;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public class PropertySourcesPlaceholderConfigurer implements Initializer{
    @Inject
    @Named
    public String properties;
    Properties propertiesObj;

    public PropertySourcesPlaceholderConfigurer() {}

    public boolean processProperty(Field f, Object o, String propName) throws Exception {
        String prop = propertiesObj.getProperty(propName);
        if (prop == null)
            return false;

       if (setIfPrimitive(f, o, prop, propName))
           return true;

        f.set(o, prop);
        return true;
    }

    private boolean setIfPrimitive(Field f, Object o, String prop, String propName) throws Exception {
        switch (propName) {
            case "int": {
                f.set(o, Integer.valueOf(prop));
                return true;
            }
            case "float": {
                f.set(o, Float.valueOf(prop));
                return true;
            }
            case "double": {
                f.set(o, Double.valueOf(prop));
                return true;
            }
            case "short": {
                f.set(o, Short.valueOf(prop));
                return true;
            }
            case "byte": {
                f.set(o, Byte.valueOf(prop));
                return true;
            }
            case "long": {
                f.set(o, Long.valueOf(prop));
                return true;
            }
            case "boolean": {
                f.set(o, Boolean.valueOf(prop));
                return true;
            }
            default: return false;
        }
    }

    @Override
    public void init() throws Exception {
        propertiesObj = new Properties();
        propertiesObj.load(new FileInputStream(new File(properties)));
    }
}
