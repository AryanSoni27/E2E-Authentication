package com.security.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private String userName;
    private String email;
    private String password;
}
