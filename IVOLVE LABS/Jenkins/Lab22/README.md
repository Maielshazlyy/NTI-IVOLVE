# Jenkins Lab 22 — CI/CD Pipeline with Shared Libraries and Jenkins Agent

## Objective

Build and deploy the iVolve Node.js and MySQL application from the earlier Docker and Kubernetes labs using Jenkins.

This lab uses a dedicated Jenkins inbound agent and three shared-library steps:

1. **BuildApp** — install dependencies, run unit tests, and validate JavaScript syntax.
2. **BuildImage** — build the Docker image, scan it with Trivy, push it to Docker Hub, and remove the local image.
3. **DeployOnK8s** — update the image in the Kubernetes Deployment, apply the Deployment and Service, and verify the rollout.

## Project Structure

```text
.
├── agent/
│   └── Dockerfile
├── frontend/
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
├── test/
│   └── db-check.test.js
├── vars/
│   ├── BuildApp.groovy
│   ├── BuildImage.groovy
│   └── DeployOnK8s.groovy
├── .dockerignore
├── Dockerfile
├── Jenkinsfile
├── db-check.js
├── db.js
├── package.json
└── server.js
```

## Application and Unit Tests

The application uses Node.js, Express, and MySQL. The unit tests use Node.js's built-in test runner, so no separate test framework is required.

Run locally from the project directory:

```bash
npm install
npm test
```

The tests check the database helper's response when the `ivolve` database exists and when it does not.

## Jenkins Shared Library

In Jenkins, configure a Global Pipeline Library with:

- **Name:** `ivolve-lab22`
- **Default version:** `main`
- **Source:** this Git repository
- **Library path:** repository root

The `Jenkinsfile` loads the library with:

```groovy
@Library('ivolve-lab22') _
```

### BuildApp

`vars/BuildApp.groovy` installs npm dependencies, runs `npm test`, and checks the syntax of `server.js`.

### BuildImage

`vars/BuildImage.groovy` builds the application image, scans it with Trivy, pushes it to Docker Hub using Jenkins credentials, and removes the local image.

### DeployOnK8s

`vars/DeployOnK8s.groovy` updates the image in `k8s/deployment.yaml`, applies the Deployment and Service, waits for rollout completion, and displays the Kubernetes resources.

## Jenkins Agent

Create a permanent inbound Jenkins agent with the label:

```text
lab22-agent
```

The agent needs:

- Java 21 for Jenkins Remoting
- Node.js and npm
- Docker CLI and access to the Docker socket
- Trivy
- kubectl

Build the agent image from the repository root:

```bash
docker build -f agent/Dockerfile -t ivolve-lab22-agent:latest .
```

Run the agent with the Jenkins URL, agent name and Jenkins-generated secret, WebSocket enabled, a work directory, and access to the Docker socket. Keep the agent secret private. Do not commit it or include it in screenshots.

## Jenkins Credentials

Create these Jenkins credentials:

- `dockerhub-creds` — Docker Hub username and access token or password.
- `k8s-kubeconfig` — Kubernetes kubeconfig stored as a Secret file.
- A GitHub credential with read access if the repository is private.

Do not put passwords, access tokens, kubeconfig files, or agent secrets in this repository.

## Kubernetes Prerequisites

The cluster should already contain the resources created in earlier labs:

- Namespace `ivolve`
- MySQL StatefulSet and Service
- ConfigMap `ivolvcm`
- Secret `ivolvesecret`
- PersistentVolumeClaim `ivolvepvc`
- A worker node labeled `project=ivolve` with the toleration required by the application Deployment

The application Deployment uses these resources to connect to MySQL and mount application logs.

## Jenkins Pipeline Job

Create a Jenkins Pipeline job with:

- **Definition:** Pipeline script from SCM
- **SCM:** Git
- **Repository:** this Lab 22 repository
- **Branch:** `*/main`
- **Script Path:** `Jenkinsfile`

The pipeline runs on the `lab22-agent` label and calls the shared-library steps in this order:

```text
Build App → Build, Scan, Push Image → Deploy to Kubernetes
```

## Verification

Confirm that:

- The Jenkins build finishes with **SUCCESS**.
- The three pipeline stages complete successfully.
- Both unit tests pass.
- The Docker image scan and Docker Hub push complete.
- The Kubernetes Deployment rollout succeeds.
- The `lab22-agent` node is online.

<img width="1897" height="634" alt="image" src="https://github.com/user-attachments/assets/21b5fdb0-fc57-4b17-a490-0497ecf6c6ab" />
<img width="1807" height="627" alt="image" src="https://github.com/user-attachments/assets/605cbdf2-e022-4f0e-a5e6-59e2d02ff599" />


