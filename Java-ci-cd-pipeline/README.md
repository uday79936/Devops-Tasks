## 🚀 End-to-End CI/CD Pipeline for Java Web Application
## Java 17 | Maven | SonarQube | Nexus Repository | Tomcat | Jenkins Multi-Server Architecture:


## CI/CD Architecture:

(Illustrative diagram – replace with your own if available)

This project demonstrates a fully automated, multi-server CI/CD pipeline for a Java web application (Java-Web-Calculator-App) using Jenkins as the orchestration engine. The pipeline includes source code analysis, secure artifact management, and zero-downtime deployment to a remote Tomcat server.

## 📁 Project Overview:

Source Code: mrtechreddy/Java-Web-Calculator-App
Build Tool: Apache Maven (3.9.11)
Language: Java 17
CI/CD Engine: Jenkins (Distributed across 4 EC2 instances)
Quality Gate: SonarQube (Code Quality & Security)
Artifact Storage: Sonatype Nexus Repository
Deployment Target: Apache Tomcat (Remote)

🧱 Architecture:
```
SVG content

Server Roles

Jenkins

CI/CD Orchestrator

jenkins

SonarQube
Static Code Analysis

sonar

Nexus
Binary Artifact Repository

nexus

Tomcat
Application Runtime
tomcat
```

## ⚙️ Prerequisites:

4 AWS EC2 instances (Ubuntu 22.04 LTS recommended)
Network connectivity between all servers (private IPs preferred)
GitHub account with a personal access token (classic, with repo scope)
DNS or /etc/hosts entries for inter-server resolution (optional but recommended)
🔧 Setup Guide

## 1. Configure Hostnames:

**On each server, run:**

# On Jenkins server:
```
sudo hostnamectl set-hostname jenkins
```

# On SonarQube server:
```
sudo hostnamectl set-hostname sonar
```

# On Nexus server:
```
sudo hostnamectl set-hostname nexus
```

# On Tomcat server:
```
sudo hostnamectl set-hostname tomcat
```

sudo reboot:

## 2. Install Java 17 & Maven (All Servers):
```

sudo apt update -y
sudo apt install openjdk-17-jdk maven -y
```

```
java -version   # Should show openjdk version "17.x"
mvn -version    # Should show Apache Maven 3.9.11+
```

## 3. Setup Jenkins Working Directory:

```
mkdir -p /home/ubuntu/jenkins
```

## 4. Configure SSH Key-Based Authentication (From Jenkins):

**On the Jenkins server, generate and distribute SSH keys:**

```
ssh-keygen 
```


# Replace IPs with your actual private IPs:
```
ssh-copy-id ubuntu@<SONAR_IP>
ssh-copy-id ubuntu@<NEXUS_IP>
ssh-copy-id ubuntu@<TOMCAT_IP>
```
## ✅ Test: ssh ubuntu@<SONAR_IP> → should log in without password

## 5. Install Jenkins Plugins:

**In Jenkins UI**
```
(http://<JENKINS_IP>:8080):
```

## Manage Jenkins → Plugins → Available: 

**Install:**
```
Publish Over SSH
GitHub Integration
Nexus Artifact Uploader
SonarQube Scanner
Sonar Quality Gates
SSH Agent Plugin
SSH Build Agents
```
**💡 Restart Jenkins after installation.**

## 6. Configure Jenkins Credentials:
```
Go to: Manage Jenkins → Credentials → Global
```

**Add the following:**

## github-token:

Secret text

Your GitHub Personal Access Token

## nexus:

Username & Password

Nexus admin credentials

## tomcat:

Username & Password
Tomcat Manager user (see
tomcat-users.xml
)
## SSH Key:

SSH Username with Private Key
Username:
ubuntu
, Private Key: Paste
~/.ssh/id_rsa

## 7. Add Jenkins Agent Nodes:
```
Manage Jenkins → Nodes → New Node
```

**Create three permanent agents:**

```
Name: sonar, nexus, tomcat
Remote root directory: /home/ubuntu/jenkins
Launch method: Launch agents via SSH
Host: Private IP or hostname
Credentials: Select the SSH key from step 6
```

## ✅ Ensure all nodes show Connected status:


## 📜 Jenkins Pipeline (Jenkinsfile)
## The pipeline is declarative and runs across multiple agents:

