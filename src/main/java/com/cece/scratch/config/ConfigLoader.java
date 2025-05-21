package com.cece.scratch.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ConfigLoader {

    public static Config loadFromFile(String configPath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(configPath), Config.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + configPath, e);
        }
    }
}
