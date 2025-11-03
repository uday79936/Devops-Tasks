## 🚀 Multi-Server Java Application Build, Store, and Deployment System:

## 📘 Overview:


This project demonstrates how to configure a multi-server environment to build, store, and deploy a Java web application.

**The environment consists of three servers:**

Java Build Server – Builds the project and uploads the artifact to Nexus.

Nexus Repository Server – Hosts the Maven repository where artifacts are stored.

Deploy Server – Pulls artifacts from Nexus and deploys them on Apache Tomcat.

## 🏗️ Architecture Diagram:
```
GitHub Repository

       │
       ▼
Java Build Server (Maven Build)

       │ mvn deploy
       ▼
Nexus Repository Server
       │ wget / mvn download
       ▼
Deploy Server (Tomcat)
       │
       ▼
Web Browser → http://<Deploy_Server_IP>:8080/JavaWebCalculator
```
## ⚙️ Prerequisites:


Before starting, ensure you have:

Three Ubuntu EC2 Instances (or any Linux VMs)

Java Build Server

Nexus Repository Server

Deploy Server

Java 17 or higher

Apache Maven

Apache Tomcat 9/10

Git installed

Proper network connectivity between all three servers (allow inbound ports 8081 for Nexus and 8080 for Tomcat).

## 1️⃣ Java Build Server Setup:

**Step 1: Connect to Build Server:**
```
ssh -i "Java_build.pem" ubuntu@<Build_Server_IP>
```

**Step 2: Install Java 17 and Maven:**
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

mvn clean install


## ✅ This will generate a .war file in the target/ directory.:


## Step 5: Configure Maven for Nexus Deployment:

**Edit Maven’s settings file:**
```
sudo vi /usr/share/maven/conf/settings.xml
```

## Add the following inside <servers>:
```
<server>
  <id>nexus-releases</id>
  <username>admin</username>
  <password>admin123</password>
</server>
```

## Step 6: Configure pom.xml for Nexus Repository:


**Edit your project’s pom.xml:**
```
<distributionManagement>
  <repository>
    <id>nexus-releases</id>
    <url>http://<NEXUS_SERVER_IP>:8081/repository/maven-releases/</url>
  </repository>
</distributionManagement>
```

**Step 7: Deploy Artifact to Nexus:**
```
mvn deploy
```

## ✅ Expected Result:

**.war file uploaded to Nexus at**
```
http://<NEXUS_SERVER_IP>:8081/.
```
## 2️⃣ Nexus Repository Server Setup:

**Step 1: Connect to Nexus Server**
```
ssh -i "nexus.pem" ubuntu@<Nexus_Server_IP>
```

**Step 2: Install Java 17:**
```
sudo apt update
sudo apt install openjdk-17-jdk -y
```

**Step 3: Install Nexus Repository Manager:**
```
sudo wget https://download.sonatype.com/nexus/3/nexus-3.85.0-03-linux-x86_64.tar.gz
sudo tar -xvzf latest-unix.tar.gz
```

**Step 4: Run Nexus as a Service:**
```
./ nexus start
```
**Step 5: Access Nexus Web UI:**

**Open browser:**
```
http://<NEXUS_SERVER_IP>:8081/
```

## Retrieve admin password:
```
sudo cat /opt/sonatype-work/nexus3/admin.password
```

## Step 6: Configure Repositories:


Login as admin

Create a new hosted Maven repository:

Name: maven-releases

Type: hosted

Version Policy: Release

Optionally, create maven-snapshots.

## Step 7: Verify Upload from Build Server:


Go to Browse → maven-releases
✅ Confirm that your .war file is visible.

## 3️⃣ Deploy Server Setup (Apache Tomcat):

**Step 1: Connect to Deploy Server:**
```
ssh -i "deploy.pem" ubuntu@<Deploy_Server_IP>
```
**Step 2: Install Java 17 and Tomcat:**
```
sudo apt update
sudo apt install openjdk-17-jdk -y

sudo wget https://downloads.apache.org/tomcat/tomcat-10/v10.1.30/bin/apache-tomcat-10.1.30.tar.gz
sudo tar -xvzf apache-tomcat-10.1.30.tar.gz
```

**Step 3: Start Tomcat:**

Access:

http://<Deploy_Server_IP>:8080/

**Step 4: Download Artifact from Nexus:**

**Create a download directory:**
```
mkdir ~/deployments
cd ~/deployments
```

## Download the .war file:
```
wget --user admin --password admin123 \
http://<NEXUS_SERVER_IP>:8081/repository/maven-releases/com/example/webapp/1.0/webapp-1.0.war
```
**Step 5: Deploy to Tomcat:**


## Copy .war file to Tomcat:
```
sudo cp webapp-1.0.war /apache-tomcat/webapps/
```

**Restart Tomcat:**

```
./startup.sh
./shutdown.sh

```


## ✅ Check deployment:
```
http://<Deploy_Server_IP>:8080/webapp-1.0/
```
## 4️⃣ End-to-End Testing:

Step	 Description	Expected Result

1	Run mvn clean install & mvn deploy on Build Server	WAR generated & uploaded to Nexus

2	Verify in Nexus	Artifact visible in maven-releases

3	Download WAR on Deploy Server	WAR fetched successfully

4	Copy to Tomcat & Start	App runs successfully

5	Access in browser	http://<Deploy_Server_IP>:8080/JavaWebCalculator works

## 🧰 Common Issues & Fixes:

Issue	Cause	Fix

mvn deploy fails	Wrong <server> credentials in settings.xml	Check Nexus username/password

Nexus not accessible	Port 8081 blocked	Allow inbound rule in AWS security group

Tomcat not starting	Permissions issue	Run startup script as sudo
WAR not deployed	Wrong file path	Ensure WAR is inside /opt/tomcat/webapps

## 📄 Deliverables:


## ✅ Java Build Server:

## Maven Validate:

<img width="1250" height="373" alt="Image" src="https://github.com/user-attachments/assets/49368a22-954a-4b9b-9b54-083728cbbbd9" />

## Maven Build Success:

<img width="1892" height="446" alt="Image" src="https://github.com/user-attachments/assets/b736aae9-5b54-4c94-9bb8-c6f51a9060ce" />

## ✅ Nexus Repository Server:

## Nexus repository:

<img width="1905" height="981" alt="Image" src="https://github.com/user-attachments/assets/4cacb51a-d627-4047-96c1-85b539600873" />

## Nexus Password:

<img width="985" height="153" alt="Image" src="https://github.com/user-attachments/assets/5fb0a452-aab3-445d-a22f-5346498c79c8" />

## Artifact uploaded successfully:

<img width="1912" height="922" alt="Image" src="https://github.com/user-attachments/assets/c542890c-f675-425e-a026-3eaa8d9c8078" />

## ✅ Deploy Server:

**Tomcat configured and running:**

<img width="1917" height="423" alt="Image" src="https://github.com/user-attachments/assets/372298f8-5766-40cc-b31d-4121031da0cd" />

## WAR deployed and accessible:

<img width="1328" height="667" alt="Image" src="https://github.com/user-attachments/assets/44443ab5-770a-4f4f-92c9-e2a2f7b6f5e4" />

## ✅ Final Outcome:

**Access Java Web Calculator App at:**

```
http://<Deploy_Server_IP>:8080/JavaWebCalculator
```

## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
