# Docker — Lab4
# Lab 4: Building a Docker Image Locally

## Overview

In this lab, I built the Java calculator application locally with Maven, then created a Docker image using a Java 17 runtime. This keeps the build tools outside the final image.

## Requirements

- Java 17
- Maven
- Docker

## Application Source

[CalculatorMavenApp](https://github.com/Ibrahim-Adel15/CalculatorMavenApp)

## Steps

### 1. Build the application locally
cd ~/CalculatorMavenApp
mvn clean package
This creates the JAR file at target/calculator.jar.

### 2. Copy the JAR into the Lab4 folder
mkdir -p "$HOME/NTI-IVOLVE/IVOLVE LABS/Docker/Lab4/target"
cp ~/CalculatorMavenApp/target/calculator.jar \
   "$HOME/NTI-IVOLVE/IVOLVE LABS/Docker/Lab4/target/"
cd "$HOME/NTI-IVOLVE/IVOLVE LABS/Docker/Lab4"

### 3. Build the Docker image
docker build -t app2 .
### 4. Check the image size
docker image ls app2

### 5. Run and verify the application
docker run --name container2 -it -p 8080:8080 app2
I entered two numbers and checked that the calculator displayed the results. The calculator is a terminal application; it does not serve web traffic on port 8080.

### 6. Compare image sizes
docker image ls app1 app2
The app2 image uses a Java runtime and the prebuilt JAR, while app1 also includes Maven and build tools.
### 7. Clean up
After recording the image sizes and verifying the application, I stopped the container with Ctrl+C and removed it and the image:
docker rm container2
docker rmi app2
### Files
- Dockerfile — instructions for creating the runtime image.
- target/calculator.jar — generated locally by Maven; excluded from Git.<img width="960" height="161" alt="image" src="https://github.com/user-attachments/assets/c537d527-4d7e-4a7f-9aa5-894f4a2439f6" />

