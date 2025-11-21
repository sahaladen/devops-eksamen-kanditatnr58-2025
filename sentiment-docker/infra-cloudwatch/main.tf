resource "aws_cloudwatch_dashboard" "sentiment_dashboard" {
  dashboard_name = "kandidat-58-sentiment-metrics-dashboard"

  dashboard_body = jsonencode({
    widgets = [
      # Oversikt: Total analyser + selskaper detektert
      {
        type = "metric"
        x = 0
        y = 0
        width = 6
        height = 6
        properties = {
          metrics = [
            [ var.metrics_namespace, "sentiment.analysis.total", { stat = "Sum", region = var.aws_region } ],
            [ var.metrics_namespace, "sentiment.analysis.companies.detected", { stat = "Average", region = var.aws_region } ]
          ]
          region      = var.aws_region
          title       = "Sentiment Analysis Overview"
          view        = "timeSeries"
          stacked     = false
          period      = 60
          annotations = {}
        }
      },

      # Alarm-widget
      {
        type = "alarm"
        x = 6
        y = 0
        width = 6
        height = 6
        properties = {
          alarms = [
            aws_cloudwatch_metric_alarm.low_confidence_alarm.arn
          ]
          title = "Low Confidence Alarm Status"
        }
      },

      # Counter: Total analyser
      {
        type = "metric"
        x = 0
        y = 6
        width = 6
        height = 6
        properties = {
          metrics = [
            [ var.metrics_namespace, "sentiment.analysis.total", { stat = "Sum", region = var.aws_region } ]
          ]
          region = var.aws_region
          title  = "Total Sentiment Analyses"
          view   = "timeSeries"
          stacked = false
          period  = 60
        }
      },

      # DistributionSummary: Average confidence
      {
        type = "metric"
        x = 6
        y = 6
        width = 6
        height = 6
        properties = {
          metrics = [
            [ var.metrics_namespace, "sentiment.analysis.confidence", { stat = "Average", region = var.aws_region } ]
          ]
          region = var.aws_region
          title  = "Average Confidence Score"
          view   = "timeSeries"
          stacked = false
          period  = 60
        }
      },

      # LongTaskTimer: Active Bedrock calls
      {
        type = "metric"
        x = 0
        y = 12
        width = 6
        height = 6
        properties = {
          metrics = [
            [ var.metrics_namespace, "sentiment.analysis.bedrock.longtask.activeTasks", { stat = "Average", region = var.aws_region } ]
          ]
          region = var.aws_region
          title  = "Active Bedrock Calls"
          view   = "timeSeries"
          stacked = false
          period  = 60
        }
      }
    ]
  })
}

# SNS topic for alarm notifications
resource "aws_sns_topic" "alarm_topic" {
  name = "sentiment-alarm-topic"
}

# E-postabonnement
resource "aws_sns_topic_subscription" "email_subscription" {
  topic_arn = aws_sns_topic.alarm_topic.arn
  protocol  = "email"
  endpoint  = "s-sahal-99@hotmail.com"
}

# CloudWatch alarm for lav confidence
resource "aws_cloudwatch_metric_alarm" "low_confidence_alarm" {
  alarm_name          = "low-confidence-alarm"
  comparison_operator = "LessThanOrEqualToThreshold"
  evaluation_periods  = 1
  metric_name         = "sentiment.analysis.latest.confidence"
  namespace           = var.metrics_namespace
  statistic           = "Average"
  period              = 60
  threshold           = 1
  alarm_description   = "Triggers if latest confidence score is 1 or lower"
  alarm_actions       = [aws_sns_topic.alarm_topic.arn]
}
