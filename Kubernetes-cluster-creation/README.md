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

## 1. EC2 Server:

<img width="1917" height="911" alt="Image" src="https://github.com/user-attachments/assets/d35a1344-5f8f-4b27-a7e4-81fb56ef9257" />

## 2. Attach IAM Role to EC2 server:

<img width="1918" height="917" alt="Image" src="https://github.com/user-attachments/assets/e4ca72be-e0d9-4f76-a9ee-f218f650f43f" />

## 3. IAM Role attached to ec2 server:

<img width="1918" height="911" alt="Image" src="https://github.com/user-attachments/assets/04144f57-5628-4129-85ac-277824d40e25" />

## 4. Change the hostname and init:

<img width="918" height="166" alt="Image" src="https://github.com/user-attachments/assets/0ec57123-bed4-44a5-9bcd-34f9970696f6" />

## 5. Update the server:

<img width="860" height="140" alt="Image" src="https://github.com/user-attachments/assets/38e6c87c-0e05-46c5-83e6-232ac64fbc0c" />

## 6. Download the aws-cli into server:

<img width="1385" height="201" alt="Image" src="https://github.com/user-attachments/assets/f073c70e-479b-4c21-9e35-6c0743a8756a" />

## 7. aws-cli extracted:

<img width="352" height="147" alt="Image" src="https://github.com/user-attachments/assets/bf88490e-2de7-48e0-a66d-1e97b7d5ee16" />

## 8. aws-cli installed:

<img width="622" height="123" alt="Image" src="https://github.com/user-attachments/assets/376e5b25-d124-4042-9c71-8908729e9417" />

## 9. Successfully installed aws-cli:

<img width="1011" height="227" alt="Image" src="https://github.com/user-attachments/assets/93f90074-2ec9-42a4-bc2e-5e570982273c" />

## 10. installed kops:

<img width="1902" height="280" alt="Image" src="https://github.com/user-attachments/assets/8e75540d-f172-4863-ac0f-6c9ec89a9c83" />

## 11. Install kubectl:

<img width="1911" height="1010" alt="Image" src="https://github.com/user-attachments/assets/b6c7cab0-3453-42d2-9cc7-362cd341b4a4" />

## 12. Create s3 bucket:

<img width="1918" height="142" alt="Image" src="https://github.com/user-attachments/assets/4cfa0f81-a95c-4977-9251-429c24ad3c53" />

## 13. Export kops to s3 bucket:

<img width="787" height="112" alt="Image" src="https://github.com/user-attachments/assets/5e0aa12f-148d-4e8a-b972-e312ac6e087a" />

## 14. Create cluster:

<img width="1820" height="106" alt="Image" src="https://github.com/user-attachments/assets/a249555f-de8c-4861-a5d2-932e85f88f75" />

## 15. Create cluster completed:

<img width="1911" height="882" alt="Image" src="https://github.com/user-attachments/assets/5baff014-cda1-4a1e-b1b5-245eae1a46f4" />

## 16. Kops validate cluster:

<img width="1772" height="527" alt="Image" src="https://github.com/user-attachments/assets/f52a566a-dfa2-42b1-b47e-5677c19a5d8a" />

## 17. kubectl get nodes:

<img width="1502" height="192" alt="Image" src="https://github.com/user-attachments/assets/fbec4e96-426b-49ca-8f9d-c0fa30c768ff" />

## 18. kubectl get all:

<img width="1007" height="156" alt="Image" src="https://github.com/user-attachments/assets/b3345453-774c-496c-b1d8-1abb3c50af05" />

## 19. kubectl run to create pod:

<img width="962" height="52" alt="Image" src="https://github.com/user-attachments/assets/404100a1-57e6-4c2d-84fd-b67507039dc3" />

## 20. kubectl get pods:

<img width="601" height="135" alt="Image" src="https://github.com/user-attachments/assets/cc1c7b01-bd65-47b1-be86-fc08afb17dc4" />

## 21. kubectl describe pod part-1:

<img width="1917" height="1015" alt="Image" src="https://github.com/user-attachments/assets/c2d28469-58c8-4f99-bd28-d46ab03ae909" />

## 22. kubectl describe pod part-2:

<img width="1905" height="1013" alt="Image" src="https://github.com/user-attachments/assets/c80571c7-76df-4863-99cb-b323fba329ec" />

## 23. kubectl get pods -o wide:

<img width="1510" height="167" alt="Image" src="https://github.com/user-attachments/assets/c646a2dd-8f0f-4dcb-87ed-01d18c620930" />

## 24. To delete the pod:

<img width="676" height="151" alt="Image" src="https://github.com/user-attachments/assets/a110553a-a076-496f-a865-4ab2fb4de7a5" />

## 25. Create Deployment:

<img width="1242" height="146" alt="Image" src="https://github.com/user-attachments/assets/dc76298d-0420-4a1d-bd60-bb58d977f70f" />

## 26. kubectl get deployments:

<img width="1281" height="310" alt="Image" src="https://github.com/user-attachments/assets/ec83efde-edd5-4678-9080-a08f8a59d9f6" />

## 27. kubectl describe deployment web-deploy:

<img width="1530" height="827" alt="Image" src="https://github.com/user-attachments/assets/16c7dbc3-df6d-4e5a-9d4f-c62c86d5cfbb" />

## 28. Service exposed:

<img width="1521" height="137" alt="Image" src="https://github.com/user-attachments/assets/153f4a6e-a515-4489-a43c-0e952f5438cc" />

## 29. kubectl get svc:

<img width="1717" height="176" alt="Image" src="https://github.com/user-attachments/assets/7749da50-dd6a-4196-87b6-c8da437e964d" />

## 30. kops get all:

<img width="1197" height="432" alt="Image" src="https://github.com/user-attachments/assets/45e6abc1-30d3-45ff-a9d9-9fe08afdf932" />

## 31. Output:

<img width="1917" height="538" alt="Image" src="https://github.com/user-attachments/assets/22356f81-ccef-4768-bbe4-76e0c443a885" />

## 32. kops delete cluster:

<img width="1905" height="592" alt="Image" src="https://github.com/user-attachments/assets/aa2f0c64-6f16-4551-8528-b4116ac4bd1a" />

---

## Author:

**Uday Kommineni**

**Devops Enginner**

**Mail-ID:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/udaysairam/


