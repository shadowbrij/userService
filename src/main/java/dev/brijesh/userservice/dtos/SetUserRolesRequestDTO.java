package dev.brijesh.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class  SetUserRolesRequestDTO {
    private Long userId;
    private List<String> roles;
}
