package com.woodcore.backend.bankingservice.service;

import com.woodcore.backend.bankingservice.dto.request.CreateAccountRequest;
import com.woodcore.backend.bankingservice.dto.request.UpdateAccountDetailsRequest;
import com.woodcore.backend.bankingservice.model.Account;

import java.util.List;

public interface AccountService {

    Account createNewAccount(CreateAccountRequest createAccountRequest, String userId);

    Account findByAccountNumber(String accountNumber);

    Account updateAccountDetails(UpdateAccountDetailsRequest request);

    Account closeAccount(String accountNumber);

    List<Account> getAllAccountsByUser(String userId);
}
