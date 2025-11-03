package org.example.api;

import org.example.api.dto.PagedScoreResponse;
import org.example.api.dto.ScoreItem;
import org.example.service.ScoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScoreController.class)
class ScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScoreService scoreService;

    @Test
    void shouldReturn200_whenValidRequest() throws Exception {
        // Given
        PagedScoreResponse response = new PagedScoreResponse(
                1, 30, 100,
                List.of(new ScoreItem(
                        "owner/repo", "http://github.com/owner/repo", "Java",
                        100, 50, OffsetDateTime.now(), OffsetDateTime.now(), 150.0
                ))
        );
        when(scoreService.calculateScores(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/repositories/score")
                        .param("algo", "default"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.total").value(100));
    }

    @Test
    void shouldReturn400_whenInvalidAlgorithm() throws Exception {
        // Given
        when(scoreService.calculateScores(any(), any(), eq("invalid"), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Unknown scoring algorithm: 'invalid'. Allowed: [default, flat]"));

        // When & Then
        mockMvc.perform(get("/api/repositories/score")
                        .param("algo", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unknown scoring algorithm: 'invalid'. Allowed: [default, flat]"));
    }

    @Test
    void shouldReturn400_whenPerPageExceedsMax() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/repositories/score")
                        .param("per_page", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_whenPerPageIsZero() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/repositories/score")
                        .param("per_page", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_whenPageIsZero() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/repositories/score")
                        .param("page", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAcceptValidDateParameter() throws Exception {
        // Given
        PagedScoreResponse response = new PagedScoreResponse(1, 30, 0, List.of());
        when(scoreService.calculateScores(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/repositories/score")
                        .param("created_after", "2024-01-01"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUseDefaultValues_whenParametersNotProvided() throws Exception {
        // Given
        PagedScoreResponse response = new PagedScoreResponse(1, 30, 0, List.of());
        when(scoreService.calculateScores(any(), any(), eq("default"), eq(30), eq(1)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/repositories/score"))
                .andExpect(status().isOk());
    }
}

