package org.example.api.dto;

import java.util.List;

public record PagedScoreResponse(int page, int perPage, long total, List<ScoreItem> items) {
}