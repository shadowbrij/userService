package dev.brijesh.userservice.services;

import dev.brijesh.userservice.dtos.UserDTO;
import dev.brijesh.userservice.models.Session;
import dev.brijesh.userservice.models.SessionStatus;
import dev.brijesh.userservice.models.User;
import dev.brijesh.userservice.repositories.SessionRepository;
import dev.brijesh.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public ResponseEntity<UserDTO> login(String email, String password){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            return  null;
        }

        User user = userOptional.get();
        if(!user.getPassword().equals(password)){
            return  null;
        }

        String token = RandomStringUtils.randomAlphabetic(30);

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);

        UserDTO userDTO = new UserDTO();

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE,"auth-token:"+token);

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
        user.setPassword(password);

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
