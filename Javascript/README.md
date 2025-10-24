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
sudo hostnamectl set-hostname javascript-build-server
```

## 1.2 Install Node.js, npm, and Angular CLI:
```
sudo apt install nodejs npm -y
node -v
npm -v
sudo npm install -g @angular/cli

```


## 📸 Screenshot 1: Node.js, npm, and Angular CLI installation successful.

<img width="1892" height="742" alt="Image" src="https://github.com/user-attachments/assets/9d332c6d-bf1e-44fa-8594-c76748e50f16" />


## 1.3 Clone the Angular Calculator Repository:
```
git clone https://github.com/Ai-TechNov/AngularCalculator.git
cd AngularCalculator
```

## 1.4 Install Project Dependencies:
```
npm install
```

## 📸 Screenshot 2: Dependencies installed successfully.

<img width="1892" height="742" alt="Image" src="https://github.com/user-attachments/assets/9d332c6d-bf1e-44fa-8594-c76748e50f16" />

## 1.5 Build Angular Application for Production:
```
sudo ng build --prod
```


**After the build completes, a new directory dist/angularCalc is created.**

## 📸 Screenshot 3: Successful build output with dist/ folder visible.

<img width="1420" height="170" alt="Image" src="https://github.com/user-attachments/assets/a5f61213-fa37-4b47-835e-443a1bc52525" />

## 🚀 VM2 – Deployment Environment Setup:

**2.1 Update and Configure System:**
```
sudo apt -y update
sudo apt -y upgrade
sudo hostnamectl set-hostname javascript-deploy-server
```

## 2.2 Install and Start Nginx:
```
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
sudo systemctl status nginx
```

## 📸 Screenshot 4: Nginx running and active.

<img width="1663" height="503" alt="Image" src="https://github.com/user-attachments/assets/36171e48-4cfe-4d61-bf49-dc3dbcbdc555" />

## 🔁 Transfer Angular Build Files from VM1 to VM2:

## 3.1 Navigate to Build Directory on VM1:
```
cd ~/AngularCalculator/dist/angularCalc
```

## 3.2 Transfer Files Using SCP:
```
scp -i <pemfile> ubuntu@ip-address:/home/ubuntuAngularCalculator/dist/class.tar.gz ubuntu@ip-address:/home/ubuntu
```

Replace <vm2-ip> with VM2’s public IP.


📸 Screenshot 5: SCP transfer output showing successful file transfer.

<img width="1886" height="401" alt="Image" src="https://github.com/user-attachments/assets/56ef3cac-fe9c-4ff4-8669-112d2dd83361" />

## 3.3 Verify Files on VM2:


**On VM2, verify that files exist in /var/www/html:**
```
ls /var/www/html/
```

## 📸 Screenshot 6: Files from angularCalc visible in /var/www/html/.

<img width="1515" height="151" alt="Image" src="https://github.com/user-attachments/assets/36108562-dbde-4ba4-8a93-95f74b5ffc20" />

## Javascript output:

<img width="1895" height="957" alt="Image" src="https://github.com/user-attachments/assets/1ab5b899-14af-4124-a09a-126661394c36" />

## Javascript output test:

<img width="1911" height="981" alt="Image" src="https://github.com/user-attachments/assets/dd61b44a-753a-4d9c-99d5-84d70947b0f8" />


## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
