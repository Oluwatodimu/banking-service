package com.woodcore.backend.bankingservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.woodcore.backend.bankingservice.model.enums.CurrencySymbol;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@ToString(exclude = {"accounts"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "currency")
public class Currency extends BaseEntity {

    @JsonIgnore
    private String name;

    @Enumerated(EnumType.STRING)
    private CurrencySymbol symbol;

    @JsonIgnore
    private boolean enabled;

    @JsonIgnore
    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL)
    private Set<Account> accounts = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                symbol,
                enabled
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(name, currency.name) &&
                Objects.equals(symbol, currency.symbol) &&
                Objects.equals(enabled, currency.enabled);
    }
}
