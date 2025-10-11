## 📦 Java + MySQL Web App Deployment on Tomcat using AWS RDS(IAAS):

**📋 Project Overview:**

This project demonstrates how to deploy a Java-based login web application on Apache Tomcat, integrated with a MySQL database hosted on AWS RDS (IaaS). The application allows user registration and login functionality.

**📁 Project Structure:**
```
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
```
**🛠️ Step-by-Step Instructions:**

## 1️⃣ Clone the Repository:
```
git clone https://github.com/mrtechreddy/aws-rds-java.git
cd aws-rds-java/LoginWebApp
```
## 2️⃣ Create a MySQL Database on AWS RDS IAAS:

Launch an RDS MySQL instance in AWS.

Note the endpoint, username, and password.

**Connect to RDS via CLI:**
```
mysql -h <your-ip-address> -u admin -p
```
**Create the Database and Table:**
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
## 3️⃣ Update DB Credentials in JSP Files:


**Update login.jsp and userRegistration.jsp:**
```
String jdbcURL = "jdbc:mysql://<your-rds-endpoint>:3306/jwt?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
String dbUser = "admin";
String dbPass = "your_password";
```
## 4️⃣ Build the Project using Maven:
```
mvn clean package
```

WAR file output: target/LoginWebApp.war

## 5️⃣ Deploy to Apache Tomcat on EC2:

**a. Copy WAR to Tomcat**
```
sudo cp target/LoginWebApp.war /opt/tomcat/webapps/
```
**b. Restart Tomcat**
```
sudo systemctl restart tomcat
```
# OR manually:
```
/opt/tomcat/bin/shutdown.sh
/opt/tomcat/bin/startup.sh
```
**c. Access the App:**
```
http://<EC2-Public-IP>:8080/LoginWebApp/
```
## 6️⃣ Test the Application:


Register a new user

Check data in RDS iaas

Login and verify redirection to success.jsp or welcome.jsp

## 🧠 Bonus Challenges:


✅ Use environment variables for DB credentials.

✅ Host Tomcat EC2 instance in the same VPC as the RDS instance.

✅ Add SSL via Nginx reverse proxy on port 443.

✅ Automate deployment using Jenkins or GitHub Actions.

## ✅ Deliverables Checklist:


 ## Screenshot of login page on Tomcat:

 ## Screenshot of AWS RDS iaas  user data:

 ## Screenshot of successful registration or login:

 
 ## Author:

 **Uday Sairam Kommineni**
 
 **Devops Engineer**

 **Mail-id:** saikommineni5@gmail.com

 **Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/