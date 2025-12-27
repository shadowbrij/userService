package dev.brijesh.userservice.exceptions;

public class WrongCredentialsException extends Exception{
    public WrongCredentialsException(String message) {
        super(message);
    }    
}
