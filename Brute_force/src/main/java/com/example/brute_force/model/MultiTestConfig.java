package com.example.brute_force.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MultiTestConfig {
    private String textLengths = "1000";
    private String patternLengths = "10";
    private List<String> dataTypes = new ArrayList<>(Arrays.asList("NATURAL", "UNIFORM", "DNA", "PERIODIC", "PATHOLOGICAL"));
    private String alphabetSizes = "26";
    private String seeds = "42";
    private List<String> patternPositions = new ArrayList<>(Arrays.asList("START", "MIDDLE", "END", "RANDOM", "MULTIPLE"));
    private String matchCounts = "1";
    private int repetitions = 1;

    public List<Integer> getTextLengthList() {
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
}
