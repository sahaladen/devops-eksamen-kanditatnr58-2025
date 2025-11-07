variable "aws_region" {
  type        = string
  default     = "eu-west-1"
  description = "AWS region"
}

variable "result_bucket_name" {
  type        = string
  description = "Navn på S3-bucket for analyseresultater"
}

variable "transition_days" {
  type        = number
  default     = 30
  description = "Antall dager før filer flyttes til billigere lagringsklasse (Glacier)"
}

variable "expiration_days" {
  type        = number
  default     = 90
  description = "Antall dager før filer slettes"
}