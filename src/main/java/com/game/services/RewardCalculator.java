package com.game.services;

import com.game.models.Symbol;
import com.game.models.WinCombination;
import com.game.models.GameConfig;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RewardCalculator {
    private final GameConfig config;

    private record SymbolCombination(String symbol, String combinationName) {}

    public RewardCalculator(GameConfig config) {
        this.config = config;
    }
    /**
     * This method checks for all the winning combinations in the game matrix and returns a map
     * of winning symbols and their corresponding winning combination names.
     * It processes the combinations defined in the configuration and applies the appropriate logic
     * based on whether the combination is for "same_symbols" or "linear_symbols".
     *
     * @param matrix The 2D array representing the game matrix, where each element is a symbol.
     * @return A map where the key is the symbol, and the value is a list of winning combination names for that symbol.
     */
    public Map<String, List<String>> checkWinningCombinations(String[][] matrix) {
        return config.getWinCombinations().entrySet().stream()  // Stream over the set of win combinations from the config
                .flatMap(entry -> {  // For each combination, process and check the winning conditions
                    String combinationName = entry.getKey();  // Get the combination name
                    WinCombination combination = entry.getValue();  // Get the WinCombination object

                    // Step 1: Based on the "when" condition, choose the appropriate combination check method
                    return switch (combination.getWhen()) {
                        case "same_symbols" -> checkSameSymbolsCombination(matrix, combinationName, combination);
                        case "linear_symbols" -> checkLinearSymbolsCombination(matrix, combinationName, combination);
                        default -> Stream.empty();  // For unsupported conditions, return an empty stream
                    };
                })
                .collect(Collectors.groupingBy(
                        SymbolCombination::symbol,  // Group by the symbol (from the SymbolCombination)
                        Collectors.mapping(SymbolCombination::combinationName, Collectors.toList())  // Map to the combination names
                ));
    }
    /**
     * This method calculates the total reward based on the winning combinations, symbol configurations, and bonus symbols.
     * It computes the reward for each winning combination, multiplies it with the betting amount, and then applies any
     * bonus symbols to adjust the total reward.
     *
     * @param matrix The 2D array representing the game matrix, where each element is a symbol.
     * @param bettingAmount The amount bet on the game, which is used to calculate the base reward for each symbol.
     * @param winningCombinations A map containing the winning symbols and their corresponding winning combination names.
     * @return The total reward after applying the winning combinations' multipliers and bonus symbols.
     */
    public double calculateReward(String[][] matrix, double bettingAmount,
                                  Map<String, List<String>> winningCombinations) {
        // Step 1: If there are no winning combinations, return a reward of 0.0
        if (winningCombinations.isEmpty()) {
            return 0.0;  // No winnings, so no reward
        }

        // Step 2: Calculate the total reward for all winning combinations
        double totalReward = winningCombinations.entrySet().stream()  // Stream over the map of winning combinations
                .mapToDouble(entry -> {  // Convert each entry into a double reward value
                    String symbol = entry.getKey();  // Get the symbol name
                    List<String> combinations = entry.getValue();  // Get the list of winning combinations for the symbol
                    Symbol symbolConfig = config.getSymbols().get(symbol);  // Get the symbol's configuration from the config

                    // Step 3: Check if the symbol is valid and of type "standard"
                    if (symbolConfig == null || !"standard".equals(symbolConfig.getType())) {
                        return 0.0;  // If the symbol is invalid or not "standard", return 0.0
                    }

                    // Step 4: Calculate the base reward based on the betting amount and the symbol's reward multiplier
                    double baseReward = bettingAmount * symbolConfig.getRewardMultiplier();

                    // Step 5: Apply multipliers from each winning combination
                    return combinations.stream()  // Stream over each winning combination
                            .map(config.getWinCombinations()::get)  // Map the combination name to the WinCombination object
                            .filter(Objects::nonNull)  // Ensure the WinCombination object is not null
                            .mapToDouble(WinCombination::getRewardMultiplier)  // Get the reward multiplier for each combination
                            .reduce(baseReward, (acc, multiplier) -> acc * multiplier);  // Accumulate the reward by multiplying each combination's multiplier
                })
                .sum();  // Sum up the total reward for all symbols and combinations

        // Step 6: Apply any bonus symbols that may modify the reward
        return applyBonusSymbols(matrix, totalReward);  // Apply bonus symbols and return the final reward
    }

    /**
     * This method applies the bonus symbols' effects on the current reward. It iterates over the matrix and checks
     * each cell for bonus symbols. Depending on the type of bonus effect (multiply or extra bonus),
     * it adjusts the reward accordingly.
     *
     * @param matrix The 2D array representing the game matrix, where each element is a symbol.
     * @param reward The current reward before applying bonus symbols.
     * @return The updated reward after applying the bonus symbols.
     */
    private double applyBonusSymbols(String[][] matrix, double reward) {
        // Step 1: If the reward is less than or equal to zero, no bonus is applied, so return the reward as is.
        if (reward <= 0) {
            return reward;  // No bonus applied if the reward is zero or negative
        }

        // Step 2: Process the matrix to apply the bonus effects
        return Arrays.stream(matrix)  // Stream over each row in the matrix
                .flatMap(Arrays::stream)  // Flatten the matrix rows into a single stream of symbols
                .map(config.getSymbols()::get)  // Map each symbol in the matrix to its corresponding Symbol configuration
                .filter(Objects::nonNull)  // Ensure the symbol configuration is not null
                .filter(symbolConfig -> "bonus".equals(symbolConfig.getType()))  // Filter for bonus symbols only
                .reduce(reward, (current, symbolConfig) -> {  // Accumulate the bonus effects into the reward value
                    switch (symbolConfig.getImpact()) {  // Check the type of bonus effect for each symbol
                        case "multiply_reward" -> {  // If the bonus symbol's impact is to multiply the reward
                            return current * symbolConfig.getRewardMultiplier();  // Multiply the reward by the bonus multiplier
                        }
                        case "extra_bonus" -> {  // If the bonus symbol's impact is to add an extra bonus to the reward
                            return current + symbolConfig.getExtra();  // Add the extra bonus amount to the reward
                        }
                        default -> {  // If the bonus symbol has no effect (or an unknown impact type)
                            return current;  // No change to the reward
                        }
                    }
                }, (a, b) -> b);  // Combiner function (used in parallel processing, but not needed here)
    }


    /**
     * This method checks for combinations of the same symbols in the provided matrix. It counts the occurrences
     * of each symbol and returns a stream of `SymbolCombination` objects for those symbols that meet the
     * specified minimum count defined in the `WinCombination`.
     *
     * @param matrix The 2D array representing the game matrix, where each element is a symbol.
     * @param combinationName The name of the winning combination (e.g., "same_symbols", "linear_symbols").
     * @param combination The win combination object containing the configuration for the symbol count required.
     * @return A stream of `SymbolCombination` objects for each valid symbol combination that meets the minimum count.
     */
    private Stream<SymbolCombination> checkSameSymbolsCombination(String[][] matrix, String combinationName,
                                                                  WinCombination combination) {
        // Step 1: Flatten the 2D matrix and count occurrences of each symbol in the matrix
        return Arrays.stream(matrix)  // Stream over each row in the matrix
                .flatMap(Arrays::stream)  // Flatten each row to get a stream of symbols (2D -> 1D)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))  // Group by symbol and count occurrences

                // Step 2: Filter out the symbols that do not meet the required count for the combination
                .entrySet().stream()  // Convert the entry set (symbol -> count) into a stream
                .filter(entry -> entry.getValue() >= combination.getCount())  // Only include symbols that meet the count requirement

                // Step 3: Map the symbol to its configuration in the `config` object and filter by "standard" type
                .map(Map.Entry::getKey)  // Get the symbol from the map entry
                .map(symbol -> Map.entry(symbol, config.getSymbols().get(symbol)))  // Map the symbol to its configuration from `config.getSymbols()`
                .filter(entry -> entry.getValue() != null && "standard".equals(entry.getValue().getType()))  // Ensure the symbol is valid and of type "standard"

                // Step 4: Create `SymbolCombination` objects for each valid symbol
                .map(entry -> new SymbolCombination(entry.getKey(), combinationName));  // Map to `SymbolCombination` using symbol and combination name
    }

    /**
     * This method checks for linear symbol combinations in the provided matrix based on the covered areas defined
     * in the win combination. It returns a stream of `SymbolCombination` for each valid combination found.
     *
     * @param matrix The 2D array representing the game matrix, where each element is a symbol.
     * @param combinationName The name of the winning combination (e.g., "same_symbols", "linear_symbols").
     * @param combination The win combination object containing the configuration for the winning areas.
     * @return A stream of `SymbolCombination` objects for each valid linear combination found.
     */
    private Stream<SymbolCombination> checkLinearSymbolsCombination(String[][] matrix, String combinationName,
                                                                    WinCombination combination) {
        // Stream over the 2D array of covered areas defined in the win combination
        return Arrays.stream(combination.getCoveredAreas())  // Get a stream of rows (each row is an array of coordinates)
                .flatMap(area -> {  // For each row (area) in the covered areas...

                    // Process each coordinate in the area (which represents a symbol position in the matrix)
                    Set<String> symbols = Arrays.stream(area)  // Convert the array of coordinates (area) into a stream
                            .map(coord -> coord.split(":"))  // Split each coordinate string into "row" and "column" parts (e.g., "0:1")
                            .map(parts -> matrix[Integer.parseInt(parts[0])][Integer.parseInt(parts[1])])  // Get the symbol from the matrix using row & column indices
                            .collect(Collectors.toSet());  // Collect all the symbols from this area into a Set (to ensure uniqueness)

                    // If the set of symbols contains only one symbol (indicating a linear combination of the same symbol)
                    return symbols.size() == 1  // If all symbols in this area are the same (set size is 1)
                            ? symbols.stream()  // Stream the symbols (there should only be one)
                            .map(symbol -> Map.entry(symbol, config.getSymbols().get(symbol)))  // Map the symbol to its configuration from `config.getSymbols()`
                            .filter(entry -> entry.getValue() != null && "standard".equals(entry.getValue().getType()))  // Ensure the symbol is valid and of type "standard"
                            .map(entry -> new SymbolCombination(entry.getKey(), combinationName))  // Create a SymbolCombination object with the symbol and combination name
                            : Stream.empty();  // If no valid linear combination is found, return an empty stream (no result)
                });
    }


}