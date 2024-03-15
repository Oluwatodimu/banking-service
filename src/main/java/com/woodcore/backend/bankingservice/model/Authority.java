package com.woodcore.backend.bankingservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "authority")
public class Authority extends BaseEntity {

    @Column(name = "authority_name")
    private String authorityName;
}
