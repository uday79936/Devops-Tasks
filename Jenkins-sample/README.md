## Jenkins Installation on AWS EC2:

This guide provides a step-by-step walkthrough to install and configure Jenkins on an AWS EC2 t2.micro instance running Ubuntu.
It also includes steps on how to create your first Jenkins user and project.

## 🧰 Prerequisites:

Before you begin, ensure you have:

An AWS EC2 instance (t2.micro or larger) running Ubuntu
SSH access to the instance
sudo privileges
Port 8080 open in the Security Group (for Jenkins web access)

## 🚀 Step 1: Connect to Your EC2 Instance:

**Use SSH to connect to your EC2 instance:**

```
ssh -i your-key.pem ubuntu@<your-ec2-public-ip>
```
## 🖥️ Step 2: Set the Hostname:

## Rename the instance for easier identification and reboot to apply the change:
```
sudo hostnamectl set-hostname Jenkins-Lab
sudo init 6
```
**The sudo init 6 command restarts your EC2 instance.**

After the reboot, reconnect to your EC2 instance using SSH.


## ⚙️ Step 3: Update Packages and Install Java:

Update your package list and install OpenJDK 21, which Jenkins requires:

```
sudo apt update
sudo apt install fontconfig openjdk-21-jre -y
java -version
```
Run java -version to verify that Java has been installed successfully.


## 🧩 Step 4: Install Jenkins:

**Add the Jenkins repository, import its GPG key, and install Jenkins: 11**
```
sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key

echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc]" \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

sudo apt update
sudo apt install jenkins -y
```

## ▶️ Step 5: Start and Enable Jenkins:

**Start the Jenkins service and enable it to launch automatically at boot:**
```
sudo systemctl enable jenkins
sudo systemctl start jenkins
sudo systemctl status jenkins
```

## 🌐 Step 6: Access Jenkins:

**Once Jenkins is running, open your web browser and navigate to:**
```
http://<your-ec2-public-ip>:8080
```

**Retrieve the Jenkins administrator password with:**
```
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

## ✅ Step 7: Complete the Initial Setup:

**Choose Install suggested plugins:**

Wait for the plugin installation to complete.
Create your first admin user (see details below).
Confirm the Jenkins URL and finish the setup wizard.


## 👤 Step 8: Create a Jenkins User (Admin Account):

After Jenkins installation, you’ll be prompted to create your first user:

**Enter your desired credentials:**
```
Username: (e.g., admin)
Password: (your secure password)
Full name: (optional)
Email address: (optional)
Click Save and Continue.
Confirm the Jenkins URL (usually http://<your-ec2-public-ip>:8080) and click Save and Finish.
```
**If you need to create additional users later:**
```
From the Jenkins dashboard, go to Manage Jenkins → Users → Create User.
Fill in the required details.
Click Create User to save.
```
## 🛠️ Step 9: Create Your First Jenkins Project (Freestyle Job):
```
From the Jenkins dashboard, click “New Item”.
Enter a project name (e.g., My-First-Project).
Select “Freestyle project” and click OK.
Click Save.
To run the job, click Build Now on the project page.
View the console output by clicking the build number under Build History.
```
## 🧾 Notes:

Instance Type: t2.micro

Default Jenkins Port: 8080

## Service Commands:
```

Start Jenkins: sudo systemctl start jenkins
Stop Jenkins: sudo systemctl stop jenkins
Restart Jenkins: sudo systemctl restart jenkins
Check Status: sudo systemctl status jenkins
```

Ensure your EC2 Security Group allows inbound traffic on port 8080 to access the Jenkins web interface.

## Images:


## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/