# Docker — Lab 7: Multi-Network Microservices, Volumes, and Bind Mounts

## Objective

Build and run a frontend, backend, and notification service using Docker. Connect the services through two bridge networks, persist logs with named volumes, and persist backend data with a bind mount.

## Application Source

[MicroserviceApp](https://github.com/Ibrahim-Adel15/MicroserviceApp)

The application contains three services:

- **Frontend:** provides the web page for creating and viewing messages.
- **Backend:** stores messages in a JSON file and sends notification requests.
- **Notification:** receives notification requests and writes them to a log file.

The services communicate over HTTP. The frontend listens on port `3000`, the backend on `5000`, and the notification service on `5001`.

## 1. Build the Docker images

Run these commands from the repository root:

```bash
docker build -t lab7-frontend "IVOLVE LABS/Docker/Lab7/frontend"
docker build -t lab7-backend "IVOLVE LABS/Docker/Lab7/backend"
docker build -t lab7-notification "IVOLVE LABS/Docker/Lab7/notification"
```

Each command builds a separate image using that service's directory as its build context.

## 2. Create the networks

```bash
docker network create --driver bridge \
  --subnet 172.28.0.0/16 frontend-backend

docker network create --driver bridge \
  --subnet 172.29.0.0/16 backend-notification
```

- `frontend-backend` connects the frontend and backend.
- `backend-notification` connects the backend and notification service.
- The backend is attached to both networks so it can communicate with both services.

## 3. Create the log volumes and data directory

```bash
docker volume create backend-logs
docker volume create notification-logs

mkdir -p "IVOLVE LABS/Docker/Lab7/backend-data"
```

The named volumes store service logs outside the containers. The host directory `backend-data` will store backend message data through a bind mount.

## 4. Start the notification service

```bash
docker run -d \
  --name notification \
  --network backend-notification \
  -v notification-logs:/app/logs \
  lab7-notification
```

The notification container joins `backend-notification` and stores its logs in the `notification-logs` volume.

## 5. Start the backend service

```bash
docker run -d \
  --name backend \
  --network frontend-backend \
  -e NOTIFICATION_URL=http://notification:5001/notify \
  -v backend-logs:/app/logs \
  -v "$PWD/IVOLVE LABS/Docker/Lab7/backend-data:/app/data" \
  lab7-backend
```

Connect the backend to the second network:

```bash
docker network connect backend-notification backend
```

The `NOTIFICATION_URL` environment variable configures the notification endpoint. The named volume stores backend logs, and the bind mount maps the host data directory to `/app/data` inside the container.

## 6. Start the frontend service

```bash
docker run -d \
  --name frontend \
  --network frontend-backend \
  -p 8080:3000 \
  -e BACKEND_URL=http://backend:5000 \
  lab7-frontend
```

The frontend joins `frontend-backend` and uses `BACKEND_URL` to reach the backend. Port `8080` on the host is mapped to port `3000` in the frontend container.

## 7. Verify the application and storage

Check the running containers:

```bash
docker ps
```

Open the web application at [http://localhost:8080](http://localhost:8080). Create a message and verify that it can be retrieved.

Check the backend data directory and service logs:

```bash
ls -l "IVOLVE LABS/Docker/Lab7/backend-data"
docker exec backend ls -l /app/logs
docker exec notification ls -l /app/logs
```

Inspect the Docker networks and volumes:

```bash
docker network inspect frontend-backend
docker network inspect backend-notification
docker volume ls
```

The backend message file is stored under `/app/data` in the container and is mapped to the host's `backend-data` directory. Backend and notification logs are stored in their respective named volumes.

## Files

- `frontend/` — frontend application, dependencies, and Dockerfile.
- `backend/` — backend application, dependencies, and Dockerfile.
- `notification/` — notification application, dependencies, and Dockerfile.
- `backend-data/` — host directory used by the backend bind mount.
<img width="1322" height="542" alt="image" src="https://github.com/user-attachments/assets/e0a8fcc0-95f0-471d-82bc-582a1ad1c630" />
<img width="852" height="340" alt="image" src="https://github.com/user-attachments/assets/1a892140-f505-4c94-9c37-c8aa0bb85260" />

