import { useState } from 'react';
import { isAddress } from 'ethers/lib/utils';
import { AxiosError } from 'axios';
import * as api from '../api';

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
  };
};