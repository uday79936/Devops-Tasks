## End-to-End CI/CD Pipeline: GitHub → SonarQube → Maven → Nexus → Docker Hub → Jenkins:

## Project Overview:


This project implements a fully automated CI/CD pipeline for Java projects. Once code is pushed to GitHub, the pipeline performs the following steps:

Clone the latest code from GitHub.

Perform static code analysis using SonarQube.

Build the project using Maven (including running unit tests).

Upload the generated artifact (JAR/WAR) to Nexus Repository Manager.

Build a Docker image containing the artifact.

Push the Docker image to Docker Hub.

All stages are orchestrated via a Jenkins declarative pipeline.

## This pipeline ensures:

Code quality enforcement via SonarQube.

Versioned artifact management via Nexus.

Containerized deployable artifacts via Docker.

Traceability from Git commit → build → artifact → Docker image.

Automated notifications for success/failure.

## Objectives:


Fully automated pipeline requiring no manual intervention post code push.

Ensure code quality, versioning, and traceability.

Provide a reusable, scalable infrastructure for future Java projects.

Transparent logging, metrics, and notifications for stakeholders.

## Stakeholders:


Development Team: Pushes code to GitHub.

DevOps Team: Implements and maintains the CI/CD pipeline.

QA Team: Validates quality criteria and test coverage.

Security Team: Ensures secure handling of credentials and pipeline best practices.

Project Manager: Monitors pipeline delivery and effectiveness.

## Functional Requirements:

**1. GitHub Integration**

Repository hosted on GitHub.

Webhook triggers Jenkins pipeline on pushes to main or develop branch.

Capture Git commit hash for traceability.

**2. SonarQube Integration**

Run SonarQube Scanner using Maven plugin.

Enforce a Quality Gate:

Minimum code coverage: 80%

No new critical vulnerabilities or blockers

Maximum allowed code duplication or code smells

Pipeline stops and notifies stakeholders if Quality Gate fails.

**3. Maven Build**

Build using Maven: mvn clean install

Run unit tests: mvn test

Artifact output: JAR/WAR

Version artifact using Git commit hash or Maven version.

**4. Artifact Management (Nexus)**

Upload artifact to Nexus repository.

Manage credentials securely via Jenkins Credentials Store.

Ensure artifact is retrievable for Docker build.

**5. Docker Image Creation**

Dockerfile present in repository.

Docker image includes Maven-built artifact.

Image tagged using version/commit hash (e.g., myapp:1.0.0-ab12cd3).

**6. Push Docker Image to Docker Hub**

Authenticate Jenkins to Docker Hub.

Push image using docker push <repo>:<tag>

Image available for downstream deployments.

**7. Jenkins Pipeline**

Declarative Jenkinsfile defines the pipeline stages:

Checkout

SonarQube Analysis

Build (Maven)

Upload Artifact to Nexus

Build Docker Image

Push Docker Image.

## Technical Specifications:


GitHub: Repository URL, branch monitored, webhook configured.

SonarQube: Version 8.x+, quality gate defined, API token in Jenkins.

Maven: Version 3.6+, commands: mvn clean test package.

Nexus: Version 3.x, artifact repository, retention policy.

Docker & Docker Hub: Docker runtime 20.x+, image tagging using commit hash.

Jenkins: LTS 2.x, required plugins (Git, Pipeline, Maven, Docker, SonarQube, Credentials, Slack/Email).

## Sample Jenkins Pipeline Structure:
```
pipeline {
    agent any

    tools {
        jdk 'JDK'
        maven 'MAVEN'
    }

    environment {
        JAVA_HOME = tool name: 'JDK', type: 'jdk'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        DOCKER_IMAGE = "udaysairam/java-webapp:${env.BUILD_NUMBER}"
        MAVEN_SETTINGS = "temp-settings.xml"
        SONAR_HOST = "http://34.201.99.100:9000"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/uday79936/Java-Web-Calculator-App.git'
            }
        }

        stage('Build with Maven') {
            steps {
                echo "Running Maven build..."
                sh 'mvn clean verify -B'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo "Running SonarQube analysis..."
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh """
                        mvn sonar:sonar -B \
                          -Dsonar.projectKey=java-webapp-region \
                          -Dsonar.host.url=${SONAR_HOST} \
                          -Dsonar.login=\$SONAR_TOKEN
                    """
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                echo "Deploying artifact to Nexus..."
                writeFile file: "${MAVEN_SETTINGS}", text: """
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd">
  <servers>
    <server>
      <id>maven-releases</id>
      <username>admin</username>
      <password>admin123</password>
    </server>
  </servers>
</settings>
"""
                sh """
                    mvn deploy -B -s ${MAVEN_SETTINGS} \
                      -DaltDeploymentRepository=maven-releases::default::http://34.201.99.100:8081/repository/maven-releases/
                """
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image: ${DOCKER_IMAGE}"
                sh """
                    docker build -t ${DOCKER_IMAGE} .
                """
            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo "Pushing Docker image to Docker Hub: ${DOCKER_IMAGE}"
                withCredentials([usernamePassword(credentialsId: 'docker_hub', usernameVariable: 'DOCKER_HUB_USR', passwordVariable: 'DOCKER_HUB_PSW')]) {
                    sh """
                        echo \$DOCKER_HUB_PSW | docker login -u \$DOCKER_HUB_USR --password-stdin
                        docker push ${DOCKER_IMAGE}
                    """
                }
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                echo "Deploying Docker container for Tomcat..."
                sh """
                    docker stop TOMCAT || true
                    docker rm TOMCAT || true
                    docker run -d --name TOMCAT -p 8501:8080 ${DOCKER_IMAGE}
                """
            }
        }
    }

    post {
        always {
            echo "Pipeline finished."
            sh 'rm -f ${MAVEN_SETTINGS}'
        }
        success {
            echo "Pipeline succeeded! All steps completed."
        }
        failure {
            echo "Pipeline failed! Check logs for details."
        }
    }
}

```


## Milestones & Timeline:

Milestone	        Duration	          Description

GitHub Webhook & Jenkins	 2-3 days	     Configure repository, webhook, basic job


SonarQube Integration	3-4 days	Setup server, plugin, quality gate

Maven Build Setup	2-3 days	Build project, run unit tests

Nexus Integration	2 days	Configure repository, upload artifact

Docker Image Build & Push	3-4 days	Create Dockerfile, build, push image

Full Pipeline Orchestration	2-3 days	Combine all steps in Jenkinsfile

Testing & QA	3-4 days	End-to-end pipeline testing

Documentation & Handoff	1-2 days	Final documentation


## Deliverables:


Jenkinsfile implementing full pipeline.

Sample Maven project with Dockerfile.

Documentation: setup steps, screenshots, pipeline explanation.

Nexus and Docker Hub configurations.

How to Use This Repository

## 1. Clone the repository:
```
git clone <repo-url>
```


## 2. Push code to GitHub (main/develop branch) to trigger pipeline.

## 3. Monitor Jenkins job for build, SonarQube analysis, artifact upload, Docker image build and push.

## 4. Check SonarQube dashboard for code quality.

## 5. Verify artifact in Nexus and Docker image in Docker Hub.

## Images:

## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:**  https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/

