package com.Ahmed.web3_service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private final TokenRewardService tokenRewardService;
    private final UserRepository userRepository;

    public RewardController(TokenRewardService tokenRewardService, UserRepository userRepository) {
        this.tokenRewardService = tokenRewardService;
        this.userRepository = userRepository;
    }

    @PostMapping("/issue/{userId}")
    public ResponseEntity<?> issueReward(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        // A simple check to see if the user has completed an action
        // In a real app, this logic would be more complex.
        if (!"action-completed".equals(payload.get("action"))) {
            return ResponseEntity.badRequest().body("Action not completed.");
        }

        return userRepository.findById(userId).map(user -> {
            try {
                // The amount of tokens to award for the action
                // For a token with 18 decimals, 1 token is 10^18.
                BigInteger rewardAmount = new BigInteger("1000000000000000000"); // 1 token

                String txHash = tokenRewardService.issueTokens(user.getWalletAddress(), rewardAmount);
                return ResponseEntity.ok(Map.of("message", "Reward issued successfully!", "transactionHash", txHash));
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Error issuing tokens: " + e.getMessage());
            }
        }).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody User newUser) {
        if (newUser.getWalletAddress() == null || newUser.getWalletAddress().isEmpty()) {
            return ResponseEntity.badRequest().body("Wallet address is required.");
        }
        User savedUser = userRepository.save(newUser);
        return ResponseEntity.ok(savedUser);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/user/wallet/{walletAddress}")
    public ResponseEntity<?> getUserByWalletAddress(@PathVariable String walletAddress) {
        return userRepository.findByWalletAddress(walletAddress)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        return userRepository.findById(userId).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setWalletAddress(updatedUser.getWalletAddress());
            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        }).orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
    
}         