# 🚀 Multi-Server Java Application Build, Store, and Deployment System

## 📘 Overview
This project demonstrates how to configure a multi-server environment to **build, store, and deploy a Java web application** using **JFrog Artifactory**.

The environment consists of three servers:

- **Java Build Server** – Builds the project and uploads the artifact to JFrog Artifactory.
- **JFrog Artifactory Server** – Hosts the Maven repository where artifacts are stored.
- **Deploy Server** – Pulls artifacts from JFrog and deploys them on Apache Tomcat.

### 🏗️ Architecture Diagram:
```
GitHub Repository

│
▼
Java Build Server (Maven Build)
│ mvn deploy
▼
JFrog Artifactory Server
│ wget / mvn download
▼
Deploy Server (Tomcat)
│
▼
Web Browser → http://<Deploy_Server_IP>:8080/JavaWebCalculator
```

## ⚙️ Prerequisites:

Before starting, ensure you have:

- Three Ubuntu EC2 Instances (or Linux VMs)
  - Java Build Server
  - JFrog Artifactory Server
  - Deploy Server
- **Java 17** or higher
- **Apache Maven**
- **Apache Tomcat 9/10**
- **Git**
- Proper network connectivity between servers  
  (Allow inbound ports **8081** for Artifactory and **8080** for Tomcat)

---

## 1️⃣ Java Build Server Setup:


### Step 1: Connect to Build Server:

```
ssh -i "Java_build.pem" ubuntu@<Build_Server_IP>
```
**Step 2: Install Java 17, Maven, Git:**

```
sudo apt update
sudo apt install openjdk-17-jdk maven git -y
java -version
mvn -version
```
**Step 3: Clone the GitHub Repository:**
```
git clone https://github.com/mrtechreddy/Java-Web-Calculator-App.git

cd Java-Web-Calculator-App
```
**Step 4: Build the Project:**

**mvn clean install**

**✅ This generates a .war file in the target/ directory.**

## Step 5: Configure Maven for JFrog Deployment:

**Edit Maven settings.xml:**

```
sudo vi /etc/maven/settings.xml
```
**Add:**

```
<servers>
  <server>
    <id>sample</id>
    <username>admin</username>
    <password>Admin123</password>
  </server>
</servers>
```
**Step 6: Configure pom.xml for JFrog Repository:**

Edit your project’s pom.xml:

```
<distributionManagement>
  <repository>
    <id>sample</id>
    <url>http://<ARTIFACTORY_SERVER_IP>:8082/artifactory/libs-release-local</url>
  </repository>
</distributionManagement>
```
**Step 7: Deploy Artifact to JFrog:**
```
mvn deploy
```
## ✅ Expected Result: .war uploaded to JFrog at http://<ARTIFACTORY_SERVER_IP>:8082/

## 2️⃣ JFrog Artifactory Server Setup:

**Step 1: Connect to Artifactory Server:**
```
ssh -i "jfrog.pem" ubuntu@<Artifactory_Server_IP>
```
**Step 2: Install Java 17:**
```
sudo apt update
sudo apt install openjdk-17-jdk -y
```
**Step 3: Install JFrog Artifactory:**
```
wget https://releases.jfrog.io/artifactory/artifactory-pro/org/artifactory/pro/jfrog-artifactory-pro/7.77.6/jfrog-artifactory-pro-7.77.6-linux.tar.gz
tar -xvzf jfrog-artifactory-pro-7.77.6-linux.tar.gz
cd artifactory-*/app/bin
```
**Step 4: Start Artifactory**
```
./artifactory.sh start
```
**Step 5: Access JFrog Web UI:**

**Open browser:**
```
http://<ARTIFACTORY_SERVER_IP>:8081/
```
**Retrieve admin password:**
```
admin: admin password: password
```
**Step 6: Configure Repositories:**

Login as admin

Create a local Maven repository

Name: libs-release-local

Type: hosted

Version Policy: Release

Optionally, create libs-snapshot-local

**Step 7: Verify Upload from Build Server:**

Go to Artifacts → libs-release-local ✅ Confirm .war file is visible

## 3️⃣ Deploy Server Setup (Apache Tomcat):

**Step 1: Connect to Deploy Server:**

```
ssh -i "deploy.pem" ubuntu@<Deploy_Server_IP>
```
**Step 2: Install Java 17 and Tomcat:**

```
sudo apt update
sudo apt install openjdk-17-jdk -y
wget https://downloads.apache.org/tomcat/tomcat-10/v10.1.30/bin/apache-tomcat-10.1.30.tar.gz
tar -xvzf apache-tomcat-10.1.30.tar.gz
```
**Step 3: Start Tomcat:**
```
Access: http://<Deploy_Server_IP>:8080/
```
**Step 4: Download Artifact from JFrog:**

