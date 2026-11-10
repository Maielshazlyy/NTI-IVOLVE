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

<img width="1485" height="450" alt="image" src="https://github.com/user-attachments/assets/75ffcc2e-4613-432a-a669-994ade48b0a6" />
<img width="1402" height="162" alt="image" src="https://github.com/user-attachments/assets/48ea1d9b-0344-4a7f-8490-fe91983bbe2c" />


