package com.woodcore.backend.bankingservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.woodcore.backend.bankingservice.model.enums.AccountStatus;
import com.woodcore.backend.bankingservice.model.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@ToString(exclude = {"currency", "user"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account extends BaseEntity {

    @Column(name = "available_balance")
    private BigDecimal availableBalance;

    @Column(name = "reserved_balance")
    private BigDecimal reservedBalance;

    @JsonIgnore
    private boolean locked = false;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "account_number")
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Transaction> transactions = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(
                availableBalance,
                reservedBalance,
                locked,
                user,
                currency
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(availableBalance, account.availableBalance) &&
                Objects.equals(reservedBalance, account.reservedBalance) &&
                Objects.equals(locked, account.locked) &&
                Objects.equals(user, account.user) &&
                Objects.equals(currency, account.currency);

    }
}
