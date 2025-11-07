package org.example.github;

import org.example.github.dto.GithubSearchResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

@Service
public class GithubClient {
    private final RestClient githubClient;

    public GithubClient(RestClient githubClient) {
        this.githubClient = githubClient;
    }

    @Cacheable(cacheNames = "githubSearch", key = "#root.target.buildCacheKey(#language, #createdAfter, #perPage, #page)")
    public GithubSearchResponse searchRepositories(String language, LocalDate createdAfter, String sort, String order, int perPage, int page) {
        String normalizedLanguage = normalizeLanguage(language);

        StringBuilder path = new StringBuilder("/search/repositories?q=");
        if (!normalizedLanguage.isBlank()) {
            path.append("language:").append(normalizedLanguage).append(' ');
        }
        if (createdAfter != null) {
            path.append("created:>=").append(createdAfter);
        }
        if (sort != null && !sort.isBlank()) {
            path.append("&sort=").append(sort.trim());
        }
        if (order != null && !order.isBlank()) {
            path.append("&order=").append(order.trim());
        }
        path.append("&type=").append(perPage);
        path.append("&per_page=").append(perPage);
        path.append("&page=").append(page);
        return githubClient.get()
                .uri(path.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(GithubSearchResponse.class);
    }

    @SuppressWarnings("unused")
    public String buildCacheKey(String language, LocalDate createdAfter, Integer perPage, Integer page) {
        String created = createdAfter != null ? createdAfter.toString() : "";
        String perPageValue = perPage != null ? perPage.toString() : "";
        String pageValue = page != null ? page.toString() : "";
        return normalizeLanguage(language) + '|' + created + '|' + perPageValue + '|' + pageValue;
    }

    private String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            return "";
        }
        return language.trim().toLowerCase();
    }
}
