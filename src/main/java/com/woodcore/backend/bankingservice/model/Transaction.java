package com.woodcore.backend.bankingservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.woodcore.backend.bankingservice.model.enums.TransactionPurpose;
import com.woodcore.backend.bankingservice.model.enums.TransactionStatus;
import com.woodcore.backend.bankingservice.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@ToString(exclude = {"account"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_transaction")
public class Transaction extends BaseEntity {

    private BigDecimal amount;

    @NonNull
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @NonNull
    @Enumerated(EnumType.STRING)
    private TransactionPurpose purpose;

    private UUID reference;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String description;

    @Column(name = "sender_account")
    private String senderAccount;

    @Column(name = "receiver_account")
    private String receiverAccount;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    private Account account;

    @Override
    public int hashCode() {
        return Objects.hash(
                amount,
                purpose,
                reference,
                status
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction transaction = (Transaction) o;
        return Objects.equals(amount, transaction.amount) &&
                Objects.equals(purpose, transaction.purpose) &&
                Objects.equals(reference, transaction.reference) &&
                Objects.equals(status, transaction.status);
    }
}
