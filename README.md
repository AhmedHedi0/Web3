 [![Full Stack CI](https://github.com/AhmedHedi0/Web3/actions/workflows/java-ci.yml/badge.svg?branch=main)](https://github.com/AhmedHedi0/Web3/actions/workflows/java-ci.yml)

# Full-Stack Web3 User Registration App

This is a complete full-stack application that allows users to register using their email and wallet address. The system is built with a Java Spring Boot backend, a Vite-based frontend, and is deployed on a modern cloud infrastructure.

## Tech Stack & Architecture

This project uses a decoupled architecture with a REST API connecting the frontend and backend.

### Backend

  * **Framework:** Spring Boot 3
  * **Language:** Java 21
  * **Database:** PostgreSQL (managed by Supabase)
  * **Data Access:** Spring Data JPA (Hibernate)
  * **Web3:** Web3j for smart contract interaction
  * **Containerization:** Docker

### Frontend

  * **Framework:** Vite (likely React, Vue, or similar)
  * **Language:** JavaScript/TypeScript
  * **Styling:** CSS

### Deployment

  * **Backend:** Deployed as a Docker container on **Google Cloud Run**.
  * **Frontend:** Deployed on **Vercel**.
  * **Database:** Hosted on **Supabase**.
  * **Secrets Management:** **GCP Secret Manager**.

-----

## Getting Started

### Prerequisites

  * Java 21 (or higher)
  * Apache Maven
  * Node.js & npm
  * Docker
  * A PostgreSQL database (local or cloud-hosted like Supabase)

### Backend Setup (Local)

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/AhmedHedi0/Web3.git
    cd Web3/backend
    ```

2.  **Configure the database:**
    Open `src/main/resources/application.properties` and update the datasource properties to point to your local PostgreSQL database.

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/your-db-name
    spring.datasource.username=your-username
    spring.datasource.password=your-password
    spring.jpa.hibernate.ddl-auto=update
    ```

3.  **Build and run the application:**

    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

    The backend server will start on `http://localhost:8080`.

-----

### Frontend Setup (Local)

1.  **Navigate to the frontend directory:**

    ```bash
    # From the root directory
    cd frontend 
    ```

2.  **Install dependencies:**

    ```bash
    npm install
    ```

3.  **Configure the API endpoint:**
    Create a file named `.env.local` in the `frontend` directory and add the following line to point to your local backend server:

    ```
    VITE_API_BASE_URL=http://localhost:8080
    ```

4.  **Run the development server:**

    ```bash
    npm run dev
    ```

    The frontend will be available at `http://localhost:5173` (or another port specified by Vite).

-----

## API Endpoints

The backend exposes the following REST API endpoints under the base path `/api/users`.

| Method | Endpoint              | Description                                                                                                    |
| :----- | :-------------------- | :------------------------------------------------------------------------------------------------------------- |
| `POST` | `/register`           | Registers a new user or returns the existing user if the wallet address is already present (responds with 409 Conflict). |
| `GET`  | `/`                   | Retrieves a list of all registered users.                                                                      |
| `GET`  | `/{userId}`           | Retrieves a single user by their unique ID. Returns 404 Not Found if the user does not exist.                    |
| `PUT`  | `/{userId}`           | Updates the details of an existing user.                                                                       |
