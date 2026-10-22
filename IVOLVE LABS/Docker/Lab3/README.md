# Docker — Lab 3: Build a Java Application Inside Docker

## Objective

Build a Docker image for a Java calculator application using Maven and Java 17. Run the application in a container and verify its calculator operations.

## Requirements

- Docker
- Git

## Application Source

[CalculatorMavenApp](https://github.com/Ibrahim-Adel15/CalculatorMavenApp)

## 1. Clone the application

```bash
git clone https://github.com/Ibrahim-Adel15/CalculatorMavenApp.git
cd CalculatorMavenApp
```

## 2. Create the Dockerfile

Create a file named `Dockerfile` in the project directory. It uses Maven with Java 17, copies the project files into the image, builds the application, and starts the generated JAR when the container runs.

The Dockerfile includes `EXPOSE 8080` as required by the lab. The calculator is a terminal application; it does not listen for network connections on port 8080.

## 3. Build the Docker image

Run this command from the directory containing the Dockerfile, `pom.xml`, and `src/`:

```bash
docker build -t app1 .
```

The `-t app1` option names the image `app1`. The `.` uses the current directory as the build context.

## 4. Check the image size

```bash
docker image ls app1
```

This command displays the image details, including its size.

## 5. Run and verify the container

```bash
docker run --name container1 -it app1
```

Enter two numbers when prompted. Verify that the calculator displays the sum, subtraction, multiplication, and division results.

## 6. Remove the container and image

After verifying the application, stop and remove the container, then remove the image:

```bash
docker stop container1
docker rm container1
docker rmi app1
```

If the container has already stopped, run `docker rm container1` directly.

## Files

- `Dockerfile` — instructions for building and running the Docker image.
- `pom.xml` — Maven project configuration.
- `src/` — Java application source code and unit tests.
<img width="981" height="405" alt="image" src="https://github.com/user-attachments/assets/1ada1555-4b00-4cfe-b57f-dd1af64c9c9f" />
<img width="998" height="52" alt="image" src="https://github.com/user-attachments/assets/fd7ca30d-0516-4d96-85d7-10dca4ce13f5" />

