import { useState, FormEvent } from 'react';
import { useRewardClaim, Status, RewardResult } from '../hooks/useRewardClaim';

const SEPOLIA_EXPLORER_URL = 'https://sepolia.etherscan.io/tx/';

export const RewardForm = () => {
  const [email, setEmail] = useState('');
  const [walletAddress, setWalletAddress] = useState('');
  const { claimReward, isSubmitting, status, error, result } = useRewardClaim();

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    claimReward({ email, walletAddress });
  };

  return (
    <>
      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <label htmlFor="email">Email Address</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="you@example.com"
            disabled={isSubmitting}
            required
          />
        </div>
        <div className="input-group">
          <label htmlFor="wallet">Ethereum Wallet Address</label>
          <input
            id="wallet"
            type="text"
            value={walletAddress}
            onChange={(e) => setWalletAddress(e.target.value)}
            placeholder="0x..."
            disabled={isSubmitting}
            required
          />
        </div>
        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Processing...' : 'Register & Claim Reward'}
        </button>
      </form>

      <StatusDisplay status={status} error={error} result={result} />
    </>
  );
};

interface StatusDisplayProps {
  status: Status;
  error: string | null;
  result: RewardResult | null;
}

const StatusDisplay = ({ status, error, result }: StatusDisplayProps) => {
  if (status === 'submitting') {
    return <div className="spinner"></div>;
  }

  if (status === 'success' && result) {
    return (
      <div className="message success">
        <strong>Success!</strong>
        <p>User registered (ID: {result.userId}). Reward issued.</p>
        <p>
          Tx:{' '}
          <a href={`${SEPOLIA_EXPLORER_URL}${result.txHash}`} target="_blank" rel="noopener noreferrer">
            {`${result.txHash.substring(0, 10)}...${result.txHash.substring(result.txHash.length - 8)}`}
          </a>
        </p>
      </div>
    );
  }

  if (status === 'error' && error) {
    return (
      <div className="message error">
        <strong>Error</strong>
        <p>{error}</p>
      </div>
    );
  }

  return null;
};