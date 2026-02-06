package org.example.progetto.exceptions;

public class CredentialsAlredyExistException extends RuntimeException {
    public CredentialsAlredyExistException(String message) {
        super(message);
    }
}
