package dev.brijesh.userservice.controllers;

import dev.brijesh.userservice.dtos.ExceptionDTO;
import dev.brijesh.userservice.exceptions.DuplicateSignupException;
import dev.brijesh.userservice.exceptions.WrongCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthControllerAdvice {
    @ExceptionHandler(DuplicateSignupException.class)
    public ResponseEntity<ExceptionDTO> handleDuplicateSignupException(DuplicateSignupException ex) {
        ExceptionDTO exceptionDTO = new  ExceptionDTO();
        exceptionDTO.setMessage(ex.getMessage());
        exceptionDTO.setStatus(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongCredentialsException.class)
    public ResponseEntity<ExceptionDTO> handleWrongCredentialsException(WrongCredentialsException ex) {
        ExceptionDTO exceptionDTO = new  ExceptionDTO();
        exceptionDTO.setMessage(ex.getMessage());
        exceptionDTO.setStatus(HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(exceptionDTO, HttpStatus.UNAUTHORIZED);
    }
}
