# Automated Web Server Configuration with Ansible

## Objective

Use an Ansible playbook to install and configure Docker, kubectl, Java 21, and Jenkins LTS on a managed Ubuntu worker, then verify the services and Jenkins web endpoint.

## Environment

- Control node: Ubuntu 24.04 EC2 instance running Ansible
- Managed node: Ubuntu 24.04 worker, reached over its private IP
- Inventory: Reuse the sanitized inventory pattern from Lab 24
- Worker memory: 2 GiB; Jenkins Java heap capped at 256 MiB

## Playbook tasks

[`site.yml`](site.yml) performs the following tasks on the `managed` inventory group:

1. Installs Docker, kubectl, fontconfig, and Java 21.
2. Enables and starts Docker.
3. Adds the official Jenkins LTS signing key and apt repository.
4. Installs Jenkins without starting it before its memory settings are applied.
5. Creates a systemd drop-in directory and limits Jenkins heap use to 256 MiB.
6. Enables and starts Jenkins and waits for its login page to return HTTP 200.
7. Reports Docker, kubectl, Java, Jenkins service, and HTTP verification results.

The directory for the systemd override is explicitly created before copying the file. This avoids failure when `/etc/systemd/system/jenkins.service.d` does not yet exist.

## Run from the control node

```bash
cd ~/ansible/lab25
ansible-playbook -i ../lab24/inventory.ini site.yml --syntax-check
ansible-playbook -i ../lab24/inventory.ini site.yml
```

The inventory supplies SSH access to the worker. `become: true` allows Ansible to configure system packages and services through sudo.

## Verify

```bash
ssh -i ~/.ssh/ansible_lab ubuntu@<WORKER_PRIVATE_IP> \
  "free -h; sudo systemctl is-active docker jenkins; \
  curl -sS -o /dev/null -w 'Jenkins HTTP %{http_code}\\n' \
  http://127.0.0.1:8080/login"
```

The successful run reported Docker and Jenkins as active, Jenkins HTTP `200`, and Ansible recap `failed=0`. Save a screenshot of the successful playbook recap and another of the service and HTTP checks in `screenshots/`.

The worker is resource constrained. After capturing Jenkins evidence, Jenkins can be stopped until it is needed again:

```bash
ssh -i ~/.ssh/ansible_lab ubuntu@<WORKER_PRIVATE_IP> \
  "sudo systemctl stop jenkins; free -h"
```

## Security

Do not commit a real inventory, private SSH key, cloud key, password, or Jenkins initial-admin credential. Keep screenshots free of secrets.
<img width="1357" height="312" alt="image" src="https://github.com/user-attachments/assets/1b37b4bc-9695-435a-870c-0dab8641c0c3" />
<img width="1457" height="455" alt="WhatsApp Image 2026-11-10 at 4 01 45 PM" src="https://github.com/user-attachments/assets/c35dfd9d-ea47-4144-9489-8d444a5735d1" />


