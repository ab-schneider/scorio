package org.example.api.dto;

import java.time.OffsetDateTime;

public record ScoreItem(
        String fullName,
        String htmlUrl,
        String language,
        long stars,
        long forks,
        OffsetDateTime createdAt,
        OffsetDateTime pushedAt,
        double score
) {}