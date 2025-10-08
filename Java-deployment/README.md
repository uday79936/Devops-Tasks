## 🚀 Java Web Calculator:


A simple Java Web Application using Java 17, built with Apache Maven, and deployed to Apache Tomcat. This project demonstrates a basic Java-based calculator using JSP and Servlets.

## 📁 Project Structure:

JavaWebCalculator/

├── pom.xml

├── src/

│   ├── main/

│   │   ├── java/mypackage/Calculator.java

│   │   └── webapp/

│   │       ├── WEB-INF/web.xml

│   │       └── index.jsp

│   └── test/

│       └── java/mypackage/CalculatorTest.java


## 🧱 Part 1: Prerequisites – Installing Java, Maven, and Tomcat:

## ✅ Step 1: Install Java 17:

**Ubuntu/Debian:**
```
sudo apt update
sudo apt install openjdk-17-jdk -y
```
**Verify:**
```
java -version
```

# Should output: openjdk version "17.0.x"

## ✅ Step 2: Install Apache Maven:

Quick Install:
```
sudo apt install maven -y
```
Or Manual Install:
```
wget https://downloads.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar -xvzf apache-maven-3.9.6-bin.tar.gz
sudo mv apache-maven-3.9.6 /opt/maven
```
## Set Environment Variables:
```
sudo vi /etc/profile.d/maven.sh
```

**Paste:**
```
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=${M2_HOME}/bin:${PATH}
```

Then run:
```
sudo chmod +x /etc/profile.d/maven.sh
source /etc/profile.d/maven.sh
```
Verify:
```
mvn -v
```
## ✅ Step 3: Install Apache Tomcat 9:
```
wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.85/bin/apache-tomcat-9.0.85.tar.gz
tar -xvzf apache-tomcat-9.0.85.tar.gz
sudo mv apache-tomcat-9.0.85 /opt/tomcat
```
**Start Tomcat:**
```
cd /opt/tomcat/bin
./startup.sh
```
**Access:**
```
http://localhost:8080
```
Note: Open port 8080 in your firewall if using a remote server.

⚙️ Part 2: Configure Tomcat for Deployment
✅ Step 4: Add User for Manager Access

Edit:

sudo vi /opt/tomcat/conf/tomcat-users.xml


Add:
```
<role rolename="manager-gui"/>
<role rolename="manager-script"/>
<user username="admin" password="admin123" roles="manager-gui,manager-script"/>
```

## ✅ Step 5: (Optional) Enable Remote Access:


Edit:
```
sudo vi /opt/tomcat/webapps/manager/META-INF/context.xml
```

Comment this out:
```
<!--
<Valve className="org.apache.catalina.valves.RemoteAddrValve"
       allow="127\.\d+\.\d+\.\d+|::1" />
-->
```

**Restart Tomcat:**
```
/opt/tomcat/bin/shutdown.sh
/opt/tomcat/bin/startup.sh
```

**🛠️ Part 3: Build WAR File with Maven**
## ✅ Step 6: Update pom.xml:


Inside <project>:
```
<packaging>war</packaging>

<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

## ✅ Step 7: Clean and Package:
```
cd JavaWebCalculator
mvn clean package
```

## The WAR file will be created in:
```
target/webapp-0.2.war
```
**🚀 Part 4: Deploy to Apache Tomcat:**

## ✅ Option 1: Tomcat Manager Interface:
```

Visit: http://localhost:8080/manager/html
```
Login: admin / admin123

## Use the Deploy section:


Upload: target/webapp-0.2.war

Click Deploy

**Visit your app at:**

```
http://localhost:8080/webapp-0.2

```

## ✅ Option 2: Copy WAR to webapps/
```
cp target/webapp-0.2.war /opt/tomcat/webapps/
```

Tomcat will auto-deploy it.

## 🧪 Part 5: Test the Application:


**Open in your browser:**
```
http://localhost:8080/webapp-0.2/
```

You should see your index.jsp page working.

## 🪵 Optional: Debugging and Logs:


**Tail logs for debugging:**
```
tail -f /opt/tomcat/logs/catalina.out
```

## Images:


## 📌 Project Summary:

Component   	                 Version

Java	                          17

Apache Maven	                 3.9.6

Apache Tomcat	                9.0.85

Output	                     .war file

Deployment Path	          /opt/tomcat/webapps/

## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-Id:** saikommineni5@gmail.com

**Linkedin-Url:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/