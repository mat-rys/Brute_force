package com.example.brute_force.model;

import java.util.List;

public class TestConfig {
    private int textLength = 30;
    private int patternLength = 4;
    private String dataType = "UNIFORM";
    private long seed = 42;
    private int repetitions = 1;
    private int repeatInsideBenchmark = 1;
    private String pattern = "aaaaa";
    private int alphabetSize = 26;
    private String patternPosition = "START"; // START, MIDDLE, END, MULTIPLE
    private int matchCount = 1;
    private String customText = "";
    private String customPattern = "";
    private boolean ignoreCase = false;

    // Getters and Setters
    public int getTextLength() { return textLength; }
    public void setTextLength(int textLength) { this.textLength = textLength; }
    public int getPatternLength() { return patternLength; }
    public void setPatternLength(int patternLength) { this.patternLength = patternLength; }
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    public long getSeed() { return seed; }
    public void setSeed(long seed) { this.seed = seed; }
    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }
    public int getRepeatInsideBenchmark() { return repeatInsideBenchmark; }
    public void setRepeatInsideBenchmark(int repeatInsideBenchmark) { this.repeatInsideBenchmark = repeatInsideBenchmark; }
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public int getAlphabetSize() { return alphabetSize; }
    public void setAlphabetSize(int alphabetSize) { this.alphabetSize = alphabetSize; }
    public String getPatternPosition() { return patternPosition; }
    public void setPatternPosition(String patternPosition) { this.patternPosition = patternPosition; }
    public int getMatchCount() { return matchCount; }
    public void setMatchCount(int matchCount) { this.matchCount = matchCount; }
    public String getCustomText() { return customText; }
    public void setCustomText(String customText) { this.customText = customText; }
    public String getCustomPattern() { return customPattern; }
    public void setCustomPattern(String customPattern) { this.customPattern = customPattern; }
    public boolean isIgnoreCase() { return ignoreCase; }
    public void setIgnoreCase(boolean ignoreCase) { this.ignoreCase = ignoreCase; }
}
