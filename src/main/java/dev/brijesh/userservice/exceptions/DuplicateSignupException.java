package dev.brijesh.userservice.exceptions;

public class DuplicateSignupException extends Exception{
    public DuplicateSignupException(String message){
        super(message);
    }
}
