# Lab 21: Jenkins Pipeline for Application Deployment

## Objective

Create a Jenkins pipeline that builds the Node.js application from Lab 8, builds and scans a Docker image, pushes it to Docker Hub, updates the Kubernetes Deployment image, and deploys the application to the existing Kubernetes cluster.

## Repository Contents

- `Jenkinsfile` — defines the CI/CD pipeline.
- `Dockerfile` — builds the Node.js application image.
- `server.js`, `db.js`, `package.json`, and `frontend/` — application source files.
- `k8s/deployment.yaml` — Kubernetes Deployment manifest for the application.
- `k8s/service.yaml` — ClusterIP Service manifest.
- `.dockerignore` — excludes unnecessary files from the Docker build context.

## Application and Database

The application uses the MySQL database configured in the existing Kubernetes cluster. The Deployment reads its database settings from the `ivolve` namespace:

- ConfigMap: `ivolvcm`
- Secret: `ivolvesecret`
- Database Service: `ivolvedbsvc`

The Kubernetes manifests use the existing cluster resources. Do not add passwords, tokens, kubeconfig files, or `secret.yaml` to this repository.

## Jenkins Credentials

Configure these credentials in Jenkins before running the pipeline:

| Credential ID | Jenkins credential type | Purpose |
|---|---|---|
| `github-lab21-read` | Username with password/token | Clone this private GitHub repository |
| `dockerhub-creds` | Username with password/token | Push the image to Docker Hub |
| `k8s-kubeconfig` | Secret file | Authenticate to the Kubernetes cluster |

Store credential values only in Jenkins. Never commit the actual tokens, passwords, or kubeconfig file.

## Pipeline Stages

1. **Checkout** — clones the repository from GitHub.
2. **Build App** — installs the Node.js application dependencies.
3. **Build Docker Image** — builds `maielshazly/ivolve-app` and tags it with the Jenkins build number.
4. **Scan Docker Image** — scans the image with Trivy for HIGH and CRITICAL vulnerabilities.
5. **Push Image to Docker Hub** — authenticates using the Jenkins credential and pushes the build-tagged image.
6. **Delete Local Image** — removes the local copy after the push.
7. **Update Deployment Image** — changes the image tag in `k8s/deployment.yaml` in the Jenkins workspace.
8. **Deploy to Kubernetes** — applies the Deployment and Service, then checks rollout status.
9. **Post Actions** — logs out of Docker and reports whether the pipeline succeeded or failed.

The Trivy command uses `--exit-code 0`, so vulnerability findings are reported but do not by themselves fail the pipeline. The Jenkins workspace manifest is updated for that build and applied to the cluster.

## Run the Pipeline

1. Create a Jenkins Pipeline job named `lab21-mysql-app`.
2. Configure it to use **Pipeline script from SCM**.
3. Select Git, enter this repository URL, and choose the `github-lab21-read` credential.
4. Set the branch to `*/main` and the script path to `Jenkinsfile`.
5. Select **Build Now**.
6. Open **Console Output** and confirm that each stage completes.

## Verify the Deployment

From a machine with `kubectl` configured for the cluster, run:

```bash
kubectl rollout status deployment/ivolveapp -n ivolve
kubectl get deployment,pods,service -n ivolve -o wide
kubectl get deployment ivolveapp -n ivolve \
  -o jsonpath='{.spec.template.spec.containers[?(@.name=="ivolve-app")].image}{"\n"}'
```

Confirm the Deployment is ready, the application Pods are `Running`, the Service exists, and the image tag matches the Jenkins build number.
<img width="1862" height="667" alt="image" src="https://github.com/user-attachments/assets/abc87108-0f0f-4900-9026-c4156e798c50" />

<img width="1600" height="305" alt="WhatsApp Image 2026-11-04 at 12 20 07 PM" src="https://github.com/user-attachments/assets/8ce0c0b5-bd7e-40a6-9962-55a1e1fa4337" />
<img width="1385" height="425" alt="WhatsApp Image 2026-11-04 at 12 20 07 PM (1)" src="https://github.com/user-attachments/assets/242ffb03-a204-4401-a8a7-dcae2a9bbf20" />
<img width="1452" height="467" alt="WhatsApp Image 2026-11-04 at 12 21 21 PM" src="https://github.com/user-attachments/assets/c5c3605a-5353-492e-818e-49c5b87cb8f5" />


