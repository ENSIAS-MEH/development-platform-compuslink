terraform {
  required_version = ">= 1.5"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # Remote state — uncomment and configure for team use
  # backend "s3" {
  #   bucket = "compuslink-terraform-state"
  #   key    = "infra/terraform.tfstate"
  #   region = "eu-west-1"
  # }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "CompusLink"
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}
