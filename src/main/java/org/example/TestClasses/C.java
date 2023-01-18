package org.example.TestClasses;

import org.example.Annotations.Inject;
import org.example.Annotations.Lazy;

public class C {
    @Inject
    public A aField;

    public void printMessage() {
        System.out.println("message");
    }
}