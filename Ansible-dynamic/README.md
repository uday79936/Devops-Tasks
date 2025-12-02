## Ansible Dynamic Inventory on AWS:

## Table of Contents:

Introduction

What is Dynamic Inventory?

Use-Case Example

Prerequisites

Installing Requirements

Creating the Dynamic Inventory File

Validating Dynamic Inventory

Fixing SSH Authentication

Determining the Correct SSH Username

Test SSH Connectivity Using Ansible

Running a Playbook Using Dynamic Inventory

Understanding AWS Dynamic Inventory Internals

Best Practices

Common Errors & Fixes

## Project Structure:
```
.
├── README.md
├── defaults
│   └── main.yml
├── files
│   └── info.html
├── handlers
│   └── main.yml
├── meta
│   └── main.yml
├── tasks
│   ├── config.yml
│   ├── installer.yml
│   └── main.yml
├── templates
├── tests
│   ├── inventory
│   └── test.yml
└── vars
    └── main.yml

```

## Summary:


## 1. Introduction:


Ansible requires an inventory to know which servers to manage.

Static inventory works for small, fixed environments.

In cloud environments like AWS, static inventory becomes impractical due to:

Dynamically created EC2 instances

Frequently changing IPs

Auto-scaling instances

Servers identified by tags

Manual updates are not feasible

Dynamic inventory solves this problem by automatically discovering servers via AWS APIs.

## 2. What is Dynamic Inventory?:


Dynamic Inventory enables Ansible to automatically fetch information about servers without manually updating inventory files.

It uses:

AWS tags

AWS metadata

AWS regions

Ansible can retrieve:

Public IP

Private IP

Instance ID

Tags (Name, Role, Application, etc.)

State (running/stopped)

This allows fully automated and scalable cloud-native deployments.

## 3. Use-Case Example:


Suppose you create an EC2 instance with:

Tag: Name = Role

Tag: Role = devops

Ansible can:

Automatically detect this instance

Group it under role_devops and name_centos

Connect over SSH

Run playbooks

All without manually editing inventory files.

## 4. Prerequisites:


On your Ansible Master (Ubuntu Server):

Python 3

Virtual environment support

boto3 & botocore

Ansible AWS collection

AWS CLI credentials

SSH private key (~/.ssh/pro.pem)

## 5. Installing Requirements (Ubuntu 22.04 / 24.04):


Ubuntu now restricts global pip installs (PEP 668), so we use a virtual environment.

**Step 1 — Install Python venv:**
```
sudo apt update
sudo apt install python3.12-venv -y
```


**Step 2 — Create Virtual Environment:**
```
python3 -m venv myenv
source myenv/bin/activate
```

**Step 3 — Install AWS Python SDK:**
```
pip install boto3 botocore
```


**Step 4 — Install Ansible AWS Collection:**
```
ansible-galaxy collection install amazon.aws
```

**Step 5 — Configure AWS CLI:**
```
aws configure
```
Provide:

AWS Access Key

AWS Secret Key

Default region (e.g., us-east-2)

Output format: json

## 6. Creating the Dynamic Inventory File:


Create /etc/ansible/aws_ec2.yml with the following configuration:
```
plugin: amazon.aws.aws_ec2

regions:
  - us-east-2

filters:
  tag:Name: Role
  tag:Role: devops

hostnames:
  - ip-address  # Use public IP

keyed_groups:
  - key: tags.Role
    prefix: role
  - key: tags.Name
    prefix: name

```

Note: Use ip-address to fetch the public IP for connecting via SSH.

## 7. Validating Dynamic Inventory:
```
ansible-inventory -i /etc/ansible/aws_ec2.yml --list
```

**Expected output:**

```
"hosts": ["54.91.47.139"]

```
This confirms dynamic inventory is working.

## 8. Fixing SSH Authentication:


If you see:

no such identity:
```
 /home/ubuntu/.ssh/pro.pem
```

It means Ansible is looking in the wrong folder. Use:
```
--private-key ~/pro.pem
```
## 9. Determining the Correct SSH Username:


**Check your AMI:**
```
AMI: ami-028bd5cea26d40561 → Ubuntu 22.04 LTS

SSH User: ubuntu
```
Not centos.

## 10. Test SSH Connectivity Using Ansible:
```
ansible -i /etc/ansible/aws_ec2.yml all -m ping -u ubuntu --private-key ~/pro.pem
```

**Expected output:**
```
54.91.47.139 | SUCCESS => {
    "changed": false,
    "ping": "pong"
}
```

## 11. Running a Playbook Using Dynamic Inventory:


Create web-setup.yml:

- hosts: all
  become: yes
  roles:
    - role1


**Run:**
```
   ansible-playbook web-setup.yml -i /etc/ansible/aws_ec2.yml -u ubuntu --private-key ~/pro.pem
```


## 12. Understanding AWS Dynamic Inventory Internals:


Plugin connects to AWS using boto3.

AWS returns all EC2 instances.

Ansible filters instances by tags (e.g., Name, Role).

Ansible fetches IP addresses using ip-address.

Instances are grouped (e.g., role_devops, name_centos).

Playbooks run on discovered instances.

No manual host updates are needed.

## 13. Best Practices:


✅ Use IAM roles for EC2 Ansible Masters.

✅ Never hardcode AWS credentials in playbooks.

✅ Secure your PEM file:

chmod 400 ~/pro.pem


## ✅ Implement a tagging strategy:

Environment=ansible
Application=web
Role=devops

## 14. Common Errors & Fixes:

Error	Cause	Fix

public_ip host shown	Wrong hostname field	Use ip-address

Permission denied	Wrong user	Use ubuntu

key not found	Wrong path	Use ~/worklab.pem

PEP 668 pip error	Ubuntu restriction	Use virtual 

environment (python3 -m venv)

Inventory empty	Wrong tags/region	Verify AWS tags

unreachable	Security group restriction	Allow SSH from Ansible master IP

## 15. Summary:


**You have successfully completed:**

Python virtual environment setup

boto3 & AWS plugin installation

AWS credential configuration

Dynamic inventory creation

Correct hostname mapping (ip-address)

SSH authentication fix

Successful Ansible ping

Running playbooks with dynamic inventory

Your Ansible environment is now cloud-ready and fully scalable.

## Images:



## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/