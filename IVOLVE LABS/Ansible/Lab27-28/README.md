# Ansible MySQL Provisioning: Vault and AWS Dynamic Inventory

This combined exercise provisions MySQL with a reusable Ansible role and Ansible Vault, then uses AWS EC2 dynamic inventory to discover the instance that receives the configuration. It covers secure secret handling, tag-based host selection, SSH connectivity, repeatable configuration, and post-deployment validation.

## Project goals

- Install and configure MySQL on an Ubuntu EC2 instance without manually logging in to configure packages or services.
- Create an `ivolve` database and a dedicated `ivolveuser` account with privileges scoped to that database.
- Keep the database password encrypted with Ansible Vault and out of Git history.
- Discover target EC2 instances from AWS by tag rather than maintaining a changing list of IP addresses.
- Validate the result by connecting to MySQL as `ivolveuser` and listing the databases visible to that account.

## How the workflow fits together

```text
AWS EC2 API
  │ discover running instances tagged server=ivolve
  ▼
ivolve.aws_ec2.yml ──> ivolve_servers inventory group
                              │
                              ▼
Ansible control node ── SSH ──> Ubuntu EC2 target
       │                              │
       ├── Ansible Vault secret       ├── Install MySQL + PyMySQL
       │                              ├── Start and enable MySQL
       │                              ├── Create ivolve database
       │                              └── Create ivolveuser
       └── site.yml validates database access
```

Ansible runs from a **control node**. The control node reads AWS inventory and the encrypted Vault file, then connects to the selected EC2 host over SSH. AWS discovery credentials are for the control node; the EC2 target only needs the SSH key and a reachable SSH port.

## Repository structure

```text
Lab27-28/
├── .gitignore
├── ansible.cfg
├── group_vars/
│   ├── ivolve_servers.yml.example  # safe template for SSH connection settings
│   └── vault.yml.example          # placeholder only; never use as a real secret
├── ivolve.aws_ec2.yml             # AWS region, tag filter, and inventory group
├── requirements.yml               # amazon.aws and community.mysql collections
├── roles/
│   └── mysql/tasks/main.yml        # reusable MySQL setup tasks
├── site.yml                       # applies the role and validates the database
└── screenshots/                    # add run evidence here
```

## Requirements

### Control node

- Ubuntu or another supported Ansible control-node environment.
- Ansible and Python 3.
- AWS API access that can describe EC2 instances and their tags. An IAM instance profile attached to the control node is preferred over long-lived access keys.
- Network connectivity to the target's private IP on TCP port 22.

### Target EC2 instance

- A running Ubuntu EC2 instance in the AWS region configured in `ivolve.aws_ec2.yml` (`eu-north-1` by default).
- The EC2 instance must have the tag **Key:** `server`, **Value:** `ivolve`.
- A security group rule allowing SSH (TCP/22) from the control node's security group or private address.
- An SSH key pair available to the control node. Keep its private key on the control node; never place it in this repository.
- Python 3 available on the target. The playbook installs MySQL and `python3-pymysql`.

> EC2 instances can incur AWS charges. Choose an appropriate size for the exercise and stop or terminate the instance when finished.

## Step 1 — Prepare the AWS target

1. Launch an Ubuntu EC2 instance in the same AWS region used by `ivolve.aws_ec2.yml`.
2. Add the tag `server=ivolve`. The dynamic inventory uses this exact tag and only includes instances in the `running` state.
3. Configure the instance security group to allow TCP/22 from the control node. When possible, reference the control node's security group instead of allowing SSH from the whole internet.
4. Select or import an EC2 key pair for SSH access. The matching private key must be present on the Ansible control node and readable only by its owner:

   ```bash
   chmod 600 ~/.ssh/lab28-ansible.pem
   ```

5. Attach an IAM role/instance profile to the **control node** with read-only EC2 discovery permissions. A minimal policy for this inventory is:

   ```json
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Action": ["ec2:DescribeInstances", "ec2:DescribeTags"],
         "Resource": "*"
       }
     ]
   }
   ```

   This role is used to list EC2 metadata. It does not grant Ansible permission to change AWS resources, and it is separate from the SSH key used to connect to the target. Do not add AWS access keys to the repository or the inventory file.

