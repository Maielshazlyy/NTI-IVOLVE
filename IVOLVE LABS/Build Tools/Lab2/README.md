# Build Tools — Lab 2: Building and Packaging a Java Application with Gradle

## Objective

Build, test, and package the Calculator application using Gradle.

## Requirements

- Java JDK 17 or later
- Gradle
- Git

## Source Code

[CalculatorGradleApp](https://github.com/Ibrahim-Adel15/CalculatorGradleApp)

## 1. Clone the project

```bash
git clone https://github.com/Ibrahim-Adel15/CalculatorGradleApp.git
cd CalculatorGradleApp
```

The first command downloads the project. The second enters its directory.

## 2. Run the unit tests

This project does not include the Gradle Wrapper, so use the installed Gradle command:

```bash
gradle test
```

Gradle runs the project's unit tests. The test report is generated under `build/reports/tests/test/`.

## 3. Build the application

```bash
gradle build
```

The build runs the required tasks and creates the JAR file at:

```text
build/libs/calculator.jar
```

## 4. Run the application

```bash
java -jar build/libs/calculator.jar
```

Enter two numbers when prompted. The application displays their sum, subtraction, multiplication, and division.

## Results

The saved test report is available at [`results/index.html`](https://github.com/Maielshazlyy/NTI-IVOLVE/blob/main/IVOLVE%20LABS/Build%20Tools/Lab2/results/index.html). Open it in a web browser to view the test results.<img width="812" height="417" alt="image" src="https://github.com/user-attachments/assets/58e2601c-b196-4b6d-b07b-db8fac9c24aa" />

