package com.woodcore.backend.bankingservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @Size(min = 10, max = 10, message = "sender account should be correct")
    private String from;

    @Size(min = 10, max = 10, message = "receiver account should be correct")
    private String to;

    @Positive(message = "you cannot send zero currency")
    private BigDecimal amount;

    @Size(max = 200, message = "word limit is 200")
    @NotEmpty(message = "description cannot be empty")
    private String description;
}
