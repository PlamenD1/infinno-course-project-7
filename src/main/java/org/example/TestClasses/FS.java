package org.example.TestClasses;

import org.example.Annotations.Inject;
import org.example.Annotations.Named;

public class FS {

    public FS() {}

    @Inject
    @Named
    public String email;
}