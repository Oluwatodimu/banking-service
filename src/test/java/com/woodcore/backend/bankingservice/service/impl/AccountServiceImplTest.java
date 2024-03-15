package com.woodcore.backend.bankingservice.service.impl;

import com.woodcore.backend.bankingservice.dto.request.CreateAccountRequest;
import com.woodcore.backend.bankingservice.dto.request.UpdateAccountDetailsRequest;
import com.woodcore.backend.bankingservice.model.Account;
import com.woodcore.backend.bankingservice.model.Currency;
import com.woodcore.backend.bankingservice.model.User;
import com.woodcore.backend.bankingservice.model.enums.AccountStatus;
import com.woodcore.backend.bankingservice.model.enums.AccountType;
import com.woodcore.backend.bankingservice.model.enums.CurrencySymbol;
import com.woodcore.backend.bankingservice.model.enums.UserType;
import com.woodcore.backend.bankingservice.repository.AccountRepository;
import com.woodcore.backend.bankingservice.repository.CurrencyRepository;
import com.woodcore.backend.bankingservice.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    public static final String ACCOUNT_NUMBER = "2209327281";

    @Mock private CurrencyRepository currencyRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private AccountServiceImpl accountService;

    private User user;
    private Currency currency;
    private Account account;

    @BeforeEach
    public void init() {
        user = User.builder()
                .firstName("test-user-first-name")
                .lastName("test-user-last-name")
                .password("test-password-12345")
                .email("test-user@example.com")
                .phoneNumber("08102023276")
                .userType(UserType.USER)
                .build();

        user.setId(UUID.randomUUID());

        currency = Currency.builder()
                .symbol(CurrencySymbol.NGN)
                .build();

        account = Account.builder()
                .accountNumber("2093255991")
                .type(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .build();
    }

    @Test
    public void successfulCreationOfAccount() {

        CreateAccountRequest accountRequest = new CreateAccountRequest();
        accountRequest.setAccountType(AccountType.SAVINGS);
        accountRequest.setCurrency(CurrencySymbol.NGN);

        BDDMockito.given(userRepository.findById(Mockito.any(UUID.class))).willReturn(Optional.of(user));
        BDDMockito.given(currencyRepository.findBySymbol(CurrencySymbol.NGN)).willReturn(Optional.of(currency));
        BDDMockito.given(accountRepository.save(Mockito.any(Account.class))).willReturn(account);

        Account newAccount = accountService.createNewAccount(accountRequest, UUID.randomUUID().toString());
        Assertions.assertThat(newAccount).isNotNull();
    }

    @Test
    public void successfullyFindByAccountNumber() {
        BDDMockito.given(accountRepository.findByAccountNumber(Mockito.any(String.class))).willReturn(Optional.of(account));

        Account retrievedAccount = accountService.findByAccountNumber(ACCOUNT_NUMBER);
        Assertions.assertThat(retrievedAccount).isNotNull();
    }

    @Test
    public void failedAttemptToFindByAccountNumber() {
        BDDMockito.given(accountRepository.findByAccountNumber(Mockito.any(String.class))).willReturn(Optional.empty());
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> accountService.findByAccountNumber(ACCOUNT_NUMBER));
    }

    @Test
    public void updateAccountDetails() {

        UpdateAccountDetailsRequest request = new UpdateAccountDetailsRequest();
        request.setAccountNumber(ACCOUNT_NUMBER);
        request.setType(AccountType.CURRENT);
        request.setStatus(AccountStatus.SUSPENDED);

        Account updatedAccount = Account.builder()
                .type(request.getType())
                .status(request.getStatus())
                .build();

        BDDMockito.given(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).willReturn(Optional.of(account));
        BDDMockito.given(accountRepository.save(Mockito.any(Account.class))).willReturn(updatedAccount);

        Account updateAccountDetails = accountService.updateAccountDetails(request);
        Assertions.assertThat(updateAccountDetails).isNotNull();
        Assertions.assertThat(updateAccountDetails.getType()).isEqualTo(AccountType.CURRENT);
        Assertions.assertThat(updateAccountDetails.getStatus()).isEqualTo(AccountStatus.SUSPENDED);
    }

    @Test
    public void successfulClosingOfAccount() {

        Account closedAccount = Account.builder()
                .status(AccountStatus.DEACTIVATED)
                .build();

        BDDMockito.given(accountRepository.findByAccountNumber(Mockito.any(String.class))).willReturn(Optional.of(account));
        BDDMockito.given(accountRepository.save(Mockito.any(Account.class))).willReturn(closedAccount);

        Account closedAccountDetails = accountService.closeAccount(ACCOUNT_NUMBER);
        Assertions.assertThat(closedAccountDetails).isNotNull();
        Assertions.assertThat(closedAccountDetails.getStatus()).isEqualTo(AccountStatus.DEACTIVATED);
    }

    @Test
    public void getAllAccountsByUser() {

        Account newAccount = Account.builder()
                .accountNumber("2093255991")
                .type(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .build();

        BDDMockito.given(userRepository.findById(Mockito.any(UUID.class))).willReturn(Optional.of(user));
        BDDMockito.given(accountRepository.findAllByUser(Mockito.any(User.class))).willReturn(List.of(account, newAccount));

        List<Account> accounts = accountService.getAllAccountsByUser(user.getId().toString());

        Assertions.assertThat(accounts).isNotNull();
        Assertions.assertThat(accounts.size()).isEqualTo(2);
    }
}