# Use Java 17 with Maven preinstalled
FROM maven:3.9.4-eclipse-temurin-17

# Set working directory inside container
WORKDIR /app

# Copy all files to container
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Run the application
CMD ["java", "-jar", "target/task-manager-1.0.0.jar"]
