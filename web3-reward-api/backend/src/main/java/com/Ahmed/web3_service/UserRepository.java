package com.Ahmed.web3_service;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByWalletAddress(String walletAddress);
}