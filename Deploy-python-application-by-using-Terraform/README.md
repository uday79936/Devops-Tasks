## 📘 Terraform – Deploy Python (Flask) Application on AWS EC2:

## 📌 Overview:


This project demonstrates how to provision AWS infrastructure and deploy a Python (Flask) application automatically using Terraform.

## Terraform is used to:

Create an EC2 instance

Configure networking and security

Install required dependencies

Deploy and run a Python Flask application automatically at instance startup

This approach follows Infrastructure as Code (IaC) best practices.

## 🛠️ Technologies Used:


Terraform – Infrastructure provisioning

AWS EC2 – Compute service

AWS Security Groups – Network access control

Python 3

Flask – Web framework

systemd – Service management on Linux

## 📂 Project Structure:
```
terraform-python-app/
│
├── app.py              # Python Flask application
├── main.tf             # Terraform configuration
├── variables.tf        # Terraform variables
├── terraform.tfvars    # Variable values (not committed)
├── README.md           # Documentation
```

## 🚀 Architecture Flow:


Terraform connects to AWS

A Security Group is created

An EC2 instance is launched

Python and Flask are installed

Flask application is copied and configured

Flask app runs as a system service

Application becomes accessible via public IP

## 🔐 Security Group Configuration:


The following inbound rules are configured:

Port	Protocol	Purpose

22	     TCP	          SSH access

5000	 TCP	        Flask application

Outbound traffic is fully allowed.

## 🧩 Flask Application (app.py):
```
from flask import Flask

app = Flask(__name__)

@app.route("/")
def hello():
    return "Flask app deployed using Terraform!"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
```

Binds to 0.0.0.0 to allow external access

Runs on port 5000

## 🏗️ Terraform Configuration (main.tf):

Key Responsibilities

Configure AWS provider

Create Security Group

Launch EC2 instance

Bootstrap the server using user_data

Configure a systemd service to keep Flask running

## Highlights:


Infrastructure creation and application deployment happen in one step

Flask application auto-starts on reboot

No manual SSH required after deployment

## 🔁 Application Startup (systemd):


**The Flask app is registered as a Linux service:**
```
[Unit]
Description=Flask Application
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/python3 /home/ec2-user/app.py
Restart=always

[Install]
WantedBy=multi-user.target
```

## Benefits:

Automatic restart on failure

Runs after system reboot

Production-ready process management

## ⚙️ Variables Configuration:

**variables.tf**
```
variable "region" {}
variable "access_key" {}
variable "secret_key" {}
variable "ami_id" {}
variable "key_name" {}
```

**terraform.tfvars (example – DO NOT COMMIT)**
```
region     = "us-east-2"
ami_id     = "ami-0abcdef1234567890"
key_name  = "my-keypair"
access_key = "YOUR_ACCESS_KEY"
secret_key = "YOUR_SECRET_KEY"
```

## ▶️ How to Deploy:

**1️⃣ Initialize Terraform:**
```
terraform init
```

**2️⃣ Validate Configuration:**
```
terraform validate
```

**3️⃣ Apply Infrastructure:**
```
terraform apply
```


Confirm with yes.

## 🌍 Access the Application:


After deployment completes, Terraform outputs the public IP.

## Open in browser:
```
http://<EC2_PUBLIC_IP>:5000
```

## 🧹 Destroy Infrastructure:


**To remove all created resources:**
```

terraform destroy
```

## ✅ Best Practices Followed:


Infrastructure as Code (IaC)

Immutable server setup

Automated provisioning

Least manual intervention

Reproducible deployments

## 📈 Possible Enhancements:


Run Flask using Gunicorn

Add Nginx as reverse proxy

Use Application Load Balancer

Enable HTTPS (ACM + ALB)

Dockerize the application

Store secrets in AWS Secrets Manager

CI/CD integration with GitHub Actions

## 👤 Author:

**Uday SaiRam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:**  https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
