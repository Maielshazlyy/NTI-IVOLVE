# Docker — Lab 6: Environment Variables and Resource Management

## Objective

Build a Docker image for a Python application, pass environment variables to containers in different ways, and limit container CPU and memory usage.

## Requirements

- Docker
- Git
- Ubuntu VM

## Application Source

[PythonApp](https://github.com/Ibrahim-Adel15/PythonApp)

The application provides:

- `/` — returns the application name, environment, and a welcome message.
- `/health` — returns the application health status.

The application listens on port `5000`.

## 1. Prepare the application

Clone the project:

```bash
git clone https://github.com/Ibrahim-Adel15/PythonApp.git
cd PythonApp
```

The application reads `APP_NAME` and `APP_ENV` from environment variables. Add `import os` to `app.py` if it is missing:

```python
import os
```

The application uses Flask, so create `requirements.txt`:

```bash
echo "Flask" > requirements.txt
```

## 2. Create the Dockerfile

Create a file named `Dockerfile`:

```dockerfile
FROM python:3.12-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app.py .

EXPOSE 5000

CMD ["python", "app.py"]
```

The image uses Python 3.12 slim, installs Flask, copies the application, and starts it with Python. The application binds to `0.0.0.0:5000`, allowing access through the container's published port.

## 3. Build the image

```bash
docker build -t ivolve1 .
docker images ivolve1
```

The first command builds the image and tags it as `ivolve1`. The second displays the image and its size.

## 4. Run `container1` with environment variables and resource limits

```bash
docker run -d \
  --name container1 \
  -p 5000:5000 \
  --cpus="1" \
  --memory="256m" \
  -e APP_NAME=iVolveApp1 \
  -e APP_ENV=stg \
  ivolve1
```

- `-p 5000:5000` publishes container port 5000 on VM port 5000.
- `--cpus="1"` limits the container to one CPU.
- `--memory="256m"` limits the container to 256 MB of memory.
- `-e` passes environment variables to the container at startup.

Verify the environment variables and application:

```bash
docker exec container1 printenv APP_NAME
docker exec container1 printenv APP_ENV
curl http://localhost:5000
curl http://localhost:5000/health
```

Monitor the container's resource usage:

```bash
docker stats container1
```

Press `Ctrl+C` to exit the `docker stats` display. This does not stop the container.

## 5. Run a container using an environment file

Stop `container1` before starting another container on VM port 5000:

```bash
docker stop container1
```

Create `env.txt`:

```text
APP_NAME=iVolveApp2
APP_ENV=prod
```

Run `container2` using the environment file:

```bash
docker run -d \
  --name container2 \
  -p 5000:5000 \
  --env-file env.txt \
  ivolve1
```

The `--env-file` option reads environment variables from `env.txt`.

Verify the values and application response:

```bash
docker exec container2 printenv APP_NAME
docker exec container2 printenv APP_ENV
curl http://localhost:5000
```

## 6. Build an image with environment variables in the Dockerfile

Stop and remove the environment-file container:

```bash
docker rm -f container2
```

Add these lines to the Dockerfile before `EXPOSE 5000`:

```dockerfile
ENV APP_NAME=iVolveApp3
ENV APP_ENV=dev
```

`ENV` sets default environment variable values in the image. Rebuild the image with a new tag:

```bash
docker build -t ivolve2 .
```

Run the new image:

```bash
docker run -d \
  --name container2 \
  -p 5000:5000 \
  ivolve2
```

Verify the Dockerfile environment values and application response:

```bash
docker exec container2 printenv APP_NAME
docker exec container2 printenv APP_ENV
curl http://localhost:5000
```

The expected values are `iVolveApp3` and `dev`.

## 7. Clean up

Remove the containers after completing the checks:

```bash
docker rm -f container1 container2
```

## Files

- `Dockerfile` — builds and runs the Python application.
- `app.py` — Flask application.
- `requirements.txt` — Python application dependencies.
- `env.txt` — environment variables used for the second container.
```~~~~
<img width="1171" height="123" alt="Screenshot 2026-10-22 190611" src="https://github.com/user-attachments/assets/7f662f9e-d688-4a4c-a52e-393b2956e16c" />
<img width="1165" height="70" alt="Screenshot 2026-10-22 190650" src="https://github.com/user-attachments/assets/c7afd9e2-2fb1-4f5e-9e27-d9f8c0cb115f" />
<img width="1232" height="343" alt="Screenshot 2026-10-22 190922" src="https://github.com/user-attachments/assets/e530d008-11c9-41f8-9394-4c64cabce5e0" />
<img width="1256" height="186" alt="Screenshot 2026-10-22 191129" src="https://github.com/user-attachments/assets/0c547905-c261-4748-8347-43fe1ba758cd" />



