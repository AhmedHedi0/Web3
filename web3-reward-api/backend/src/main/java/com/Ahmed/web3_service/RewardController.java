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
    
}         