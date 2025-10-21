## 🧾 SonarQube Installation & Java Application Code Analysis Guide:


## Document Purpose:

This guide provides a complete step-by-step process to:

- Connect it with a Java-based application.  
- Perform static code analysis to detect bugs, vulnerabilities, and code smells.  

---

## 🧱 1. Prerequisites:


| Component        | Requirement                                         |
|-----------------|----------------------------------------------------|
| OS              | Ubuntu 22.04 LTS / Amazon linux / ubuntu Server |
| RAM             | Minimum 4 GB (8 GB recommended)                   |
| Java            | Java 17 (LTS, required by SonarQube 9.x and above)
| User Privileges | `sudo` privileges required                         |
| Application Code| Java-based project (Maven or Gradle build)        |

---

## ⚙️ 2. SonarQube Installation Steps:


### 🧩 Step 1: Install Java:

```
sudo apt update
sudo apt install openjdk-17-jdk -y
java -version
```
## 🧩 Step 2: Download and Configure SonarQube:

```
sudo apt install wget unzip -y
sudo wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-10.6.0.92116.zip
sudo unzip sonarqube-10.6.0.92116.zip
```

**Edit the configuration:**

```
sudo vi /sonarqube/conf/sonar.properties
```
**Allow external access if needed:**

```
sonar.web.host=0.0.0.0
sonar.web.port=9000
```
Save and exit :wq

## 🧩 Step 3: Access the SonarQube Dashboard:

Open your browser:
```
http://<server-ip>:9000
```
**Default credentials:**

Username: admin

Password: admin

You will be prompted to change the password on first login.

💡 3. Configure Java Application for SonarQube Analysis
Assume a Maven-based Java project, e.g., Currency Converter.

## (optional)🧩 Step 1: Install Sonar Scanner:

```
sudo apt install unzip -y
cd /opt
sudo wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-6.2.0.61181-linux.zip
sudo unzip sonar-scanner-6.2.0.61181-linux.zip
sudo mv sonar-scanner-6.2.0.61181-linux sonar-scanner
```
**Add to PATH:**

```
sudo nano /etc/profile.d/sonar-scanner.sh
export PATH=$PATH:/opt/sonar-scanner/bin
```
**Apply and verify:**

```
source /etc/profile.d/sonar-scanner.sh
sonar-scanner -v
```
## 🧩 Step 2: Generate a Sonar Token:

Login to SonarQube → My Account → Security → Generate Tokens

Name: maven-analysis

Copy and store the token securely.

## 🧩 Step 3: Add Sonar Configuration to Maven Project:

Create sonar-project.properties in the project root:

```
sonar.projectKey=CurrencyConverter
sonar.projectName=Currency Converter Java App
sonar.projectVersion=1.0
sonar.sources=src
sonar.language=java
sonar.java.binaries=target/classes
sonar.host.url=http://<sonarqube-server-ip>:9000
sonar.login=<your-generated-token>
```
## 🧩 Step 4: Build and Run Sonar Analysis:

Option 1 — Using Sonar Scanner:

```
mvn clean install
sonar-scanner
```
**Option 2 — Directly via Maven:**

```
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=my-java-app \
  -Dsonar.host.url=http://<sonarqube-server-ip>:9000 \
  -Dsonar.login=<your-generated-token>
  ```
## 🔍 4. Verify SonarQube Analysis Results:

Check the dashboard → Projects → Your App. Metrics include:

✅ Code Smells

🐞 Bugs

🔐 Vulnerabilities

📊 Duplications

🧪 Test Coverage (if included)

## ⚙️ 5. Optional — Integrate SonarQube with Jenkins CI/CD

**Plugin Setup:**

Manage Jenkins → Manage Plugins → Available

Search for SonarQube Scanner → Install & Restart

Configure Jenkins:

Manage Jenkins → Configure System → Add SonarQube Server URL & Token

**Jenkinsfile Stage Example:**

```
stage('SonarQube Analysis') {
    steps {
        script {
            withSonarQubeEnv('SonarQubeServer') {
                sh 'mvn clean verify sonar:sonar'
            }
        }
    }
}
```
## 📊 6. Common Troubleshooting:

Issue	Possible Cause	Fix

SonarQube not starting	Insufficient memory	Increase VM RAM (≥ 4GB)

“Java not found”	Wrong JAVA_HOME	Export correct JAVA_HOME

Scanner fails	Invalid token or URL	Recheck token & SonarQube URL

Port 9000 busy	Another service using it	Change sonar.web.port in sonar.properties


## ✅ 7. Validation Checklist:

Step	Description	Status

Java Installed	java -version	✅

SonarQube Running	systemctl status sonarqube	✅

Web Access	http://<ip>:9000	✅

Sonar Scanner Installed	sonar-scanner -v	✅

Java Project Analyzed	Results visible in dashboard	✅


## 🧠 8. Summary:

SonarQube provides deep static code analysis for improving code quality, security, and maintainability. Using its embedded database, you can quickly deploy SonarQube for training, testing, or CI/CD integration without an external PostgreSQL instance.

**It seamlessly integrates with:**

☕ Maven / Gradle

🧩 Jenkins / CI pipelines

🧠 Developers’ workflows via real-time feedback

## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/