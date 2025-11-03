package org.example.score;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class DefaultScoringTest {

    private DefaultScoring scoring;

    @BeforeEach
    void setUp() {
        scoring = new DefaultScoring();
    }

    @Test
    void shouldReturnPositiveScore_whenGivenBasicValues() {
        Instant recentUpdate = Instant.now().minus(5, ChronoUnit.DAYS);
        double score = scoring.score(100, 50, recentUpdate);

        assertTrue(score > 0, "Score should be positive");
    }

    @Test
    void shouldReturnHigherScore_whenMoreStars() {
        Instant update = Instant.now().minus(10, ChronoUnit.DAYS);
        double lowStars = scoring.score(10, 5, update);
        double highStars = scoring.score(100, 5, update);

        assertTrue(highStars > lowStars, "More stars should produce higher score");
    }

    @Test
    void shouldReturnHigherScore_whenMoreForks() {
        Instant update = Instant.now().minus(10, ChronoUnit.DAYS);
        double lowForks = scoring.score(100, 5, update);
        double highForks = scoring.score(100, 50, update);

        assertTrue(highForks > lowForks, "More forks should produce higher score");
    }

    @Test
    void shouldReturnHigherScore_whenMoreRecentUpdate() {
        Instant oldUpdate = Instant.now().minus(60, ChronoUnit.DAYS);
        Instant recentUpdate = Instant.now().minus(5, ChronoUnit.DAYS);

        double oldScore = scoring.score(100, 50, oldUpdate);
        double recentScore = scoring.score(100, 50, recentUpdate);

        assertTrue(recentScore > oldScore, "More recent updates should produce higher score");
    }

    @Test
    void shouldHandleZeroStarsAndForks() {
        Instant update = Instant.now().minus(5, ChronoUnit.DAYS);
        double score = scoring.score(0, 0, update);

        assertTrue(score >= 0, "Score should be non-negative even with zero stars/forks");
    }

    @Test
    void shouldReturnHigherScore_whenUpdatedWithinLast7Days() {
        Instant veryRecent = Instant.now().minus(3, ChronoUnit.DAYS);
        Instant slightlyOld = Instant.now().minus(10, ChronoUnit.DAYS);

        double recentScore = scoring.score(100, 50, veryRecent);
        double oldScore = scoring.score(100, 50, slightlyOld);

        assertTrue(recentScore > oldScore, "Updates within 7 days should boost score");
    }
}

