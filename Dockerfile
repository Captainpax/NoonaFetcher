# Step 1: Use Ubuntu 20.04 as the base image
FROM ubuntu:20.04

# Step 2: Install dependencies (OpenJDK 17 and Maven)
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    gnupg2 \
    lsb-release \
    ca-certificates \
    openjdk-17-jdk \
    maven \
    && apt-get clean;

# Step 3: Set the working directory to /app
WORKDIR /app

# Step 4: Copy the entire project into the container
COPY . /app/

# Step 5: Expose the port that your app will run on
EXPOSE 7000

# Step 6: Run the app using Maven (without compiling it)
CMD ["mvn", "exec:java", "-Dexec.mainClass=com.paxkun.Main"]
