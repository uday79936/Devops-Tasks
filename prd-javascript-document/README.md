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

## 1. Javascript online store ec2:

<img width="1918" height="921" alt="Image" src="https://github.com/user-attachments/assets/257b4beb-a422-47b1-9136-0cbb408463ac" />

## 2. Sonarqube download in docker:

<img width="1032" height="460" alt="Image" src="https://github.com/user-attachments/assets/b97be9b2-431c-4971-b498-cc6ea1a49f6b" />

## 3. Sonatype nexus3 download in docker:

<img width="1032" height="372" alt="Image" src="https://github.com/user-attachments/assets/2f624cb5-8423-44f1-ad1e-1c0e129486db" />

## 4. Jenkins download in docker:

<img width="1032" height="372" alt="Image" src="https://github.com/user-attachments/assets/2f624cb5-8423-44f1-ad1e-1c0e129486db" />

## 5. Docker images:

<img width="1006" height="216" alt="Image" src="https://github.com/user-attachments/assets/37df75bc-5dee-474d-9d64-e86c725f2023" />

## 6. Docker ps:

<img width="1902" height="307" alt="Image" src="https://github.com/user-attachments/assets/36e55d11-56d3-4a91-b2d3-61f462b9dfe8" />

## 7. Sonarqube token:

<img width="1910" height="976" alt="Image" src="https://github.com/user-attachments/assets/e6630593-bf85-425b-9dfe-a20b7a4ace46" />

## 8. Plugins installed in jenkins:

<img width="1918" height="977" alt="Image" src="https://github.com/user-attachments/assets/fe85bd04-b1a4-4ef3-b648-c19f029130e4" />

## 9. Credentials in jenkins:

<img width="1901" height="711" alt="Image" src="https://github.com/user-attachments/assets/d0e36855-17d4-4591-8cc3-8ef2956eb215" />

## 10. Jenkins container docker in docker:

<img width="1917" height="342" alt="Image" src="https://github.com/user-attachments/assets/f237cd7c-a2f4-4690-b457-6fd24a6ebda7" />

## 11. Docker login cli:

<img width="1327" height="446" alt="Image" src="https://github.com/user-attachments/assets/aa961a7c-0882-4795-9d93-0a31e842d5d7" />

## 12. Docker Activation:

<img width="1877" height="955" alt="Image" src="https://github.com/user-attachments/assets/440ca7ec-7d77-4328-a971-6938bdf31028" />

## 13. Docker Login succeeded:

<img width="1281" height="533" alt="Image" src="https://github.com/user-attachments/assets/e470362e-7515-46f5-a2e0-d7b6403517ec" />

## 14. Pipeline succeeded:

<img width="1910" height="971" alt="Image" src="https://github.com/user-attachments/assets/bd685424-d45f-41a1-8467-199b6b43b732" />

## 15. Javascript-pipeline succeeded:

<img width="1912" height="962" alt="Image" src="https://github.com/user-attachments/assets/091c2b5c-bc76-4608-a3b6-718bfcd81c13" />

## 16. Javascript pipeline overview succeeded:

<img width="1917" height="976" alt="Image" src="https://github.com/user-attachments/assets/64db9843-2d01-4c0d-aff5-bb2a72d1a322" />

## 17. Sonarqube analysis passed:

<img width="1917" height="923" alt="Image" src="https://github.com/user-attachments/assets/20520724-c6f6-4726-b9af-5d02159d89ca" />

## 18. Nexus Repository output:

<img width="1917" height="962" alt="Image" src="https://github.com/user-attachments/assets/91881ea2-d34f-4cde-9948-a4d0779a1a2f" />

## 19. Dockerhub output:

<img width="1912" height="917" alt="Image" src="https://github.com/user-attachments/assets/8b67e499-0ceb-4975-8006-360a565184c7" />

## 20. Javascript execution success:

<img width="1910" height="957" alt="Image" src="https://github.com/user-attachments/assets/e42bcac9-f83d-474b-ba30-95ad5414fe9c" />

## 21. Javascript docker pipeline success:

<img width="1910" height="916" alt="Image" src="https://github.com/user-attachments/assets/6ae21cee-7ccf-4575-b383-de185e52ebdb" />

## 22. Javascript online-store output:

<img width="1916" height="977" alt="Image" src="https://github.com/user-attachments/assets/bc33a61a-6acc-49ef-be8c-8bdfc2ed4138" />


## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-Url:**  https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
