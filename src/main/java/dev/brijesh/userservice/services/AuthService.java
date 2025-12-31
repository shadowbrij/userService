package dev.brijesh.userservice.services;

import dev.brijesh.userservice.dtos.LoginResponseDTO;
import dev.brijesh.userservice.dtos.UserDTO;
import dev.brijesh.userservice.exceptions.DuplicateSignupException;
import dev.brijesh.userservice.exceptions.WrongCredentialsException;
import dev.brijesh.userservice.models.Session;
import dev.brijesh.userservice.models.SessionStatus;
import dev.brijesh.userservice.models.User;
import dev.brijesh.userservice.repositories.SessionRepository;
import dev.brijesh.userservice.repositories.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseEntity<LoginResponseDTO> login(String email, String password) throws WrongCredentialsException{
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            return  null;
        }

        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
            throw new WrongCredentialsException("Incorrect username or password");
        }

        MacAlgorithm alg = Jwts.SIG.HS256; //or HS384 or HS256
        SecretKey key = alg.key().build();

        Map<String,Object> payload = new HashMap<>();
        payload.put("email",user.getEmail());
        payload.put("generatedAt", new Date());
        payload.put("expiresAt", DateUtils.addDays(new Date(),30));
        payload.put("roles", List.of(user.getRoles()));

        String jws = Jwts.builder()
                .claims(payload)
                .signWith(key)
                .compact();

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(jws);
        session.setUser(user);

        //set expiry 4 hours from now
        session.setExpiryDate(DateUtils.addHours(new Date(),4));
        sessionRepository.save(session);

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setUserId(user.getId());
        responseDTO.setMessage("Login Successful");
        responseDTO.setToken(jws);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE,"auth-token:"+jws);

        return new ResponseEntity<>(responseDTO,headers, HttpStatus.OK);
    }

    public ResponseEntity<Void> logout(String token, Long userId){
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token,userId);
        if(sessionOptional.isEmpty()){
            return null;
        }

        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.Ended);

        sessionRepository.save(session);

        return ResponseEntity.ok().build();
    }

    public UserDTO signUp(String email, String password) throws DuplicateSignupException {
        User existingUser = userRepository.findByEmail(email).orElse(null);
        if(existingUser != null){
            throw new DuplicateSignupException("User with email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        userRepository.save(user);

        return  UserDTO.from(user);
    }

    public SessionStatus validate(String token, Long userId){
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token,userId);

        if(sessionOptional.isEmpty()){
            return  null;
        }

        return SessionStatus.ACTIVE;
    }
    public ResponseEntity<Long> getUserIdFromToken(String authToken){
        Optional<Session> sessionOptional = sessionRepository.findSessionByToken(authToken);
        if(sessionOptional.isEmpty()){
            return null;
        }

        Session session = sessionOptional.get();
        return new ResponseEntity<>(session.getUser().getId(),HttpStatus.OK);
    }
}