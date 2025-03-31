package com.game.models;


import java.util.List;
import java.util.Map;

public class GameResult {
    private String[][] matrix;
    private double reward;
    private Map<String, List<String>> appliedWinningCombinations;
    private String appliedBonusSymbol;

    public GameResult(String[][] matrix, double reward,
                      Map<String, List<String>> appliedWinningCombinations,
                      String appliedBonusSymbol) {
        this.matrix = matrix;
        this.reward = reward;
        this.appliedWinningCombinations = appliedWinningCombinations;
        this.appliedBonusSymbol = appliedBonusSymbol;
    }

    // Getters
    public String[][] getMatrix() { return matrix; }
    public double getReward() { return reward; }
    public Map<String, List<String>> getAppliedWinningCombinations() { return appliedWinningCombinations; }
    public String getAppliedBonusSymbol() { return appliedBonusSymbol; }
}