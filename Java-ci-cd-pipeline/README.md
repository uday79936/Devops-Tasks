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

## 1. servers:

**Jenkins:**

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/cfd22eb6-1681-4e6b-b12b-76d067da3b9b" />

**sonar:**

<img width="1918" height="921" alt="Image" src="https://github.com/user-attachments/assets/9793364a-0041-47a4-8e49-fb02ee1e608e" />

**nexus:**

<img width="1920" height="967" alt="Image" src="https://github.com/user-attachments/assets/8db30842-9ccc-4036-b7c1-42a331f3829a" />

**tomcat:**

<img width="1919" height="972" alt="Image" src="https://github.com/user-attachments/assets/5c21047c-c792-423b-9b6c-139953f4c908" />

## 2. Change Hostnames:

**Jenkins:**

<img width="951" height="133" alt="Image" src="https://github.com/user-attachments/assets/9d024df2-c7f1-4821-b287-2235990db113" />

**sonar:**

<img width="959" height="190" alt="Image" src="https://github.com/user-attachments/assets/26e4cfa2-8587-470d-a6ef-e92cfb18dc02" />

**nexus:**

<img width="987" height="150" alt="Image" src="https://github.com/user-attachments/assets/395034a9-eb87-4631-a17c-a23ef97d0d98" />

**Tomcat:**

<img width="1219" height="210" alt="Image" src="https://github.com/user-attachments/assets/cd758587-5f81-4aca-9621-1e3361d9bcaf" />

## 3. ssh keygen generated:


<img width="1304" height="165" alt="Image" src="https://github.com/user-attachments/assets/627929a7-dad6-4eee-9f12-02f0f7a87332" />

**sonar:**

<img width="1904" height="1018" alt="Image" src="https://github.com/user-attachments/assets/7f83bf15-b797-4f5c-9fcb-6b385716d1cc" />

**nexus:**

<img width="1910" height="344" alt="Image" src="https://github.com/user-attachments/assets/2747fdac-39b9-458b-bf91-8e815fda0b07" />

**tomcat:**

<img width="1907" height="402" alt="Image" src="https://github.com/user-attachments/assets/cdb3897a-e23d-465c-ae49-7fe5cfcfb85f" />

## 4. Sonarcube Dashboard:

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/864e9aa0-6641-42c9-ab10-a780733dbd44" />

## 5. sonar generate token:

<img width="1914" height="1006" alt="Image" src="https://github.com/user-attachments/assets/3c4e7f4d-c820-4dc4-8b03-87c107191b3c" />

## 6. Download nexus:

<img width="1768" height="475" alt="Image" src="https://github.com/user-attachments/assets/a4467714-3d91-48fc-94aa-c0633d6c3759" />

## 7. started nexus:

<img width="1056" height="361" alt="Image" src="https://github.com/user-attachments/assets/6fd55c04-82ae-49de-900a-33f048be9ed7" />

## 8. nexus password:

<img width="1277" height="337" alt="Image" src="https://github.com/user-attachments/assets/4335e4bb-a6d8-4b8a-bc64-5a234d7ef28f" />

## 9. Tomcat configuration:


<img width="1227" height="892" alt="Image" src="https://github.com/user-attachments/assets/27ea57fe-5237-485c-8f80-aac43a6a8868" />

## 10. configuration with maven settings:

<img width="1517" height="847" alt="Image" src="https://github.com/user-attachments/assets/d840df5c-66ef-43e0-b68f-f0ad86c1f3d0" />

## 11. Plugins installed:

<img width="1917" height="977" alt="Image" src="https://github.com/user-attachments/assets/4f4863c8-b17b-4047-9246-e9e982cf7b3c" />

## 12. Node agents:

**sonar agent:**

<img width="1172" height="657" alt="Image" src="https://github.com/user-attachments/assets/509663be-b597-4871-bbd7-3f8d7581e989" />

**nexus agent:**

<img width="1177" height="677" alt="Image" src="https://github.com/user-attachments/assets/9e9d3c44-6f58-4914-8a2e-c198f373c086" />

**tomcat agent:**

<img width="1227" height="677" alt="Image" src="https://github.com/user-attachments/assets/30000d70-f155-4018-a6f8-c4012d8ba5ed" />

## 13. nodes output:

<img width="1907" height="958" alt="Image" src="https://github.com/user-attachments/assets/11e28726-0059-4c6c-82fe-37c9c1548234" />


## outputs:

## 14. pipeline console output:

<img width="1915" height="967" alt="Image" src="https://github.com/user-attachments/assets/895e90be-d04c-46c8-8882-aa3f7b312d71" />

## 15. Pipeline output:

<img width="1911" height="961" alt="Image" src="https://github.com/user-attachments/assets/35869fbe-29eb-4142-9697-8530c559dc95" />

## 16.sonarcube success:

<img width="1917" height="971" alt="Image" src="https://github.com/user-attachments/assets/7fafee81-7ee9-46a9-8880-9f95eed45098" />

## 17. nexus repository:

<img width="1913" height="975" alt="Image" src="https://github.com/user-attachments/assets/dc9d9a3d-1760-4b0b-8203-04de25c63dba" />

## 18. tomcat output:

<img width="1917" height="971" alt="Image" src="https://github.com/user-attachments/assets/94a318ae-20b9-4e75-94a8-5e85ebb0b041" />

## 19. Java web calculator:

<img width="1916" height="960" alt="Image" src="https://github.com/user-attachments/assets/cb96b4d1-4471-47dd-9b31-3b619eed25fb" />


## Auuthor:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/

