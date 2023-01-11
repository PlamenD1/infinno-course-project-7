package org.example.TestClasses;

import org.example.Annotations.Inject;
import org.example.Annotations.Named;

public class F {
    public F() {}
    @Inject
    @Named
    public A iname;
}