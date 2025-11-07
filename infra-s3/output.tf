output "bucket_name" {
  value       = aws_s3_bucket.analysis.bucket
  description = "Navn på S3-bucketen"
}

output "bucket_region" {
  value       = var.aws_region
  description = "Region der bucket er opprettet"
}

output "lifecycle_rule_id" {
  value       = aws_s3_bucket_lifecycle_configuration.midlertidig_lifecycle.rule[0].id
  description = "ID for lifecycle-regelen"
}