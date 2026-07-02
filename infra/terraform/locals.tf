locals {
  name_prefix = "${var.project_name}-${var.environment}"

  common_tags = {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "terraform"
  }

  initial_image_uri = "${var.ecr_repository_url}:${var.initial_image_tag}"
}
