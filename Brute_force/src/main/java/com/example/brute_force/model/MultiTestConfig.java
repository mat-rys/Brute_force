package com.example.brute_force.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MultiTestConfig {
    private String textLengths = "10000, 20000, 30000";
    private int minN = 10000;
    private int maxN = 30000;
    private int stepN = 10000;
    private String patternLengths = "10";
    private List<String> dataTypes = new ArrayList<>(Arrays.asList("NATURAL", "UNIFORM", "DNA", "PERIODIC", "PATHOLOGICAL"));
    private String alphabetSizes = "26";
    private String seeds = "42";
    private List<String> patternPositions = new ArrayList<>(Arrays.asList("START", "MIDDLE", "END", "RANDOM"));
    private String matchCounts = "1";
    private int repetitions = 8;
    private int samplesPerN = 4;
    private int repeatInsideBenchmark = 10;
    private double partialMatchDensity = 0.1;
    private double repeatability = 0.0;

    public List<Integer> getTextLengthList() {
        if (minN > 0 && maxN >= minN && stepN > 0) {
            List<Integer> list = new ArrayList<>();
            for (int n = minN; n <= maxN; n += stepN) {
                list.add(n);
            }
            return list;
        }
        return parseIntegerList(textLengths);
    }

    public List<Integer> getPatternLengthList() {
        return parseIntegerList(patternLengths);
    }

    public List<Integer> getAlphabetSizeList() {
        return parseIntegerList(alphabetSizes);
    }

    public List<Long> getSeedList() {
        return Arrays.stream(seeds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public List<Integer> getMatchCountList() {
        return parseIntegerList(matchCounts);
    }

    private List<Integer> parseIntegerList(String input) {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public String getTextLengths() { return textLengths; }
    public void setTextLengths(String textLengths) { this.textLengths = textLengths; }
    public int getMinN() { return minN; }
    public void setMinN(int minN) { this.minN = minN; }
    public int getMaxN() { return maxN; }
    public void setMaxN(int maxN) { this.maxN = maxN; }
    public int getStepN() { return stepN; }
    public void setStepN(int stepN) { this.stepN = stepN; }
    public String getPatternLengths() { return patternLengths; }
    public void setPatternLengths(String patternLengths) { this.patternLengths = patternLengths; }
    public List<String> getDataTypes() { return dataTypes; }
    public void setDataTypes(List<String> dataTypes) { this.dataTypes = dataTypes; }
    public String getAlphabetSizes() { return alphabetSizes; }
    public void setAlphabetSizes(String alphabetSizes) { this.alphabetSizes = alphabetSizes; }
    public String getSeeds() { return seeds; }
    public void setSeeds(String seeds) { this.seeds = seeds; }
    public List<String> getPatternPositions() { return patternPositions; }
    public void setPatternPositions(List<String> patternPositions) { this.patternPositions = patternPositions; }
    public String getMatchCounts() { return matchCounts; }
    public void setMatchCounts(String matchCounts) { this.matchCounts = matchCounts; }
    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }
    public int getSamplesPerN() { return samplesPerN; }
    public void setSamplesPerN(int samplesPerN) { this.samplesPerN = samplesPerN; }
    public int getRepeatInsideBenchmark() { return repeatInsideBenchmark; }
    public void setRepeatInsideBenchmark(int repeatInsideBenchmark) { this.repeatInsideBenchmark = repeatInsideBenchmark; }
    public double getPartialMatchDensity() { return partialMatchDensity; }
    public void setPartialMatchDensity(double partialMatchDensity) { this.partialMatchDensity = partialMatchDensity; }
    public double getRepeatability() { return repeatability; }
    public void setRepeatability(double repeatability) { this.repeatability = repeatability; }
}
