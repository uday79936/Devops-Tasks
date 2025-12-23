variable "access_key" {
  type      = string
  sensitive = true
}

variable "secret_key" {
  type      = string
  sensitive = true
}

variable "region" {
  type    = string
  default = "us-east-2"
}

variable "ami_id" {
  type        = string
  description = "EC2 Ubuntu AMI id"
}

variable "key_name" {
  type = string
}
