package org.example.score;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Default scoring algorithm that estimates a repository’s popularity.
 * <p>
 * The score combines:
 * - Stars (weighted higher) with logarithmic scaling, so 10001st cost less than 1st
 * - Forks (weighted lower) with logarithmic scaling
 * - Recent activity (bonus for updates in the last week)
 * <p>
 * This gives higher scores to popular and recently maintained repositories.
 */
@Component
public class DefaultScoring implements ScoringStrategy {
    private static final double STAR_WEIGHT = 2.0;
    private static final double FORK_WEIGHT = 1.0;
    private static final double PUSH_WEIGHT = 3.0;

    @Override public String name() { return "default"; }

    @Override
    public double score(long stars, long forks, Instant pushedAt) {
        double lastPushedDays = Duration.between(pushedAt, Instant.now()).toDays();
        double recency = Math.exp(-lastPushedDays / 30.0) + (lastPushedDays <= 7 ? 1.0 : 0.0);
        return STAR_WEIGHT * Math.log1p(stars) + FORK_WEIGHT * Math.log1p(forks) + PUSH_WEIGHT * recency;
    }
}
