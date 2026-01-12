# Use OpenJDK image with JDK17
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy fat JAR (to be built)
COPY build/libs/kubernetes-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Let profile be supplied at runtime, default to 'local'
ENV SPRING_PROFILES_ACTIVE=local

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]