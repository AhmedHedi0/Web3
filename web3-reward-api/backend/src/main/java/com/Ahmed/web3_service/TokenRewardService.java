package com.Ahmed.web3_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import com.Ahmed.web3_service.contracts.RewardToken;

import java.math.BigInteger;

@Service
public class TokenRewardService {
    private final Web3j web3j;
    private final Credentials credentials;
    private final RewardToken tokenContract;

    public TokenRewardService(
            @Value("${infura.url}") String infuraUrl,
            @Value("${issuer.private.key}") String privateKey,
            @Value("${contract.address}") String contractAddress
    ) {
        this.web3j = Web3j.build(new HttpService(infuraUrl));
        this.credentials = Credentials.create(privateKey);
        this.tokenContract = RewardToken.load(contractAddress, web3j, credentials, new DefaultGasProvider());
    }

    public String issueTokens(String toAddress, BigInteger amount) throws Exception {
        var receipt = tokenContract.reward(toAddress, amount).send();
        return receipt.getTransactionHash();
    }
}