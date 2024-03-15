package com.woodcore.backend.bankingservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotEmpty(message = "first name cannot be empty")
    @Size(min = 1, max = 20, message = "First name must be between 1 and 20 characters long")
    private String firstName;

    @NotEmpty(message = "last name cannot be empty")
    @Size(min = 1, max = 20, message = "Last name must be between 1 and 20 characters long")
    private String lastName;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotEmpty(message = "phone number cannot be empty")
    @Pattern(regexp = "^\\d{11}$", message = "Phone number must be a valid phone number")
    private String phoneNumber;

    @NotEmpty(message = "Password cannot be empty")
    private String password;
}
