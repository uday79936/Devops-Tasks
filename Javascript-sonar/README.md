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

## 1. Javascript sonarcube ec2:

<img width="1918" height="912" alt="Image" src="https://github.com/user-attachments/assets/9fe71c1a-8cb4-4653-b6d7-c77bac1f2057" />

## 2. Git and Java version:

<img width="1162" height="232" alt="Image" src="https://github.com/user-attachments/assets/0e43651b-102d-425a-a731-4da8329bdc51" />

## 3. Extract the sonarcube:

<img width="976" height="191" alt="Image" src="https://github.com/user-attachments/assets/1d675676-9eac-473d-84e2-53477713fd3b" />

## 4. Edit the sonar.properties:

<img width="1910" height="1016" alt="Image" src="https://github.com/user-attachments/assets/391c5b25-89d1-4ccc-8999-965ae0764136" />

## 5. Loginpage of sonarcube:

<img width="1912" height="976" alt="Image" src="https://github.com/user-attachments/assets/41b1e65c-b56a-4598-9c2f-a28544dc1cb4" />

## 6. Update password of sonarcube:

<img width="1918" height="906" alt="Image" src="https://github.com/user-attachments/assets/1858966d-2b36-40d7-b0ef-239aee4a02b3" />

## 7. Token Generated:

<img width="1912" height="972" alt="Image" src="https://github.com/user-attachments/assets/25f7685a-45f4-433a-a216-b0efb4d3c088" />

## 8. ## Sonar scanner Download:

<img width="1913" height="408" alt="Image" src="https://github.com/user-attachments/assets/a68dacc2-c273-4709-a80d-d14c6934a078" />

## 9. ## Move sonar scanner:

<img width="1783" height="221" alt="Image" src="https://github.com/user-attachments/assets/1623bb94-3344-46ed-9efc-f4769667758c" />

## 10. Sonar scanner:

<img width="1751" height="377" alt="Image" src="https://github.com/user-attachments/assets/52da94d1-20f6-4e1f-a266-1cd81245694b" />

## 11. Clone the repository:

<img width="1233" height="241" alt="Image" src="https://github.com/user-attachments/assets/379800dc-4e5c-4bf9-bb6e-5b0ddfe3640f" />

## 12. Change the directory Angular-calc:

<img width="1012" height="140" alt="Image" src="https://github.com/user-attachments/assets/8696c1e1-cd1d-4083-8adf-fe67d160ba97" />

## 13. Sonar.properties:

<img width="880" height="472" alt="Image" src="https://github.com/user-attachments/assets/6f159883-8383-482b-9bd9-318a72597395" />

## 14. Sonar scanner success:

<img width="1916" height="1008" alt="Image" src="https://github.com/user-attachments/assets/e2691d84-633f-46ff-b157-9b5abff9fcd0" />

## 15. Sonar Output:

<img width="1913" height="977" alt="Image" src="https://github.com/user-attachments/assets/6bda0b79-020b-4d49-8b5c-0df013296d40" />

## 16. Sonar Quality Gate Output:

<img width="1918" height="972" alt="Image" src="https://github.com/user-attachments/assets/bf90f4c1-0037-4ef4-b8ee-b49214421d24" />


## Author:

**Uday Sairam**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:**  https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
