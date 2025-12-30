package dev.brijesh.userservice;

import dev.brijesh.userservice.models.Role;
import dev.brijesh.userservice.repositories.RoleRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SetupUserRolesTest {
    @Inject
    RoleRepository reoleRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Commit
    public void setupRoles(){
        var roleNames = List.of("Admin","User","Mentor");
        var roles = new ArrayList<Role>();
        roleNames.forEach(roleName->{;
            var role = new Role();
            role.setRole(roleName);
            roles.add(role);
        });

        roleRepository.saveAll(roles);
    }
}
