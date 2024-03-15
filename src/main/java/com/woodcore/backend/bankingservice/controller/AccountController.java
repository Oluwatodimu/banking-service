package com.woodcore.backend.bankingservice.controller;

import com.woodcore.backend.bankingservice.dto.BaseResponse;
import com.woodcore.backend.bankingservice.dto.request.CreateAccountRequest;
import com.woodcore.backend.bankingservice.dto.request.UpdateAccountDetailsRequest;
import com.woodcore.backend.bankingservice.model.Account;
import com.woodcore.backend.bankingservice.service.AccountService;
import com.woodcore.backend.bankingservice.utils.MethodSecurityConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/accounts", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AccountController extends BaseController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<BaseResponse> createAccount(Authentication authentication, @RequestBody @Valid CreateAccountRequest accountRequest) {
        log.info("creating new account for user: {}", authentication.getName());
        Account createdAccount = accountService.createNewAccount(accountRequest, authentication.getName());
        return new ResponseEntity<>(createBseResponse(createdAccount), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{accountNumber}")
    public ResponseEntity<BaseResponse> getAccountWithAccountNumber(
                @PathVariable @NotEmpty(message = "account number cannot be empty") String accountNumber) {

        log.info("getting account details for account number: {}", accountNumber);
        Account account = accountService.findByAccountNumber(accountNumber);
        return new ResponseEntity<>(createBseResponse(account), HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize(MethodSecurityConstants.ADMIN)
    public ResponseEntity<BaseResponse> updateAccountDetails(@RequestBody @Valid UpdateAccountDetailsRequest request) {
        log.info("updating user details for account number: {}", request.getAccountNumber());
        Account account = accountService.updateAccountDetails(request);
        return new ResponseEntity<>(createBseResponse(account), HttpStatus.OK);
    }

    @PutMapping(value = "/{accountNumber}")
    @PreAuthorize(MethodSecurityConstants.ADMIN)
    public ResponseEntity<BaseResponse> closeAccount(
            @PathVariable @NotEmpty(message = "account number cannot be empty") String accountNumber) {

        log.info("closing account: {}", accountNumber);
        Account account = accountService.closeAccount(accountNumber);
        return new ResponseEntity<>(createBseResponse(account), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getAllUserAccounts(Authentication authentication) {
        log.info("getting all accounts for user: {}", authentication.getName());
        List<Account> accounts = accountService.getAllAccountsByUser(authentication.getName());
        return new ResponseEntity<>(createBseResponse(accounts), HttpStatus.OK);
    }
}
