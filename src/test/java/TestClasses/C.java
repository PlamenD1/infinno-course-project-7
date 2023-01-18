package TestClasses;

import org.example.Annotations.Inject;

public class C {
    @Inject
    public B bField;

    public void printMessage() {
        System.out.println("message");
    }
}
