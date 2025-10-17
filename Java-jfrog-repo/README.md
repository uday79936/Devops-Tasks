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



## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-Url:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
