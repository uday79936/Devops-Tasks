## 📘 End-to-End CI/CD Pipeline Documentation:

JavaScript Application Deployment using Jenkins, SonarQube, Nexus, Docker & NGINX

## 1. Project Overview:


This project demonstrates a fully automated CI/CD pipeline for a JavaScript-based application using industry-standard DevOps tools.

Whenever code is pushed to GitHub, the pipeline is automatically triggered in Jenkins, performing:

Source code checkout

Static code analysis using SonarQube

JavaScript build using Node.js & npm

Artifact upload to Nexus Repository Manager

Docker image creation

Docker image push to Docker Hub

Deployment using NGINX container

This ensures code quality, traceability, artifact management, containerization, and automated deployment.

## 2. CI/CD Architecture Diagram:
```
Developer
   |
   v
GitHub (Push)
   |
   v
Jenkins (Webhook Trigger)
   |
   +--> SonarQube (Static Code Analysis & Quality Gate)
   |
   +--> npm install / npm test / npm run build
   |
   +--> Nexus (Upload Build Artifact)
   |
   +--> Docker Build (NGINX Image)
   |
   +--> Docker Hub (Push Image)
   |
   v
NGINX Container (Deployment)
```

## 3. Tools & Versions Used:

Tool	Purpose
GitHub	Source code management

Jenkins (LTS)	CI/CD orchestration

SonarQube (LTS)	Code quality & security

Node.js (LTS)	JavaScript runtime

npm	Dependency & build tool

Nexus 3.x	Artifact repository

Docker	Containerization

Docker Hub	Image registry

NGINX	Application deployment

## 4. GitHub Setup:

**4.1 Repository**

JavaScript application repository hosted on GitHub

Branch monitored: main

**4.2 Webhook Configuration**

**Webhook URL:**
```
http://<jenkins-url>/github-webhook/
```


**Trigger events:**

Push events

**4.3 Traceability**

Jenkins captures:

Commit SHA

Build number

Artifact version

Docker image tag

## 5. Jenkins Setup:


**5.1 Required Plugins**

Git

GitHub Integration

Pipeline (Declarative)

NodeJS

SonarQube Scanner

Docker Pipeline

Credentials Binding

Email / Slack Notification

**5.2 Credentials Stored in Jenkins:**

Credential	       Type

GitHub Token	Secret Text

SonarQube Token	Secret Text

Nexus Username/Password	Username + Password

Docker Hub Credentials	Username + Password


⚠️ No secrets are stored in GitHub or Jenkinsfile

## 6. SonarQube Integration:

**6.1 Configuration:**

SonarQube server configured in Jenkins (Global Tool Config)

Project created in SonarQube with unique project key

**6.2 Quality Gate Rules:**

Code coverage ≥ 80%

No new critical/blocker issues

Maintainability & reliability ratings must pass

**6.3 Pipeline Behavior:**

If Quality Gate fails, pipeline stops immediately

Jenkins build marked as FAILED

## 7. JavaScript Build Process (npm):

**7.1 Build Steps:**
```
npm install
npm test
npm run build
```


**7.2 Output Artifact:**

**Generated build directory:**
```
dist/   or   build/
```

**Packaged as:**
```
myapp-build-<commit-sha>.zip
```

## 8. Nexus Artifact Management:

**8.1 Repository Type:**
```
Hosted repository (raw / npm)
```

**8.2 Upload Strategy:**


**Artifact name includes:**

Application name

Commit SHA

Build number

**Example:**
```
myapp-build-ab12cd3.zip
```

**8.3 Benefits:**


Centralized artifact storage

Version history & traceability

Reusable for Docker builds

## 9. Docker & NGINX Deployment:

**9.1 Dockerfile (NGINX-based):**
```
# ---- Build Stage ----
FROM node:18 AS builder
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# ---- Run Stage ----
FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

```

**9.2 Image Tagging Strategy:**
```
myorg/myapp:<commit-sha>
```


**Example:**
```
myorg/myapp:ab12cd3
```

## 10. Docker Hub Integration:

**10.1 Authentication:**

Docker Hub credentials stored in Jenkins

docker login executed securely

**10.2 Push:**
```
docker push myorg/myapp:<commit-sha>
```

**10.3 Result:**

Image available for deployment

**Fully traceable to:**

Commit

Jenkins build

Nexus artifact

