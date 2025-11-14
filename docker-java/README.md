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

## Delete the Container:

```
docker rm -rf cal-app
```

## Images:

## 1. Docker Java Ec2 server:

<img width="1918" height="940" alt="Image" src="https://github.com/user-attachments/assets/eb9a79f9-2fc3-426e-aa39-6dc666bf2e56" />

## 2. Change Hostname:

<img width="1026" height="197" alt="Image" src="https://github.com/user-attachments/assets/41fd910d-3af7-42bd-bf9b-9dd13dee0727" />

## 3. Update the Server:

<img width="787" height="112" alt="Image" src="https://github.com/user-attachments/assets/6087048f-72cf-4766-a635-b78d2ee545eb" />

## 4. Install Docker:

<img width="828" height="108" alt="Image" src="https://github.com/user-attachments/assets/d88d31ee-76be-48e2-a525-7b3e6b7e3c0a" />

## 5. Start and enable docker:

<img width="712" height="168" alt="Image" src="https://github.com/user-attachments/assets/86dfa7d2-1756-4b5e-9a67-df49eab3cefb" />

## 6. Docker Status Active:

<img width="1912" height="577" alt="Image" src="https://github.com/user-attachments/assets/436932c7-792f-4c26-9d7a-b550b4f698ae" />

## 7. Change Permissions:

<img width="1892" height="202" alt="Image" src="https://github.com/user-attachments/assets/483f03ab-4047-4e0c-aa32-59e9a9306fbe" />

## 8. Clone the repository:

<img width="1418" height="267" alt="Image" src="https://github.com/user-attachments/assets/7e82bb62-f22a-4fec-9bb9-0a3b3849519b" />

## 9. Change the directory:

<img width="617" height="157" alt="Image" src="https://github.com/user-attachments/assets/991678f0-e9eb-4a3f-bfa8-45595280bf2d" />

## 10. Docker file:

<img width="1040" height="896" alt="Image" src="https://github.com/user-attachments/assets/d56b5e39-0b25-4b44-a715-bd95e18147c4" />

## 11. Docker Build:

<img width="936" height="125" alt="Image" src="https://github.com/user-attachments/assets/ef084fab-c66d-4fcc-88c5-6bffeb3332ef" />

## 12. Docker Build Success:

<img width="1913" height="997" alt="Image" src="https://github.com/user-attachments/assets/9b4aecb8-1851-4ecf-b5b1-6fc198c804d9" />

## 13. Docker Images:

<img width="1187" height="213" alt="Image" src="https://github.com/user-attachments/assets/48d1774e-365b-41df-b6d7-f81384ceb802" />

## 14. Docker run:

<img width="1273" height="126" alt="Image" src="https://github.com/user-attachments/assets/5229b231-7edc-4b28-be6b-f586a5e52cf1" />

## 15. Docker ps:

<img width="1677" height="166" alt="Image" src="https://github.com/user-attachments/assets/ab73fc75-16f6-4912-b5a5-0db006ed0dbc" />

## 16. Docker execute:


<img width="1631" height="228" alt="Image" src="https://github.com/user-attachments/assets/7c4ed1a3-c61f-4c64-a7d2-0094b39cf34d" />

## 17. Java Web-calculator output:

<img width="828" height="397" alt="Image" src="https://github.com/user-attachments/assets/4eeaef96-d4a8-4b2f-8222-9cc2d39a69ef" />

## 18. Output:

<img width="1005" height="422" alt="Image" src="https://github.com/user-attachments/assets/6e49f569-dd21-4323-9226-f867f7aa1b4f" />

## 19. Delete the Container:

<img width="1665" height="242" alt="Image" src="https://github.com/user-attachments/assets/f96b6a3b-3fe5-4960-9173-88947c8deb92" />

## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-URL:**  https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
