# Jenkins — Lab 20: Role-Based Authorization

## Objective

Create two Jenkins users and assign each user a different role:

- `user1` receives the `admin` role.
- `user2` receives the `read-only` role.

This lab uses the Role-based Authorization Strategy plugin to control what each user can do in Jenkins.

## Concepts

- **Authentication** verifies a user's identity when they sign in.
- **Authorization** determines which Jenkins features and actions that user can access.
- **Global roles** define permissions across Jenkins.
- **Role assignments** connect users to roles.

## Prerequisites

- Jenkins is running and accessible.
- You can sign in with an administrator account.
- The **Role-based Authorization Strategy** plugin is installed.

Plugin documentation: https://plugins.jenkins.io/role-strategy/

## Create the Users

1. Open **Manage Jenkins → Users**.
2. Select **Create User**.
3. Create an account with username `user1`.
4. Repeat and create an account with username `user2`.
5. Use test passwords. Do not include passwords in this README, screenshots, or GitHub.

## Enable Role-Based Authorization

1. Open **Manage Jenkins → Security**.
2. Under **Authorization**, select **Role-Based Strategy**.
3. Save the configuration.

Keep the current administrator session open while setting up and testing the new roles. Make sure at least one trusted administrator retains the `Overall → Administer` permission.

## Create the Roles

Open **Manage Jenkins → Role Management → Manage Roles**. Under **Global roles**, create these roles:

### admin

Grant:

- **Overall → Administer**

### read-only

Grant:

- **Overall → Read**
- **Job → Read**
- **View → Read**

Do not grant the `read-only` role permissions such as **Overall → Administer**, **Job → Configure**, or **Job → Build**.

Save the roles.

## Assign the Roles

Open **Manage Jenkins → Role Management → Assign Roles**:

- Assign `user1` to the `admin` role.
- Assign `user2` to the `read-only` role.
- Save the assignments.

The users must be separate Jenkins accounts. The username is the account ID; the Full Name field controls the name displayed in the interface.

## Verify

1. Open a private/incognito browser window and sign in as `user1`.
2. Confirm `user1` can access Jenkins administration.
3. Sign out, then sign in as `user2`.
4. Confirm `user2` can view Jenkins and jobs but cannot administer Jenkins or configure/build jobs.

Do not remove the current administrator's permissions until `user1` has been tested successfully.

## Evidence to Submit

Capture screenshots showing:

1. The Jenkins Users page with `user1` and `user2`.
2. The Manage Roles page showing permissions for `admin` and `read-only`.
3. The Assign Roles page showing `user1 → admin` and `user2 → read-only`.
4. Jenkins signed in as `user2`, showing read-only access.

<img width="1747" height="202" alt="image" src="https://github.com/user-attachments/assets/87052eef-d458-42d0-9001-dd0cdb2a68ab" />
<img width="727" height="737" alt="image" src="https://github.com/user-attachments/assets/a5672e24-85f2-41a8-a446-e7ea2cd1a2eb" />
<img width="727" height="737" alt="image" src="https://github.com/user-attachments/assets/000aa3e3-e501-46bc-8497-c34a04c4026f" />


