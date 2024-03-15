package com.woodcore.backend.bankingservice.service;

import com.woodcore.backend.bankingservice.dto.request.CreateAccountRequest;
import com.woodcore.backend.bankingservice.dto.request.RegisterUserRequest;
import com.woodcore.backend.bankingservice.dto.request.UserLoginRequest;
import com.woodcore.backend.bankingservice.model.Account;
import com.woodcore.backend.bankingservice.model.User;
import com.woodcore.backend.bankingservice.security.jwt.JwtToken;

public interface UserService {

    User registerUser(RegisterUserRequest registerUserRequest);

    JwtToken authenticateUser(UserLoginRequest loginRequest);
}