## Step 2 — Install Ansible dependencies

Run on the control node from this project directory:

```bash
sudo apt update
sudo apt install -y ansible python3-boto3
ansible-galaxy collection install -r requirements.yml
```

`amazon.aws` provides the EC2 inventory plugin. `community.mysql` provides the MySQL database, user, and query modules. `python3-boto3` lets the inventory plugin call the AWS API.

## Step 3 — Configure the SSH connection

Create a local group variables file from the checked-in example:

```bash
cp group_vars/ivolve_servers.yml.example group_vars/ivolve_servers.yml
nano group_vars/ivolve_servers.yml
```

Set `ansible_ssh_private_key_file` to the private key's actual path **on the control node**. For example:

```yaml
ansible_user: ubuntu
ansible_ssh_private_key_file: /home/ubuntu/.ssh/lab28-ansible.pem
ansible_python_interpreter: /usr/bin/python3
```

Protect the key file and keep this environment-specific file out of Git. `.gitignore` excludes `group_vars/ivolve_servers.yml` and private-key files.

Test discovery and SSH before changing the target:

```bash
ansible-inventory -i ivolve.aws_ec2.yml --graph
ansible ivolve_servers -i ivolve.aws_ec2.yml -m ping
```

The graph should show the target beneath `@ivolve_servers`, and the ping should return `"ping": "pong"`. Ansible's `ping` module checks Ansible connectivity and Python execution; it is not an ICMP network ping.

## Step 4 — Encrypt the MySQL password

Create an encrypted variable file on the control node:

```bash
ansible-vault create group_vars/vault.yml
```

Choose a new Vault password when prompted. In the editor, enter a separate, strong database password:

```yaml
vault_mysql_password: "<strong-unique-database-password>"
```

Save and exit the editor. `ansible-vault create` encrypts the entire file. The Vault password protects that encrypted file; the MySQL password is the value stored inside it. They are distinct credentials. Keep both private and do not paste them into screenshots, chat, or Git. The repository's `vault.yml.example` is only a placeholder; do not use it as the real Vault file.

To inspect or update the encrypted file later:

```bash
ansible-vault view group_vars/vault.yml
ansible-vault edit group_vars/vault.yml
```

## Step 5 — Review the implementation

This section walks through the actual YAML and explains how Ansible evaluates it. The snippets below are excerpts from the files in this folder.

### `ansible.cfg` — project defaults

```ini
[defaults]
inventory = ./ivolve.aws_ec2.yml
roles_path = ./roles
host_key_checking = True
```

- `inventory` makes Ansible use the AWS EC2 inventory plugin when a command is run from this project directory and no `-i` option is supplied. The commands in this README still pass `-i` explicitly so the source is clear.
- `roles_path` tells Ansible where to find the local `mysql` role invoked by `site.yml`.
- `host_key_checking = True` keeps SSH host identity checking enabled. On first connection, verify the host and accept its key.

### `requirements.yml` — collections

```yaml
collections:
  - name: amazon.aws
  - name: community.mysql
```

Collections extend Ansible with plugins and modules. `amazon.aws` contains `amazon.aws.aws_ec2`, which queries AWS for inventory. `community.mysql` provides `mysql_db`, `mysql_user`, and `mysql_query`. Install these on the control node using `ansible-galaxy collection install -r requirements.yml`.

### `ivolve.aws_ec2.yml`

- Selects the `eu-north-1` region (change this to the region containing the target).
- Filters for instances in the `running` state with `server=ivolve`.
- Uses each instance's private IP as the SSH destination.
- Places filtered hosts into the `ivolve_servers` group.

The plugin configuration is YAML, not a static inventory list:

