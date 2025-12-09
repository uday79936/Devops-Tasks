## AWS Asymmetric VPC Architecture — Terraform Deployment
## Production Network Build with Variable-Size Subnets

This repository contains Terraform code for deploying a highly scalable, asymmetric AWS VPC designed for production environments. The architecture follows enterprise-grade network segmentation practices, using subnets of different sizes to accommodate heterogeneous workloads (load balancers, application clusters, databases, monitoring systems, etc.).

## 📘 Project Overview:


This project creates an AWS VPC named asymmetric-vpc with a /16 CIDR block and six subnets, each sized according to expected IP consumption.

Public and Private tiers are strictly isolated using dedicated route tables, NAT gateways, and subnet-level routing policies.

## 🧩 Architecture Components:

```
| Property      | Value            |
| ------------- | ---------------- |
| Name          | `asymmetric-vpc` |
| CIDR          | `10.0.0.0/16`    |
| DNS Support   | Enabled          |
| DNS Hostnames | Enabled          |

```

All resources include detailed tagging for inventory, cost tracking, and compliance.

## 🧮 2. Subnet Design (Variable IP Capacity):


A core objective of this project is to simulate real-world IP demand by provisioning subnets with different CIDR sizes.

```
| Subnet Name      | Tier    | Approx. Required IPs | CIDR Size | Intended Workload                    |
| ---------------- | ------- | -------------------- | --------- | ------------------------------------ |
| Public Subnet A  | Public  | ~256                 | `/24`     | Load balancers, bastions             |
| Public Subnet B  | Public  | ~4096                | `/20`     | Large-scale public-facing systems    |
| Public Subnet C  | Public  | ~8192                | `/19`     | Global edge services, proxy clusters |
| Private Subnet A | Private | ~1024                | `/22`     | App servers, microservices           |
| Private Subnet B | Private | ~512                 | `/23`     | Internal services, monitoring        |
| Private Subnet C | Private | ~8192                | `/19`     | Data layer, analytics clusters       |

```


## Subnet Deployment Practices:


Each subnet is deployed in a different Availability Zone.
```
Public tier → auto-assign public IPs enabled.

Private tier → no auto-assigned public IPs.

Private subnets reach the internet through NAT Gateway(s).
```

## 🌐 3. Internet Gateway:


**An Internet Gateway named:**

asym-igw


is attached to the VPC and serves as the public-tier egress for internet-facing workloads.

## 🔄 4. NAT Gateway Design:


This project includes a high availability NAT architecture.

✔ Recommended Strategy (Implemented):
Multiple NAT Gateways — one per AZ

This improves reliability and prevents single-AZ failures from impacting outbound internet connectivity.

**Each NAT Gateway:**

Resides inside the public subnet of the same AZ.

Has its own separate Elastic IP.

Supports only the private subnet in the same AZ.

Justification for Multi-NAT High Availability

Eliminates single points of failure.

Ensures bidirectional isolation between AZs.

Provides best practice for production workloads.

Reduces cross-AZ data charges for NAT usage.

## 🗺 5. Route Table Architecture:


Each subnet receives its own dedicated route table.

Public Route Tables

**3 public RTs**

Default route: 0.0.0.0/0 → Internet Gateway

Each associated with one public subnet

Private Route Tables

**3 private RTs**

Default route: 0.0.0.0/0 → NAT Gateway (same AZ)

Strict isolation: no shared route tables

## Routing Summary:
```
| Tier    | Default Route | Next Hop                  |
| ------- | ------------- | ------------------------- |
| Public  | `0.0.0.0/0`   | IGW                       |
| Private | `0.0.0.0/0`   | NAT Gateway (AZ-specific) |

```
## 🏷 6. Tagging Standard:


All AWS resources follow a consistent and mandatory tagging schema:
```
Environment = "production"
Owner       = "network-team"
Project     = "asymmetric-vpc-build"
CostCenter  = "AWS-Networking"
Tier        = "public" or "private" 
```


Tagging is implemented via Terraform modules and variable inheritance.

📁 Repository Structure:
```
.
├── main.tf
├── variables.tf
├── outputs.tf
├── network/
│   ├── vpc.tf
│   ├── subnets.tf
│   ├── igw.tf
│   ├── nat.tf
│   ├── route-tables.tf
│   ├── associations.tf
│   └── tags.tf
└── docs/
    ├── architecture-diagram.png
    ├── subnet-plan.md
    └── routing-overview.md
```

## 🚀 How to Deploy:

## 1. Initialize Terraform
```
terraform init
```

## 2. Validate:
```
terraform validate
```

## 3. Preview the Infrastructure:
```
terraform plan
```

## 4. Deploy:
```
terraform apply
```


