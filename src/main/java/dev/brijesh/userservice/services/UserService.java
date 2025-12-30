package dev.brijesh.userservice.services;

import dev.brijesh.userservice.dtos.UserDTO;
import dev.brijesh.userservice.models.Role;
import dev.brijesh.userservice.models.User;
import dev.brijesh.userservice.repositories.RoleRepository;
import dev.brijesh.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserDTO getUserDetails(Long userId){
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            return  null;
        }
        return UserDTO.from(userOptional.get());
    }
    public UserDTO setUserRoles(Long userId, List<String> roleNames){
        Optional<User> userOptional = userRepository.findById(userId);
        List<Role> roles = roleRepository.findAllByRoleIn(roleNames);

        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        user.setRoles(new HashSet<>(roles));

        User savedUser = userRepository.save(user);

        return UserDTO.from(savedUser);
    }
}

