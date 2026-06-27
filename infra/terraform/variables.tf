variable "aws_region" {
  description = "AWS region where resources are created."
  type        = string
  default     = "ap-northeast-2"
}

variable "aws_profile" {
  description = "AWS CLI profile used by the provider."
  type        = string
  default     = "ys-admin"
}

variable "project_name" {
  description = "Project name used for resource names and tags."
  type        = string
  default     = "concerts"
}

variable "environment" {
  description = "Deployment environment name."
  type        = string
  default     = "prod"
}

variable "ecr_repository_name" {
  description = "ECR repository name for the backend API image."
  type        = string
  default     = "concerts-api"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets."
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets."
  type        = list(string)
  default     = ["10.0.11.0/24"]
}

variable "app_port" {
  description = "Spring Boot application port exposed by ECS tasks."
  type        = number
  default     = 8080
}

variable "domain_name" {
  description = "Root domain name managed by Route 53."
  type        = string
  default     = "stagepick.cloud"
}

variable "api_subdomain" {
  description = "Subdomain used by the backend API."
  type        = string
  default     = "api"
}
