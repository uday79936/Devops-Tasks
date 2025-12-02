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

## 1. EC2 Servers:

**Ansible-Master ec2:**

<img width="1918" height="922" alt="Image" src="https://github.com/user-attachments/assets/033bea88-a8ff-47f4-82a7-59589b09e351" />

**web ec2:**

<img width="1916" height="913" alt="Image" src="https://github.com/user-attachments/assets/bf234d18-aaba-4e54-9a4f-9c9a69bbfdfe" />

## 2. Change Hostname:

<img width="962" height="176" alt="Image" src="https://github.com/user-attachments/assets/b87f8908-b737-48c0-917c-589ca6cf3a81" />

## 3. Ansible Installation:

<img width="788" height="91" alt="Image" src="https://github.com/user-attachments/assets/1a99cee7-3e15-4603-a590-1042449f13cb" />

## 4. AWS install cli:

<img width="1382" height="177" alt="Image" src="https://github.com/user-attachments/assets/8a2a9fdd-9cfb-45b4-baff-30735d68129a" />

## 5. Install python3.12:

<img width="765" height="122" alt="Image" src="https://github.com/user-attachments/assets/9419e0b1-ed92-4db0-8156-e9fa879f5ece" />

## 6. python3 menv:

<img width="643" height="127" alt="Image" src="https://github.com/user-attachments/assets/198b1aab-1fc1-4171-882a-fe9452c1089d" />

## 7. python activate:

<img width="640" height="112" alt="Image" src="https://github.com/user-attachments/assets/d866fdd6-c859-4523-9605-9acbfafc2302" />

## 8. Boto3:

<img width="773" height="125" alt="Image" src="https://github.com/user-attachments/assets/40363c5f-464f-4070-9be1-73c0a15999eb" />

## 9. Ansible-galaxy-collection:

<img width="1047" height="142" alt="Image" src="https://github.com/user-attachments/assets/faf36663-f99e-4afe-b82a-347e07abb568" />

## 10. Ansible galaxy:

<img width="1488" height="152" alt="Image" src="https://github.com/user-attachments/assets/9639d281-4e46-47a0-9563-8a5985526e08" />

## 11. Ansible inventory list:

<img width="1912" height="1023" alt="Image" src="https://github.com/user-attachments/assets/96fea576-c405-4a3e-8366-dcee6401936d" />

## 12. Secure copy:

<img width="1917" height="272" alt="Image" src="https://github.com/user-attachments/assets/3a9691c0-a1b6-4d4b-ba59-c9dff7c75847" />

## 13. Pem file permissions:

<img width="951" height="808" alt="Image" src="https://github.com/user-attachments/assets/7b65632e-3a3f-40f3-bc3b-067e01ecb335" />

## 14. Successfully triggered:

<img width="1636" height="282" alt="Image" src="https://github.com/user-attachments/assets/a49cbe69-def1-455e-acf0-bd3abf8c6066" />

## 15. Role1 was created successfully:

<img width="1037" height="158" alt="Image" src="https://github.com/user-attachments/assets/a3253291-3983-4dcd-9535-234104be4d6f" />

## 16. Role1 folder:

<img width="1010" height="157" alt="Image" src="https://github.com/user-attachments/assets/b04ee818-5b32-4bc0-8c77-3b1267acb15f" />

## 17. Installer.yml:

<img width="992" height="531" alt="Image" src="https://github.com/user-attachments/assets/4c04190d-2266-4ac8-b309-e4eb0374e593" />

## 18. Config.yml

<img width="632" height="381" alt="Image" src="https://github.com/user-attachments/assets/2163c49d-666e-4db8-a8d4-251a225b397b" />

## 19. info.html

<img width="711" height="172" alt="Image" src="https://github.com/user-attachments/assets/74b0d3f7-3111-46c5-a0b4-84b72c318286" />

## 20. handlers.main.yml:

<img width="496" height="356" alt="Image" src="https://github.com/user-attachments/assets/61008fe1-1425-4f7c-974b-ce9a3bd855c7" />

## 21. Role1 project structure:

<img width="798" height="555" alt="Image" src="https://github.com/user-attachments/assets/23407525-58a7-41d3-a72a-c5699bf627fb" />

## 22. web-setup.yml:

<img width="443" height="257" alt="Image" src="https://github.com/user-attachments/assets/6330cb5b-51ec-42f1-956e-dc72b3ce9459" />

## 23. tasks.main.yml:

<img width="425" height="245" alt="Image" src="https://github.com/user-attachments/assets/a0284f8f-18ca-4c8d-8d31-44edb9d6177e" />

## 24. Executed yml:

<img width="1917" height="677" alt="Image" src="https://github.com/user-attachments/assets/8ac412b3-4e90-436e-beba-81b4b60749bc" />

## 25. Output:

<img width="1036" height="318" alt="Image" src="https://github.com/user-attachments/assets/6a8d90be-7e62-4631-a518-6f706bf64f53" />


## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