**11. Jenkinsfile (Declarative Pipeline):**
```
pipeline {

    agent any

    tools {
        nodejs 'nodejs-18'
    }

    environment {

        VERSION   = "0.0.${BUILD_NUMBER}"
        ARTIFACT  = "online-book-store-${VERSION}.tar.gz"

        SONARQUBE_SERVER = 'sonar'

        NEXUS_URL  = 'http://3.144.160.233:8081'
        NEXUS_REPO = 'raw-releases'
        NEXUS_ARTIFACT = 'online-book-store'

        DOCKER_IMAGE_FULL = 'udaysairam/online-book-store'
    }

    stages {

        /*---------------------------------
         Checkout Code
        ----------------------------------*/
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/uday79936/Online-book-store.git'
                    ]]
                ])
            }
        }

        /*---------------------------------
         Install Dependencies
        ----------------------------------*/
        stage('Install Dependencies') {
            steps {
                sh 'npm ci || npm install'
            }
        }

        /*---------------------------------
         SonarQube Scan
        ----------------------------------*/
        stage('SonarQube Scan') {
            steps {
                script {

                    def sonarSources = sh(
                        script: '[ -d "src" ] && echo src || echo .',
                        returnStdout: true
                    ).trim()

                    withSonarQubeEnv("${SONARQUBE_SERVER}") {

                        sh """
                            mkdir -p coverage
                            echo "" > coverage/lcov.info

                            npx sonar-scanner \
                            -Dsonar.projectKey=online-book-store \
                            -Dsonar.projectName=online-book-store \
                            -Dsonar.projectVersion="${VERSION}" \
                            -Dsonar.sources="${sonarSources}" \
                            -Dsonar.javascript.lcov.reportPaths="coverage/lcov.info" \
                            -Dsonar.sourceEncoding="UTF-8"
                        """
                    }
                }
            }
        }

        /*---------------------------------
         Build App
        ----------------------------------*/
        stage('Build App') {
            steps {
                sh 'npm run build'
            }
        }

        /*---------------------------------
         Package Compress Build Folder
        ----------------------------------*/
        stage('Package Artifact') {
            steps {
                sh '''
                    tar -czf "${ARTIFACT}" -C build .
                '''
            }
        }

        /*---------------------------------
         Upload Artifact To Nexus
        ----------------------------------*/
        stage('Upload Artifact To Nexus') {

            steps {
                withCredentials([
                    usernamePassword(credentialsId: 'nexus',
                                     usernameVariable: 'NEXUS_USR',
                                     passwordVariable: 'NEXUS_PSW')
                ]) {

                    sh '''
                        UPLOAD_URL="${NEXUS_URL}/repository/${NEXUS_REPO}/${ARTIFACT}"

                        curl -f -u "${NEXUS_USR}:${NEXUS_PSW}" \
                        --upload-file "${ARTIFACT}" "${UPLOAD_URL}"
                    '''
                }
            }
        }

        /*---------------------------------
         Build Docker Image
        ----------------------------------*/
        stage('Build Docker Image') {

            steps {
                sh '''
                    docker build \
                    -t ${DOCKER_IMAGE_FULL}:${VERSION} .
                '''
            }
        }

        /*---------------------------------
         Push DockerHub Image
        ----------------------------------*/
        stage('Push DockerHub Image') {

            steps {

                withCredentials([
                    usernamePassword(credentialsId: 'dockerhub',
                                     usernameVariable: 'USER',
                                     passwordVariable: 'PASS')
                ]) {

                    sh '''
                        echo "$PASS" | docker login -u "$USER" --password-stdin
                        docker push ${DOCKER_IMAGE_FULL}:${VERSION}
                        docker logout
                    '''
                }
            }
        }

        /*---------------------------------
         Deploy To Nginx Server
        ----------------------------------*/
        stage('Deploy To Nginx') {

            steps {

                sh '''
                    VERSION="0.0.${BUILD_NUMBER}"

                    docker pull ${DOCKER_IMAGE_FULL}:${VERSION}

                    docker stop nginx || true
                    docker rm nginx || true

                    docker run -d --name nginx \
                    -p 80:80 \
                    ${DOCKER_IMAGE_FULL}:${VERSION}
                '''
            }
        }

    }

    post {
        success {
            echo "🚀 Pipeline Success → DockerHub + Nginx Deploy ✔"
        }
        failure {
            echo "❌ Pipeline Failed"
        }
    }
}

```

## 12. End-to-End Execution Evidence (Screenshots Required):

✔ GitHub push trigger
✔ Jenkins pipeline execution
✔ SonarQube dashboard & Quality Gate
✔ npm build success
✔ Nexus artifact upload
✔ Docker image build
✔ Docker Hub image
✔ NGINX container running

## 13. Security Best Practices:


Jenkins Credentials Manager used

No secrets in repository

Docker base image from trusted source

SonarQube vulnerability scanning enabled

Restricted access to Jenkins & Nexus

## 14. Troubleshooting & Learnings:

Issues Encountered

SonarQube token misconfiguration

npm dependency conflicts

Docker permission issues on Jenkins agent

Resolutions

Corrected Jenkins global configs

Clean npm cache

Added Jenkins user to Docker group

## Learnings:


Importance of fail-fast pipelines

Value of artifact traceability

Secure credential handling

## 15. Final Summary:


✔ Fully automated CI/CD pipeline
✔ JavaScript-based build
✔ Quality gate enforcement
✔ Artifact management with Nexus
✔ Dockerized deployment via NGINX
✔ Production-ready DevOps workflow

## 16. Future Enhancements:


Kubernetes deployment

Automated rollback

Image vulnerability scanning (Trivy)

Multi-environment promotion (Dev → QA → Prod)

## Images:




## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-Url:**  https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
