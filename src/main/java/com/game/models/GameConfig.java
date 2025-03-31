package com.game.models;

import java.util.Map;

public class GameConfig {
    private int columns = 3;
    private int rows = 3;
    private Map<String, Symbol> symbols;
    private Probabilities probabilities;
    private Map<String, WinCombination> winCombinations;

    // Getters and setters
    public int getColumns() { return columns; }
    public void setColumns(int columns) { this.columns = columns; }
    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public Map<String, Symbol> getSymbols() { return symbols; }
    public void setSymbols(Map<String, Symbol> symbols) { this.symbols = symbols; }
    public Probabilities getProbabilities() { return probabilities; }
    public void setProbabilities(Probabilities probabilities) { this.probabilities = probabilities; }
    public Map<String, WinCombination> getWinCombinations() { return winCombinations; }
    public void setWinCombinations(Map<String, WinCombination> winCombinations) { this.winCombinations = winCombinations; }

    public static class Probabilities {
        private StandardSymbolProbability[] standard_symbols;
        private BonusSymbolProbability bonus_symbols;

        // Getters and setters
        public StandardSymbolProbability[] getStandardSymbols() { return standard_symbols; }
        public void setStandardSymbols(StandardSymbolProbability[] standard_symbols) { this.standard_symbols = standard_symbols; }
        public BonusSymbolProbability getBonusSymbols() { return bonus_symbols; }
        public void setBonusSymbols(BonusSymbolProbability bonus_symbols) { this.bonus_symbols = bonus_symbols; }
    }

    public static class StandardSymbolProbability {
        private int column;
        private int row;
        private Map<String, Integer> symbols;

        // Getters and setters
        public int getColumn() { return column; }
        public void setColumn(int column) { this.column = column; }
        public int getRow() { return row; }
        public void setRow(int row) { this.row = row; }
        public Map<String, Integer> getSymbols() { return symbols; }
        public void setSymbols(Map<String, Integer> symbols) { this.symbols = symbols; }
    }

    public static class BonusSymbolProbability {
        private Map<String, Integer> symbols;

        // Getters and setters
        public Map<String, Integer> getSymbols() { return symbols; }
        public void setSymbols(Map<String, Integer> symbols) { this.symbols = symbols; }
    }
}