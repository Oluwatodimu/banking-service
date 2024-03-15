package com.woodcore.backend.bankingservice.service.impl;

import com.woodcore.backend.bankingservice.dto.request.CreateAccountRequest;
import com.woodcore.backend.bankingservice.dto.request.UpdateAccountDetailsRequest;
import com.woodcore.backend.bankingservice.model.Account;
import com.woodcore.backend.bankingservice.model.Currency;
import com.woodcore.backend.bankingservice.model.User;
import com.woodcore.backend.bankingservice.model.enums.AccountStatus;
import com.woodcore.backend.bankingservice.model.enums.AccountType;
import com.woodcore.backend.bankingservice.repository.AccountRepository;
import com.woodcore.backend.bankingservice.repository.CurrencyRepository;
import com.woodcore.backend.bankingservice.repository.UserRepository;
import com.woodcore.backend.bankingservice.service.AccountService;
import com.woodcore.backend.bankingservice.utils.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final CurrencyRepository currencyRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Account createNewAccount(CreateAccountRequest createAccountRequest, String userId) {

        try {

            User user = userRepository.findById(UUID.fromString(userId)).orElseThrow();
            Currency currency = currencyRepository.findBySymbol(createAccountRequest.getCurrency()).orElseThrow();
            Account newAccount = createAccount(createAccountRequest, user, currency);
            return accountRepository.save(newAccount);

        } catch (RuntimeException exception) {
            System.err.println(exception.getMessage());
            throw new RuntimeException("error creating new account for user");
        }
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow();
    }

    @Override
    @Transactional
    public Account updateAccountDetails(UpdateAccountDetailsRequest request) {

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber()).orElseThrow();

        if (request.getType() != null) {
            account.setType(request.getType());
        }

        if (request.getStatus() != null) {
            account.setStatus(request.getStatus());
        }

        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account closeAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
        account.setStatus(AccountStatus.DEACTIVATED);
        return accountRepository.save(account);
    }

    @Override
    public List<Account> getAllAccountsByUser(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow();
        return accountRepository.findAllByUser(user);
    }

    private Account createAccount(CreateAccountRequest createAccountRequest, User user, Currency currency) {
        return Account.builder()
                .availableBalance(BigDecimal.ZERO)
                .reservedBalance(BigDecimal.ZERO)
                .locked(false)
                .status(AccountStatus.ACTIVE)
                .type(createAccountRequest.getAccountType())
                .currency(currency)
                .accountNumber(AccountNumberGenerator.generateAccountNumber())
                .user(user)
                .build();
    }
}
