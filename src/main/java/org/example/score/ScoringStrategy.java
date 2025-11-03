package org.example.score;

import java.time.Instant;

public interface ScoringStrategy {
    String name();
    double score(long stars, long forks, Instant pushedAt);
}
