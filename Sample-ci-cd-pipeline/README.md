## 📘Jenkins Multi-Server CI/CD Infrastructure Setup:

```
(Java 17 + Maven 3.9.11 + SonarQube + Nexus + Tomcat)
```

## 🚀 Overview:


This documentation describes the complete multi-server CI/CD infrastructure using Jenkins as the central automation server.
It automates source code building, code quality analysis, artifact storage, and deployment using GitHub → Jenkins → SonarQube → Nexus → Tomcat integration.

## 🧱 Architecture Diagram:
```
                   +----------------+
                   |   Developer    |
                   |  (GitHub Push) |
                   +-------+--------+
                           |
                           v
                   +----------------+
                   |   Jenkins CI   |  (Main Server)
                   |  Java + Maven  |
                   +----------------+
                    /      |       \
                   /       |        \
         SSH to -> /        |         \ -> SSH to
   +---------------+  +---------------+  +---------------+
   |   SonarQube   |  |    Nexus      |  |    Tomcat     |
   | Code Quality  |  | Artifact Repo |  | App Deployment|
   +---------------+  +---------------+  +---------------+
```

## ⚙️ Step 1: Server Setup:

Server Name	Purpose	Hostname

Jenkins	CI/CD Orchestrator	jenkins

SonarQube	Code Analysis	sonar

Nexus	Artifact Repository	nexus

Tomcat	Application Server	tomcat

## 🔐 Step 2: Set Hostnames:


Run the following commands on each respective EC2 instance:
```
sudo hostnamectl set-hostname jenkins
sudo hostnamectl set-hostname sonar
sudo hostnamectl set-hostname nexus
sudo hostnamectl set-hostname tomcat
```


**Then restart each instance:**
```
sudo init 6
```

## ☕ Step 3: Install Java 17 & Maven 3.9.11:


**Run these commands on all four servers:**
```
sudo apt update -y
sudo apt install openjdk-17-jdk -y
java -version

readlink -f $(which java)

```

**Install Maven if not already installed:**
```
sudo apt install maven -y
mvn -version
```

## 📁 Step 4: Create Directory for Jenkins:


**Create a working directory for Jenkins on all servers:**
```
mkdir jenkins
```

## 🔑 Step 5: Configure SSH Key-Based Authentication:


**Generate SSH Keys on Jenkins Server:**
```
ssh-keygen
```


(Press Enter for all prompts to use defaults.)

Copy Public Key to other servers:
```
ssh-copy-id ubuntu@<Sonar_Server_IP>
ssh-copy-id ubuntu@<Nexus_Server_IP>
ssh-copy-id ubuntu@<Tomcat_Server_IP>
```


**Verify connection:**
```
ssh ubuntu@<Sonar_Server_IP>
```


✅ You should connect without a password.

## 🧩 Step 6: Install Jenkins Plugins:


**Navigate to:**
```
Manage Jenkins → Plugins → Available Plugins
```

**Install these essential plugins:**

Plugin Name	Purpose

Publish Over SSH	Deploy via SSH

GitHub Integration	Connect Jenkins with GitHub

GitHub Authentication	GitHub login integration

Nexus Artifact Uploader	Upload build artifacts

SonarQube Scanner	Run SonarQube analysis

Sonar Quality Gates	Enforce code quality

SSH Agent Plugin	Use SSH credentials in pipelines

SSH Build Agents	Connect remote build nodes

After installation → Restart Jenkins.

## 🧾 Step 7: Configure Jenkins Credentials:


**Path:**
```
Jenkins → Manage Jenkins → Credentials → Global
```

**Add the following credentials:**

Credential Type	ID/Name	Description

SSH Username with Private Key	jenkins_ssh_key	For SSH connection to Sonar, Nexus, Tomcat

Secret Text	github_token	GitHub personal access token

Username & Password	nexus_credentials	Nexus repository 
credentials

## 🖥️ Step 8: Configure Jenkins Nodes:


**Path:**
```
Manage Jenkins → Nodes → New Node
```

**For each agent:**

Node	Type	Directory	Host	Label

Sonar	Permanent Agent	/home/ubuntu/jenkins	<Sonar_IP>	sonar

Nexus	Permanent Agent	/home/ubuntu/jenkins	<Nexus_IP>	nexus

Tomcat	Permanent Agent	/home/ubuntu/jenkins	<Tomcat_IP>	tomcat


**Launch method:**

➡️ Launch agents via SSH
➡️ Use SSH credentials (jenkins_ssh_key)

✅ Once connected → You’ll see each agent Online with a green indicator.

## 🧠 Step 9: Verify Node Connections:


**Navigate to:**
```
Jenkins Dashboard → Manage Jenkins → Nodes
```

## All agents (sonar, nexus, tomcat) should show status:

**🟢 Connected**

## 🧰 Step 10: Verification Summary:

Component	Status	Verification Command

Java 17	✅	java -version

Maven 3.9.11	✅	mvn -version

Jenkins	✅	sudo systemctl status jenkins

SonarQube Node	✅	Connected to Jenkins

Nexus Node	✅	Connected to Jenkins

Tomcat Node	✅	Connected to Jenkins

🧩 Jenkins Pipeline as Code (Jenkinsfile)


