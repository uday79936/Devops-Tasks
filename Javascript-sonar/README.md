## 🧩 SonarQube Self-Contained Lab Setup & JavaScript Project Analysis:

## 🎯 Purpose:

This documentation guides you through installing SonarQube on Ubuntu, setting up the Sonar Scanner CLI, and performing static code analysis for a JavaScript (Angular) project — all in a self-contained single-server setup (no external DB or CI/CD pipeline required).

## 1️⃣ Prerequisites:

Component	Requirement
OS	Ubuntu 22.04 LTS
RAM	Minimum 4GB (8GB recommended)
Java	OpenJDK 17 (for running SonarQube)
User Privileges	sudo access
Application	JavaScript / Angular project
Network	Port 9000 open for SonarQube

## 2️⃣ Set Hostname:
```
sudo hostnamectl set-hostname build-sonar-test
sudo init 6
```

## 3️⃣ Install Required Packages:

**Update and install dependencies:**
```
sudo apt update -y
sudo apt install -y openjdk-17-jdk git wget unzip
```

**Verify installations:**
```
java -version
git --version
```


## 4️⃣ Install and Configure SonarQube:

**Step 1 — Create SonarQube user:**
```
sudo useradd -m -d /opt/sonarqube -r -s /bin/bash sonar
```

**Step 2 — Download and extract SonarQube:**
```
cd /opt
sudo wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-10.6.0.92116.zip
sudo unzip sonarqube-10.6.0.92116.zip
sudo mv sonarqube-10.6.0.92116 sonarqube
sudo chown -R sonar:sonar sonarqube
```

**Step 3 — Configure SonarQube:**
```
sudo vi /opt/sonarqube/conf/sonar.properties
```

**Add the following:**
```
sonar.web.host=0.0.0.0
sonar.web.port=9000
```


**5️⃣ Start SonarQube:**

**Start the SonarQube service as the sonar user:**
```
sudo -u sonar /opt/sonarqube/bin/linux-x86-64/sonar.sh start
```

**Check status:**
```
sudo -u sonar /opt/sonarqube/bin/linux-x86-64/sonar.sh status
```


**6️⃣ Access SonarQube Dashboard:**

**Open in your browser:**
```
http://<server-ip>:9000
```
**Default login credentials:**

Username: admin
Password: admin

**7️⃣ Generate Authentication Token:**

Log in to the SonarQube dashboard.

Go to My Account → Security → Generate Tokens.

Give a name, e.g., js-lab-token.

Click Generate and copy the token securely.

You’ll use this token in the CLI for authentication instead of your password.


## 8️⃣ Install Sonar Scanner (for JavaScript / Angular Analysis):

**Step 1 — Download Sonar Scanner:**

If the latest version is restricted (403 error), use a working older one:
```
cd /opt
sudo wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-5.0.1.3006-linux.zip
sudo unzip sonar-scanner-5.0.1.3006-linux.zip
sudo mv sonar-scanner-5.0.1.3006-linux sonar-scanner
Step 2 — Set up environment variables
sudo vi /etc/profile.d/sonar-scanner.sh
```
**Add:**
```
export PATH=$PATH:/opt/sonar-scanner/bin
```
**Save and reload:**
```
source /etc/profile.d/sonar-scanner.sh
```

**Step 3 — Verify installation:**
```
sonar-scanner -v
```
**Expected output:**
```
INFO: SonarScanner 5.0.2.3006
INFO: Java 17.0.7 Eclipse Adoptium (64-bit)
INFO: Linux 6.x amd64
```

## ✅ Sonar Scanner is now ready to use!

## 9️⃣ Clone JavaScript / Angular Project:
```
cd ~
git clone <your-angular-repo-url>
cd Angular-Calc-App
```
**Example:**
```
git clone https://github.com/example/Angular-Calc-App.git
cd Angular-Calc-App
```

**🔟 Configure sonar-project.properties:**

**Create the configuration file in your project root:**
```
sudo vi sonar-project.properties
```

**Paste the following content (edit IP & token):**

```
# Project identification
sonar.projectKey=AngularCalcApp
sonar.projectName=Angular Calculator App
sonar.projectVersion=1.0

# Source settings
sonar.sources=.

# Exclude unnecessary directories
sonar.exclusions=node_modules/**,**/*.spec.ts

# Encoding
sonar.sourceEncoding=UTF-8

# SonarQube server details
sonar.host.url=http://<your-sonarqube-server-ip>:9000
sonar.login=<your-generated-token>
Example:

sonar.host.url=http://13.59.89.88:9000
sonar.login=sqp_abc123xyz456token
```

Save and exit :wq or :q!

## 1️⃣1️⃣ Run SonarQube Analysis:

**Run the scanner from your project directory:**
```
sonar-scanner
```

**You’ll see logs like:**
```
INFO: Scanner configuration file: /opt/sonar-scanner/conf/sonar-scanner.properties
INFO: Project root configuration file: sonar-project.properties
INFO: Analyzing Angular Calculator App
INFO: EXECUTION SUCCESS
INFO: Analysis report uploaded to http://<server-ip>:9000
dashboard?id=AngularCalcApp
```

## 1️⃣2️⃣ Verify Results on SonarQube Dashboard:

**Open the browser and visit:**
```
http://<server-ip>:9000
```

**Navigate to:**
```
Projects → Angular Calculator App
```

**Review metrics:**

**Metric	Description:**

🐞 Bugs	Coding errors that may cause issues
🔐 Vulnerabilities	Security flaws
✅ Code Smells	Maintainability issues
📊 Duplications	Repeated code blocks

## ✅ Lab Status Checklist:

Step	Status

Java Installed	✅

SonarQube Installed & Configured	✅

SonarQube Running on Port 9000	✅

Sonar Scanner Installed	✅

JavaScript Project Cloned	✅

sonar-project.properties Created	✅

Project Analyzed Successfully	✅

Results Visible on Dashboard	✅

## Images:



## Author:

**Uday Sairam**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:**  https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/