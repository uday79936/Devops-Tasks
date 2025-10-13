## 📘 Project: Advanced Angular Application Development and Deployment Across Two Virtual Machines (VMs):

## 🎯 Objective:


This project demonstrates the complete process of developing, building, and deploying an Angular Calculator application across two separate Ubuntu VMs:

VM1 (Development VM): Used for developing and building the Angular application.

VM2 (Deployment VM): Used to deploy the built application using Nginx.

## 🏗️ VM1 – Development Environment Setup:

**1.1 Update and Configure System:**
```
sudo apt -y update
sudo apt -y upgrade
sudo hostnamectl set-hostname vm1-dev
```

## 1.2 Install Node.js, npm, and Angular CLI:
```
sudo apt install nodejs npm -y
node -v
npm -v
sudo npm install -g @angular/cli

```


📸 Screenshot 2: Node.js, npm, and Angular CLI installation successful.


## 1.3 Clone the Angular Calculator Repository:
```
git clone https://github.com/Ai-TechNov/AngularCalculator.git
cd AngularCalculator
```

## 1.4 Install Project Dependencies:
```
npm install
```

📸 Screenshot 4: Dependencies installed successfully.

## 1.5 Build Angular Application for Production:
```
sudo ng build --prod
```


**After the build completes, a new directory dist/angularCalc is created.**

📸 Screenshot 5: Successful build output with dist/ folder visible.

## 🚀 VM2 – Deployment Environment Setup:

**2.1 Update and Configure System:**
```
sudo apt -y update
sudo apt -y upgrade
sudo hostnamectl set-hostname vm2-prod
```

## 2.2 Install and Start Nginx:
```
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
sudo systemctl status nginx
```

📸 Screenshot 7: Nginx running and active.

## 🔁 Transfer Angular Build Files from VM1 to VM2:

## 3.1 Navigate to Build Directory on VM1:
```
cd ~/AngularCalculator/dist/angularCalc
```

## 3.2 Transfer Files Using SCP:
```
scp -r * ubuntu@<vm2-ip>:/var/www/html/
```

Replace <vm2-ip> with VM2’s public IP.


📸 Screenshot 8: SCP transfer output showing successful file transfer.

## 3.3 Verify Files on VM2:


**On VM2, verify that files exist in /var/www/html:**
```
ls /var/www/html/
```

📸 Screenshot 9: Files from angularCalc visible in /var/www/html/.

## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/