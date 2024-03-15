package com.woodcore.backend.bankingservice.dto.request;

import com.woodcore.backend.bankingservice.model.enums.AccountType;
import com.woodcore.backend.bankingservice.model.enums.CurrencySymbol;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAccountRequest {

    @NotNull(message = "currency cannot be null")
    private CurrencySymbol currency;

    @NotNull(message = "accountType cannot be null")
    private AccountType accountType;
}
