# Build Tools — Lab 1: Building and Packaging a Java Application with Maven

## Objective

Build, test, and package a Java calculator application using Maven.

## Requirements

- Java JDK 17 or later
- Apache Maven
- Git

## Source Code

[CalculatorMavenApp](https://github.com/Ibrahim-Adel15/CalculatorMavenApp)

## 1. Check Java and Maven

```bash
java -version
mvn -version
```

These commands display the installed Java and Maven versions.

## 2. Clone the application

```bash
git clone https://github.com/Ibrahim-Adel15/CalculatorMavenApp.git
cd CalculatorMavenApp
```

The first command downloads the source code. The second enters the project directory.

## 3. Run the unit tests

```bash
mvn test
```

Maven compiles the application and runs its unit tests. Detailed test reports are saved under `target/surefire-reports/`.

## 4. Package the application

```bash
mvn package
```

Maven runs the tests and packages the application into a JAR file at:

```text
target/calculator.jar
```

## 5. Run the application

```bash
java -jar target/calculator.jar
```

Enter the requested numbers when prompted.

## 6. Verify the result

Check that the calculator starts and displays the expected results for its operations, such as addition, subtraction, multiplication, and division.
<img width="835" height="301" alt="image" src="https://github.com/user-attachments/assets/16be941d-4e75-451d-b19e-cab0061debec" />

