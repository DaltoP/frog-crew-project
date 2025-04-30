package edu.tcu.cs.frogcrewbackend.system.exception;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}