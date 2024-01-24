package dev.brijesh.userservice.services;

import dev.brijesh.userservice.dtos.UserDTO;
import dev.brijesh.userservice.models.Session;
import dev.brijesh.userservice.models.SessionStatus;
import dev.brijesh.userservice.models.User;
import dev.brijesh.userservice.repositories.SessionRepository;
import dev.brijesh.userservice.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseEntity<UserDTO> login(String email, String password){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            return  null;
        }

        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
            System.out.println("DEBUG");
            //TODO : Create EXception DTO
            throw new RuntimeException("Wrong Password!!");
        }

        //Generating the TOKEN
        //String token = RandomStringUtils.randomAlphabetic(30);


        // Create a test key suitable for the desired HMAC-SHA algorithm:
        MacAlgorithm alg = Jwts.SIG.HS256; //or HS384 or HS256
        SecretKey key = alg.key().build();

        //String message = "Hello World!";
        //byte[] content = message.getBytes(StandardCharsets.UTF_8);
        Map<String,Object> payload = new HashMap<>();
        payload.put("email","bk@gmail.com");
        payload.put("generatedAt", new Date());
        payload.put("expiresAt", DateUtils.addDays(new Date(),30));
        payload.put("roles", List.of(user.getRoles()));


        // Create the compact JWS:
        //String jws = Jwts.builder().content(content, "text/plain").signWith(key, alg).compact();

        String jws = Jwts.builder()
                .claims(payload)
                .signWith(key)
                .compact();

        // Parse the compact JWS:
       // content = Jwts.parser().verifyWith(key).build().parseSignedContent(jws).getPayload();
        //assert message.equals(new String(content, StandardCharsets.UTF_8));


        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(jws);
        session.setUser(user);
        sessionRepository.save(session);

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE,"auth-token:"+jws);

        ResponseEntity<UserDTO> response = new ResponseEntity<>(userDTO,headers, HttpStatus.OK);

        return  response;
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

    public UserDTO signUp(String email, String password){
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
}

/*
Task-1 : Implement limit on number of active sessions for a user.
Task-2 : Implement login workflow using the token details with validation of expiry date.
*/