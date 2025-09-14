package com.Ahmed.web3_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User newUser) {
        if (newUser.getWalletAddress() == null || newUser.getWalletAddress().isEmpty() || newUser.getEmail() == null || newUser.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and wallet address are required."));
        }

        // Check if user already exists by wallet address
        return userRepository.findByWalletAddress(newUser.getWalletAddress())
                .map(existingUser -> {
                    // User exists, return 409 Conflict with existing user data
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(existingUser);
                })
                .orElseGet(() -> {
                    // User does not exist, save new user and return 200 OK
                    User savedUser = userRepository.save(newUser);
                    return ResponseEntity.ok(savedUser);
                });
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        return userRepository.findById(userId).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setWalletAddress(updatedUser.getWalletAddress());
            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        }).orElse(ResponseEntity.notFound().build());
    }
}