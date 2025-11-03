package org.example.score;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Simple flat scoring algorithm.
 * <p>
 * Calculates the score as the sum of stars and forks,
 * with a small bonus for repositories updated within the last week.
 * <p>
 * Provides a straightforward baseline without logarithmic scaling or decay.
 */
@Component
public class FlatScoring implements ScoringStrategy {

    @Override public String name() { return "flat"; }

    @Override
    public double score(long stars, long forks, Instant pushedAt) {
        long lastPushedDays = Duration.between(pushedAt, Instant.now()).toDays();
        return stars + forks + (lastPushedDays <= 7 ? 100 : 0);
    }
}
