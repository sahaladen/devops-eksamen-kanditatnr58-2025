package com.aialpha.sentiment.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SentimentMetrics {

    private final MeterRegistry meterRegistry;

    // AtomicInteger for Gauge som kan oppdateres dynamisk
    private final AtomicInteger companiesDetected = new AtomicInteger(0);

    // AtomicReference for siste confidence score
    private final AtomicReference<Double> latestConfidence = new AtomicReference<>(0.0);

    // DistributionSummary for confidence scores
    private final DistributionSummary confidenceSummary;

    // LongTaskTimer for langvarige Bedrock-kall
    private final LongTaskTimer bedrockLongTaskTimer;

    public SentimentMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Gauge for antall selskaper
        Gauge.builder("sentiment.analysis.companies.detected", companiesDetected, AtomicInteger::get)
                .description("Number of companies detected in the last analysis")
                .register(meterRegistry);

        // Gauge for siste confidence
        Gauge.builder("sentiment.analysis.latest.confidence", latestConfidence, AtomicReference::get)
                .description("Latest confidence score")
                .register(meterRegistry);

        // DistributionSummary for alle confidence scores
        this.confidenceSummary = DistributionSummary.builder("sentiment.analysis.confidence")
                .description("Distribution of confidence scores per company")
                .baseUnit("score")
                .register(meterRegistry);

        // LongTaskTimer for langvarige Bedrock-anrop
        this.bedrockLongTaskTimer = LongTaskTimer.builder("sentiment.analysis.bedrock.longtask")
                .description("Tracks long-running Bedrock analysis calls")
                .register(meterRegistry);
    }

    /** Counter for sentiment analysis requests */
    public void recordAnalysis(String sentiment, String company) {
        Counter.builder("sentiment.analysis.total")
                .tag("sentiment", sentiment)
                .tag("company", company)
                .description("Total number of sentiment analysis requests")
                .register(meterRegistry)
                .increment();
    }

    /** Timer for å måle varighet av sentimentanalyse */
    public void recordDuration(long milliseconds, String company, String model) {
        Timer.builder("sentiment.analysis.duration")
                .description("Time taken for sentiment analysis")
                .tag("company", company)
                .tag("model", model)
                .register(meterRegistry)
                .record(milliseconds, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    /** Oppdater Gauge for antall selskaper */
    public void recordCompaniesDetected(int count) {
        companiesDetected.set(count);
    }

    /** Oppdater DistributionSummary og siste confidence */
    public void recordConfidence(double confidence, String sentiment, String company) {
        latestConfidence.set(confidence);
        confidenceSummary.record(confidence);
    }

    /** Start LongTaskTimer */
    public LongTaskTimer.Sample startBedrockCall() {
        return bedrockLongTaskTimer.start();
    }

    /** Stop LongTaskTimer */
    public void stopBedrockCall(LongTaskTimer.Sample sample) {
        if (sample != null) {
            sample.stop();
        }
    }
}
