package com.woodcore.backend.bankingservice.controller;

import com.woodcore.backend.bankingservice.dto.BaseResponse;
import com.woodcore.backend.bankingservice.dto.request.TransferRequest;
import com.woodcore.backend.bankingservice.model.Transaction;
import com.woodcore.backend.bankingservice.model.enums.TransactionType;
import com.woodcore.backend.bankingservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/transactions", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TransactionController extends BaseController {

    private final TransactionService transactionService;

    @PostMapping(path = "/deposit")
    public ResponseEntity<BaseResponse> deposit(@RequestBody @Valid TransferRequest transferRequest) {
        log.info("external deposit from account: {} to account: {}", transferRequest.getFrom(), transferRequest.getTo());
        Transaction transaction = transactionService.deposit(transferRequest);
        return new ResponseEntity<>(createBseResponse(transaction), HttpStatus.OK);
    }

    @PostMapping(path = "/withdraw")
    public ResponseEntity<BaseResponse> withdraw(@RequestBody @Valid TransferRequest transferRequest) {
        log.info("external withdrawal from account: {} to account: {}", transferRequest.getFrom(), transferRequest.getTo());
        Transaction transaction = transactionService.withdraw(transferRequest);
        return new ResponseEntity<>(createBseResponse(transaction), HttpStatus.OK);
    }

    @PostMapping(path = "/transfer")
    public ResponseEntity<BaseResponse> transfer(@RequestBody @Valid TransferRequest transferRequest) {
        log.info("internal transfer from account: {} to account: {}", transferRequest.getFrom(), transferRequest.getTo());
        Transaction transaction = transactionService.transfer(transferRequest);
        return new ResponseEntity<>(createBseResponse(transaction), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getAllTransactions(
            @RequestParam @Length(min = 10, max = 10) String accountNumber,
                @RequestParam(required = false) Integer pageNumber,
                    @RequestParam(required = false) Integer pageSize) {

        log.info("get all transactions for account: {}", accountNumber);
        Pageable pageable = createSortedPageableObject(pageNumber, pageSize);
        Page<Transaction> transactions = transactionService.getAllTransactions(accountNumber, pageable);
        return new ResponseEntity<>(createBseResponse(transactions), HttpStatus.OK);
    }

    @GetMapping(path = "/type")
    public ResponseEntity<BaseResponse> getAllTransactionsByType(
            @RequestParam @Length(min = 10, max = 10) String accountNumber,
                @RequestParam @NonNull TransactionType type,
                    @RequestParam(required = false) Integer pageNumber,
                        @RequestParam(required = false) Integer pageSize) {

        log.info("get all {} transactions for account: {}", type, accountNumber);
        Pageable pageable = createSortedPageableObject(pageNumber, pageSize);
        Page<Transaction> transactions = transactionService.getAllTransactionsByTransactionType(
                accountNumber,
                type,
                pageable
        );
        return new ResponseEntity<>(createBseResponse(transactions), HttpStatus.OK);
    }
}
