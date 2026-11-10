# Structured Configuration Management with Ansible Roles

## Objective

Configure a managed Ubuntu worker with Docker, kubectl, and Jenkins by organizing each tool's tasks into a reusable Ansible role.

## Role layout

```text
Ansible-Lab26/
├── site.yml
└── roles/
    ├── docker/tasks/main.yml
    ├── kubectl/tasks/main.yml
    └── jenkins/
        ├── tasks/main.yml
        └── handlers/main.yml
```

The Docker role installs Docker and keeps its service enabled. The kubectl role ensures the Kubernetes client package is present. The Jenkins role configures the official LTS repository, installs Java 21 and Jenkins, applies a 256 MiB Java heap limit for the 2 GiB lab worker, and checks that Jenkins responds. `site.yml` applies all three roles and verifies package versions, service states, and Jenkins HTTP status.

## Run on the control node

Copy the `Ansible-Lab26` directory to `~/ansible/lab26` on the control node. Reuse the Lab 24 inventory, which defines the `managed` group and SSH access to the worker.

```bash
cd ~/ansible/lab26
ansible-playbook -i ../lab24/inventory.ini site.yml --syntax-check
ansible-playbook -i ../lab24/inventory.ini site.yml
```

The playbook uses privilege escalation for package and service configuration. Since Lab 25 already installed these tools, most tasks should report `ok` on this run. A changed Jenkins systemd override triggers its handler to reload systemd and restart Jenkins.

## Verify cluster workloads

```bash
kubectl --request-timeout=20s get nodes
kubectl --request-timeout=20s get pods -n ivolve
kubectl --request-timeout=20s get pods -n argocd
```

## Screenshots

Save screenshots in a `screenshots/` directory in the lab submission. Capture the successful playbook run with the role names and `failed=0`, the final version/service checks with Jenkins HTTP `200`, and the Kubernetes nodes and workloads. Keep credentials and private keys out of screenshots.
