package com.Ahmed.web3_service;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

@Data // Lombok annotation for getters, setters, etc.
@Entity(name = "users") // Maps this class to a table named "users"
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String walletAddress; // The user's public Web3 wallet address
}