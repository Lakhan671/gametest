package com.game.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.game.models.GameConfig;
import com.game.models.GameResult;

import java.io.File;
import java.io.IOException;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public static GameConfig loadConfig(String filePath) throws IOException {
        return objectMapper.readValue(new File(filePath), GameConfig.class);
    }


    public static String toJson(GameResult result) throws IOException {
        return objectMapper.writeValueAsString(result);
    }
}