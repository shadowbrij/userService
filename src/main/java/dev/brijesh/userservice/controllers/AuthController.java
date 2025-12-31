package dev.brijesh.userservice.controllers;

import dev.brijesh.userservice.dtos.*;
import dev.brijesh.userservice.exceptions.DuplicateSignupException;
import dev.brijesh.userservice.exceptions.WrongCredentialsException;
import dev.brijesh.userservice.models.SessionStatus;
import dev.brijesh.userservice.services.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) throws WrongCredentialsException{
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) {
        return authService.logout(request.getToken(), request.getUserId());
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@RequestBody SignUpRequestDto request) throws DuplicateSignupException {
        UserDTO userDto = authService.signUp(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(ValidateTokenRequestDto request) {
        SessionStatus sessionStatus = authService.validate(request.getToken(), request.getUserId());

        return new ResponseEntity<>(sessionStatus, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<Long> getUser(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)  String bearerToken){
        return authService.getUserIdFromToken(bearerToken);
    }
}
