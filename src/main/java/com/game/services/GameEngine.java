package com.game.services;

import com.game.models.GameConfig;
import com.game.models.GameResult;
import com.game.models.Symbol;

import java.util.*;

public class GameEngine {
    private final GameConfig config;
    private final MatrixGenerator matrixGenerator;
    private final RewardCalculator rewardCalculator;

    public GameEngine(GameConfig config) {
        this.config = config;
        this.matrixGenerator = new MatrixGenerator(config);
        this.rewardCalculator = new RewardCalculator(config);
    }

    public GameResult play(double bettingAmount) {
        // Generate game matrix
        String[][] matrix = matrixGenerator.generateMatrix();

        // Analyze matrix for winning combinations
        Map<String, List<String>> winningCombinations = rewardCalculator.checkWinningCombinations(matrix);

        // Calculate reward
        double reward = rewardCalculator.calculateReward(matrix, bettingAmount, winningCombinations);

        // Find applied bonus symbol (if any)
        String bonusSymbol = findAppliedBonusSymbol(matrix, winningCombinations);

        return new GameResult(matrix, reward, winningCombinations, bonusSymbol);
    }

    private String findAppliedBonusSymbol(String[][] matrix, Map<String, List<String>> winningCombinations) {
        if (winningCombinations.isEmpty()) {
            return null;
        }
      StringBuilder bresult= new StringBuilder();
        // Check all cells for bonus symbols
        for (String[] row : matrix) {
            for (String symbol : row) {
                Symbol symbolConfig = config.getSymbols().get(symbol);
                if (symbolConfig != null && "bonus".equals(symbolConfig.getType()) && !"MISS".equals(symbol)) {
                    bresult.append(symbol).append(",");
                }
            }
        }
        return bresult.toString();
    }
}
