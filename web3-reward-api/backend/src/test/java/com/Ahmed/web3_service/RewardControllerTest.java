package com.Ahmed.web3_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenRewardService tokenRewardService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Map<String, String> rewardPayload;

    private static final String TEST_WALLET_ADDRESS = "0x1234567890123456789012345678901234567890";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setWalletAddress(TEST_WALLET_ADDRESS);

        rewardPayload = Map.of("action", "action-completed");
    }

    @Test
    @DisplayName("Should issue reward successfully when user exists and action is completed")
    void issueReward_Success() throws Exception {
        String txHash = "0xabc123";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tokenRewardService.issueTokens(eq(user.getWalletAddress()), any())).thenReturn(txHash);

        mockMvc.perform(post("/api/rewards/issue/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rewardPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Reward issued successfully!")))
                .andExpect(jsonPath("$.transactionHash", is(txHash)));
    }

    @Test
    @DisplayName("Should return bad request when action is not completed")
    void issueReward_ActionNotCompleted() throws Exception {
        Map<String, String> payload = Map.of("action", "action-not-completed");

        mockMvc.perform(post("/api/rewards/issue/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Action not completed."));
    }

    @Test
    @DisplayName("Should return not found when user does not exist for reward issuance")
    void issueReward_UserNotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/rewards/issue/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rewardPayload)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return internal server error when token issuance fails")
    void issueReward_TokenIssuanceFails() throws Exception {
        String errorMessage = "Blockchain is down";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tokenRewardService.issueTokens(eq(user.getWalletAddress()), any())).thenThrow(new Exception(errorMessage));

        mockMvc.perform(post("/api/rewards/issue/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rewardPayload)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error issuing tokens: " + errorMessage));
    }

    @Test
    @DisplayName("Should register a new user successfully")
    void registerUser_Success() throws Exception {
        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/rewards/register-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.walletAddress", is(user.getWalletAddress())));
    }

    @Test
    @DisplayName("Should return bad request when registering user without a wallet address")
    void registerUser_MissingWalletAddress() throws Exception {
        User userWithoutWallet = new User();
        userWithoutWallet.setUsername("test");

        mockMvc.perform(post("/api/rewards/register-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithoutWallet)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Wallet address is required."));
    }

    @Test
    @DisplayName("Should get user by ID when user exists")
    void getUser_Found() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/rewards/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())));
    }

    @Test
    @DisplayName("Should return not found when getting user by ID that does not exist")
    void getUser_NotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rewards/user/{userId}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get user by wallet address when user exists")
    void getUserByWalletAddress_Found() throws Exception {
        when(userRepository.findByWalletAddress(user.getWalletAddress())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/rewards/user/wallet/{walletAddress}", user.getWalletAddress()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletAddress", is(user.getWalletAddress())));
    }

    @Test
    @DisplayName("Should return not found when getting user by wallet address that does not exist")
    void getUserByWalletAddress_NotFound() throws Exception {
        when(userRepository.findByWalletAddress(user.getWalletAddress())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rewards/user/wallet/{walletAddress}", user.getWalletAddress()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update user successfully when user exists")
    void updateUser_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setUsername("updatedUser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setWalletAddress("0x0987654321098765432109876543210987654321");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/rewards/user/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("updatedUser")));
    }

    @Test
    @DisplayName("Should return not found when updating a user that does not exist")
    void updateUser_NotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/rewards/user/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new User())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete user successfully when user exists")
    void deleteUser_Success() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        mockMvc.perform(delete("/api/rewards/user/{userId}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return not found when deleting a user that does not exist")
    void deleteUser_NotFound() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/rewards/user/{userId}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return a list of all users")
    void getAllUsers() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/rewards/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())));
    }
}
