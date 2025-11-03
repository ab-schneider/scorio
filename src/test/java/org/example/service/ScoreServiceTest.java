package org.example.service;

import org.example.api.dto.PagedScoreResponse;
import org.example.api.dto.ScoreItem;
import org.example.config.GithubProperties;
import org.example.github.GithubClient;
import org.example.github.dto.GithubSearchResponse;
import org.example.score.DefaultScoring;
import org.example.score.ScoringRegistry;
import org.example.score.ScoringStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoreServiceTest {

    @Mock
    private GithubClient githubClient;

    @Mock
    private ScoringRegistry scoringRegistry;

    @Mock
    private GithubProperties githubProperties;

    private ScoreService scoreService;

    @BeforeEach
    void setUp() {
        scoreService = new ScoreService(githubClient, scoringRegistry, githubProperties);
    }

    @Test
    void shouldCalculateScoresAndSortByHighestFirst() {
        // Given
        ScoringStrategy strategy = new DefaultScoring();
        when(scoringRegistry.get("default")).thenReturn(strategy);
        when(githubProperties.defaultSort()).thenReturn("stars");
        when(githubProperties.defaultOrder()).thenReturn("desc");

        OffsetDateTime now = OffsetDateTime.now();
        GithubSearchResponse.Item item1 = new GithubSearchResponse.Item(
                "repo1/name", "http://github.com/repo1", "Java",
                100, 50, now, now, "Description 1"
        );
        GithubSearchResponse.Item item2 = new GithubSearchResponse.Item(
                "repo2/name", "http://github.com/repo2", "Java",
                200, 100, now, now, "Description 2"
        );

        GithubSearchResponse githubResponse = new GithubSearchResponse(
                2, false, List.of(item1, item2)
        );

        when(githubClient.searchRepositories(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(githubResponse);

        // When
        PagedScoreResponse result = scoreService.calculateScores(null, null, "default", 30, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.page());
        assertEquals(30, result.perPage());
        assertEquals(2, result.total());
        assertEquals(2, result.items().size());

        // Verify sorting: higher score should come first
        List<ScoreItem> items = result.items();
        assertTrue(items.get(0).score() >= items.get(1).score(),
                "Items should be sorted by score descending");
    }

    @Test
    void shouldThrowException_whenInvalidAlgorithm() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> scoreService.calculateScores(null, null, "invalid", 30, 1)
        );

        assertTrue(exception.getMessage().contains("Unknown scoring algorithm"));
        assertTrue(exception.getMessage().contains("invalid"));
    }
}

