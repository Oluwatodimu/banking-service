package com.woodcore.backend.bankingservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {

    @NotEmpty(message = "email field cannot be empty")
    private String email;

    @NotEmpty(message = "password field cannot be empty")
    private String password;

    private boolean rememberMe = false;
}
