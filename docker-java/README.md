## EC2 Server Setup and Docker Deployment:

## 📌 1. Launch EC2 Instance:


Launch a Ubuntu 22.04 EC2 instance.

Download the .pem key.

Attach a security group that allows SSH (port 22) initially.

## 📌 2. Connect to EC2 via SSH:
```
ssh -i "your-key.pem" ubuntu@<EC2-Public-IP>
```

## 📌 3. Change Hostname:
```
sudo hostnamectl set-hostname docker-java
```

## Reboot using:
```
sudo init 6
```

Reconnect with SSH.

## 📌 4. Update and Upgrade Server:
```
sudo apt update && sudo apt upgrade -y
```

## 📌 5. Install Docker:
```
sudo apt install docker.io -y
```

## Enable and start Docker:

```
sudo systemctl start docker
sudo systemctl enable docker
sudo systemctl status docker
```

## 📌 6. Clone Your GitHub Repository:
```
git clone https://github.com/uday79936/Java-Web-Calculator-App.git
```

## Move into the directory:
```
cd Java-Web-Calculator-App
```

## 📌 7. Create Dockerfile:


## Create a Dockerfile:
```
vi Dockerfile
```


## Add the following content:
```
# ===============================
# 1️⃣ BUILD STAGE — MAVEN BUILDER
# ===============================
FROM maven:3.9.6-eclipse-temurin-17 AS builder


WORKDIR /app


COPY pom.xml .


RUN mvn dependency:go-offline


COPY src ./src


RUN mvn clean package -DskipTests


# ==========================
# 2️⃣ RUNTIME STAGE — TOMCAT
# ==========================
FROM tomcat:9.0-jdk17


RUN rm -rf /usr/local/tomcat/webapps/*


COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/


EXPOSE 8080


CMD ["catalina.sh", "run"]
```

## Save and exit:
```
:wq
```

## 📌 8. Build Docker Image:
```
docker build -t java-app .
```

## 📌 9. Run the Docker Container:
```
docker run -dt --name cal-app -p 8000:8080 java-app
```

## 📌 10. Access Container (Optional):
```
docker exec -it cal-app /bin/bash
```


## Check WAR file:
```
cd /usr/local/tomcat/webapps
ls
```

## 📌 11. Configure Security Group:

```
Go to EC2 → Security Groups → Inbound Rules and add:

Custom TCP: Port 0-65535, Source 0.0.0.0/0

(or better: only port 8000 for security)

Save rules.
```


## 📌 12. Test Application in Browser:


## Open:
```
http://<EC2-Public-IP>:8000
```


**Your Java Web Calculator App should load.**


## ✅ Deployment Completed Successfully:

## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:**  https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/