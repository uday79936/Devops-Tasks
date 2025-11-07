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




## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