groovy
```
pipeline {
    agent { label 'sonar' }

    tools {
        jdk 'JDK17'
        maven 'Maven'
    }

    environment {
        SONARQUBE_SERVER = 'http://18.212.142.140:9000'
        SONARQUBE_TOKEN = 'squ_aa5e62c5e4b239d040227e37930671ede97fb85b'
        MVN_SETTINGS = '/etc/maven/settings.xml'
        NEXUS_URL = 'http://18.208.136.157:8081'
        NEXUS_REPO = 'maven-releases'
        NEXUS_GROUP = 'com.web.cal'
        NEXUS_ARTIFACT = 'webapp-add'
        TOMCAT_URL = 'http://54.83.80.22:8080/manager/text'
    }

    stages {

        /* === Stage 1: Checkout Code === */
        stage('Checkout Code') {
            steps {
                echo '📦 Cloning source from GitHub...'
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/mrtechreddy/Java-Web-Calculator-App.git'
                    ]]
                ])
            }
        }

        /* === Stage 2: SonarQube Analysis === */
        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Running SonarQube static analysis...'
                sh '''
                mvn clean verify sonar:sonar \
                  -DskipTests \
                  -Dsonar.host.url=${SONARQUBE_SERVER} \
                  -Dsonar.login=${SONARQUBE_TOKEN} \
                  --settings ${MVN_SETTINGS}
                '''
            }
        }

        /* === Stage 3: Build Artifact === */
        stage('Build Artifact') {
            steps {
                echo '⚙️ Building WAR...'
                sh 'mvn clean package -DskipTests --settings ${MVN_SETTINGS}'
                sh 'echo ✅ Build Completed!'
                sh 'ls -lh target/*.war || echo "No WAR file found."'
            }
        }

        /* === Stage 4: Upload Artifact to Nexus === */
        stage('Upload Artifact to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USR', passwordVariable: 'NEXUS_PSW')]) {
                    sh '''#!/bin/bash
                    set -e
                    WAR_FILE=$(find target -type f -name "*.war" | head -n1)
                    if [[ ! -f "$WAR_FILE" ]]; then
                        echo "❌ No WAR file found in target/"; exit 1
                    fi

                    FILE_NAME=$(basename "$WAR_FILE")
                    VERSION="0.0.${BUILD_NUMBER}"
                    GROUP_PATH=$(echo "${NEXUS_GROUP}" | tr '.' '/')

                    echo "📤 Uploading $FILE_NAME to Nexus as version $VERSION..."
                    curl -f -u "${NEXUS_USR}:${NEXUS_PSW}" --upload-file "$WAR_FILE" \
                    "${NEXUS_URL}/repository/${NEXUS_REPO}/${GROUP_PATH}/${NEXUS_ARTIFACT}/${VERSION}/${NEXUS_ARTIFACT}-${VERSION}.war"
                    echo "✅ Artifact uploaded successfully to Nexus!"
                    '''
                }
            }
        }

        /* === Stage 5: Deploy to Tomcat === */
        stage('Deploy to Tomcat') {
            agent { label 'tomcat' }
            steps {
                withCredentials([
                    usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USR', passwordVariable: 'NEXUS_PSW'),
                    usernamePassword(credentialsId: 'tomcat', usernameVariable: 'TOMCAT_USR', passwordVariable: 'TOMCAT_PSW')
                ]) {
                    sh '''#!/bin/bash
                    set -e
                    cd /tmp || exit 1
                    rm -f *.war

                    VERSION="0.0.${BUILD_NUMBER}"
                    GROUP_PATH=$(echo "${NEXUS_GROUP}" | tr '.' '/')
                    WAR_URL="${NEXUS_URL}/repository/${NEXUS_REPO}/${GROUP_PATH}/${NEXUS_ARTIFACT}/${VERSION}/${NEXUS_ARTIFACT}-${VERSION}.war"

                    echo "⬇️ Downloading WAR from Nexus: $WAR_URL"
                    curl -u "${NEXUS_USR}:${NEXUS_PSW}" -O "$WAR_URL"

                    WAR_FILE=$(basename "$WAR_URL")
                    APP_NAME="${NEXUS_ARTIFACT}"

                    echo "🧹 Undeploying old app (if exists)..."
                    curl -u "${TOMCAT_USR}:${TOMCAT_PSW}" "${TOMCAT_URL}/undeploy?path=/${APP_NAME}" || true

                    echo "🚀 Deploying new WAR to Tomcat..."
                    curl -u "${TOMCAT_USR}:${TOMCAT_PSW}" --upload-file "$WAR_FILE" \
                    "${TOMCAT_URL}/deploy?path=/${APP_NAME}&update=true"

                    echo "✅ Deployment successful! Application updated."
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '🎉 Pipeline completed successfully — Application live on Tomcat!'
        }
        failure {
            echo '❌ Pipeline failed — Check Jenkins logs.'
        }
    }
}
```
## ✅ Note: Replace <SONAR_IP>, <NEXUS_IP>, <TOMCAT_IP> with actual private IPs:


## 🔄 Pipeline Stages Explained:

## 1. Checkout Code:
```
Clones
main
branch from GitHub
```
## 2. SonarQube Analysis:

Runs static code analysis (skips tests)

## 3. Build Artifact:

Packages
.war
file using Maven

## 4. Upload to Nexus:

Uploads versioned WAR to Nexus repo (
0.0.${BUILD_NUMBER}
)
## 5. Deploy to Tomcat:

Downloads WAR from Nexus and deploys via Tomcat Manager API

## 🔐 Security Notes:

Never hardcode secrets in Jenkinsfile – always use withCredentials
Use private IPs for inter-server communication (not public IPs)
Restrict Nexus/Tomcat access via security groups
Rotate SonarQube token and GitHub PAT periodically
## 🛠️ Required Configurations Tomcat (tomcat-users.xml)
```
<tomcat-users>
  <role rolename="manager-script"/>
  <user username="admin" password="secure_password" roles="manager-script"/>
</tomcat-users>
Maven settings.xml (for Nexus)
```
## Place in /etc/maven/settings.xml on all servers:
```
<servers>
  <server>
    <id>nexus</id>
    <username>admin</username>
    <password>your-nexus-password</password>
  </server>
</servers>
```

## ✅ Verification Checklist:
```
Java 17
java -version
Maven
mvn -version
Jenkins Nodes
Green "Connected" status
SonarQube

```
**Access:**
```
http://<SONAR_IP>:9000
```

## Nexus:

**Access:**
```
http://<NEXUS_IP>:8081
```

## Tomcat Manager:
```
curl -u admin:pass http://<TOMCAT_IP>:8080/manager/text/list
```

## 🎯 Final Outcome:

✅ Code pushed to GitHub triggers Jenkins pipeline

✅ Code quality enforced via SonarQube

✅ Artifact securely stored in Nexus with versioning

✅ Application auto-deployed to Tomcat

✅ Full traceability from commit → build → deploy

## Images:


## Auuthor:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/

