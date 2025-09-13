 [![Full Stack CI](https://github.com/AhmedHedi0/Web3/actions/workflows/java-ci.yml/badge.svg?branch=main)](https://github.com/AhmedHedi0/Web3/actions/workflows/java-ci.yml)
 
 # Web3 Reward API
 
 This project is a full-stack decentralized application demonstrating how a Java Spring Boot backend can interact with an Ethereum smart contract. The application provides a REST API to manage users and issue ERC-20 token rewards for specific actions.
 
 ## Table of Contents
 
 - [Features](#features)
 - [Project Structure](#project-structure)
 - [Technologies Used](#technologies-used)
 - [Prerequisites](#prerequisites)
 - [Getting Started](#getting-started)
   - [1. Smart Contract Setup](#1-smart-contract-setup)
   - [2. Backend Setup](#2-backend-setup)
 - [API Endpoints](#api-endpoints)
 - [Running Tests](#running-tests)
 - [CI/CD](#cicd)
 
 ## Features
 
 - **User Management**: CRUD operations for users, including wallet address registration.
 - **Token Rewards**: An endpoint to issue a fixed amount of ERC-20 tokens to a user's wallet address.
 - **Smart Contract Interaction**: Uses Web3j to load and interact with a deployed `RewardToken` smart contract.
 - **Automated Contract Wrappers**: Leverages the `web3j-maven-plugin` to automatically generate Java wrappers from the smart contract ABI.
 
 ## Project Structure
 
 ```
 .
 ├── backend/         # Spring Boot application
 │   ├── pom.xml
 │   └── src/
 ├── smart-contract/  # Hardhat project for the Solidity smart contract
 │   ├── contracts/
 │   ├── scripts/
 │   └── hardhat.config.js
 └── .github/         # GitHub Actions CI/CD workflow
 ```
 
 ## Technologies Used
 
 - **Backend**: Java 21, Spring Boot 3, Maven
 - **Blockchain**: Web3j, Solidity
 - **Smart Contract Development**: Hardhat, Ethers.js
 - **Database**: H2 (In-memory, for demonstration)
 - **Testing**: JUnit 5, Mockito
 
 ## Prerequisites
 
 - JDK 21 or later
 - Apache Maven
 - Node.js and npm
 - An Ethereum testnet account with some test ETH (e.g., on Sepolia).
 - An RPC URL from a node provider like Infura or Alchemy.
 
 ## Getting Started
 
 Follow these steps to set up and run the project locally.
 
 ### 1. Smart Contract Setup
 
 First, you need to compile and deploy the `RewardToken` smart contract.
 
 ```bash
 # Navigate to the smart contract directory
 cd smart-contract
 
 # Install dependencies
 npm install
 
 # Compile the smart contract
 npx hardhat compile
 ```
 
 Before deploying, create a `.env` file in the `smart-contract` directory and add your deployment configuration:
 
 **smart-contract/.env**
 ```
 PRIVATE_KEY=YOUR_ETHEREUM_ACCOUNT_PRIVATE_KEY
 INFURA_API_KEY=YOUR_INFURA_API_KEY 
 ETHSCAN_API_KEY=YOUR_ETHSCAN_API_KEY
 ```
 
 Now, deploy the contract to a test network (e.g., Sepolia):
 
 ```bash
 npx hardhat run scripts/deploy.js --network sepolia
 ```
 
 After a successful deployment, **copy the deployed contract address** printed in the console. You will need it for the backend configuration.
 
 ### 2. Backend Setup
 
 Next, configure and run the Spring Boot application.
 
 1.  **Copy the ABI:** After compiling the smart contract, copy the generated ABI file to the backend's resources directory. This is required for the Web3j plugin to generate the Java wrapper.
     -   **From:** `smart-contract/artifacts/contracts/RewardToken.sol/RewardToken.json`
     -   **To:** `backend/src/main/resources/solidity/RewardToken.json`
 
 2.  **Configure the Backend:** Create an `application.properties` file in `backend/src/main/resources/` and add your blockchain credentials.
 
     **backend/src/main/resources/application.properties**
     ```properties
     # Web3 Configuration
     infura.url=YOUR_INFURA_OR_ALCHEMY_RPC_URL
     wallet.private-key=YOUR_ETHEREUM_ACCOUNT_PRIVATE_KEY
     contract.address=THE_DEPLOYED_CONTRACT_ADDRESS_FROM_STEP_1
     
     # H2 Database Configuration
     spring.h2.console.enabled=true
     spring.datasource.url=jdbc:h2:mem:testdb
     spring.datasource.driverClassName=org.h2.Driver
     spring.datasource.username=sa
     spring.datasource.password=
     spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
     ```
 
 3.  **Build and Run:**
 
     ```bash
     # Navigate to the backend directory
     cd backend
     
     # Compile the project. This will also trigger the web3j-maven-plugin
     # to generate the RewardToken.java wrapper class.
     mvn compile
     
     # Run the application
     mvn spring-boot:run
     ```
 
 The application will be running at `http://localhost:8080`.
 
 ## API Endpoints
 
 | Method | Endpoint                               | Description                                  |
 | :----- | :------------------------------------- | :------------------------------------------- |
 | `POST` | `/api/users/register`                  | Registers a new user.                        |
 | `GET`    | `/api/users`                           | Retrieves a list of all users.               |
 | `GET`    | `/api/users/{id}`                      | Retrieves a user by their ID.                |
 | `GET`    | `/api/users/wallet/{walletAddress}`    | Retrieves a user by their wallet address.    |
 | `PUT`    | `/api/users/{id}`                      | Updates an existing user's information.      |
 | `DELETE` | `/api/users/{id}`                      | Deletes a user by their ID.                  |
 | `POST`   | `/api/rewards/issue/{userId}`          | Issues a token reward to the specified user. |
 
 ## Running Tests
 
 To run the backend unit and integration tests, navigate to the `backend` directory and run:
 
 ```bash
 mvn test
 ```
 
 ## CI/CD
 
 This project includes a GitHub Actions workflow defined in `.github/workflows/ci.yml`. The workflow is triggered on every push to the `main` branch and performs the following steps:
 1.  Sets up Java and Maven.
 2.  Builds the Spring Boot application.
 3.  Runs all tests to ensure code quality and correctness.

 For GitHub Actions, these same values need to be added as **Repository Secrets**:  
1. Go to your GitHub repo → `Settings` → `Secrets and variables` → `Actions`.  
2. Add each key (`INFURA_API_KEY`, `ETHSCAN_API_KEY`, `PRIVATE_KEY`, `ISSUER_PRIVATE_KEY`) with the appropriate value.  
3. The CI workflow will use these secrets automatically.