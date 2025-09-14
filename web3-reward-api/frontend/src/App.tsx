import { RewardForm } from './components/RewardForm';

function App() {
  return (
    <div className="container">
      <div className="card">
        <h1>Claim Your Reward</h1>
        <p>Register your email and wallet to receive a token reward.</p>
        <RewardForm />
      </div>
    </div>
  );
}

export default App;