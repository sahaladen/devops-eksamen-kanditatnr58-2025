terraform {
  required_version = ">= 1.5"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
  backend "s3" {
    bucket = "kandidat-58-data"
    key    = "test"
    region = "eu-west-1"
  }
}

provider "aws" {
  region = "eu-west-1"
}