```yaml
plugin: amazon.aws.aws_ec2
regions:
  - eu-north-1
filters:
  instance-state-name: running
  tag:server: ivolve
hostnames:
  - private-ip-address
compose:
  ansible_host: private_ip_address
groups:
  ivolve_servers: true
```

The plugin first asks AWS for instances in the selected region. AWS filters the response to running instances carrying the tag `server=ivolve`. `hostnames` chooses a private address as the inventory hostname, while `compose` sets `ansible_host`, the address Ansible uses for SSH. Since the AWS filters already restrict the result, `ivolve_servers: true` adds every returned host to the group that the playbook targets. This is why the inventory graph should list the private IP under `@ivolve_servers`.

### `group_vars/ivolve_servers.yml` — connection variables

The untracked, machine-specific file is created from `group_vars/ivolve_servers.yml.example`. Ansible automatically applies variables in `group_vars/` to matching inventory groups. For this group, the values specify the SSH account, private-key path on the control node, and Python interpreter on the target:

```yaml
ansible_user: ubuntu
ansible_ssh_private_key_file: /home/ubuntu/.ssh/lab28-ansible.pem
ansible_python_interpreter: /usr/bin/python3
```

The example is safe to commit; the real file is ignored because key paths are environment-specific. The private key itself must not be copied into the repository.

### `roles/mysql/tasks/main.yml`

The role performs these tasks in order:

1. Installs `mysql-server` and `python3-pymysql` through APT.
2. Enables and starts the MySQL service.
3. Ensures the `ivolve` database exists.
4. Creates `ivolveuser` and grants `ALL` privileges on `ivolve.*`.

The role uses the local MySQL Unix socket for administrative setup. The user creation task has `no_log: true` so Ansible does not print the password in task output. The tasks use declared desired states, so rerunning the playbook should leave already-correct resources unchanged.

Here is what the role's key declarations mean:

```yaml
- name: Install MySQL server and Python driver
  ansible.builtin.apt:
    name:
      - mysql-server
      - python3-pymysql
    state: present
```

`name` labels the task in output. `ansible.builtin.apt` is Ansible's built-in APT module; `state: present` describes the desired result, so a package is installed only if needed. `python3-pymysql` is the Python database driver used by the MySQL collection modules on the managed Ubuntu host.

```yaml
- name: Create the ivolve database
  community.mysql.mysql_db:
    name: ivolve
    state: present
    login_unix_socket: /var/run/mysqld/mysqld.sock
```

`community.mysql.mysql_db` ensures that the database exists. `state: present` makes this task repeatable. `login_unix_socket` uses the local MySQL socket for administrative setup, avoiding a root database password in the playbook.

```yaml
- name: Create ivolveuser with privileges on the ivolve database
  community.mysql.mysql_user:
    name: ivolveuser
    password: "{{ vault_mysql_password }}"
    priv: "ivolve.*:ALL"
    host: localhost
    state: present
    update_password: on_create
    login_unix_socket: /var/run/mysqld/mysqld.sock
  no_log: true
```

`{{ vault_mysql_password }}` is an Ansible variable, not a literal password. Ansible loads it from the encrypted Vault file. `priv: "ivolve.*:ALL"` grants all privileges on objects in `ivolve` only; `host: localhost` limits this database account to local connections. `update_password: on_create` avoids resetting the password on every run. `no_log: true` suppresses sensitive task details from Ansible output.

### `site.yml`

The playbook targets the dynamic group `ivolve_servers`, enables privilege escalation for system configuration, loads `group_vars/vault.yml`, and calls the `mysql` role. Its post-tasks authenticate to MySQL as `ivolveuser` and run `SHOW DATABASES` to validate that the account can access the database.

The important play-level fields are:

```yaml
- name: Configure tagged ivolve EC2 instances with MySQL
  hosts: ivolve_servers
  become: true
  gather_facts: true
  vars_files:
    - group_vars/vault.yml
  roles:
    - mysql
```

