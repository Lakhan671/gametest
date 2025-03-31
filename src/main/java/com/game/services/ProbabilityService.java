package com.game.services;


import com.game.models.GameConfig;

import java.util.*;
public class ProbabilityService {
    private final GameConfig config;
    private final Map<String, Integer> bonusSymbols;

    public ProbabilityService(GameConfig config) {
        this.config = config;
        this.bonusSymbols = config.getProbabilities().getBonusSymbols().getSymbols();
    }

    public String getRandomStandardSymbol(int row, int col) {
        GameConfig.StandardSymbolProbability cellProb = findCellProbability(row, col);
        if (cellProb == null) {
            cellProb = config.getProbabilities().getStandardSymbols()[0];
        }
        return selectSymbol(cellProb.getSymbols());
    }

    public String getRandomBonusSymbol() {
        return selectSymbol(bonusSymbols);
    }

    private GameConfig.StandardSymbolProbability findCellProbability(int row, int col) {
        for (GameConfig.StandardSymbolProbability prob : config.getProbabilities().getStandardSymbols()) {
            if (prob.getRow() == row && prob.getColumn() == col) {
                return prob;
            }
        }
        return null;
    }

    private String selectSymbol(Map<String, Integer> symbolWeights) {
        int total = symbolWeights.values().stream().mapToInt(Integer::intValue).sum();
        double random = Math.random() * total;
        double accumulated = 0.0;

        for (Map.Entry<String, Integer> entry : symbolWeights.entrySet()) {
            accumulated += entry.getValue();
            if (random < accumulated) {
                return entry.getKey();
            }
        }
        return symbolWeights.keySet().iterator().next();
    }
}
