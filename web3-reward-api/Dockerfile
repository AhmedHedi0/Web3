FROM eclipse-temurin:21-jdk AS build

# Set the working directory for the backend
WORKDIR /app/backend

# Copy Maven wrapper and pom.xml to leverage Docker layer caching
COPY backend/pom.xml .
COPY backend/.mvn/ .mvn
COPY backend/mvnw .
# Copy the Hardhat artifacts which contain the contract ABI
COPY smart-contract/artifacts /app/smart-contract/artifacts

# Download dependencies (optional but good practice)
# This command also triggers the web3j-maven-plugin to generate sources
RUN ./mvnw dependency:go-offline

# Copy the rest of the backend source code
COPY backend/src ./src

# Build the application.
RUN ./mvnw clean package -DskipTests

# --- Final Stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copy the built JAR from the build stage
COPY --from=build /app/backend/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
