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

## 1. Jenkins Lab Server:

<img width="1917" height="908" alt="Image" src="https://github.com/user-attachments/assets/f00a8115-5e4f-4ba4-884d-1f5602ec4a9a" />

## 2. Change Hostname:

<img width="948" height="140" alt="Image" src="https://github.com/user-attachments/assets/f137e7b7-e515-4250-8525-fc47ad72be5a" />

## 3. Update the server java and versions:

<img width="811" height="172" alt="Image" src="https://github.com/user-attachments/assets/60640290-f466-4527-a362-7a51d4b29a43" />

## 4. Installation of Java:

<img width="1917" height="966" alt="Image" src="https://github.com/user-attachments/assets/d61c573d-a144-4980-82ba-170a07d2c2fb" />

## 5. Long term support in jenkins:

<img width="1915" height="968" alt="Image" src="https://github.com/user-attachments/assets/40dd8315-d27f-44ca-b5ae-efc39e21d9ed" />

## 6. Install Jenkins key:

<img width="1140" height="205" alt="Image" src="https://github.com/user-attachments/assets/404a7eb2-b9e3-497d-a65b-847ad72e3739" />

## 7. Install Jenkins:

<img width="545" height="148" alt="Image" src="https://github.com/user-attachments/assets/80e665d1-8f9d-4219-ba93-0ef896af10ff" />

## 8. Start the Jenkins:

<img width="1918" height="680" alt="Image" src="https://github.com/user-attachments/assets/f7df3f3f-733e-4a6e-989e-f74d0a124df8" />

## 9. Jenkins Get started:

<img width="1918" height="968" alt="Image" src="https://github.com/user-attachments/assets/18044283-7f21-46d7-80f5-e281b704282d" />

## 10. To see the jenkins password:

<img width="1007" height="122" alt="Image" src="https://github.com/user-attachments/assets/61ec977c-e86d-48c5-b2c1-97ac2eb157ab" />

## 11. Create admin user in jenkins:

<img width="1615" height="937" alt="Image" src="https://github.com/user-attachments/assets/65810439-4bf0-4ec1-abf2-4a8d9a8ce04c" />

## 12. Jenkins Dashboard:

<img width="1918" height="967" alt="Image" src="https://github.com/user-attachments/assets/56b1420f-ee6c-4872-9e69-593e7b5a9a3d" />

## 13. Manage Jenkins:

<img width="1918" height="970" alt="Image" src="https://github.com/user-attachments/assets/9f48e058-7485-48f3-a7d3-869610db33b9" />

## 14. Users in jenkins:

<img width="1918" height="962" alt="Image" src="https://github.com/user-attachments/assets/d1e25f75-ea89-4b0d-9f36-1d39d757b5fd" />

## 15. Create user in jenkins:

<img width="1917" height="962" alt="Image" src="https://github.com/user-attachments/assets/ccdb7017-dc57-4a08-b48c-db243fe966e5" />

## 16. Jenkins own database:

<img width="1911" height="946" alt="Image" src="https://github.com/user-attachments/assets/ea7c2ab0-9f8f-434b-b823-0cb45c5b4fae" />

## 17. New Item in jenkins:

<img width="1918" height="966" alt="Image" src="https://github.com/user-attachments/assets/a890eaaa-9845-47f3-8ded-59d2939ca672" />

## 18. Sample project in jenkins:

<img width="1916" height="962" alt="Image" src="https://github.com/user-attachments/assets/b8172470-48ec-4dca-8bc2-517651c492c5" />

## 19. Configuration in jenkins:

<img width="1918" height="971" alt="Image" src="https://github.com/user-attachments/assets/0f7532a6-6987-42e1-b341-a0386165d311" />

## 20. Sample project in param links:

<img width="1917" height="972" alt="Image" src="https://github.com/user-attachments/assets/dbeb9b78-7f62-4f88-abef-899b567a8998" />

## 21. Homepage of jenkins:

<img width="1916" height="972" alt="Image" src="https://github.com/user-attachments/assets/33d18a77-4809-49e8-b8a9-2043e99ede24" />

## 22. Excute shell giving message:

<img width="1912" height="962" alt="Image" src="https://github.com/user-attachments/assets/27daff65-7aa7-46ac-a551-acf68e4017b5" />

## 23. Console Output:

<img width="1913" height="967" alt="Image" src="https://github.com/user-attachments/assets/5eb369f1-6b77-49e5-a37d-2443d4426379" />



## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
