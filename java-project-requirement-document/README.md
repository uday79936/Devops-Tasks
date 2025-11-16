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

## 1. EC2 Server:

<img width="1917" height="917" alt="Image" src="https://github.com/user-attachments/assets/a547c5fa-5c0c-4c60-9068-c54f7608a9cb" />

## 2. Change permissions to docker path:

<img width="1918" height="177" alt="Image" src="https://github.com/user-attachments/assets/2aeaaa8b-2482-42f4-84b7-027226d2ef5f" />

## 3. Jenkins Installation:

<img width="1018" height="512" alt="Image" src="https://github.com/user-attachments/assets/aaca3aac-eb77-4973-96a6-75b960180b73" />

## 4. in jenkins container in install docker via root:

<img width="1752" height="503" alt="Image" src="https://github.com/user-attachments/assets/a650f64b-ec5a-4673-9356-8edad61ba370" />

## 5. In Jenkins container see the Docker version:

**docker version part-1:**

<img width="1912" height="820" alt="Image" src="https://github.com/user-attachments/assets/f251da9e-1471-4c37-816f-3c9b2b759bd6" />

**docker version part-2:**

<img width="1912" height="910" alt="Image" src="https://github.com/user-attachments/assets/bf60fb63-5fd1-4b07-bbcf-a3fb93ed4fbe" />

## 5. Sonarqube installation:

<img width="1147" height="447" alt="Image" src="https://github.com/user-attachments/assets/e32faccd-980a-4394-bd7c-18d6e54ec43d" />

## 6. Nexus Installation:

<img width="1133" height="448" alt="Image" src="https://github.com/user-attachments/assets/9c58083f-6d74-4cf6-a461-4a4964fa8c47" />

## 7. Docker Images:

<img width="861" height="221" alt="Image" src="https://github.com/user-attachments/assets/17cb0485-ba4b-4601-b708-25d4ba245135" />

## 8. Docker Containers:

<img width="1891" height="232" alt="Image" src="https://github.com/user-attachments/assets/fb08f502-b8fc-43fa-a038-3f7c62750165" />

## 9. Jenkins admin.password:

<img width="978" height="113" alt="Image" src="https://github.com/user-attachments/assets/9ffbded0-4952-4f39-b473-7fb380cb5557" />

## 10. Sonar Token:

<img width="1913" height="960" alt="Image" src="https://github.com/user-attachments/assets/98b8ae84-cfa1-4e98-9be6-040a849faee8" />

## 11. Nexus admin.password:

<img width="1886" height="328" alt="Image" src="https://github.com/user-attachments/assets/a0f79ed2-a577-4dc3-97d6-c4d74ee3a832" />

## 12. Credientials:

<img width="1913" height="961" alt="Image" src="https://github.com/user-attachments/assets/b1629780-cdc5-4b2c-a834-5aa4799c1c91" />

## 13. Plugins Installed:

<img width="1913" height="980" alt="Image" src="https://github.com/user-attachments/assets/a5ec3e14-5777-47aa-858e-8f547cf11507" />

## 14. console outputs:

**Build-Success:**

<img width="1913" height="980" alt="Image" src="https://github.com/user-attachments/assets/a5ec3e14-5777-47aa-858e-8f547cf11507" />

**Sonarqube-analysis-success:**

<img width="1915" height="907" alt="Image" src="https://github.com/user-attachments/assets/13aff849-59b3-4b97-8770-e71a4477419e" />

**nexus artifact success:**

<img width="1911" height="962" alt="Image" src="https://github.com/user-attachments/assets/0c448f2e-38f5-421e-91fd-abae5d6b3093" />

**Docker image created success and pushed to dockerhub:**

<img width="1918" height="971" alt="Image" src="https://github.com/user-attachments/assets/bbceb3fa-5670-4767-9897-214ea8dbe2bf" />

**Deploy to Tomcat Success:**

<img width="1912" height="966" alt="Image" src="https://github.com/user-attachments/assets/547967d8-ee2a-4d9b-be6d-681e1f5f8806" />

## 15. Sonarqube Output success:

<img width="1913" height="976" alt="Image" src="https://github.com/user-attachments/assets/0c9745c9-2a55-4dcb-a8a7-8d1f8a1c87e9" />

## 16. Nexus artifactory output:

<img width="1907" height="982" alt="Image" src="https://github.com/user-attachments/assets/1bfc9f8c-be3d-4129-a32f-7c06406d7b8a" />

## 17. Docker Repository output:

<img width="1916" height="973" alt="Image" src="https://github.com/user-attachments/assets/1459f5e5-279f-418b-9692-d08f849bcdb1" />

## 18. Tomcat container in war file:

<img width="763" height="151" alt="Image" src="https://github.com/user-attachments/assets/bff0fcb6-5dfb-454f-b6d9-fa4b17012810" />

## 19. pipeliine success:

**pipeline output:**

<img width="1916" height="973" alt="Image" src="https://github.com/user-attachments/assets/eebe9aa2-207b-4a31-bc86-4ce694714979" />

**pipeline overview:**

<img width="1917" height="972" alt="Image" src="https://github.com/user-attachments/assets/9d1d2c79-c4d7-453c-adfc-a433b467a722" />

## 20. output:

<img width="1907" height="972" alt="Image" src="https://github.com/user-attachments/assets/6b42df96-ac5c-4539-af1f-a6b68f528213" />

## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:**  https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/

