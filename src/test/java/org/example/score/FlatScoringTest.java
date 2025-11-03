package org.example.score;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class FlatScoringTest {

    private FlatScoring scoring;

    @BeforeEach
    void setUp() {
        scoring = new FlatScoring();
    }

    @Test
    void shouldCalculateScoreAsStarsPlusForks() {
        Instant update = Instant.now().minus(10, ChronoUnit.DAYS);
        double score = scoring.score(100, 50, update);

        assertEquals(150.0, score, "Score should be stars + forks (100 + 50)");
    }

    @Test
    void shouldAddBonus_whenUpdatedWithinLast7Days() {
        Instant recentUpdate = Instant.now().minus(3, ChronoUnit.DAYS);
        double score = scoring.score(100, 50, recentUpdate);

        assertEquals(250.0, score, "Should add 100 bonus for recent updates (100 + 50 + 100)");
    }

    @Test
    void shouldNotAddBonus_whenUpdatedMoreThan7DaysAgo() {
        Instant oldUpdate = Instant.now().minus(10, ChronoUnit.DAYS);
        double score = scoring.score(100, 50, oldUpdate);

        assertEquals(150.0, score, "Should not add bonus for old updates");
    }

    @Test
    void shouldHandleZeroValues() {
        Instant update = Instant.now().minus(10, ChronoUnit.DAYS);
        double score = scoring.score(0, 0, update);

        assertEquals(0.0, score, "Zero stars and forks should produce zero score");
    }

    @Test
    void shouldReturnHigherScore_whenMoreStars() {
        Instant update = Instant.now().minus(10, ChronoUnit.DAYS);
        double lowStars = scoring.score(10, 5, update);
        double highStars = scoring.score(100, 5, update);

        assertTrue(highStars > lowStars, "More stars should produce higher score");
    }
}

