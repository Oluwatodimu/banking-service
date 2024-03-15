package com.woodcore.backend.bankingservice.controller;

import com.woodcore.backend.bankingservice.dto.BaseResponse;
import com.woodcore.backend.bankingservice.dto.request.RegisterUserRequest;
import com.woodcore.backend.bankingservice.dto.request.UserLoginRequest;
import com.woodcore.backend.bankingservice.model.User;
import com.woodcore.backend.bankingservice.security.jwt.JwtToken;
import com.woodcore.backend.bankingservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users", produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController extends BaseController {

    private final UserService userService;

    @PostMapping(value = "/register")
    public ResponseEntity<BaseResponse> registerUser(@RequestBody @Valid RegisterUserRequest signupRequest) {
        log.info("registering new user with email {}", signupRequest.getEmail());
        User registeredUser = userService.registerUser(signupRequest);
        return new ResponseEntity<>(createBseResponse(registeredUser), HttpStatus.CREATED);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<BaseResponse> loginUser(@RequestBody @Valid UserLoginRequest loginRequest) {
        log.info("authenticating user: {}", loginRequest.getEmail());
        JwtToken jwtToken = userService.authenticateUser(loginRequest);
        return new ResponseEntity<>(createBseResponse(jwtToken), HttpStatus.OK);
    }
}
