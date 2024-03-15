package com.woodcore.backend.bankingservice.repository;

import com.woodcore.backend.bankingservice.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorityRepository extends JpaRepository<Authority, UUID> {

    Optional<Authority> findByAuthorityName(String authorityName);
}
