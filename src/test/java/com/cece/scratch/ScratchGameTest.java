package com.cece.scratch;

import com.cece.scratch.config.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ScratchGameTest {

    private Config config;

    @BeforeEach
    void setup() {
        config = ConfigLoader.loadFromFile("config.json");
    }

    @Test
    void shouldCountSymbols() {
        String[][] testMatrix = {
                {"A", "A", "B"},
                {"B", "A", "A"},
                {"E", "F", "B"}
        };

        ScratchGame game = new ScratchGame(config, testMatrix);
        GameResult result = game.placeBet(100);

        assertThat(result.getAppliedWinningCombinations())
                .containsKey("A")
                .satisfies(map -> assertThat(map.get("A")).contains("same_symbol_4_times"));
    }


    @Test
    void shouldHaveSameSymbolsHorizontally() {
        String[][] testMatrix = {
                {"A", "A", "A"},
                {"B", "C", "D"},
                {"E", "F", "B"}
        };

        ScratchGame game = new ScratchGame(config, testMatrix);
        GameResult result = game.placeBet(100);

        assertThat(result.getAppliedWinningCombinations())
                .containsKey("A")
                .satisfies(map -> assertThat(map.get("A")).contains("same_symbols_horizontally"));
    }

    @Test
    void shouldHaveSameSymbolsVertically() {
        String[][] testMatrix = {
                {"A", "B", "C"},
                {"A", "B", "D"},
                {"A", "F", "B"}
        };

        ScratchGame game = new ScratchGame(config, testMatrix);
        GameResult result = game.placeBet(100);

        assertThat(result.getAppliedWinningCombinations())
                .containsKey("A")
                .satisfies(map -> assertThat(map.get("A")).contains("same_symbols_vertically"));
    }

    @Test
    void shouldHaveDiagonalLeftToRight() {
        String[][] testMatrix = {
                {"A", "B", "C"},
                {"B", "A", "5x"},
                {"C", "B", "A"}
        };

        ScratchGame game = new ScratchGame(config, testMatrix);
        GameResult result = game.placeBet(100);

        assertThat(result.getAppliedWinningCombinations())
                .containsKey("A")
                .satisfies(map -> assertThat(map.get("A")).contains("same_symbols_diagonally_left_to_right"));
    }

    @Test
    void shouldHaveDiagonalRightToLeft() {
        String[][] testMatrix = {
                {"A", "B", "A"},
                {"B", "A", "5x"},
                {"A", "B", "C"}
        };

        ScratchGame game = new ScratchGame(config, testMatrix);
        GameResult result = game.placeBet(100);

        assertThat(result.getAppliedWinningCombinations())
                .containsKey("A")
                .satisfies(map -> assertThat(map.get("A")).contains("same_symbols_diagonally_right_to_left"));
    }

    @Test
    void shouldHaveBonusSymbolIfWinning() {
        String[][] testMatrix = {
                {"A", "A", "C"},
                {"D", "A", "5x"},
                {"B", "D", "E"}
        };

        ScratchGame game = new ScratchGame(config, testMatrix);
        GameResult result = game.placeBet(100);

        assertThat(result.getAppliedWinningCombinations())
                .containsKey("A")
                .satisfies(map -> assertThat(map.get("A")).contains("same_symbol_3_times"));
        assertThat(result.getAppliedBonusSymbol()).isEqualTo("5x");
    }

    @Test
    void shouldHaveNoBonusSymbolIfLosing() {
        String[][] testMatrix = {
                {"A", "B", "C"},
                {"D", "E", "5x"},
                {"B", "D", "E"}
        };

        ScratchGame game = new ScratchGame(config, testMatrix);
        GameResult result = game.placeBet(100);

        assertThat(result.getReward()).isZero();
        assertThat(result.getAppliedBonusSymbol()).isNull();
    }


    @Test
    void shouldCalculateRewardExtra() {
        //A symbol *5
        //A same_symbol_5_times *2
        //A same_symbols_vertically *2
        // 100*5*2*2 = 2000

        //B symbol *3
        //B same_symbol_3_times *1
        //A same_symbols_vertically *2
        // 100*3*1*2 = 600

        //bonus symbol +1000
        // 2000 + 600 + 1000 = 3600

        String[][] testMatrix = {
                {"A", "A", "B"},
                {"A", "+1000", "B"},
                {"A", "A", "B"}
        };

        ScratchGame game = new ScratchGame(config, testMatrix);
        GameResult result = game.placeBet(100);

        assertThat(result.getReward()).isEqualTo(3600);
    }

    @Test
    void shouldCalculateRewardMultiplier() {
        //B symbol *3
        //B same_symbol_3_times *1
        // 100*3*1 = 300

        //bonus symbol 10x
        // 300 * 10 = 3000

        String[][] testMatrix = {
                {"A", "B", "C"},
                {"E", "B", "10x"},
                {"F", "D", "B"}
        };


        ScratchGame game = new ScratchGame(config, testMatrix);
        GameResult result = game.placeBet(100);

        assertThat(result.getReward()).isEqualTo(3000);
    }

    @Test
    void shouldCalculateRewardComplexMultiplier(){
        //A symbol *5
        //A same_symbol_8_times *10
        //A same_symbols_horizontally *2
        //A same_symbols_vertically *2
        //A same_symbols_diagonally_left_to_right *5
        //A same_symbols_diagonally_right_to_left *5
        // 100*5*10*2*2*5*5 = 500_000

        //bonus symbol 10x
        // 500000 * 10 = 5000_000

        String[][] testMatrix = {
                {"A", "A", "A"},
                {"A", "A", "10x"},
                {"A", "A", "A"}
        };

        ScratchGame game = new ScratchGame(config, testMatrix);
        GameResult result = game.placeBet(100);

        assertThat(result.getReward()).isEqualTo(5_000_000);
    }
}
