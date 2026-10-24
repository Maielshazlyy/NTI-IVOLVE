# Docker — Lab 8: Node.js and MySQL with Docker Compose

## Objective

Run a Node.js application and a MySQL database as separate services using Docker Compose. The services communicate over a custom Docker network, while Docker volumes preserve the application logs and database data.

## Application Source

[iVolveApp](https://github.com/Ibrahim-Adel15/iVolveApp)

## Project Structure

- `Dockerfile` — builds the Node.js application image.
- `docker-compose.yml` — defines the application, MySQL database, network, and volumes.
- `server.js` and `db.js` — application server and database connection code.
- `package.json` — Node.js project configuration and dependencies.
- `frontend/` — static frontend files.

## Configuration

The Compose file defines two services:

- **app** — builds the application image, publishes port `3000`, and connects to the database using the hostname `db`.
- **db** — runs MySQL and creates the `ivolve` database.

Both services use the `ivolve-net` network. The `app_logs` volume stores application logs at `/app/logs`, and the `db_data` volume stores MySQL data at `/var/lib/mysql`.

## Run the Application

Run these commands from this directory:

```bash
docker compose config
docker compose up -d --build
docker compose ps
```

`docker compose config` checks the Compose configuration. `docker compose up -d --build` builds the application image and starts both services in the background.

View the application logs:

```bash
docker compose logs -f app
```

Press `Ctrl+C` to stop following the logs. This does not stop the application.

## Verify the Application

Open the application in a browser:

- [http://localhost:3000](http://localhost:3000)
- [http://localhost:3000/health](http://localhost:3000/health)

The health endpoint checks that the application can communicate with MySQL.

## Push the Application Image to Docker Hub

The application image is tagged as `maielshazly/ivolve-app:lab8`.

```bash
docker login -u maielshazly
docker compose build app
docker compose push app
```

The image is available in the [Docker Hub repository](https://hub.docker.com/r/maielshazly/ivolve-app), under the `lab8` tag.

## Stop the Services

```bash
docker compose down
```

This stops and removes the containers and network. Named volumes remain, so the database data and application logs are preserved.

To remove the volumes and their data as well, run:

```bash
docker compose down -v
```

## Result

The Node.js application and MySQL database ran as separate Compose services. The application was verified through its health endpoint, and its Docker image was pushed to Docker Hub with the `lab8` tag.
<img width="1252" height="772" alt="image" src="https://github.com/user-attachments/assets/2f15783b-b205-4dce-b2a1-9ab1ad6c645f" />
<img width="832" height="327" alt="image" src="https://github.com/user-attachments/assets/1ef0447b-5e58-4758-9ae1-f34fc7628c0a" />
<img width="832" height="327" alt="image" src="https://github.com/user-attachments/assets/129e3e3a-ba0d-42af-ac4b-f785684dcf35" />
<img width="1600" height="700" alt="image" src="https://github.com/user-attachments/assets/3bd146a2-0046-4666-9469-e04d9276a8e2" />
