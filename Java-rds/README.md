# 🚀 Java + MySQL Web Application Deployment on Tomcat using AWS RDS

## 📌 Objective

Deploy a Java-based login web application on Apache Tomcat, backed by a MySQL database hosted on AWS RDS. This project demonstrates full-stack integration and cloud deployment skills.

---

## 🧾 Table of Contents

- [📦 Prerequisites](#-prerequisites)
- [🔧 Project Setup](#-project-setup)
- [🛠️ RDS Configuration](#%EF%B8%8F-rds-configuration)
- [🧱 Database Schema](#-database-schema)
- [⚙️ Application Configuration](#%EF%B8%8F-application-configuration)
- [📤 Build and Deploy](#-build-and-deploy)
- [🌐 Access the App](#-access-the-app)
- [✅ Screenshots](#-screenshots)
- [🎯 Bonus Challenges](#-bonus-challenges)
- [📁 Project Structure](#-project-structure)
- [📚 License](#-license)

---

## 📦 Prerequisites

- Java 8+
- Maven
- Apache Tomcat 9/10
- AWS account with RDS access
- MySQL Client
- Git
- EC2 instance (for Tomcat deployment)

---

## 🔧 Project Setup

# Clone the repository
```
git clone https://github.com/mrtechreddy/aws-rds-java.git
```
```
cd LoginWebApp
```
## 🛠️ RDS Configuration:

1. Create MySQL DB on AWS RDS
Engine: MySQL 8.x

DB Identifier: jwt

Username: admin

Password: admin123

Public Access: Enabled

Port: 3306

Security Group: Allow your IP or EC2 IP on port 3306

## ✅ After creation, note your RDS endpoint. Example:

```
ai-db.crno3nsbxzen.us-west-1.rds.amazonaws.com
```
## 🧱 Database Schema:

**Connect to the DB:**
```
mysql -h <your-rds-endpoint> -u admin -p
```
## Create database and table:
```
CREATE DATABASE jwt;
USE jwt;
```
```
CREATE TABLE `USER` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(100) NOT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(150) NOT NULL,
  `username` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `regdate` DATE NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_username` (`username`),
  UNIQUE KEY `unique_email` (`email`)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci;
  ```
## ⚙️ Application Configuration:

**Update DB connection details in the following files:**
```
src/main/webapp/login.jsp

src/main/webapp/userRegistration.jsp
```
Replace:

```
String jdbcURL = "jdbc:mysql://<your-rds-endpoint>:3306/jwt?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
String dbUser = "admin";
String dbPass = "admin123";
With your actual RDS details.
```
## 📤 Build and Deploy:

**1. Package the application:**
```
mvn clean package
Result: target/LoginWebApp.war
```

## 2. Deploy to Tomcat:

```
sudo cp target/LoginWebApp.war /opt/tomcat/webapps/
sudo systemctl restart tomcat
# or
/opt/tomcat/bin/shutdown.sh
/opt/tomcat/bin/startup.sh
```
## 🌐 Access the App:

Open your browser:

```
http://<EC2-Public-IP>:8080/LoginWebApp/
```
## ✅ Screenshots:

✅ Login page running on Tomcat

✅ User registration and login

✅ Data stored in AWS RDS

Add your screenshots here.

## 🎯 Bonus Challenges:

🔐 Secure DB credentials using environment variables

☁️ Deploy EC2 and RDS inside the same VPC

🔒 Add HTTPS with Nginx as reverse proxy

🤖 Automate builds using GitHub Actions or Jenkins

## 📁 Project Structure:

pgsql
Copy code
LoginWebApp/
├── pom.xml
└── src/
    └── main/
        └── webapp/
            ├── WEB-INF/web.xml
            ├── index.jsp
            ├── login.jsp
            ├── register.jsp
            ├── userRegistration.jsp
            ├── success.jsp
            └── welcome.jsp
📚 License

## Author:

**Uday Sairam Kommineni**

**MAIL-Id:** saikommineni5@gmail.com

**Linkedin-Url:**  https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/