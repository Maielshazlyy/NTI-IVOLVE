# Build Tools — Lab1
# Lab 1: Building and Packaging a Java Application with Maven

## Objective

Build, test, and package a Java calculator application using Maven.

## Requirements

- Java JDK 17 or later
- Apache Maven
- Git

## Source Code

[CalculatorMavenApp](https://github.com/Ibrahim-Adel15/CalculatorMavenApp)

## Steps

### 1. Check Java and Maven


java -version
mvn -version

### 2. Clone the application
git clone https://github.com/Ibrahim-Adel15/CalculatorMavenApp.git
cd CalculatorMavenApp

### 3. Run the unit tests
mvn test
Maven displays the test results in the terminal and saves detailed reports under target/surefire-reports/.

### 4. Build the application
mvn package
This creates the packaged JAR file at target/calculator.jar.

### 5. Run the application
java -jar target/calculator.jar

### 6. Verify the result
Follow the terminal prompts and confirm that the calculator runs and produces the expected result.
Results
The test output is saved in [`results/tests.txt`](results/tests.txt)
<img width="835" height="301" alt="image" src="https://github.com/user-attachments/assets/16be941d-4e75-451d-b19e-cab0061debec" />