## To destroy resources:
```
terraform destroy
```


⚠ Use terraform destroy cautiously in production environments.

## 🔧 Design Benefits:


✔ Supports large-scale, asymmetric workloads

✔ Full subnet isolation using dedicated route tables

✔ Highly available outbound internet connectivity

✔ Predictable IP utilization with CIDR sizing

✔ Production-grade tagging and metadata

✔ Modular, reusable Terraform code

## 🏁 Conclusion:


This project provides an enterprise-ready, fully modular AWS VPC with asymmetric subnet design, multi-AZ deployment, isolated routing tiers, and high availability NAT configuration.

## The Terraform module is designed for:

production cloud networking

high-scale distributed systems

security-hardened architectures

## Images:

## 1. Create V.P.C:

<img width="1918" height="952" alt="Image" src="https://github.com/user-attachments/assets/d9476f5c-0e0d-4d2f-9abe-f13bc19208eb" />

## 2. Create I.G.W:

<img width="1918" height="920" alt="Image" src="https://github.com/user-attachments/assets/929aec87-a34d-44b0-a223-cbcb73e41f19" />

## 3. Attach I.G.W:

<img width="1918" height="937" alt="Image" src="https://github.com/user-attachments/assets/0f1a5f1e-812d-42fc-8585-41156ece42fa" />

## 4. I.G.W Attached:

<img width="1915" height="915" alt="Image" src="https://github.com/user-attachments/assets/8c48707f-1883-44df-8a45-2c1b85c8d8da" />

## 5. 10.0.0.0/24 cidr calculation:

<img width="1912" height="985" alt="Image" src="https://github.com/user-attachments/assets/8e678c8a-de4c-4f40-9db1-bdcd42fc45de" />

## 6. 10.0.16.0/20 cidr calculation:

<img width="1906" height="970" alt="Image" src="https://github.com/user-attachments/assets/65b49fdf-2c63-451d-a8e1-f1a6c9f09a7e" />

## 7. 10.0.32.0/19 cidr calculation:

<img width="1911" height="975" alt="Image" src="https://github.com/user-attachments/assets/501397b7-4038-43a2-b811-fc6a5e36c761" />

## 8. 10.0.64.0/22 cidr calculation:

<img width="1911" height="975" alt="Image" src="https://github.com/user-attachments/assets/501397b7-4038-43a2-b811-fc6a5e36c761" />

## 9. 10.0.68.0/23 cidr calculation:

<img width="1915" height="960" alt="Image" src="https://github.com/user-attachments/assets/eb458514-948b-440f-a7da-3bb0c2ad2cd0" />

## 10. 10.0.96.0/19 cidr calculation:

<img width="1913" height="971" alt="Image" src="https://github.com/user-attachments/assets/2820be70-97ec-4973-a4c0-39aef0e6b58d" />

## 11. Create pub-pvt subnets:

<img width="1910" height="967" alt="Image" src="https://github.com/user-attachments/assets/a4533122-cc6d-44ed-94f9-e24fe29b3aca" />

## 12. Pub-route-table:

<img width="1917" height="955" alt="Image" src="https://github.com/user-attachments/assets/848612b2-6c3b-4181-8687-688e2be6de57" />

## 13. Create Pub-Pvt-route-tables:

<img width="1916" height="905" alt="Image" src="https://github.com/user-attachments/assets/d864604e-f3a7-45f8-bd84-5c66b8692fc3" />

## 14. pub-route-tables in routes:

<img width="1917" height="976" alt="Image" src="https://github.com/user-attachments/assets/1fd68212-51a5-4f9a-9c13-21ea25283340" />

## 15. Edit pub-route-table:

<img width="1918" height="927" alt="Image" src="https://github.com/user-attachments/assets/ea10cc08-2887-4322-a17e-e4d80e4b7497" />

## 16. pub Route tables subnet associations:

<img width="1917" height="912" alt="Image" src="https://github.com/user-attachments/assets/9d7e2661-b85f-43fc-9139-ccfecdc0354e" />

## 17. Edit subnet associations:

<img width="1917" height="917" alt="Image" src="https://github.com/user-attachments/assets/0debfd61-e83b-4bfe-b72d-57952cfab382" />

## 18.  Pvt Route tables subnet associations:

<img width="1912" height="927" alt="Image" src="https://github.com/user-attachments/assets/6ea54509-1201-4f3d-a501-84bd17a89d00" />


## Author:

**Uday Sairam Kommineni**

**Devops Engineer**

**Mail-ID:** saikommineni5@gmail.com

**Linkedin-URL:** https://www.linkedin.com/in/uday-sai-ram-kommineni-uday-sai-ram/
