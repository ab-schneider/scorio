package org.example.score;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ScoringRegistry {
    private final Map<String, ScoringStrategy> strategies;

    public ScoringRegistry(List<ScoringStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(ScoringStrategy::name, s -> s));
    }

    public ScoringStrategy get(String name) {
        return strategies.get(name);
    }

    public Set<String> names() { return strategies.keySet(); }
}
