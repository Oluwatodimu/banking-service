package com.woodcore.backend.bankingservice.dto.request;

import com.woodcore.backend.bankingservice.model.enums.AccountStatus;
import com.woodcore.backend.bankingservice.model.enums.AccountType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateAccountDetailsRequest {

    @NotEmpty(message = "email cannot be empty")
    private String accountNumber;

    private AccountType type;
    private AccountStatus status;
}