## Create a file in your GitHub repo named Jenkinsfile with the following content:
```
pipeline {
    agent none

    stages {
        stage('Sonar Stage') {
            agent { label 'sonar' }
            steps {
                script {
                    echo "=== Running on SonarQube Agent ==="
                    sh '''
                        echo "Current User:"
                        whoami
                        echo "Host IP:"
                        hostname -i
                    '''
                }
            }
        }

        stage('Nexus Stage') {
            agent { label 'nexus' }
            steps {
                script {
                    echo "=== Running on Nexus Agent ==="
                    sh '''
                        echo "Current User:"
                        whoami
                        echo "Host IP:"
                        hostname -i
                    '''
                }
            }
        }

        stage('Tomcat Stage') {
            agent { label 'tomcat' }
            steps {
                script {
                    echo "=== Running on Tomcat Agent ==="
                    sh '''
                        echo "Current User:"
                        whoami
                        echo "Host IP:"
                        hostname -i
                    '''
                }
            }
        }
    }
}
```

## ✅ What This Pipeline Does:

Stage 1: Sonar Stage → Executes on Sonar node; prints user and IP.

Stage 2: Nexus Stage → Executes on Nexus node; prints user and IP.

Stage 3: Tomcat Stage → Executes on Tomcat node; prints user and IP.

This verifies that all remote agents are accessible and Jenkins can execute commands on each.

## Images:

# 1. Change Hostname:

**jenkins:**

<img width="951" height="133" alt="Image" src="https://github.com/user-attachments/assets/2edf80e8-bde4-4cdb-a939-7a5a59b24416" />

**sonar:**

<img width="959" height="190" alt="Image" src="https://github.com/user-attachments/assets/61bc1a73-66f9-4d97-b35e-59b73a6eb65a" />

**Nexus:**

<img width="987" height="150" alt="Image" src="https://github.com/user-attachments/assets/bd8c26bb-d9d2-45c8-a659-6aed6a6fa6d1" />

**Tomcat:**

<img width="1219" height="210" alt="Image" src="https://github.com/user-attachments/assets/b4980e88-5898-4091-8c8a-cd7c0b49f9ac" />

## 2. jenkins:

**Jenkins status:**

<img width="1920" height="744" alt="Image" src="https://github.com/user-attachments/assets/50f7e206-e13c-4e6e-9790-3e4df41111ec" />

**Unlock Jenkins:**

<img width="1911" height="994" alt="Image" src="https://github.com/user-attachments/assets/5e9298ee-0434-4b25-801e-cff65da4f209" />

**To see the Jenkins Password:**

<img width="1149" height="146" alt="Image" src="https://github.com/user-attachments/assets/26c1b4a7-fb30-4290-a521-8102d79287e9" />

**Customize jenkins:**

<img width="1920" height="1006" alt="Image" src="https://github.com/user-attachments/assets/56d23ced-1ec5-40a9-b08f-5c8e64b21249" />

**Getting started:**

<img width="1920" height="1007" alt="Image" src="https://github.com/user-attachments/assets/2c5f0678-e23e-4aea-9821-7097b09dfaee" />

**Create First admin User:**

<img width="1920" height="1009" alt="Image" src="https://github.com/user-attachments/assets/504e794b-658d-4ca1-a6fe-abdda7bfc97c" />


# 3. ssh keys generated:

<img width="1304" height="165" alt="Image" src="https://github.com/user-attachments/assets/c79fc7bb-04e1-40da-977b-a7c988e906cf" />

<img width="1576" height="635" alt="Image" src="https://github.com/user-attachments/assets/959d8ad5-2f2f-46bc-8705-a93fffbe1518" />

## 4. Copy the public key in servers are sonar->nexus->tomcat:

**sonar:**

<img width="1904" height="1018" alt="Image" src="https://github.com/user-attachments/assets/73d40fb8-845f-4c37-8d8a-baf4abd48246" />

**Nexus:**

<img width="1910" height="344" alt="Image" src="https://github.com/user-attachments/assets/fb11fbf9-687f-4667-8660-9669f0718a76" />

**Tomcat:**

<img width="1907" height="402" alt="Image" src="https://github.com/user-attachments/assets/872c855e-bc72-4f74-bfc1-54a05cd83d03" />


## 5. Plugins installed:

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/5d0a06a4-a2be-4c57-acc1-af5274e59cb6" />

## 6. Nodes configuration Output:

<img width="1893" height="883" alt="Image" src="https://github.com/user-attachments/assets/65578b70-f6ca-42e3-8540-fdcaf8b8832c" />

## 7. log outputs:

**sonar log output:**


<img width="1911" height="1001" alt="Image" src="https://github.com/user-attachments/assets/93b6aef1-a434-4518-865c-6ad1c60414ed" />

**Nexus log output:**


<img width="1909" height="1014" alt="Image" src="https://github.com/user-attachments/assets/2f43f961-b00f-4896-b5f0-50edbcf7142c" />

**Tomcat Log output:**

<img width="1883" height="1018" alt="Image" src="https://github.com/user-attachments/assets/6f6032f6-3bfa-414b-ad91-043d18b663c6" />

## 7. Pipeline Console Outputs:

**sonar pipeline console output:**

<img width="1903" height="1014" alt="Image" src="https://github.com/user-attachments/assets/cf3498c8-f138-4b16-be48-8708a7646f0e" />


**Nexus pipeline console output:**

<img width="1920" height="984" alt="Image" src="https://github.com/user-attachments/assets/181e6ce7-e319-476f-8c74-5b859e0085b0" />

**Tomcat pipeline console output:**

<img width="1904" height="1000" alt="Image" src="https://github.com/user-attachments/assets/6684c56a-ffcf-4fe7-9b77-c172859222c0" />

## 🏁 Final Output:


✅ 4 servers fully connected via SSH

✅ Jenkins controlling Sonar, Nexus, and Tomcat remotely

✅ Verified Java, Maven, and agent connectivity

✅ Ready for CI/CD pipeline with GitHub integration

## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/

