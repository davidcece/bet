package com.cece.scratch.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class GameResult {
    private String[][] matrix;
    private double reward;

    @JsonProperty("applied_winning_combinations")
    private Map<String, Set<String>> appliedWinningCombinations;

    @JsonProperty("applied_bonus_symbol")
    private String appliedBonusSymbol;
}
