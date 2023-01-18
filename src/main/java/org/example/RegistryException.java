package org.example;

public class RegistryException extends Exception {
    String message;

    public RegistryException(String message) {
        this.message = message;
    }
}
