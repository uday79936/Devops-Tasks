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

readlink -f $(which java)

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

## 1. EC2 Servers:

**Jenkins-server:**

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/2710adbb-bf11-4cb9-a45c-b76c268871b0" />

**Sonarqube-server:**

<img width="1918" height="921" alt="Image" src="https://github.com/user-attachments/assets/6611481e-70d2-44cf-bba0-e6c8b9832a3c" />

**Nexus-server:**

<img width="1920" height="967" alt="Image" src="https://github.com/user-attachments/assets/179f6e23-8534-474d-891c-eb9d4d218242" />

**Tomcat-Server:**

<img width="1919" height="972" alt="Image" src="https://github.com/user-attachments/assets/589b4347-2052-42f8-aa9e-ff59432759aa" />

## 2. Change Hostname for four servers:

**Jenkins-server:**

<img width="951" height="133" alt="Image" src="https://github.com/user-attachments/assets/58fe54bc-1083-4a52-8e1f-58122b02a74c" />

**sonar-server:**

<img width="959" height="190" alt="Image" src="https://github.com/user-attachments/assets/7362ee88-b961-41f8-8eff-fc8a3348f1fa" />

**Nexus-server:**

<img width="987" height="150" alt="Image" src="https://github.com/user-attachments/assets/194f5cb4-a78f-4ca6-925f-ce7b3f7abcdc" />

**Tomcat-server:**

<img width="1219" height="210" alt="Image" src="https://github.com/user-attachments/assets/f1bb3ad0-8dbb-4347-ad41-f5156fa43083" />

## 3. Java Version:


<img width="1379" height="220" alt="Image" src="https://github.com/user-attachments/assets/8f57828e-472e-44a7-9162-e0b0b24f3d16" />


## 4. Jenkins status:

<img width="1920" height="744" alt="Image" src="https://github.com/user-attachments/assets/4bc12f12-bc88-47be-aa4e-5f5271dc4f7b" />

## 5. Unlock jenkins:

<img width="1911" height="994" alt="Image" src="https://github.com/user-attachments/assets/858d9b42-10aa-46d3-8868-58524d3d3994" />

## 6. To see the jenkins password:

<img width="1149" height="146" alt="Image" src="https://github.com/user-attachments/assets/ea5f5f39-d01f-4558-aba2-8861e7adb706" />

## 7. Customize jenkins:

<img width="1920" height="1006" alt="Image" src="https://github.com/user-attachments/assets/a6adca8c-f687-4d53-8a6c-f7c294b2e16c" />

## 8. Getting started:

<img width="1920" height="1007" alt="Image" src="https://github.com/user-attachments/assets/83b54157-2b4f-421b-b345-8894d442bf65" />

## 9. Create first admin user:

<img width="1920" height="1009" alt="Image" src="https://github.com/user-attachments/assets/192e606b-f8db-41a2-a61e-deb0a811ba81" />

## 10. SSH KEYS:

**ssh-keygen**

<img width="1304" height="165" alt="Image" src="https://github.com/user-attachments/assets/b6f393ba-0efc-4b6d-bb01-c83a2b94eab1" />

**list the keys:**

<img width="1576" height="635" alt="Image" src="https://github.com/user-attachments/assets/dc337048-9b8d-4c15-8965-5981118e267e" />

## 11. Public key copied in authorized keys:

**sonar-key:**

<img width="1904" height="1018" alt="Image" src="https://github.com/user-attachments/assets/a7d08588-8bef-4fa1-ac7d-4d75b853a12e" />

**nexus-key:**

<img width="1910" height="344" alt="Image" src="https://github.com/user-attachments/assets/ea1c6499-c5f9-48b4-ad09-decd704e1598" />

**Tomcat-key:**

<img width="1907" height="402" alt="Image" src="https://github.com/user-attachments/assets/4ecd23c0-a3f7-4855-89ea-5c0b82ab593c" />

## 12. Java17, Java21 and maven via jenkins dashboard:

**java17 and java21:**

<img width="1898" height="1019" alt="Image" src="https://github.com/user-attachments/assets/b428a4fe-f3ea-4957-b3ec-1ddebdf6e8b3" />

**maven:**

<img width="1920" height="1023" alt="Image" src="https://github.com/user-attachments/assets/be3d4f15-73ea-4be5-8118-1fa3413c9feb" />

## 13. credientials:

<img width="1906" height="1036" alt="Image" src="https://github.com/user-attachments/assets/972bf059-cfb8-48c2-b549-01b50fccd6c2" />

## 14. sonarqube:

**sonarqube-dashboard:**

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/fc8771b1-bf8b-4d17-947f-f7f58cb5fb1d" />

**sonarqube-token:**

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/1e810c61-0c2b-4a8f-9215-d82a49fa2b83" />

## 15. Plugins:

**plugins**

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/f704fd1f-bc18-4b09-9844-805cb5fcba3a" />

**plugins-1**

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/6a8c6593-3588-44e8-aeda-505ffe0f1627" />

**plugins-2**

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/1a23e4d2-15dd-42c7-bd3e-1b0bd218f335" />

**Plugins-success:**


<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/a3dc5ef8-57f1-430c-9575-508fbe453247" />

## 16. Logs:

**Sonar-log output:**

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/fe88e3da-dbc4-46ed-b031-060a989b9165" />

**Nexus-log output:**

<img width="1920" height="955" alt="Image" src="https://github.com/user-attachments/assets/20c83c38-bcbc-4dff-b15f-127ddcaa0efc" />

**Tomcat-log output:**

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/d56a26e9-0b22-4db6-94fd-948e33b1ded1" />

## 17. Output:


<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/bc69ac35-8f75-4303-a984-94f621ab7f73" />




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
