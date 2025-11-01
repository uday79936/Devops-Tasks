## 🚀 Jenkins Multi-Server CI/CD Infrastructure Setup (Java 17 + Maven + SonarQube + Nexus + Tomcat):

## 📘 Overview:


This documentation describes a complete multi-server CI/CD setup using Jenkins as the central automation tool.
It covers creating four EC2 servers, configuring SSH-based connections, installing Java & Maven, and integrating Jenkins with SonarQube, Nexus, and Tomcat for a full deployment pipeline.

## 🧱 Architecture Diagram:
```

                   +----------------+
                   |   Developer    |
                   | (GitHub Push)  |
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

**Servers Created**

Server Name	Purpose	Hostname

Jenkins	CI/CD Orchestrator	jenkins

SonarQube	Code Analysis	sonar

Nexus	Artifact Repository	nexus

Tomcat	Application Server	tomcat

## 🔐 Step 2: Set Hostnames:


**Run these commands on each server:**
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

## ☕ Step 3: Install Java 17 and Maven 3.9.11 (Auto Installed):


**Run on all four servers:**
```
sudo apt update -y
sudo apt install openjdk-17-jdk -y
java -version
```

**Maven 3.9.11 is installed automatically with Jenkins dependencies, or manually via:**
```
sudo apt install maven -y
mvn -version
```

## 📁 Step 4: Create Directory for Jenkins:


**On all servers:**
```
mkdir jenkins
```

## 🔑 Step 5: SSH Key-Based Authentication:

**On Jenkins Server:**

**Generate SSH key pair:**
```
ssh-keygen
```


Press Enter for default file path (~/.ssh/id_rsa).

**Copy Public Key to Other Servers:**

**Run from Jenkins server:**
```
ssh-copy-id ubuntu@<Sonar_Server_IP>
ssh-copy-id ubuntu@<Nexus_Server_IP>
ssh-copy-id ubuntu@<Tomcat_Server_IP>
```

## Now test the connection:
```
ssh ubuntu@<Sonar_Server_IP>
```

You should connect without a password.

## 🧩 Step 6: Jenkins Plugin Installation:

```
In Jenkins UI → Manage Jenkins → Plugins → Available Plugins, install:
```

## Plugin Name	Purpose:

Publish Over SSH	Deploy via SSH

GitHub Authentication	GitHub login integration

GitHub Integration	Connect repositories

Nexus Artifact Uploader	Upload builds to Nexus

SonarQube Scanner	Run SonarQube analysis

Sonar Quality Gates	Enforce code quality checks

SSH Agent Plugin	Use SSH credentials in pipelines

SSH Build Agents	Connect remote build nodes

After installation, restart Jenkins.


## 🧾 Step 7: Configure Jenkins Credentials:


**Navigate to:**
```
Jenkins → Manage Jenkins → Credentials → Global
```

**Add credentials for:**

SSH Key for Sonar, Nexus, Tomcat nodes


Type: SSH Username with Private Key

Username: ubuntu

Private Key: select Enter directly → paste content of ~/.ssh/id_rsa

GitHub Token

Type: Secret Text

Token from your GitHub account

Nexus Credentials

Type: Username & Password

For artifact upload

## 🖥️ Step 8: Configure Jenkins Nodes:

Add New Nodes (Sonar, Nexus, Tomcat)

**Path:**

```
Manage Jenkins → Nodes → New Node
```

**For each node:**

Name: sonar / nexus / tomcat

Type: Permanent Agent

Remote root directory: /home/ubuntu/jenkins

Launch method: Launch agents via SSH

Host: (Enter each server's private IP or hostname)

Credentials: Select SSH key created earlier

## Click Save and Launch:


**✅ Once configured, you should see:**

Agent successfully connected and online

## 🧠 Step 9: Verify Node Connections:


**Navigate to:**
```
Jenkins Dashboard → Manage Jenkins → Nodes
```

All nodes should show "Connected" status (green).

## 🧰 Step 10: Verification Summary:

Component	Status	       Verification Command

Java 17	        ✅	           java -version

Maven 3.9.11	✅	        mvn -version

Jenkins	        ✅	          sudo systemctl status jenkins

SonarQube Node	✅	    Connected to Jenkins

Nexus Node	    ✅	    Connected to Jenkins

Tomcat Node	    ✅	     Connected to Jenkins

## 🎯 Final Output:


All four servers configured with proper hostnames and SSH connectivity.

Jenkins can control Sonar, Nexus, and Tomcat nodes remotely.

Credentials securely managed inside Jenkins.

Ready for CI/CD pipeline integration with GitHub.

## Images:



## 📎 Next Steps:


You can now create a Jenkins pipeline that:

Clones code from GitHub.

Builds using Maven.

Runs static analysis on SonarQube.

Uploads artifacts to Nexus.

Deploys WAR file to Tomcat.

## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
