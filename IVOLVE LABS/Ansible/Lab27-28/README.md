# MySQL Provisioning with Ansible Vault and Dynamic EC2 Inventory

This combined exercise provisions a MySQL database and least-scope database user through a reusable Ansible role, protects the database password with Ansible Vault, and discovers eligible EC2 hosts from AWS tags using dynamic inventory.

## What it does

1. The `mysql` role installs MySQL and its Python driver, then enables and starts the service.
2. The role creates the `ivolve` database and provisions `ivolveuser` with privileges on that database.
3. Ansible Vault supplies the database password without storing plaintext secrets in Git.
4. The AWS EC2 inventory plugin discovers running instances tagged `server=ivolve`.
5. The playbook configures discovered hosts and validates database access by running `SHOW DATABASES` as `ivolveuser`.

```text
AWS EC2 (tag server=ivolve)
        │ dynamic inventory
        ▼
Ansible control node ──SSH──> EC2 host
        │                       └── MySQL role
        │                           ├── ivolve database
        │                           └── ivolveuser
        └── Ansible Vault provides database password
```

## Repository layout

```text
.
├── ansible.cfg
├── group_vars/
│   ├── ivolve_servers.yml.example
│   └── vault.yml.example
├── ivolve.aws_ec2.yml
├── requirements.yml
├── roles/mysql/tasks/main.yml
├── site.yml
└── screenshots/
```

## Prerequisites

- Ansible installed on a control node with network access to the EC2 instance over SSH.
- A running Ubuntu EC2 instance in `eu-north-1` with the EC2 tag `server=ivolve`. Update `ivolve.aws_ec2.yml` if using another region.
- An IAM instance profile attached to the control node with read-only `ec2:DescribeInstances` and `ec2:DescribeTags` permissions. Do not store AWS access keys in this repository.
- A key pair usable for SSH to the target. The target security group must allow TCP/22 from the control node. Keep the private key on the control node.
- Ubuntu target with Python 3 available; the playbook installs MySQL and `python3-pymysql`.

## Configure

Run these commands on the Ansible control node from this directory:

```bash
sudo apt update
sudo apt install -y python3-boto3
ansible-galaxy collection install -r requirements.yml
cp group_vars/ivolve_servers.yml.example group_vars/ivolve_servers.yml
```

Edit `group_vars/ivolve_servers.yml` to point to the private key path on the control node. This file is ignored by Git.

Create an encrypted password file:

```bash
ansible-vault create group_vars/vault.yml
```

Add the following YAML in the editor, replacing the placeholder with a strong unique password:

```yaml
vault_mysql_password: "<strong-database-password>"
```

Save and exit the editor. Use the Vault password you created whenever prompted by `--ask-vault-pass`. Never commit the plaintext password, Vault password, or unencrypted `group_vars/vault.yml`.

## Discover and configure the EC2 host

Confirm the tagged host is discovered and grouped:

```bash
ansible-inventory -i ivolve.aws_ec2.yml --graph
ansible ivolve_servers -i ivolve.aws_ec2.yml -m ping
```

Expected inventory graph includes `@ivolve_servers` and the private IP of the tagged instance. Then check the playbook syntax and run it:

```bash
ansible-playbook -i ivolve.aws_ec2.yml site.yml --syntax-check --ask-vault-pass
ansible-playbook -i ivolve.aws_ec2.yml site.yml --ask-vault-pass
```

The final validation output should list `information_schema`, `ivolve`, and `performance_schema`. The play recap should report `failed=0`. The MySQL user password is hidden from task output with `no_log`.

## Evidence

Add screenshots to `screenshots/` and embed them here when available:

- `01-dynamic-inventory.png` — inventory graph showing the discovered host in `ivolve_servers`.
- `02-playbook-success.png` — successful Ansible run and recap.
- `03-database-validation.png` — `SHOW DATABASES` output with `ivolve` visible.

Before capturing screenshots, make sure no Vault prompt, password, private key, or AWS credentials are visible.

## Cleanup

Stop or terminate the dedicated EC2 instance when the exercise is complete to avoid unnecessary AWS charges. Remove its `server=ivolve` tag if keeping the instance for another purpose so it is no longer selected by this inventory.
