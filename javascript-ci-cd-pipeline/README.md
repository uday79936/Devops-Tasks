## 🍅 Pomodoro Timer Web App – CI/CD Pipeline with Jenkins, SonarQube, Nexus & Nginx:

A lightweight, responsive Pomodoro timer built in JavaScript with a fully automated DevOps pipeline for continuous integration and deployment.

25-min work → 5-min short break → 15-min long break

✅ Browser notifications | Responsive UI | Customizable session lengths 

## 🚀 Features:

Web-based Pomodoro timer with intuitive controls

Real-time browser notifications for session transitions
Mobile-first responsive design
Automated:

Code checkout & dependency install

Unit testing (npm test)

Static code analysis via SonarQube

Production builds (npm run build)

Artifact packaging & versioning

Secure storage in Nexus Repository

Zero-downtime deployment to Nginx

## 🏗️ Architecture Overview:

```
GitHub
   │
   ▼
Jenkins (CI/CD Orchestrator)
   ├── Agent: sonar → Build, Test, Analyze, Package, Upload
   └── Agent: nginx (formerly "tomcat") → Deploy to Nginx
   │
   ▼
SonarQube → Code Quality & Security Gates
   │
   ▼
Nexus → Artifact Storage (Raw Repository)
   │
   ▼
Nginx → Production Web Server (http://18.116.203.32)
```
## change Hostnames:


**jenkins:**

```
sudo hostnamectl set-hostname Jenkins
sudo init 6
```

**Sonarqube:**

```
sudo hostnamectl set-hostname sonar
sudo init 6
```
**nexus:**

```
sudo hostnamectl set-hostname nexus
sudo init 6
```
**nginx:**

```
sudo hostnamectl set-hostname nginx
sudo init 6
```
## install nodejs:

```
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs
```
## 📦 Pipeline Stages:

**step-1:**

Checkout Code
sonar
Clone from
main
branch

**step-2:**

Install Dependencies
sonar
npm ci
or
npm install

**step-3:**

Run Tests
sonar
Execute test suite

**step-4:**

SonarQube Analysis
sonar
Code quality + coverage

**step-5:**

Build Artifact
sonar
Generate
dist/
via
npm run build

**step-6:**

Package Artifact
sonar
Create
pomodoro-app-0.0.X.tar.gz

**step-7:**

Upload to Nexus
sonar
Store in
raw-releases
repo

**step-8:**

Deploy to Nginx
nginx
Download, extract, serve via Nginx

🔧 Environment Variables (via Jenkinsfile)
groovy
```
// SonarQube
SONARQUBE_SERVER = 'sonar'

// Nexus
NEXUS_URL       = 'http://54.152.171.119:8081'
NEXUS_REPO      = 'raw-releases'
NEXUS_GROUP     = 'com/web/pomodoro'
NEXUS_ARTIFACT  = 'pomodoro-app'

// Nginx
NGINX_WEB_ROOT  = '/var/www/html'
Note: Artifact path in Nexus:
http://<nexus>:8081/repository/raw-releases/com/web/pomodoro/pomodoro-app/0.0.X/pomodoro-app-0.0.X.tar.gz 
```

## 🛠️ Prerequisites:

