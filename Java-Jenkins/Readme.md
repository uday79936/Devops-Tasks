## CI/CD Pipeline Using Jenkins and GitHub Webhooks:

## Overview:

This project demonstrates a complete CI/CD pipeline built using Jenkins. The pipeline automates the process of cloning source code, testing it, building it into deployable artifacts, and finally deploying it to the target environment.

## Source Code Repository:

This pipeline uses the following GitHub repository as the source:

Repository: 
```
https://github.com/KishanGollamudi/Java-Web-Calculator-App.git
```

## Pipeline Stages:

**Cloning Job**

Fetches the latest code from GitHub.

Test Job:


Executes automated tests to verify the integrity and functionality of the code.

## Build Job:


Builds the project and creates the output artifacts.

## Deploy Job

Deploys the final build to the target server/environment.


## Server Details:

The Jenkins server was hosted on an AWS EC2 instance (t2.micro). 1

Jenkins Installation Script
The following script was used to install Jenkins on an Ubuntu server: 3
```
#!/bin/bash

sudo apt update
sudo apt install fontconfig openjdk-21-jre -y

sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc]" \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
sudo apt update
sudo apt install jenkins

echo "---------------------------Jenkins has been installed ------------------------"
```

## Jenkins Setup Steps:

Installed Jenkins using a script.sh file.

Accessed Jenkins via 
```
http://<server-ip>:8080.
```

Retrieved Jenkins initial admin password from the system path.

Created admin credentials for Jenkins dashboard usage.

Created the four jobs listed above.

Installed the following plugins:

Build Pipeline Plugin
GitHub Plugin
GitHub Webhook Plugin

Configured pipeline view to visualize and trigger jobs sequentially.
GitHub Webhook Integration
Configured a webhook in GitHub to notify Jenkins upon every code push.
Jenkins automatically triggered the pipeline when changes were pushed.

## Pipeline Flow:
```
GitHub Commit → Clone Job → Test Job → Build Job → Deploy Job
```

## Images:


## Conclusion:

This CI/CD setup ensures automated, repeatable, and reliable software delivery triggered directly from source code changes. It helps maintain consistency, reduces manual interventions, and increases deployment efficiency.

## Author:

**Uday Sairam**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:**  https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/

