package TestClasses;

import org.example.Annotations.Inject;
import org.example.Annotations.Lazy;

public class B {
    @Inject
    @Lazy
    public A aField;
}