- `hosts` matches the dynamic inventory group created by `ivolve.aws_ec2.yml`.
- `become: true` runs operating-system configuration with elevated privileges (sudo).
- `gather_facts: true` collects target details such as OS and architecture for Ansible facts.
- `vars_files` loads the encrypted database password. `--ask-vault-pass` asks for the separate Vault decryption password at runtime.
- `roles` runs the reusable MySQL setup tasks in order.

After the role, the playbook validates the account with a query task:

```yaml
- name: List databases using the provisioned database user
  community.mysql.mysql_query:
    login_user: ivolveuser
    login_password: "{{ vault_mysql_password }}"
    login_unix_socket: /var/run/mysqld/mysqld.sock
    query: SHOW DATABASES
  register: mysql_database_check
```

`login_user` and `login_password` test the credentials created by the role. `query` is the SQL sent to MySQL, and `register` stores the result in an Ansible variable. The next `ansible.builtin.debug` task prints that result so the run visibly confirms `ivolve` is available to the user.

## Step 6 — Validate and run

Check that the dynamic inventory can find the target:

```bash
ansible-inventory -i ivolve.aws_ec2.yml --list
ansible-inventory -i ivolve.aws_ec2.yml --graph
```

Check the playbook syntax, then run it. Ansible will prompt for the Vault password:

```bash
ansible-playbook -i ivolve.aws_ec2.yml site.yml --syntax-check --ask-vault-pass
ansible-playbook -i ivolve.aws_ec2.yml site.yml --ask-vault-pass
```

In the successful run, look for the database validation task and a play recap with `failed=0` and `unreachable=0`. `SHOW DATABASES` should include `ivolve` alongside MySQL's standard system databases. A rerun should be safe and normally show few or no changed tasks.

## Troubleshooting

| Symptom | Checks |
| --- | --- |
| Inventory shows no hosts | Confirm the instance is running, the `server=ivolve` tag is exact, the configured AWS region is correct, and the control node's IAM role has `ec2:DescribeInstances` and `ec2:DescribeTags`. |
| `Unable to locate credentials` | Attach the inventory-read IAM role to the control node, or configure AWS credentials securely outside this repository. |
| SSH timeout or connection refused | Confirm both instances can route to each other in the VPC and the target security group allows TCP/22 from the control node. |
| `Permission denied (publickey)` | Verify the EC2 key pair matches the private key configured in `group_vars/ivolve_servers.yml`, the username is `ubuntu`, and key permissions are restrictive. |
| Host key prompt on first connection | Verify the host is expected, then accept its SSH host key. Do not disable host-key checking as a routine fix. |
| Vault password error | Enter the password chosen when creating `group_vars/vault.yml`; it is not the MySQL password. |
| MySQL module cannot connect | Confirm the role installed `python3-pymysql`, MySQL is active, and the playbook is using the local socket path. |

## Screenshots and evidence

Save screenshots in `screenshots/` and embed them in the matching section of this README so they appear directly in the GitHub preview. Suggested evidence:

1. **Dynamic inventory:** `ansible-inventory -i ivolve.aws_ec2.yml --graph` showing the discovered host under `ivolve_servers`.
2. **Connectivity:** the `ansible ... -m ping` result showing `SUCCESS` and `pong`.
3. **Provisioning:** the successful playbook tasks and final recap, with no Vault prompt visible.
4. **Database validation:** the `SHOW DATABASES` query result showing `ivolve`.

Use readable crops and ensure credentials, public keys, private-key contents, and passwords are not visible. Add each file using a relative Markdown image reference, for example:

```markdown
![Dynamic inventory showing the tagged EC2 host](screenshots/01-dynamic-inventory.png)
```
<img width="1261" height="222" alt="image" src="https://github.com/user-attachments/assets/30beff3a-a56c-4f18-86f7-6c8edc7140a7" />
<img width="1472" height="641" alt="image" src="https://github.com/user-attachments/assets/4c5d834e-9c0d-4d6e-8e94-dcb2e74f61bb" />

<img width="1482" height="571" alt="image" src="https://github.com/user-attachments/assets/cd9d8acb-23ad-4e32-9b06-fa9dbeb6e55d" />



