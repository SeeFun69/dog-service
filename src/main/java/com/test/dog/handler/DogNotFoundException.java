package com.test.dog.handler;

public class DogNotFoundException extends RuntimeException {
    public DogNotFoundException(Long id) {
        super("Could not find dog with id: " + id);
    }
}



