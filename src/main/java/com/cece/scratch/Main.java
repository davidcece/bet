package com.cece.scratch;


import com.cece.scratch.config.Config;
import com.cece.scratch.config.ConfigLoader;
import com.cece.scratch.config.GameResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String,String> argMap = parseArgs(args);
        validateArgs(argMap);

        Config config = ConfigLoader.loadFromFile(argMap.get("config"));
        ScratchGame scratchGame = new ScratchGame(config);
        GameResult gameResult = scratchGame.placeBet(Double.parseDouble(argMap.get("betting-amount")));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gameResult);
            System.out.println(jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> argMap = new HashMap<>();
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].startsWith("--")) {
                argMap.put(args[i].substring(2), args[i + 1]);
                i++;
            }
        }
        return argMap;
    }

    private static void validateArgs(Map<String, String> argMap) {
        if (!argMap.containsKey("config")) {
            throw new IllegalArgumentException("Missing required argument: --config");
        }
        if (!argMap.containsKey("betting-amount")) {
            throw new IllegalArgumentException("Missing required argument: --betting-amount");
        }
    }
}