package org.example.github;

import org.example.github.dto.GithubSearchResponse;
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

    public GithubSearchResponse searchRepositories(String language, LocalDate createdAfter, String sort, String order, int perPage, int page) {

        StringBuilder path = new StringBuilder("/search/repositories?q=");
        if (language != null && !language.isBlank()) {
            path.append("language:").append(language.trim()).append(' ');
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
}
