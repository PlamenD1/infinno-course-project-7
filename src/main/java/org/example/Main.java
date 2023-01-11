package org.example;

import org.example.TestClasses.B;
import org.example.TestClasses.E;
import org.example.TestClasses.FSI;

public class Main {
    public static void main(String[] args) throws Exception {
        Registry r = new Registry();
        B inst = r.getInstance(B.class);

        System.out.println(inst);
        System.out.println(inst.aField);
    }
}