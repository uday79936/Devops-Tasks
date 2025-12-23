terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region     = var.region
  access_key = var.access_key
  secret_key = var.secret_key
}

resource "aws_security_group" "python_sg" {
  name        = "python-app-sg"
  description = "Allow SSH and Flask"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 5000
    to_port     = 5000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "python_server" {
  ami                    = var.ami_id
  instance_type          = "t2.micro"
  key_name               = var.key_name
  vpc_security_group_ids = [aws_security_group.python_sg.id]

  tags = {
    Name = "Terraform-python-app"
  }

  user_data = <<-EOF
    #!/bin/bash
    yum update -y
    yum install -y python3 python3-pip

    pip3 install flask

    cat << 'APP' > /home/ec2-user/app.py
    from flask import Flask
    app = Flask(__name__)

    @app.route("/")
    def hello():
        return "Hello all Welcome to Terraform session at 2:00 PM!"

    if __name__ == "__main__":
        app.run(host="0.0.0.0", port=5000)
    APP

    chown ec2-user:ec2-user /home/ec2-user/app.py

    cat << 'SERVICE' > /etc/systemd/system/flask.service
    [Unit]
    Description=Flask Application
    After=network.target

    [Service]
    User=ec2-user
    WorkingDirectory=/home/ec2-user
    ExecStart=/usr/bin/python3 /home/ec2-user/app.py
    Restart=always

    [Install]
    WantedBy=multi-user.target
    SERVICE

    systemctl daemon-reexec
    systemctl daemon-reload
    systemctl enable flask
    systemctl start flask
  EOF
}
