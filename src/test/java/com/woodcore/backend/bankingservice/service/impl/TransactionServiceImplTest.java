package com.woodcore.backend.bankingservice.service.impl;

import com.woodcore.backend.bankingservice.dto.request.TransferRequest;
import com.woodcore.backend.bankingservice.model.Account;
import com.woodcore.backend.bankingservice.model.Currency;
import com.woodcore.backend.bankingservice.model.Transaction;
import com.woodcore.backend.bankingservice.model.User;
import com.woodcore.backend.bankingservice.model.enums.*;
import com.woodcore.backend.bankingservice.repository.AccountRepository;
import com.woodcore.backend.bankingservice.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock private AccountRepository accountRepository;
    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private TransactionServiceImpl transactionService;

    private User user;
    private Currency nairaCurrency;
    private Account activeAccount;
    private Account secondActiveAccount;
    private Account suspendedAccount;
    private Transaction creditTransaction;
    private Transaction debitTransaction;
    private TransferRequest transferRequest;

    @BeforeEach
    public void init() {
        String username = "610d0a5a-55c7-4047-b65c-cb8403b4861c";
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        user = User.builder()
                .firstName("test-user-first-name")
                .lastName("test-user-last-name")
                .password("test-password-12345")
                .email("test-user@example.com")
                .phoneNumber("08102023276")
                .userType(UserType.USER)
                .build();

        user.setId(UUID.fromString("610d0a5a-55c7-4047-b65c-cb8403b4861c"));

        transferRequest = new TransferRequest();
        transferRequest.setTo("3093255991");
        transferRequest.setFrom("2209327281");
        transferRequest.setAmount(BigDecimal.valueOf(3000));
        transferRequest.setDescription("mock description");

        nairaCurrency = Currency.builder()
                .symbol(CurrencySymbol.NGN)
                .build();

        activeAccount = Account.builder()
                .user(user)
                .availableBalance(BigDecimal.ZERO)
                .accountNumber("2093255991")
                .type(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .currency(nairaCurrency)
                .build();

        secondActiveAccount = Account.builder()
                .user(user)
                .availableBalance(BigDecimal.ZERO)
                .accountNumber("2093255992")
                .type(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .currency(nairaCurrency)
                .build();

        suspendedAccount = Account.builder()
                .user(user)
                .availableBalance(BigDecimal.ZERO)
                .accountNumber("2093255991")
                .type(AccountType.SAVINGS)
                .status(AccountStatus.SUSPENDED)
                .build();

        creditTransaction = Transaction.builder()
                .amount(transferRequest.getAmount())
                .type(TransactionType.CREDIT)
                .purpose(TransactionPurpose.DEPOSIT_EXTERNAL)
                .reference(UUID.randomUUID())
                .status(TransactionStatus.SUCCESS)
                .description(transferRequest.getDescription())
                .senderAccount(transferRequest.getFrom())
                .receiverAccount(transferRequest.getTo())
                .account(activeAccount)
                .build();

        debitTransaction = Transaction.builder()
                .amount(transferRequest.getAmount())
                .type(TransactionType.DEBIT)
                .purpose(TransactionPurpose.WITHDRAWAL_EXTERNAL)
                .reference(UUID.randomUUID())
                .status(TransactionStatus.SUCCESS)
                .description(transferRequest.getDescription())
                .senderAccount(transferRequest.getFrom())
                .receiverAccount(transferRequest.getTo())
                .account(activeAccount)
                .build();

    }

    @Test
    public void successfulDepositTransaction() {
        BDDMockito.given(accountRepository.findByAccountNumber(Mockito.any(String.class))).willReturn(Optional.of(activeAccount));
        BDDMockito.given(accountRepository.save(Mockito.any(Account.class))).willReturn(activeAccount);
        BDDMockito.given(transactionRepository.save(Mockito.any(Transaction.class))).willReturn(creditTransaction);

        Transaction transaction = transactionService.deposit(transferRequest);

        org.assertj.core.api.Assertions.assertThat(transaction).isNotNull();
        org.assertj.core.api.Assertions.assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        org.assertj.core.api.Assertions.assertThat(transaction.getType()).isEqualTo(TransactionType.CREDIT);
    }

    @Test
    public void failedDepositTransactionWhenAccountIsNotActive() {
        BDDMockito.given(accountRepository.findByAccountNumber(Mockito.any(String.class))).willReturn(Optional.of(suspendedAccount));
        Assertions.assertThrows(RuntimeException.class, () -> transactionService.deposit(transferRequest));
    }

    @Test
    public void failedWithdrawBecauseSendAccountIdAndUSerIdDontMatch() {
        BDDMockito.given(accountRepository.findByAccountNumber(Mockito.any(String.class))).willReturn(Optional.of(activeAccount));

        // change id of the user object in the account so the tests fail
        activeAccount.getUser().setId(UUID.fromString("610d0a5a-55c7-4047-b65c-cb8403b4861d"));

        Assertions.assertThrows(Exception.class, () -> transactionService.withdraw(transferRequest));
    }

    @Test
    public void failedWithdrawalTransactionBecauseAccountNotActive() {
        BDDMockito.given(accountRepository.findByAccountNumber(Mockito.any(String.class))).willReturn(Optional.of(suspendedAccount));
        Assertions.assertThrows(RuntimeException.class, () -> transactionService.withdraw(transferRequest));
    }

    @Test
    public void failedWithdrawalTransactionBecauseOfInsufficientFunds() {
        BDDMockito.given(accountRepository.findByAccountNumber(Mockito.any(String.class))).willReturn(Optional.of(activeAccount));
        Assertions.assertThrows(RuntimeException.class, () -> transactionService.withdraw(transferRequest));
    }

    @Test
    public void successfulWithdrawalTransaction() {
        BDDMockito.given(accountRepository.findByAccountNumber(Mockito.any(String.class))).willReturn(Optional.of(activeAccount));
        activeAccount.setAvailableBalance(BigDecimal.valueOf(5000));
        BDDMockito.given(accountRepository.save(Mockito.any(Account.class))).willReturn(activeAccount);
        BDDMockito.given(transactionRepository.save(Mockito.any(Transaction.class))).willReturn(debitTransaction);

        Transaction transaction = transactionService.withdraw(transferRequest);

        org.assertj.core.api.Assertions.assertThat(transaction).isNotNull();
        org.assertj.core.api.Assertions.assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        org.assertj.core.api.Assertions.assertThat(transaction.getType()).isEqualTo(TransactionType.DEBIT);
    }

    @Test
    public void successfulTransferBetweenAccounts() {
        BDDMockito.given(accountRepository.findByAccountNumber(transferRequest.getFrom())).willReturn(Optional.of(activeAccount));
        BDDMockito.given(accountRepository.findByAccountNumber(transferRequest.getTo())).willReturn(Optional.of(secondActiveAccount));

        activeAccount.setAvailableBalance(BigDecimal.valueOf(5000));
        creditTransaction.setAccount(secondActiveAccount);

        // Set strictness to lenient for stubbing save method of transactionRepository
        Mockito.lenient().when(transactionRepository.save(debitTransaction)).thenReturn(debitTransaction);
        Mockito.lenient().when(transactionRepository.save(creditTransaction)).thenReturn(creditTransaction);

        // Stubbing the save method of accountRepository
        BDDMockito.given(accountRepository.save(activeAccount)).willReturn(activeAccount);
        BDDMockito.given(accountRepository.save(secondActiveAccount)).willReturn(secondActiveAccount);

        Transaction transaction = transactionService.transfer(transferRequest);
        org.assertj.core.api.Assertions.assertThat(transaction).isNotNull();
    }

    @Test
    public void failedTransferBetweenAccountsBecauseAccountsAreNotSameCurrency() {
        Currency dollar = Currency.builder()
                .symbol(CurrencySymbol.USD)
                .build();

        Account dollarAccount = Account.builder()
                .user(user)
                .availableBalance(BigDecimal.ZERO)
                .accountNumber("2093255991")
                .type(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .currency(dollar)
                .build();

        BDDMockito.given(accountRepository.findByAccountNumber(transferRequest.getFrom())).willReturn(Optional.of(activeAccount));
        BDDMockito.given(accountRepository.findByAccountNumber(transferRequest.getTo())).willReturn(Optional.of(dollarAccount));

        Assertions.assertThrows(Exception.class, () -> transactionService.transfer(transferRequest));
    }

    @Test
    public void failedTransactionBecauseAccountSentMoneyToItself() {
        BDDMockito.given(accountRepository.findByAccountNumber(transferRequest.getFrom())).willReturn(Optional.of(activeAccount));
        BDDMockito.given(accountRepository.findByAccountNumber(transferRequest.getFrom())).willReturn(Optional.of(activeAccount));

        activeAccount.setAvailableBalance(BigDecimal.valueOf(5000));

        Assertions.assertThrows(RuntimeException.class, () -> transactionService.transfer(transferRequest));
    }

    @Test
    public void getAllTransactions() {
        Pageable pageable = PageRequest.of(0, 15);
        Page<Transaction> transactions = new PageImpl<>(List.of(creditTransaction, debitTransaction), pageable, 0);

        BDDMockito.given(accountRepository.findByAccountNumber(Mockito.any(String.class))).willReturn(Optional.of(activeAccount));
        BDDMockito.given(transactionRepository.findAllByAccount(Mockito.any(Account.class), Mockito.eq(pageable))).willReturn(transactions);

        Page<Transaction> transactionPage = transactionService.getAllTransactions(transferRequest.getFrom(), pageable);

        org.assertj.core.api.Assertions.assertThat(transactionPage.getContent()).isNotNull();
    }

    @Test
    public void getAllTransactionsByTransactionTypeCredit() {
        Pageable pageable = PageRequest.of(0, 15);
        Page<Transaction> transactions = new PageImpl<>(List.of(creditTransaction), pageable, 0);
        BDDMockito.given(accountRepository.findByAccountNumber(Mockito.any(String.class))).willReturn(Optional.of(activeAccount));
        BDDMockito.given(transactionRepository.findAllByAccountAndType(Mockito.any(Account.class), Mockito.eq(TransactionType.CREDIT), Mockito.eq(pageable))).willReturn(transactions);

        Page<Transaction> transactionPage = transactionService.getAllTransactionsByTransactionType(transferRequest.getFrom(), TransactionType.CREDIT, pageable);
        org.assertj.core.api.Assertions.assertThat(transactionPage.getContent()).isNotNull();
    }

    @Test
    public void getAllTransactionsByTransactionTypeDebit() {
        Pageable pageable = PageRequest.of(0, 15);
        Page<Transaction> transactions = new PageImpl<>(List.of(debitTransaction), pageable, 0);
        BDDMockito.given(accountRepository.findByAccountNumber(Mockito.any(String.class))).willReturn(Optional.of(activeAccount));
        BDDMockito.given(transactionRepository.findAllByAccountAndType(Mockito.any(Account.class), Mockito.eq(TransactionType.DEBIT), Mockito.eq(pageable))).willReturn(transactions);

        Page<Transaction> transactionPage = transactionService.getAllTransactionsByTransactionType(transferRequest.getFrom(), TransactionType.DEBIT, pageable);
        org.assertj.core.api.Assertions.assertThat(transactionPage.getContent()).isNotNull();
    }
}