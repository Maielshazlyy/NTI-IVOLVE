 # Lab 4: Docker Image Optimization and Export/Load

## Objective

Build and run the original Docker image, create an optimized multi-stage image that runs as a non-root user, compare image sizes, then save, delete, and restore the optimized image.

## Project

Source code: [ImageOptimization-](https://github.com/Ibrahim-Adel15/ImageOptimization-)

The Maven build generates:

```text
target/demo-0.0.1-SNAPSHOT.jar
```

The application uses port `8080`.

## 1. Build the original image

Run this command from the project directory:

```bash
docker build -t image1 .
```

Check the image size:

```bash
docker images image1
```

## 2. Run and verify the original container

Start the container and map port `8080` on the VM to port `8080` inside the container:

```bash
docker run -d --name container1 -p 8080:8080 image1
```

Check the port mapping:

```bash
docker port container1
```

Open the application at [http://localhost:8080](http://localhost:8080) and verify that it is working.

Stop and remove the container:

```bash
docker stop container1
docker rm container1
```

## 3. Build and compare the optimized image

The optimized Dockerfile uses two stages:

- The `builder` stage uses Maven to build the application and generate the JAR file.
- The `runtime` stage contains the Java runtime and JAR file, without Maven or the source code.
- The application runs as the non-root user `appuser`.

Build the optimized image:

```bash
docker build -f Dockerfile.optimized -t image2 .
```

Compare the image sizes:

```bash
docker images image1 image2
```

Record the sizes shown on your VM:

| Image | Size |
|---|---:|
| `image1` — original | Add the size reported by Docker |
| `image2` — optimized | Add the size reported by Docker |

## 4. Run and verify the optimized container

Start the optimized container:

```bash
docker run -d --name container2 -p 8080:8080 image2
```

Check the port mapping and the user running inside the container:

```bash
docker port container2
docker exec container2 id
```

The `id` command should show `appuser`, confirming that the application is not running as root.

Open [http://localhost:8080](http://localhost:8080) and verify that the application is working.

Stop and remove the container:

```bash
docker stop container2
docker rm container2
```

## 5. Save and load the optimized image

Save `image2` to a TAR archive:

```bash
docker save -o lab-image.tar image2
```

Remove the local image:

```bash
docker image rm image2
```

Load the image from the archive and verify that it has been restored:

```bash
docker load -i lab-image.tar
docker images image2
```

The final command should show `image2` in the local Docker image list.

## Results

- The original image was built and tested.
- The optimized multi-stage image was built.
- The optimized container ran as a non-root user.
- The image sizes were compared.
- The optimized image was saved to `lab-image.tar`, removed, and restored.

## Files

- `Dockerfile` — optimized multi-stage Dockerfile.
- `Dockerfile.original` — original Dockerfile used to build `image1`.
- `pom.xml` and `src/` — Maven project files.
- `lab-image.tar` — exported image archive, if retained with the lab files.<img width="981" height="405" alt="image" src="https://github.com/user-attachments/assets/967039d9-e20a-4ee6-8991-e4d99b3ae398" />
<img width="998" height="52" alt="Screenshot 2026-10-22 130509" src="https://github.com/user-attachments/assets/7e99b141-cd1a-4eee-ab7b-c1b7700fae68" />
