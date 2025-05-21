package com.cece.scratch.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Probabilities {
    @JsonProperty("standard_symbols")
    private List<StandardSymbol> standardSymbols;

    @JsonProperty("bonus_symbols")
    private BonusSymbols bonusSymbols;
}
