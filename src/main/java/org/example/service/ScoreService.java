package org.example.service;

import org.example.config.GithubProperties;
import org.example.github.GithubClient;
import org.example.score.ScoringRegistry;
import org.example.api.dto.PagedScoreResponse;
import org.example.api.dto.ScoreItem;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class ScoreService {
    private final GithubClient githubClient;
    private final ScoringRegistry scoringRegistry;
    private final GithubProperties githubProperties;

    public ScoreService(GithubClient githubClient, ScoringRegistry scoringRegistry, GithubProperties githubProperties) {
        this.githubClient = githubClient;
        this.scoringRegistry = scoringRegistry;
        this.githubProperties = githubProperties;
    }

    public PagedScoreResponse calculateScores(
            String language,
            LocalDate createdAfter,
            String algorithmName,
            int perPage,
            int page) {

        var scoringStrategy = scoringRegistry.get(algorithmName);
        if (scoringStrategy == null) {
            throw new IllegalArgumentException(
                    "Unknown scoring algorithm: '%s'. Allowed: %s".formatted(algorithmName, scoringRegistry.names())
            );
        }
        var githubResponse = githubClient.searchRepositories(language, createdAfter, githubProperties.defaultSort(), githubProperties.defaultOrder(), perPage, page);

        List<ScoreItem> items = githubResponse.items().stream()
                .map(item -> {
                    double score = scoringStrategy.score(
                            item.stars(),
                            item.forks(),
                            item.pushedAt().toInstant()
                    );

                    return new ScoreItem(
                            item.fullName(),
                            item.htmlUrl(),
                            item.language(),
                            item.stars(),
                            item.forks(),
                            item.createdAt(),
                            item.pushedAt(),
                            score);

                })
                .sorted(Comparator.comparingDouble(ScoreItem::score).reversed())
                .toList();

        return new PagedScoreResponse(page, perPage, githubResponse.totalCount(), items);
    }
}