```
mkdir ~/deployments
cd ~/deployments
wget --user admin --password admin123 \
http://<ARTIFACTORY_SERVER_IP>:8081/artifactory/libs-release-local/com/example/webapp/1.0/webapp-1.0.war
```
**Step 5: Deploy to Tomcat:**
```
sudo cp webapp-1.0.war /apache-tomcat/webapps/
cd /apache-tomcat/bin
./shutdown.sh
./startup.sh
```
## ✅ Check deployment:
```
http://<Deploy_Server_IP>:8080/webapp-1.0/
```
## 4️⃣ End-to-End Testing:

Step	Description	Expected Result

1	Run mvn clean install & mvn deploy on Build Server	WAR generated & uploaded to JFrog

2	Verify in JFrog	Artifact visible in libs-release-local

3	Download WAR on Deploy Server	WAR fetched successfully

4	Copy to Tomcat & Start	App runs successfully

5	Access in browser	http://<Deploy_Server_IP>:8080/JavaWebCalculator works

## 🧰 Common Issues & Fixes:

Issue	Cause	Fix

mvn deploy fails	Wrong credentials in settings.xml	Check 
Artifactory username/password

JFrog not accessible	Port 8081 blocked	Allow inbound rule in AWS security group

Tomcat not starting	Permissions issue	Run startup script as sudo
WAR not deployed	Wrong file path	Ensure WAR is inside /apache-tomcat/webapps

## Images:

## Instances

**Java-build-Deploy-server:**

<img width="1918" height="920" alt="Image" src="https://github.com/user-attachments/assets/ed9e1220-837d-4655-b66e-a724c15e6985" />

**Jfrog-server:**

<img width="1913" height="902" alt="Image" src="https://github.com/user-attachments/assets/6b1aecf0-f373-438b-87f4-76e4af6c0529" />

## build-deploy-server:

## Clone the Repository:

<img width="1368" height="268" alt="Image" src="https://github.com/user-attachments/assets/ce3f2985-561f-472e-9557-2bb2ca20a5b1" />

## Java 17 Version:

<img width="1377" height="247" alt="Image" src="https://github.com/user-attachments/assets/bc42c030-0fbf-4def-b383-5f0e6b6daf7a" />

## Maven validate:

<img width="1096" height="431" alt="Image" src="https://github.com/user-attachments/assets/f4f3d961-4ddf-4c7c-a613-d753535bebf2" />

## Maven Package:

<img width="1916" height="617" alt="Image" src="https://github.com/user-attachments/assets/c9fc0dca-bfae-40e3-80b1-b48df6ec9d2e" />

## Download the jfrog-artifactory:

<img width="1898" height="655" alt="Image" src="https://github.com/user-attachments/assets/1a4a670d-8c04-409b-8ce3-15f540dd68e7" />

## fix broken:

<img width="1632" height="720" alt="Image" src="https://github.com/user-attachments/assets/18094274-eba2-464e-85c4-538adcf45c8e" />

## Status active:

<img width="1897" height="602" alt="Image" src="https://github.com/user-attachments/assets/7e100ce6-e19e-4123-a263-6b5a0d030396" />

## Login Page:

<img width="1911" height="950" alt="Image" src="https://github.com/user-attachments/assets/8094fd37-5bf2-472a-a641-c4bd860e2374" />

## To store artifact in jfrog:

<img width="1878" height="625" alt="Image" src="https://github.com/user-attachments/assets/288e8e8f-0e60-4866-a40e-06eff02b890e" />

## artifact in jfrog:

<img width="1918" height="912" alt="Image" src="https://github.com/user-attachments/assets/6ffcd66a-adbc-4729-bad9-a5f5af97ff9f" />

## final version:

<img width="1917" height="972" alt="Image" src="https://github.com/user-attachments/assets/8cc36292-3a9a-473e-a742-1e72c2fb0380" />

## Path:

<img width="1918" height="967" alt="Image" src="https://github.com/user-attachments/assets/6d55435b-6db5-4129-97cb-20b4d2a1e89f" />

## To download the tomcat:

<img width="1902" height="385" alt="Image" src="https://github.com/user-attachments/assets/80e363a7-99f5-426d-94dc-3056f4788efd" />

## Extract the tomcat:

<img width="1112" height="186" alt="Image" src="https://github.com/user-attachments/assets/c7cdf5d2-ef65-4f34-aee0-e9d6c2c8e4cc" />



## To download the war file in tomcat:

<img width="1916" height="176" alt="Image" src="https://github.com/user-attachments/assets/edd5dce8-b24d-45d0-b2b6-482974a68103" />

## Successfully upload to deploy to tomcat:

<img width="1917" height="427" alt="Image" src="https://github.com/user-attachments/assets/7bfbc0f5-f5e1-4397-bcd8-187b032aaf64" />

## War file in webapps:

<img width="1177" height="142" alt="Image" src="https://github.com/user-attachments/assets/485c40c2-7ce9-4c60-972b-6784c3d003c6" />

## Successfully deployed:

<img width="1908" height="967" alt="Image" src="https://github.com/user-attachments/assets/7e22a6ff-9a8b-46cd-919f-911cb1aff522" />

## Output:

<img width="1910" height="747" alt="Image" src="https://github.com/user-attachments/assets/156c147b-2758-48dc-a8a3-dac2cec440ea" />



## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-Url:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
