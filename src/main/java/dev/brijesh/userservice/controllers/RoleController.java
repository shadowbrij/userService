package dev.brijesh.userservice.controllers;

import dev.brijesh.userservice.dtos.CreateRoleRequestDTO;
import dev.brijesh.userservice.models.Role;
import dev.brijesh.userservice.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    public ResponseEntity<Role> createRole(@RequestBody CreateRoleRequestDTO request){
        Role role = roleService.createRole(request.getName());
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
}
