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

