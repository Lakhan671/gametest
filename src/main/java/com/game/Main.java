package com.game;

import com.game.models.GameConfig;
import com.game.models.GameResult;
import com.game.services.GameEngine;
import com.game.utils.JsonUtils;

public class Main {
    public static void main(String[] args) {
        try {
            // Parse command line arguments
            String configPath = null;
            double bettingAmount = 0;

            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("--config") && i + 1 < args.length) {
                    configPath = args[i + 1];
                } else if (args[i].equals("--betting-amount") && i + 1 < args.length) {
                    bettingAmount = Double.parseDouble(args[i + 1]);
                }
            }

            if (configPath == null || bettingAmount <= 0) {
                System.out.println("Usage: java -jar scratch-game.jar --config <config-file> --betting-amount <amount>");
                return;
            }

            // Load game configuration
            GameConfig config = JsonUtils.loadConfig(configPath);

            // Create and play game
            GameEngine game = new GameEngine(config);
            GameResult result = game.play(bettingAmount);

            // Output result as JSON
            System.out.println(JsonUtils.toJson(result));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}