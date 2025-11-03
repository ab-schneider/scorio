package org.example.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public record GithubSearchResponse(
        @JsonProperty("total_count") long totalCount,
        @JsonProperty("incomplete_results") boolean incompleteResults,
        List<Item> items
) {
    public record Item(
            @JsonProperty("full_name") String fullName,
            @JsonProperty("html_url") String htmlUrl,
            @JsonProperty("language") String language,
            @JsonProperty("stargazers_count") long stars,
            @JsonProperty("forks_count") long forks,
            @JsonProperty("created_at") OffsetDateTime createdAt,
            @JsonProperty("pushed_at") OffsetDateTime pushedAt,
            String description
    ) {}
}
