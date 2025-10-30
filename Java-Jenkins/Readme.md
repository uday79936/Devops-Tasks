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

## 1. Java jenkins ec2:

<img width="1916" height="906" alt="Image" src="https://github.com/user-attachments/assets/5bfcdb59-ecd0-4dd6-bbe2-f163bb27ac41" />

## 2. Get started jenkins:

<img width="1917" height="981" alt="Image" src="https://github.com/user-attachments/assets/b7242964-0dac-4f48-bd3f-98e4ee1e0074" />

## 3. Jenkins password:

<img width="1023" height="127" alt="Image" src="https://github.com/user-attachments/assets/6aa048ff-31a9-44bd-aaf8-808bf3063359" />

## 4. cloned job:

<img width="1918" height="960" alt="Image" src="https://github.com/user-attachments/assets/d4184a8a-74d2-4705-964c-386b391ce166" />

<img width="1910" height="981" alt="Image" src="https://github.com/user-attachments/assets/01d7c612-bf5d-4ecc-956f-349408c012e1" />

## 5. Build job:

<img width="1913" height="973" alt="Image" src="https://github.com/user-attachments/assets/ec4e6445-e49a-4dda-b258-fd210688a8fd" />

## 7. Test Job:

<img width="1918" height="977" alt="Image" src="https://github.com/user-attachments/assets/1b56f965-0fa0-4ba6-bf06-77167215c4ca" />

<img width="1917" height="977" alt="Image" src="https://github.com/user-attachments/assets/4a5f0a75-3964-40bf-997d-2cc90c83f9e1" />

## 8. Plugins:

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/16a67092-421c-4a88-a4a0-7527502e9e06" />

<img width="1820" height="892" alt="Image" src="https://github.com/user-attachments/assets/91a9290c-d981-4962-b458-a4ebe2bec4e8" />

## github plugins and webhooks:

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/4778c992-e945-474a-a073-b1cee0b3eb0d" />

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/06423bd4-c6ee-4789-aeea-dea37edcd692" />

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/b67aad33-949d-441d-ac69-e08b5439ea14" />

## Conclusion:

This CI/CD setup ensures automated, repeatable, and reliable software delivery triggered directly from source code changes. It helps maintain consistency, reduces manual interventions, and increases deployment efficiency.

## Author:

**Uday Sairam**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:**  https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/

