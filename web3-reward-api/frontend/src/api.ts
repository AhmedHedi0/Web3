import axios, { AxiosError } from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export interface User {
  id: number;
  email: string;
  walletAddress: string;
}

export interface RewardIssuance {
  message: string;
  transactionHash: string;
}

/**
 * Registers a new user or retrieves the ID of an existing user.
 */
export const registerUser = async (email: string, walletAddress: string): Promise<User> => {
  try {
    // The backend expects a username for new users. We can derive a simple one
    // from the email address provided.
    const username = email.split('@')[0];
    const response = await axios.post<User>(`${API_BASE_URL}/api/users/register`, {
      username,
      email,
      walletAddress,
    });
    return response.data;
  } catch (err) {
    const axiosError = err as AxiosError<{ message?: string; id?: number }>;
    // Handle conflict by returning the existing user's data
    if (axiosError.response?.status === 409 && axiosError.response.data) {
      console.log('User already exists, proceeding with existing ID.');
      return axiosError.response.data as User;
    }
    // Re-throw other errors to be handled by the caller
    throw err;
  }
};

/**
 * Issues a reward to a user.
 */
export const issueReward = async (userId: number): Promise<RewardIssuance> => {
  const response = await axios.post<RewardIssuance>(`${API_BASE_URL}/api/rewards/issue/${userId}`, {
    action: 'action-completed',
  });
  return response.data;
};