package org.example.TestClasses;

import org.example.Annotations.Inject;

public class E {
    public E() {}

    public A aField;

    @Inject
    public E(A afield) {
        this.aField = afield;
    }
}