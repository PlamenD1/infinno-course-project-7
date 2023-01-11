package org.example.TestClasses;

import org.example.Annotations.Inject;
import org.example.Annotations.Named;
import org.example.Initializer;

public class FSI implements Initializer {

    public FSI() {}

    @Override
    public void init() throws Exception {
        email = "mailto:" + email;
    }

    @Inject
    @Named
    public String email;
}