package TestClasses;

import org.example.Annotations.Inject;
import org.example.Annotations.NamedParameter;

public class E {
    public A aField;

    @Inject
    public E(@NamedParameter("afield") A afield) {
        this.aField = afield;
    }
}