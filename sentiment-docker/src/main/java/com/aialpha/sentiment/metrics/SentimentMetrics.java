package com.aialpha.sentiment.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class SentimentMetrics {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger companiesDetected;
    private final Map<String, Timer> timerCache;
    private final Map<String, DistributionSummary> summaryCache;

    // Constructor injection of MeterRegistry
    public SentimentMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.companiesDetected = new AtomicInteger(0);
        this.timerCache = new ConcurrentHashMap<>();
        this.summaryCache = new ConcurrentHashMap<>();

        // Register Gauge for companies detected once in constructor
        // Gauge tracks current state - can go up or down
        Gauge.builder("sentiment.companies.detected", companiesDetected, AtomicInteger::get)
                .description("Number of companies detected in the last analysis")
                .baseUnit("companies")
                .register(meterRegistry);
    }

    /**
     * Counter for sentiment analysis requests
     * Tracks the total number of sentiment analyses by sentiment type and company
     */
    public void recordAnalysis(String sentiment, String company) {
        Counter.builder("sentiment.analysis.total")
                .tag("sentiment", sentiment)
                .tag("company", company)
                .description("Total number of sentiment analysis requests")
                .register(meterRegistry)
                .increment();
    }

    /**
     * Timer for recording duration of API calls
     * Measures how long it takes to communicate with AWS Bedrock API
     * Tracks count, total time, max, and percentiles
     */
    public void recordDuration(long milliseconds, String company, String model) {
        String key = "company:" + company + ",model:" + model;

        Timer timer = timerCache.computeIfAbsent(key, k ->
                Timer.builder("sentiment.api.duration")
                        .tag("company", company)
                        .tag("model", model)
                        .description("Duration of AWS Bedrock API calls")
                        .publishPercentiles(0.5, 0.95, 0.99)
                        .register(meterRegistry)
        );

        timer.record(milliseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * Gauge for tracking number of companies detected
     * Represents a value that can increase or decrease
     * Shows the number of companies found in the most recent analysis
     */
    public void recordCompaniesDetected(int count) {
        companiesDetected.set(count);
    }

    /**
     * DistributionSummary for confidence scores
     * Shows statistical distribution of confidence values (0.0 to 1.0)
     * Provides count, sum, max, and percentiles for confidence scores
     */
    public void recordConfidence(double confidence, String sentiment, String company) {
        String key = "sentiment:" + sentiment + ",company:" + company;

        DistributionSummary summary = summaryCache.computeIfAbsent(key, k ->
                DistributionSummary.builder("sentiment.confidence.score")
                        .tag("sentiment", sentiment)
                        .tag("company", company)
                        .description("Distribution of confidence scores")
                        .baseUnit("confidence")
                        .publishPercentiles(0.5, 0.95, 0.99)
                        .minimumExpectedValue(0.0)
                        .maximumExpectedValue(1.0)
                        .register(meterRegistry)
        );

        summary.record(confidence);
    }
}