package edu.tcu.cs.frogcrewbackend.system.exception;
 
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
} 