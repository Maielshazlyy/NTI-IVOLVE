# Build Tools — Lab2
# Lab 2: Building and Packaging a Java Application with Gradle

## Objective

Build, test, and package the Calculator application using Gradle.

## Requirements

- Java JDK 17 or later
- Gradle
- Git

## Source Code

[CalculatorGradleApp](https://github.com/Ibrahim-Adel15/CalculatorGradleApp)

## Steps

### 1. Clone the project
git clone https://github.com/Ibrahim-Adel15/CalculatorGradleApp.git
cd CalculatorGradleApp

2. Run the unit tests
This project does not include the Gradle Wrapper, so use the installed Gradle command:
gradle test

3. Build the application
gradle build
The build creates the JAR file at:
build/libs/calculator.jar

4. Run the application
java -jar build/libs/calculator.jar
Enter two numbers when prompted. The application displays their sum, subtraction, multiplication, and division.

Results
The Gradle test report is saved at [`results/index.html`](results/index.html). Open it in a web browser to view the test results.<img width="812" height="417" alt="image" src="https://github.com/user-attachments/assets/58e2601c-b196-4b6d-b07b-db8fac9c24aa" />

