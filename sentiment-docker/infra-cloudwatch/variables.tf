variable "aws_region" {
  description = "AWS region to deploy resources"
  type        = string
  default     = "eu-west-1"
}

variable "metrics_namespace" {
  description = "CloudWatch namespace for sentiment metrics"
  type        = string
  default     = "kandidat-58-metric-2025"
}