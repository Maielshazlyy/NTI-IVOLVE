# Lab 23: GitOps Workflow with Jenkins and Argo CD

## Objective

Jenkins builds, scans, and publishes a new application image, then updates the Kubernetes Deployment manifest in GitHub. Argo CD watches that Git repository and automatically synchronizes the manifest change to the `ivolve` namespace.

## Repositories and workflow

- **Public lab submission:** `NTI-IVOLVE/IVOLVE LABS/ArgoCD/Lab23` (this folder).
- **Live GitOps source watched by Argo CD:** `https://github.com/Maielshazlyy/jenkins-lab-22.git`.
- **Branch and manifest directory:** `main`, path `k8s`.
- **Argo CD application:** `ivolve`, namespace `argocd`; destination namespace `ivolve`.
- **Jenkins pipeline:** checks out the private Lab22 repository and uses the `ivolve-lab22` shared library.

This public folder is a submission/reference copy. The private Lab22 repository remains the live GitOps source: Jenkins must successfully push the changed manifest there so Argo CD can detect and sync it.

## Contents

- `Jenkinsfile` — runs the CI/CD stages on the `lab22-agent` Jenkins agent and invokes the shared-library functions.
- `vars/BuildApp.groovy` — application build and test step.
- `vars/BuildImage.groovy` — Docker build, Trivy scan, Docker Hub push, and local image cleanup.
- `vars/DeployOnK8s.groovy` — updates the image tag in `k8s/deployment.yaml` and pushes the updated manifest to the GitOps repository.
- `k8s/deployment.yaml` — Node.js Deployment; consumes the existing ConfigMap and Secret, initializes the database, and mounts the Lab13 PVC for logs.
- `k8s/service.yaml` — ClusterIP service exposing the app on port 3000.
- `argocd-application.yaml` — declarative reference equivalent of the Argo CD application created in the UI.

## Jenkins configuration prerequisites

Configure these in Jenkins; keep all secret values out of Git:

1. Shared library `ivolve-lab22`, pointing to the private Lab22 repository and its `main` branch.
2. Docker Hub credential ID `dockerhub-creds` for image publishing.
3. GitHub write credential ID `github-write-creds` for committing and pushing the updated manifest.
4. Jenkins agent `lab22-agent` with Docker, Node.js/npm, Trivy, and kubectl, with access to Docker Engine and the Kubernetes cluster.
5. If the shared deployment step needs a kubeconfig credential, configure the referenced file credential in Jenkins separately.

Never commit GitHub or Docker Hub tokens, database passwords, kubeconfig files, or agent connection secrets.

## Argo CD application settings

The application configuration is:

- Name: `ivolve`
- Project: `default`
- Repository: `https://github.com/Maielshazlyy/jenkins-lab-22.git`
- Revision: `main`
- Path: `k8s`
- Destination: in-cluster Kubernetes API, namespace `ivolve`
- Automated synchronization enabled

The YAML in `argocd-application.yaml` documents these settings. The live application was created through the Argo CD UI. Do not apply this file if an application named `ivolve` already exists, since that would attempt to manage the same object declaratively.

## Steps performed

1. Installed Argo CD in the Kubernetes cluster and opened the Argo CD UI.
2. Connected the `jenkins-lab-22` GitHub repository.
3. Created the Argo CD application `ivolve` to monitor `main` and the `k8s` directory, targeting namespace `ivolve`.
4. Enabled automated synchronization.
5. Configured the Jenkins pipeline and shared-library deployment step to update the Deployment image tag and push the manifest change to GitHub.
6. Confirmed Argo CD detected the Git revision and reported the application as **Healthy** and **Synced**.

## Verification

Run on the Kubernetes control-plane:

```bash
kubectl get applications.argoproj.io ivolve -n argocd
kubectl get deployment ivolveapp -n ivolve -o wide
kubectl get pods -n ivolve -o wide
kubectl get deployment ivolveapp -n ivolve -o jsonpath='{.spec.template.spec.containers[?(@.name=="ivolve-app")].image}{"\n"}'
```

In Argo CD, confirm **Healthy**, **Synced**, and automatic sync. In Jenkins, confirm the test/build, image scan and push, and Git manifest update stages all succeeded. A successful image push by itself does not prove the GitOps update succeeded.

## Evidence to submit

1. Jenkins pipeline stages showing a successful build, scan, image push, and manifest commit/push.
2. Argo CD application overview showing `ivolve` as **Healthy** and **Synced**, with automatic sync enabled.
3. Argo CD application details showing the repository, `main` revision, `k8s` path, and `ivolve` destination namespace.
4. Kubernetes Deployment and Pods using the image tag produced by the successful build.
5. GitHub history or the private repository's updated `k8s/deployment.yaml` showing the Jenkins-generated image tag commit.

Keep tokens and credentials out of screenshots.
