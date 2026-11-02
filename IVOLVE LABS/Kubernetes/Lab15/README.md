# Kubernetes — Lab 15: Node.js Application Deployment with a ClusterIP Service

## Objective

Deploy two replicas of the Node.js application in the `ivolve` namespace. The application uses the MySQL database from Lab 14, reads its connection settings from the ConfigMap and Secret, and stores logs on the PersistentVolumeClaim created in Lab 13.

## Resources Used

- Docker Hub image: `maielshazly/ivolve-app:lab8`
- Namespace: `ivolve`
- ConfigMap: `ivolvcm`
- Secret: `ivolvesecret`
- MySQL StatefulSet: `ivolvedb`
- MySQL headless Service: `ivolvedbsvc`
- Application PVC: `ivolvepvc`
- Deployment manifest: `ivolve-app.yaml`

The ConfigMap contains `DB_HOST=ivolvedbsvc` and `DB_USER=ivolve_user`. The Secret provides the MySQL root and application passwords. Secret values are intentionally not included in this repository.

## Deployment Configuration

The manifest defines a Deployment named `ivolveapp` with two replicas and a ClusterIP Service named `ivolveappsvc`.

The application Pods:

- Use the custom image pushed to Docker Hub in Lab 8.
- Read database settings from `ivolvcm` and `ivolvesecret`.
- Select nodes labeled `project=ivolve`.
- Tolerate the `workload=ivolveApp:NoSchedule` taint on the worker.
- Mount the Lab 13 claim, `ivolvepvc`, at `/app/logs`.

An init container uses the MySQL client image to wait for the database, create the `ivolve` database if needed, and grant the application user access. The `MYSQL_DATABASE` setting was removed from the Lab 14 StatefulSet so the init container handles database creation for this lab.

Kubernetes resource names must be lowercase, so the resources use `ivolveapp` and `ivolveappsvc`.

## Apply the Manifest

Run these commands from the directory containing `ivolve-app.yaml`:

```bash
kubectl apply --dry-run=server -f ivolve-app.yaml
kubectl apply -f ivolve-app.yaml
kubectl rollout status deployment/ivolveapp -n ivolve --timeout=180s
```

The server-side dry run checks the manifest with the Kubernetes API before creating resources.

## Verify the Deployment

Check the Deployment, Pods, Service, and persistent volume claims:

```bash
kubectl get deployment,pods,service,pvc -n ivolve
```

Check that both application Pods are scheduled on the labeled worker:

```bash
kubectl get pods -n ivolve -l app=ivolveapp -o wide
```

Check that the ClusterIP Service has endpoints for both Pods:

```bash
kubectl get endpointslice -n ivolve \
  -l kubernetes.io/service-name=ivolveappsvc
```

Review the init container and application logs:

```bash
kubectl logs -n ivolve -l app=ivolveapp \
  --all-containers=true --prefix=true
```

The application logs should show that it connected to MySQL and found the `ivolve` database.

## Verify the Database and User Privileges

Connect to MySQL from the control-plane node:

```bash
kubectl exec -it -n ivolve ivolvedb-0 -- \
  mysql -h ivolvedbsvc -uivolve_user -p ivolve
```

Enter the application database password when prompted, then run:

```sql
SHOW DATABASES;
SHOW GRANTS FOR 'ivolve_user'@'%';
exit
```

The `ivolve` database should appear, and the application user should have privileges on `ivolve.*`.

## Results

- The `ivolveapp` Deployment reached `2/2` available replicas.
- Both Pods ran on the worker selected by `project=ivolve`.
- The `ivolveappsvc` ClusterIP Service had endpoints for both application Pods.
- The init container completed database setup.
- Application logs confirmed a successful connection to the `ivolve` database.
- The application user had privileges on the database.
- The application Pods used the `ivolvepvc` claim for `/app/logs`.

A ClusterIP Service is reachable from inside the Kubernetes cluster; it is not directly exposed to the public internet.

## Files

- `ivolve-app.yaml` — Deployment, init container, volume mount, and ClusterIP Service.
- `README.md` — Lab instructions and verification steps.
<img width="1902" height="330" alt="image" src="https://github.com/user-attachments/assets/0b262eff-1d43-45ad-b1e7-a18cd2282ce0" />
<img width="1910" height="290" alt="image" src="https://github.com/user-attachments/assets/0a8e4798-1d10-455c-858e-d438e961df40" />
<img width="1112" height="482" alt="image" src="https://github.com/user-attachments/assets/4575260f-8b7b-4196-aefa-5df9308a5c5a" />


