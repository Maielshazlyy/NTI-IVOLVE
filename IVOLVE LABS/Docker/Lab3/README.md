# Docker — Lab3

## Overview

In this lab, I built a Docker image for a Java calculator application. I used a Maven image with Java 17, so Docker could build the application inside the image. Then I ran the application in a container and checked that it worked.

## Requirements

- Docker
- Git

## Application Source

[CalculatorMavenApp](https://github.com/Ibrahim-Adel15/CalculatorMavenApp)

## Steps

### 1. Prepare the project
git clone https://github.com/Ibrahim-Adel15/CalculatorMavenApp.git
cd CalculatorMavenApp

### 2. Create the Dockerfile
I created a Dockerfile in the project directory. It uses Maven with Java 17, copies the source code into the image, builds the application, and starts the calculator when the container runs.
The application runs in the terminal. It is not a web application, so it does not listen for connections on port 8080. I included EXPOSE 8080 as requested in the lab.

### 3. Build the Docker image
From the directory containing the Dockerfile, pom.xml, and src, I built an image named app1:
docker build -t app1 .

### 4. Check the image size
I checked the image size with:
docker image ls app1

### 5. Run and verify the container
I started a container named container1 and entered two numbers when the calculator prompted me:
docker run --name container1 -it -p 8080:8080 app1
The calculator displayed the sum, subtraction, multiplication, and division results.

### 6. Clean up
After verifying the application, I removed the container and image:
docker rm container1
docker rmi app1

### Files in This Folder
- Dockerfile — instructions for building and running the Docker image.
- pom.xml — Maven project configuration.
- src/ — Java source code and unit tests.
