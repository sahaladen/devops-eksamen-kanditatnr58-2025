resource "aws_s3_bucket" "analysis" {
  bucket = var.result_bucket_name

  tags = {
    Project = "Analytics"
    Managed = "Terraform"
  }
}


resource "aws_s3_bucket_lifecycle_configuration" "midlertidig_lifecycle" {
  bucket = aws_s3_bucket.analysis.id

  rule {
    id     = "midlertidig-rule"
    status = "Enabled"

    filter {
      prefix = "midlertidig/"
    }

    transition {
      days          = var.transition_days
      storage_class = "GLACIER"
    }

    expiration {
      days = var.expiration_days
    }
  }
}