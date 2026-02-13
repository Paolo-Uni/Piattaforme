package org.example.progetto.exceptions;

public class CredentialsAlreadyExistException extends RuntimeException {
    public CredentialsAlreadyExistException(String message) {
        super(message);
    }
}