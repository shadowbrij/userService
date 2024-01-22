package dev.brijesh.userservice.controllers;

import dev.brijesh.userservice.dtos.SetUserRolesRequestDTO;
import dev.brijesh.userservice.dtos.UserDTO;
import dev.brijesh.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable("id") Long userId){
        UserDTO userDTO =  userService.getUserDetails(userId);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<UserDTO> setUserRoles(@PathVariable("id")Long userId, @RequestBody SetUserRolesRequestDTO roles){
        UserDTO userDTO = userService.setUserRoles(userId,roles.getRoles());
        return new ResponseEntity<>(userDTO,HttpStatus.OK);
    }
}
