package org.example;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import org.example.TestClasses.*;

import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) throws Exception {
        Registry r = new Registry();
        r.registerInstance("properties", "C:\\work\\infinno-course-project-7\\src\\main\\resources\\config.properties");
        r.registerInstance(PropertySourcesPlaceholderConfigurer.class, new PropertySourcesPlaceholderConfigurer());

        J j = r.getInstance(J.class);

        System.out.println(j.number);
    }
}