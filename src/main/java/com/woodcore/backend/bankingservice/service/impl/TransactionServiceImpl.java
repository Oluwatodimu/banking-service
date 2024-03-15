package com.woodcore.backend.bankingservice.service.impl;

import com.woodcore.backend.bankingservice.dto.request.TransferRequest;
import com.woodcore.backend.bankingservice.exception.AccountNotActiveException;
import com.woodcore.backend.bankingservice.model.Account;
import com.woodcore.backend.bankingservice.model.Transaction;
import com.woodcore.backend.bankingservice.model.enums.AccountStatus;
import com.woodcore.backend.bankingservice.model.enums.TransactionPurpose;
import com.woodcore.backend.bankingservice.model.enums.TransactionStatus;
import com.woodcore.backend.bankingservice.model.enums.TransactionType;
import com.woodcore.backend.bankingservice.repository.AccountRepository;
import com.woodcore.backend.bankingservice.repository.TransactionRepository;
import com.woodcore.backend.bankingservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Transaction deposit(TransferRequest transferRequest) {

        try {

            Account receiverAccount = getReceiverAccount(transferRequest.getTo());

            if (!receiverAccount.getStatus().equals(AccountStatus.ACTIVE)) {
                throw new AccountNotActiveException("account not active");
            }

            BigDecimal newAvailableBalance = receiverAccount.getAvailableBalance().add(transferRequest.getAmount());
            receiverAccount.setAvailableBalance(newAvailableBalance);
            accountRepository.save(receiverAccount);
            Transaction transaction = createTransactionForDeposits(transferRequest, receiverAccount, TransactionPurpose.DEPOSIT_EXTERNAL);
            return transactionRepository.save(transaction);

        } catch (RuntimeException exception) {
            System.err.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    @Transactional
    public Transaction withdraw(TransferRequest transferRequest) {

        try {

            Account senderAccount = getReceiverAccount(transferRequest.getFrom());

            if (!senderAccount.getUser().getId().toString().equals(getLoggedInUser())) {
                throw new BadCredentialsException("action not allowed");
            }

            if (transferRequest.getAmount().compareTo(senderAccount.getAvailableBalance()) > 0) {
                throw new RuntimeException("insufficient balance");
            }

            if (!senderAccount.getStatus().equals(AccountStatus.ACTIVE)) {
                throw new AccountNotActiveException("account not active");
            }

            BigDecimal newAvailableBalance = senderAccount.getAvailableBalance().subtract(transferRequest.getAmount());
            senderAccount.setAvailableBalance(newAvailableBalance);
            accountRepository.save(senderAccount);
            Transaction transaction = createTransactionForWithdrawals(transferRequest, senderAccount, TransactionPurpose.WITHDRAWAL_EXTERNAL);
            return transactionRepository.save(transaction);

        } catch (RuntimeException exception) {
            System.err.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    @Transactional
    public Transaction transfer(TransferRequest transferRequest) {

        // get receiver account
        Account senderAccount = accountRepository.findByAccountNumber(transferRequest.getFrom()).orElseThrow();

        // get sender account
        Account receiverAccount = accountRepository.findByAccountNumber(transferRequest.getTo()).orElseThrow();

        // accounts have to be active
        if (!senderAccount.getStatus().equals(AccountStatus.ACTIVE) || !receiverAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new AccountNotActiveException("account not active");
        }

        // ensure logged in user is performing the action
        if (!receiverAccount.getUser().getId().toString().equals(getLoggedInUser())) {
            throw new BadCredentialsException("action not allowed");
        }

        // ensure balance is sufficient
        if (transferRequest.getAmount().compareTo(senderAccount.getAvailableBalance()) > 0) {
            throw new RuntimeException("insufficient balance");
        }

        // ensure accounts are not the same
        if (senderAccount.getAccountNumber().equals(receiverAccount.getAccountNumber())) {
            throw new BadCredentialsException("you cannot send funds to same account");
        }

        // ensure you are sending to an account with the same currency
        if (!senderAccount.getCurrency().equals(receiverAccount.getCurrency())) {
            throw new BadCredentialsException("money cannot be transferred to an account in a different currency");
        }

        // step 1
        // deduct the amount from the sender and create a transaction
        BigDecimal newAvailableBalanceForSender = senderAccount.getAvailableBalance().subtract(transferRequest.getAmount());
        senderAccount.setAvailableBalance(newAvailableBalanceForSender);
        Transaction senderTransaction = createTransactionForDeposits(transferRequest, senderAccount, TransactionPurpose.WITHDRAWAL_INTERNAL);

        // save the transaction and the new wallet balance
        accountRepository.save(senderAccount);
        transactionRepository.save(senderTransaction);

        // step 2
        // add the amount to the sender and create a transaction
        BigDecimal newAvailableBalanceForReceiver = receiverAccount.getAvailableBalance().add(transferRequest.getAmount());
        receiverAccount.setAvailableBalance(newAvailableBalanceForReceiver);
        Transaction receiverTransaction = createTransactionForWithdrawals(transferRequest, receiverAccount, TransactionPurpose.DEPOSIT_INTERNAL);

        // save the transaction and the new wallet balance
        accountRepository.save(receiverAccount);
        transactionRepository.save(receiverTransaction);

        return senderTransaction;
    }

    @Override
    public Page<Transaction> getAllTransactions(String accountNumber, Pageable pageable) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
        return transactionRepository.findAllByAccount(account, pageable);
    }

    @Override
    public Page<Transaction> getAllTransactionsByTransactionType(String accountNumber, TransactionType type, Pageable pageable) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
        return transactionRepository.findAllByAccountAndType(account, type, pageable);
    }

    private Account getReceiverAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow();

        if (!account.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new AccountNotActiveException("account not active");
        }

        return account;
    }

    private Transaction createTransactionForDeposits(TransferRequest request, Account account, TransactionPurpose purpose) {
        return Transaction.builder()
                .amount(request.getAmount())
                .type(TransactionType.CREDIT)
                .purpose(purpose)
                .reference(UUID.randomUUID())
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .senderAccount(request.getFrom())
                .receiverAccount(request.getTo())
                .account(account)
                .build();
    }

    private Transaction createTransactionForWithdrawals(TransferRequest request, Account account, TransactionPurpose purpose) {
        return Transaction.builder()
                .amount(request.getAmount())
                .type(TransactionType.DEBIT)
                .purpose(purpose)
                .reference(UUID.randomUUID())
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .senderAccount(request.getFrom())
                .receiverAccount(request.getTo())
                .account(account)
                .build();
    }

    private String getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return  authentication.getName();
    }
}
