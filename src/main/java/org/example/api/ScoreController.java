package org.example.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.example.api.dto.PagedScoreResponse;
import org.example.service.ScoreService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class ScoreController {
    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping("/repositories/score")
    public PagedScoreResponse search(
            @RequestParam(name = "language", required = false) String language,
            @RequestParam(name = "created_after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAfter,
            @RequestParam(name = "algo", defaultValue = "default") String algo,
            @RequestParam(name = "per_page", defaultValue = "30") @Min(1) @Max(100) int perPage,
            @RequestParam(name = "page", defaultValue = "1") @Min(1) int page
    ) {
        try {
            return scoreService.calculateScores(language, createdAfter, algo, perPage, page);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (HttpClientErrorException.Forbidden exception) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "GitHub rate limit exceeded. Please retry shortly.");
        }
    }
}