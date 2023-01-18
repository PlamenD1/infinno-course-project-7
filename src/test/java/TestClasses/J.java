package TestClasses;

import org.example.Annotations.Inject;
import org.example.Annotations.Value;

public class J {
    @Inject
    @Value("${int}")
    public int number;
}