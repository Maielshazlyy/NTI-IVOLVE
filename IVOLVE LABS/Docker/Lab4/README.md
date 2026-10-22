# Lab 4: Docker Image Optimization and Export/Load

## Objective

In this lab, I built and ran the original Docker image, created a smaller multi-stage image that runs as a non-root user, compared both image sizes, and tested saving and loading the optimized image.

## Project

The application source code is from [ImageOptimization-](https://github.com/Ibrahim-Adel15/ImageOptimization-.git).

The application is built with Maven and produces:

target/demo-0.0.1-SNAPSHOT.jar
The application uses port 8080.
### 1. Build the original image
I built the original image from the provided Dockerfile:
docker build -t image1 .
To check the image size:
docker images image1
###2. Run and verify the original container
I started a container and mapped port 8080 on the VM to port 8080 in the container:
docker run -d --name container1 -p 8080:8080 image1
I checked the port mapping with:
docker port container1
Then I opened the application at:
http://localhost:8080
After verifying the application, I stopped and removed the container:
docker stop container1
docker rm container1
### 3. Build the optimized image
The optimized Dockerfile uses two stages:
- The builder stage uses Maven to compile and package the application.
- The runtime stage contains the Java runtime and the packaged JAR, without Maven build tools.
- The application runs as a non-root user named appuser.
I built the optimized image with:
docker build -f Dockerfile.optimized -t image2 .
I compared the image sizes with:
docker images image1 image2
Image	Size
image1 — original	Add the size shown by docker images
image2 — optimized	Add the size shown by docker images


### 4. Run and verify the optimized container
I started the optimized image:
docker run -d --name container2 -p 8080:8080 image2
I checked the port mapping and confirmed the user inside the container:
docker port container2
docker exec container2 id
The id command should show appuser, confirming that the application is not running as root.
I opened the application at http://localhost:8080 to verify it was working. Then I stopped and removed the container:
docker stop container2
docker rm container2
### 5. Save and load the optimized image
I saved image2 as a TAR archive:
docker save -o lab-image.tar image2
I removed the local image and loaded it again from the archive:
docker image rm image2
docker load -i lab-image.tar
docker images image2
The final command confirmed that image2 was restored to the local Docker host.
### Results
- The original image was built and tested.
- The optimized image was built using a multi-stage Dockerfile.
- The optimized container ran as a non-root user.
- The two image sizes were compared.
- The optimized image was saved to lab-image.tar, removed, and successfully loaded again.
### Files
- Dockerfile — optimized multi-stage Dockerfile.
- Dockerfile.original — original Dockerfile used for the first image.
- pom.xml and src/ — Maven project files.
- lab-image.tar — exported optimized image, if retained with the lab files. <img width="981" height="405" alt="image" src="https://github.com/user-attachments/assets/967039d9-e20a-4ee6-8991-e4d99b3ae398" />
<img width="998" height="52" alt="Screenshot 2026-10-22 130509" src="https://github.com/user-attachments/assets/7e99b141-cd1a-4eee-ab7b-c1b7700fae68" />
