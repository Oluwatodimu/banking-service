package com.woodcore.backend.bankingservice.repository;

import com.woodcore.backend.bankingservice.model.Currency;
import com.woodcore.backend.bankingservice.model.enums.CurrencySymbol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CurrencyRepository extends JpaRepository<Currency, UUID> {

    Optional<Currency> findBySymbol(CurrencySymbol symbol);
}
