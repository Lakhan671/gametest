package com.game.services;


import com.game.models.GameConfig;
import java.util.Random;
import java.util.stream.IntStream;

public class MatrixGenerator {
    private final GameConfig config;
    private final ProbabilityService probabilityService;

    private final Random random = new Random();

    public MatrixGenerator(GameConfig config) {
        this.config = config;
        this.probabilityService = new ProbabilityService(config);
    }
    public String[][] generateMatrix() {
        var rows = config.getRows();
        var cols = config.getColumns();

        var matrix = IntStream.range(0, rows)
                .mapToObj(row -> IntStream.range(0, cols)
                        .mapToObj(col -> probabilityService.getRandomStandardSymbol(row, col))
                        .toArray(String[]::new))
                .toArray(String[][]::new);

        // Add bonus symbols
        var bonusSymbolsCount = random.nextInt(3);
        IntStream.range(0, bonusSymbolsCount)
                .forEach(i -> {
                    var row = random.nextInt(rows);
                    var col = random.nextInt(cols);
                    matrix[row][col] = probabilityService.getRandomBonusSymbol();
                });

        return matrix;
    }
}