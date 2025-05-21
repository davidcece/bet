package com.cece.scratch;

import com.cece.scratch.config.*;

import java.util.*;
import java.util.stream.Collectors;


public class ScratchGame {

    private final Config config;
    private final Random random;
    private final String[][] matrix;
    private final Map<String, Set<String>> appliedWinningCombinations;
    private String appliedBonusSymbol;

    public ScratchGame(Config config) {
        this.config = config;
        this.random = new Random();
        this.matrix = new String[config.getRows()][config.getColumns()];
        this.appliedWinningCombinations = new HashMap<>();
        initializeMatrix();
    }

    public ScratchGame(Config config, String[][] testMatrix) {
        this.config = config;
        this.matrix = testMatrix;
        this.random = new Random();
        this.appliedWinningCombinations = new HashMap<>();
        this.appliedBonusSymbol = getAppliedBonusSymbolFromMatrix();
    }


    public GameResult placeBet(double amount) {
        getCountMultiplier();
        getLinearSymbolMultipliers();

        GameResult gameResult = new GameResult();
        gameResult.setMatrix(matrix);
        gameResult.setReward(0);
        gameResult.setAppliedWinningCombinations(appliedWinningCombinations);
        gameResult.setAppliedBonusSymbol(null);

        for (var entry : appliedWinningCombinations.entrySet()) {
            String symbol = entry.getKey();
            Double multiplier = config.getSymbols().get(symbol).getRewardMultiplier();

            Set<String> winCombinationNames = entry.getValue();
            for (String winCombinationName : winCombinationNames) {
                WinCombination winCombination = config.getWinCombinations().get(winCombinationName);
                multiplier *= winCombination.getRewardMultiplier();
            }
            gameResult.setReward(gameResult.getReward() + amount * multiplier);
        }

        if (gameResult.getReward() > 0 && appliedBonusSymbol != null) {
            gameResult.setAppliedBonusSymbol(appliedBonusSymbol);
            Symbol symbol = config.getSymbols().get(appliedBonusSymbol);
            if (symbol.getImpact().equalsIgnoreCase("extra_bonus")) {
                gameResult.setReward(gameResult.getReward() + symbol.getExtra());
            } else {
                gameResult.setReward(gameResult.getReward() * symbol.getRewardMultiplier());
            }
        }

        double reward = Math.round(gameResult.getReward()/100.0) * 100.0;
        gameResult.setReward(reward);
        return gameResult;
    }

    public void initializeMatrix() {
        List<StandardSymbol> standardSymbols = config.getProbabilities().getStandardSymbols();
        for (StandardSymbol standardSymbol : standardSymbols) {
            int column = standardSymbol.getColumn();
            int row = standardSymbol.getRow();
            Map<String, Integer> symbols = standardSymbol.getSymbols();
            String symbol = getRandomSymbol(symbols);
            matrix[row][column] = symbol;
        }

        Map<String, Integer> bonusSymbols = config.getProbabilities().getBonusSymbols().getSymbols();
        String bonusSymbol = getRandomSymbol(bonusSymbols);
        if (hasBonus(bonusSymbol)) {
            appliedBonusSymbol = bonusSymbol;
            int row = random.nextInt(config.getRows());
            int column = random.nextInt(config.getColumns());
            matrix[row][column] = bonusSymbol;
        }
    }

    private String getRandomSymbol(Map<String, Integer> symbols) {
        int totalWeight = 0;
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : symbols.entrySet()) {
            totalWeight += entry.getValue();
            treeMap.put(totalWeight, entry.getKey());
        }

        int randomValue = random.nextInt(totalWeight);
        return treeMap.higherEntry(randomValue).getValue();
    }

    private boolean hasBonus(String symbol) {
        return !symbol.equalsIgnoreCase("MISS");
    }

    private void getCountMultiplier() {
        Map<String, Integer> symbolCounts = new HashMap<>();
        for (int i = 0; i < config.getRows(); i++) {
            for (int j = 0; j < config.getColumns(); j++) {
                String symbol = matrix[i][j];
                if (symbol != null) {
                    symbolCounts.put(symbol, symbolCounts.getOrDefault(symbol, 0) + 1);
                }
            }
        }

        Map<String, WinCombination> sameSymbolGroup = config.getWinCombinations().entrySet().stream()
                .filter(s -> s.getValue().getWhen().equalsIgnoreCase("same_symbols"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (var entry : sameSymbolGroup.entrySet()) {
            String winCombinationName = entry.getKey();
            WinCombination winCombination = entry.getValue();
            int count = winCombination.getCount();

            for (var symbolCountEntry : symbolCounts.entrySet()) {
                String symbol = symbolCountEntry.getKey();
                int symbolCount = symbolCountEntry.getValue();
                if (symbolCount == count) {
                    appliedWinningCombinations.putIfAbsent(symbol, new HashSet<>());
                    appliedWinningCombinations.get(symbol).add(winCombinationName);
                }
            }
        }
    }

    private void getLinearSymbolMultipliers() {
        Map<String, WinCombination> linearSymbols = config.getWinCombinations().entrySet().stream()
                .filter(s -> s.getValue().getWhen().equalsIgnoreCase("linear_symbols"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        linearSymbols.forEach(this::getLinearSymbolMultiplier);
    }

    private void getLinearSymbolMultiplier(String name, WinCombination winCombination) {
        for (List<String> area : winCombination.getCoveredAreas()) {
            String symbol = "";
            for (String position : area) {
                int row = Integer.parseInt(position.split(":")[0]);
                int column = Integer.parseInt(position.split(":")[1]);
                if (symbol.isEmpty()) {
                    symbol = matrix[row][column];
                } else {
                    if (!symbol.equalsIgnoreCase(matrix[row][column])) {
                        symbol = "";
                        break;
                    }
                }
            }
            if (!symbol.isEmpty()) {
                appliedWinningCombinations.putIfAbsent(symbol, new HashSet<>());
                appliedWinningCombinations.get(symbol).add(name);
            }
        }
    }

    private String getAppliedBonusSymbolFromMatrix(){
        for(int i = 0; i < config.getRows(); i++) {
            for (int j = 0; j < config.getColumns(); j++) {
                String symbol = matrix[i][j];
                String type = config.getSymbols().get(symbol).getType();
                if("bonus".equalsIgnoreCase(type)){
                    return symbol;
                }
            }
        }

        return null;
    }

}
