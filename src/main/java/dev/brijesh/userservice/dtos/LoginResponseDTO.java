package dev.brijesh.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    Long userId;
    String message;
    String token;
}
