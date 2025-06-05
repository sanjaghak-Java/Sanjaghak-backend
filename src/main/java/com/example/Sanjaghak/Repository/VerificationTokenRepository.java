package com.example.Sanjaghak.Repository;


import com.example.Sanjaghak.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByEmailAndPhoneNumberAndCode(String email, String phoneNumber, String code);

    Optional<VerificationToken> findByEmailAndCode(String email, String code);

    Optional<VerificationToken> findByPhoneNumberAndCode(String phoneNumber, String code);

    Optional<VerificationToken> findTopByEmailAndPhoneNumberOrderByExpiresAtDesc(String email, String phoneNumber);

}
