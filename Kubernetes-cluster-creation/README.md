# Kubernetes Cluster Setup on AWS using Kops:

This document provides a step-by-step guide to create, validate, manage, and delete a Kubernetes cluster on AWS using **kops**, **kubectl**, and **AWS CLI**. The setup is performed on an EC2 instance accessed via SSH.

---

## 1. Prerequisites:

* AWS Account with required IAM permissions
* EC2 instance (Ubuntu) with:

  * Internet access
  * IAM role or AWS credentials configured
* Basic knowledge of Linux, AWS, and Kubernetes

---

## 2. Connect to EC2 and Update Server:

```bash
sudo apt update && sudo apt upgrade -y
```

This ensures the system packages are up to date.

---

## 3. Install AWS CLI (v2):

```bash
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

Verify installation:

```bash
aws --version
```

---

## 4. Install Kops

```bash
curl -Lo kops https://github.com/kubernetes/kops/releases/download/$(curl -s https://api.github.com/repos/kubernetes/kops/releases/latest | grep tag_name | cut -d '"' -f 4)/kops-linux-amd64
chmod +x kops
sudo mv kops /usr/local/bin/kops
```

Verify installation:

```bash
kops version
```

---

## 5. Install Kubectl

```bash
sudo apt-get update
sudo apt-get install -y apt-transport-https ca-certificates curl gnupg
```

Install kubectl binary:

```bash
curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.29/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg
echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.29/deb/ /' | sudo tee /etc/apt/sources.list.d/kubernetes.list
sudo apt-get update
sudo apt-get install -y kubectl
```

Verify installation:

```bash
kubectl version --client
```

---

## 6. Create S3 Bucket for Kops State Store

```bash
aws s3 mb s3://uday2026 --region us-east-2
```

This S3 bucket is used by kops to store cluster configuration and state.

---

## 7. Export Kops State Store

```bash
export KOPS_STATE_STORE=s3://uday2026
```

(Optional) Make it persistent:

```bash
echo "export KOPS_STATE_STORE=s3://uday2026" >> ~/.bashrc
```

---

## 8. Create Kubernetes Cluster using Kops

```bash
kops create cluster \
--name=test.k8.local \
--zones=us-east-2a,us-east-2b,us-east-2c \
--node-count=2 \
--state=s3://uday2026 \
--yes
```

This command:

* Creates a Kubernetes cluster named `test.k8.local`
* Spreads nodes across 3 availability zones
* Creates 2 worker nodes

⏳ Cluster creation takes around **10 minutes**.

---

## 9. Validate the Cluster

```bash
kops validate cluster
```

Wait until the cluster status shows **READY**.

---

## 10. Verify Kubernetes Resources

```bash
kubectl get pods
kubectl get deployments
kubectl get svc
```

Describe resources:

```bash
kubectl describe pod <pod-name>
kubectl describe pod <pod-name> -o wide
```

---

## 11. Deploy a Sample Application (Apache HTTPD)

Create a deployment:

```bash
kubectl create deployment web-deploy --image=httpd --replicas=2
```

Expose deployment using LoadBalancer:

```bash
kubectl expose deployment web-deploy --type=LoadBalancer --port=80 --target-port=80
```

Verify service:

```bash
kubectl get svc
```

---

## 12. Delete Deployment

```bash
kubectl delete deployment web-deploy
```

(Optional) Delete service:

```bash
kubectl delete svc web-deploy
```

---

## 13. Verify Kops Resources

```bash
kops get all
```

---

## 14. Delete Kubernetes Cluster

⚠️ This will delete all AWS resources created by kops.

```bash
kops delete cluster --name=test.k8.local --yes
```

---

## 15. Conclusion

You have successfully:

* Set up AWS CLI, kops, and kubectl
* Created an S3-backed Kubernetes cluster using kops
* Deployed and exposed a sample application
* Cleaned up all resources

This documentation can be added to your GitHub repository as a professional DevOps project reference.

## Images:



---

## Author:

**Uday Kommineni**

**Devops Enginner**

**Mail-ID:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/udaysairam/


