# Use an official OpenJDK 17 base image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Install dependencies
RUN apt-get update && apt-get install -y \
    curl unzip wget maven android-sdk adb \
    && rm -rf /var/lib/apt/lists/*

# Verify Java & Maven installation
RUN java -version && mvn -version

# Install Node.js and npm
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs

# Install Appium globally
RUN npm install -g appium@next

# Install Appium drivers
RUN appium driver install uiautomator2

# Set environment variables for Android SDK
ENV ANDROID_HOME=/usr/lib/android-sdk
ENV PATH=$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools:$PATH

# Copy only required files to the container
COPY pom.xml /app/
COPY src /app/src

# Run Maven to download dependencies before runtime (faster container startup)
# RUN mvn clean install -DskipTests

# Expose Appium server port
EXPOSE 4723

# Start Appium server and execute tests
CMD ["sh", "-c", "appium & mvn test"]
