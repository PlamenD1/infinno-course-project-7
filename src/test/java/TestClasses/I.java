package TestClasses;

import org.example.Annotations.Inject;
import org.example.Annotations.Lazy;

public class I {
    @Inject
    @Lazy
    public C cField;
}
