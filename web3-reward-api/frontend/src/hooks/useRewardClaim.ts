import { useState } from 'react';
import { isAddress } from 'ethers/lib/utils';
import { AxiosError } from 'axios';
import * as api from '../api';

declare global {
  interface Window {
    ethereum?: any;
  }
}

export type Status = 'idle' | 'submitting' | 'success' | 'error';

export interface RewardResult {
  userId: number;
  txHash: string;
}

export const useRewardClaim = () => {
  const [status, setStatus] = useState<Status>('idle');
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<RewardResult | null>(null);

  const isSubmitting = status === 'submitting';

  const addTokenToMetaMask = async () => {
    const tokenAddress = import.meta.env.VITE_CONTRACT_ADDRESS;
    const tokenSymbol = 'RWT'; // Your token's symbol
    const tokenDecimals = 18;  // Your token's decimals

    if (!tokenAddress) {
      const message = "Contract address is not configured. Cannot add token.";
      console.error(message);
      alert(message);
      return;
    }

    if (typeof window.ethereum === 'undefined') {
      alert('MetaMask is not installed. Please install it to add the token.');
      return;
    }

    try {
      // Check if the site is already connected.
      let accounts = await window.ethereum.request({ method: 'eth_accounts' });

      // If not connected, request connection.
      if (!accounts || accounts.length === 0) {
        accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
      }

      // If still no accounts, user has denied the connection.
      if (!accounts || accounts.length === 0) {
        console.log('User denied account access.');
        return;
      }

      // 'wallet_watchAsset' is the method that prompts the user to add a token
      const wasAdded = await window.ethereum.request({
        method: 'wallet_watchAsset',
        params: {
          type: 'ERC20',
          options: {
            address: tokenAddress,
            symbol: tokenSymbol,
            decimals: tokenDecimals,
          },
        },
      });

      console.log(wasAdded ? 'RewardToken has been added to MetaMask.' : 'User declined to add the token.');
    } catch (error) {
      console.error('Error adding token to MetaMask:', error);
    }
  };

  const claimReward = async ({ email, walletAddress }: { email: string; walletAddress: string }) => {
    setStatus('submitting');
    setError(null);
    setResult(null);

    // 1. Client-side validation
    if (!email || !/^\S+@\S+\.\S+$/.test(email)) {
      setError('Please enter a valid email address.');
      setStatus('error');
      return;
    }
    if (!isAddress(walletAddress)) {
      setError('The provided wallet address is invalid.');
      setStatus('error');
      return;
    }

    try {
      // 2. Register User (or get existing)
      const user = await api.registerUser(email, walletAddress);

      if (!user.id) {
        throw new Error('Could not retrieve user ID after registration attempt.');
      }

      // 3. Issue Reward
      const reward = await api.issueReward(user.id);

      setResult({ userId: user.id, txHash: reward.transactionHash });
      setStatus('success');
    } catch (err) {
      const axiosError = err as AxiosError<{ message?: string }>;
      const errorMessage = axiosError.response?.data?.message || axiosError.message || 'An unknown error occurred.';
      setError(`Operation failed: ${errorMessage}`);
      setStatus('error');
    }
  };

  return {
    status,
    error,
    result,
    isSubmitting,
    claimReward,
    addTokenToMetaMask,
  };
};