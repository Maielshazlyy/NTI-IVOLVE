# Jenkins Lab 22 — CI/CD Pipeline with Shared Libraries and Agent

## Overview

This lab runs the iVolve Node.js and MySQL application from the earlier Docker/Kubernetes labs through a Jenkins pipeline on a dedicated inbound agent. The pipeline uses three Jenkins Shared Library steps:

1. `BuildApp` installs Node.js dependencies, runs the unit tests, and checks JavaScript syntax.
2. `BuildImage` builds the Docker image, scans it with Trivy, pushes it to Docker Hub, and removes the local image.
3. `DeployOnK8s` updates the image in the Kubernetes Deployment manifest, applies the Deployment and Service, and waits for rollout completion.

## Repository contents

- `Jenkinsfile` — pipeline that calls the shared library steps on the `lab22-agent` label.
- `vars/BuildApp.groovy`, `vars/BuildImage.groovy`, `vars/DeployOnK8s.groovy` — global shared library steps.
- `agent/Dockerfile` — reproducible inbound-agent image with Node.js/npm, Docker CLI, kubectl, and Trivy.
- `Dockerfile`, `server.js`, `db.js`, `db-check.js`, `frontend/` — application source and image build instructions.
- `test/db-check.test.js` — unit tests using Node's built-in test runner.
- `k8s/deployment.yaml`, `k8s/service.yaml` — Kubernetes resources for the application.

## Jenkins configuration

Configure a Global Pipeline Library named `ivolve-lab22`, using this repository and the `main` branch. The `Jenkinsfile` imports it with `@Library('ivolve-lab22')`.

Create a permanent inbound agent with the label `lab22-agent`. Build its image from `agent/Dockerfile`, then run it with access to the Docker socket and a Jenkins-generated inbound-agent secret. Keep the secret in Jenkins/your local environment; never commit it or include it in screenshots.

Create these Jenkins credentials:

- `dockerhub-creds` — Docker Hub username and access token/password.
- `k8s-kubeconfig` — Kubernetes kubeconfig as a Secret file.
- A GitHub credential with read access if the repository is private.

## Kubernetes prerequisites

The cluster must already have namespace `ivolve`, the MySQL service/database, ConfigMap `ivolvcm`, Secret `ivolvesecret`, and PVC `ivolvepvc` from the earlier labs. The target worker must have label `project=ivolve` and tolerate the workload taint used by the application Deployment.

## Run locally

Run the unit tests with:

```bash
npm install
npm test
```

Build the agent image with:

```bash
docker build -f agent/Dockerfile -t ivolve-lab22-agent:latest .
```

Configure the Jenkins Pipeline job to use **Pipeline script from SCM**, this repository, branch `*/main`, and script path `Jenkinsfile`.

## Security note

No passwords, personal access tokens, kubeconfig files, or inbound-agent secrets belong in this repository. Store them in Jenkins credentials or local secret storage.