GitHub Repository: Public (https://github.com/ashuvee/pomodoro-app-js.git)

Jenkins Master (Ubuntu 22.04+)

Two Jenkins Agents:

sonar: For build/test/analysis (Node.js + Java 17)

nginx: For deployment (Java 17 + sudo for file ops)

SonarQube Server (v10.6+, Java 17)

Nexus Repository Manager (OSS 3.x, Java 11)

Nginx Web Server (serves static files from /var/www/html)


## pipeline code:

```
pipeline {
    agent { label 'sonar' }

    environment {
        // SonarQube
        SONARQUBE_SERVER = 'sonar'

        // Nexus
        NEXUS_URL       = 'http://54.152.171.119:8081'
        NEXUS_REPO      = 'raw-releases'
        NEXUS_GROUP     = 'com/web/pomodoro'
        NEXUS_ARTIFACT  = 'pomodoro-app'

        // Nginx Deployment
        NGINX_WEB_ROOT  = '/var/www/html'
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
                        url: 'https://github.com/ashuvee/pomodoro-app-js.git' // Fixed trailing space
                    ]]
                ])
            }
        }

        /* === Stage 2: Install Dependencies === */
        stage('Install Dependencies') {
            steps {
                echo '📥 Installing npm dependencies...'
                sh 'npm ci || npm install'  // Prefer `npm ci` for reproducible builds
                echo '✅ Dependencies installed!'
            }
        }

        /* === Stage 3: Run Tests (Before Sonar) === */
        stage('Run Tests') {
            steps {
                echo '🧪 Running tests and generating coverage...'
                sh 'npm test'
                // Ensure coverage/lcov.info exists for Sonar
            }
        }

        /* === Stage 4: SonarQube Analysis === */
        stage('SonarQube Analysis') {
            steps {
                script {
                    // Check if src/ exists; if not, adjust sonar.sources
                    def srcExists = sh(script: '[ -d "src" ] && echo true || echo false', returnStdout: true).trim()
                    def sonarSources = srcExists == 'true' ? 'src' : '.'
                    
                    echo "🔍 Using SonarQube sources directory: ${sonarSources}"
                    
                    sh 'npm install -g sonarqube-scanner || npm install --save-dev sonarqube-scanner'
                    
                    withSonarQubeEnv("${SONARQUBE_SERVER}") {
                        sh """
                            npx sonar-scanner \\
                              -Dsonar.projectKey=pomodoro-app-js \\
                              -Dsonar.projectName="Pomodoro App JS" \\
                              -Dsonar.projectVersion=0.0.\${BUILD_NUMBER} \\
                              -Dsonar.sources=${sonarSources} \\
                              -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info \\
                              -Dsonar.sourceEncoding=UTF-8
                        """
                    }
                }
            }
        }

        /* === Stage 5: Build Artifact === */
        stage('Build Artifact') {
            steps {
                echo '⚙️ Building application...'
                sh 'npm run build'
                echo '✅ Build completed!'
                sh 'ls -lh dist/ || echo "⚠️ dist/ directory is empty or missing"'
            }
        }

        /* === Stage 6: Package Artifact === */
        stage('Package Artifact') {
            steps {
                sh '''
                    VERSION="0.0.${BUILD_NUMBER}"
                    TARBALL="${NEXUS_ARTIFACT}-${VERSION}.tar.gz"
                    tar -czf "$TARBALL" -C dist .
                    echo "✅ Package created: $TARBALL"
                    ls -lh "$TARBALL"
                '''
            }
        }

        /* === Stage 7: Upload Artifact to Nexus === */
        stage('Upload Artifact to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USR', passwordVariable: 'NEXUS_PSW')]) {
                    sh '''#!/bin/bash
                        set -e
                        VERSION="0.0.${BUILD_NUMBER}"
                        TARBALL="${NEXUS_ARTIFACT}-${VERSION}.tar.gz"
                        UPLOAD_URL="${NEXUS_URL}/repository/${NEXUS_REPO}/${NEXUS_GROUP}/${NEXUS_ARTIFACT}/${VERSION}/${TARBALL}"

                        echo "📤 Uploading $TARBALL to Nexus: $UPLOAD_URL"
                        curl -f -u "${NEXUS_USR}:${NEXUS_PSW}" --upload-file "$TARBALL" "$UPLOAD_URL"
                        echo "✅ Artifact uploaded successfully to Nexus!"
                    '''
                }
            }
        }

        /* === Stage 8: Deploy to Nginx === */
        stage('Deploy to Nginx') {
            agent { label 'nginx' }  // Changed from 'tomcat' for clarity (use appropriate agent label)
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USR', passwordVariable: 'NEXUS_PSW')]) {
                    sh '''#!/bin/bash
                        set -e
                        VERSION="0.0.${BUILD_NUMBER}"
                        TARBALL="${NEXUS_ARTIFACT}-${VERSION}.tar.gz"
                        DOWNLOAD_URL="${NEXUS_URL}/repository/${NEXUS_REPO}/${NEXUS_GROUP}/${NEXUS_ARTIFACT}/${VERSION}/${TARBALL}"
                        WEB_ROOT="${NGINX_WEB_ROOT}"

                        echo "⬇️ Downloading $TARBALL from Nexus..."
                        curl -f -u "${NEXUS_USR}:${NEXUS_PSW}" -o "/tmp/$TARBALL" "$DOWNLOAD_URL"

                        if [ ! -f "/tmp/$TARBALL" ]; then
                            echo "❌ Tarball not found after download!"
                            exit 1
                        fi

                        echo "🚀 Deploying to Nginx at $WEB_ROOT..."
                        sudo mkdir -p "$WEB_ROOT"
                        sudo rm -rf "$WEB_ROOT"/*
                        sudo tar -xzf "/tmp/$TARBALL" -C "$WEB_ROOT"
                        sudo chown -R www-data:www-data "$WEB_ROOT"
                        sudo chmod -R 755 "$WEB_ROOT"

                        echo "✅ Deployment successful! App is live on Nginx."
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '🎉 Pipeline completed successfully — Application is live on Nginx!'
        }
        failure {
            echo '❌ Pipeline failed — Please check Jenkins console output for details.'
        }
    }
}
```

## 🚦 Running the Pipeline:

Manual Trigger
In Jenkins: Open job pomodoro-js
Click Build Now
Automatic Trigger (Recommended)
Configure GitHub Webhook:
URL: http://<jenkins-ip>:8080/github-webhook/
Events: push to main

Any git push to main triggers full pipeline

✅ Versioning: Artifacts use 0.0.${BUILD_NUMBER} (e.g., 0.0.42) 

## 🔍 Verification:

After successful pipeline run:

# Confirm app is live:

```
curl -s http://18.116.203.32/ | head -n 5
```

# Check files on Nginx server:
```
ssh jenkins@18.116.203.32 'ls -la /var/www/html'
```

# View Nginx logs:
```
sudo tail -f /var/log/nginx/access.log
```
## 📊 Monitoring & Dashboards:

**Jenkins:**

Pipeline logs, history, stage view
```
http://<jenkins>:8080/job/pomodoro-js/
```
**SonarQube:**

Code quality, bugs, coverage
```
http://<sonarqube>:9000
```

**Nexus:**

Browse/download artifacts
```
http://<nexus>:8081
```

## 📌 Default Credentials: 

SonarQube: admin/admin (change on first login)

Nexus: admin/<initial-password> (from /opt/sonatype-work/nexus3/admin.password)

## 📁 Project Structure:

```
pomodoro-app-js/
├── dist/                 # Production build (created by pipeline)
├── src/                  # Source code (if used)
├── package.json          # Scripts: start, build, test, clean
├── Jenkinsfile           # Declarative CI/CD pipeline
├── .gitignore
└── README.md             # ← You are here!
```

## Key npm Scripts:
```
npm start      # Dev server (port 8080)
npm run build  # Build to dist/
npm test       # Run tests
npm run clean  # Clean dist/
```

## 📌 Important Notes:

The Jenkinsfile assumes:

Jenkins has NodeJS-LTS (v20.x) configured under Global Tools

nexus credential ID exists (Username/Password for Nexus admin)

sonar server is configured under SonarQube Servers with token sonar-token

Agent labels must match:

Build stages → agent { label 'sonar' }

Deployment stage → agent { label 'nginx' } (was "tomcat" in early docs)

Nginx agent must allow passwordless sudo for mkdir, rm, tar, chown

## 🤝 Contributing:


Fork the repo

Create a feature branch (git checkout -b feature/new-timer-sound)
Commit changes (git commit -m "Add bell sound")
Push to branch (git push origin feature/new-timer-sound)
Open a Pull Request to main
⚠️ Merging to main triggers full CI/CD pipeline automatically! 


## 📜 License:
```
MIT License – see LICENSE for details.
```

## 📬 Support:

**For pipeline issues, check:**

Jenkins console output

SonarQube quality gate status

Nexus upload logs

Nginx error logs (/var/log/nginx/error.log)

## Images:

## 1. servers:

**Jenkins:**

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/774c421f-9e85-454b-8e2c-c4c8e2064ac5" />

**sonar:**

<img width="1918" height="921" alt="Image" src="https://github.com/user-attachments/assets/c177e880-15b5-469e-ad3f-2b3e87806d12" />

**nexus:**

<img width="1920" height="967" alt="Image" src="https://github.com/user-attachments/assets/785a5d14-a0aa-4985-9791-b2f8ea312510" />

**Nginx:**

<img width="1897" height="955" alt="Image" src="https://github.com/user-attachments/assets/32f182f0-9ac9-4f63-9b83-f15b73709d80" />

## 2. Change hostnames:

**Jenkins:**

<img width="951" height="133" alt="Image" src="https://github.com/user-attachments/assets/1786d23f-f501-4a52-9845-1b9027c9cdd2" />

**sonar:**

<img width="987" height="150" alt="Image" src="https://github.com/user-attachments/assets/8a5c2dde-2447-4bb6-8061-683d44e34ab8" />

**nexus:**

<img width="987" height="150" alt="Image" src="https://github.com/user-attachments/assets/f4c4218b-b3de-41b9-9f7e-e9f1ec6e87d8" />

**nginx:**

<img width="1150" height="165" alt="Image" src="https://github.com/user-attachments/assets/ebac1244-6341-479f-ae5b-cbc1c7c30b89" />

## 3. Jenkins:

**Jenkins status active:**

<img width="1920" height="744" alt="Image" src="https://github.com/user-attachments/assets/883e85e3-d23a-43e9-9c73-0d15246ce960" />

**Unlock Jenkins:**


<img width="1911" height="994" alt="Image" src="https://github.com/user-attachments/assets/32cb71a3-764f-42ec-acff-4589d12377ac" />

**To see the Jenkins Password:**

<img width="1149" height="146" alt="Image" src="https://github.com/user-attachments/assets/09dabd3b-a455-4862-b8bc-7388060ad35c" />

**Customize Jenkins:**

<img width="1920" height="1006" alt="Image" src="https://github.com/user-attachments/assets/0195f773-59ec-4773-b2ab-6ac936a5dcd8" />

**Getting started:**

<img width="1920" height="1007" alt="Image" src="https://github.com/user-attachments/assets/d1b1b8fe-9e69-49ec-92f4-b214ca7fffea" />

**Create admin user:**

<img width="1920" height="1009" alt="Image" src="https://github.com/user-attachments/assets/a73f9c0d-19d6-4189-90b3-f9b50f10cb48" />

**ssh keys generated:**

<img width="1304" height="165" alt="Image" src="https://github.com/user-attachments/assets/0e7210f0-8fa8-42f0-a283-fbe95fc4e343" />

**sonar:**

<img width="1904" height="1018" alt="Image" src="https://github.com/user-attachments/assets/d0833d8c-4d4b-4fa7-b2a1-378ef10e88de" />

**nexus:**

<img width="1910" height="344" alt="Image" src="https://github.com/user-attachments/assets/fcf8b702-d6f3-4382-8d48-2fd155f0e5a9" />

**nginx:**

<img width="1918" height="267" alt="Image" src="https://github.com/user-attachments/assets/0d3a162e-6d17-4b0b-a22d-1627454eb737" />

## sonarqube:

**sonarqube admin dashboard:**

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/1cf994cd-ad1f-44fc-bf15-65c88a4e026a" />

**Sonar token:**

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/6919fbdf-e7bc-448b-820b-f7f6c5b53e47" />

**nginx status active:**

<img width="1466" height="512" alt="Image" src="https://github.com/user-attachments/assets/966ec038-6ca7-49dc-a39f-e10a4c029dae" />

**Plugins installed:**

<img width="1908" height="967" alt="Image" src="https://github.com/user-attachments/assets/cc679c9e-5e0d-48f9-a7e8-34417150f44b" />

## Nexus output:

<img width="1918" height="973" alt="Image" src="https://github.com/user-attachments/assets/e0c80b43-382e-430e-b1f9-08e50df15333" />

## sonarqube output:

<img width="1906" height="956" alt="Image" src="https://github.com/user-attachments/assets/3844ec16-0108-4d64-bd7c-81a1c7284593" />

## Javascript console output:

<img width="1907" height="971" alt="Image" src="https://github.com/user-attachments/assets/c3e110c3-82ab-4b1c-b20f-158a1bf21cf6" />

## Javascript pipeline output:

<img width="1912" height="972" alt="Image" src="https://github.com/user-attachments/assets/4e297aa8-612d-46b6-aa52-1204f5f17d21" />

## Javascript output:

<img width="1918" height="962" alt="Image" src="https://github.com/user-attachments/assets/3c7e1826-6b4b-46d3-a550-20f83c8be5b4" />




## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
