package com.example.brute_force.model;

import com.example.brute_force.algorithm.BruteForceAlgorithm;
import java.util.List;

public class TestResult {
    private String id;
    private String dataType;
    private int textLength;
    private int patternLength;
    private int alphabetSize;
    private long seed;
    private String patternPosition;
    private String repeatability;
    private int occurrences;
    private List<Integer> positions;
    private long comparisons;
    private double cn;
    private long durationNs;
    private long minDurationNs;
    private long maxDurationNs;
    private double avgDurationNs;
    private double stdDevNs;
    private double opsPerNs;
    private List<BruteForceAlgorithm.VisualStep> visualSteps;
    private String text;
    private String pattern;
    private String dataDescription;
    private String algorithmImpact;
    private String difficultyExplanation;
    private String searchSummary;
    private String generationMethod;
    private long shifts;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getAlphabetSize() { return alphabetSize; }
    public void setAlphabetSize(int alphabetSize) { this.alphabetSize = alphabetSize; }
    public long getSeed() { return seed; }
    public void setSeed(long seed) { this.seed = seed; }
    public String getPatternPosition() { return patternPosition; }
    public void setPatternPosition(String patternPosition) { this.patternPosition = patternPosition; }
    public String getRepeatability() { return repeatability; }
    public void setRepeatability(String repeatability) { this.repeatability = repeatability; }
    public double getCn() { return cn; }
    public void setCn(double cn) { this.cn = cn; }
    public String getGenerationMethod() { return generationMethod; }
    public void setGenerationMethod(String generationMethod) { this.generationMethod = generationMethod; }
    public String getDifficultyExplanation() { return difficultyExplanation; }
    public void setDifficultyExplanation(String difficultyExplanation) { this.difficultyExplanation = difficultyExplanation; }
    public String getSearchSummary() { return searchSummary; }
    public void setSearchSummary(String searchSummary) { this.searchSummary = searchSummary; }
    public String getDataDescription() { return dataDescription; }
    public void setDataDescription(String dataDescription) { this.dataDescription = dataDescription; }
    public String getAlgorithmImpact() { return algorithmImpact; }
    public void setAlgorithmImpact(String algorithmImpact) { this.algorithmImpact = algorithmImpact; }
    public long getShifts() { return shifts; }
    public void setShifts(long shifts) { this.shifts = shifts; }
    public List<BruteForceAlgorithm.VisualStep> getVisualSteps() { return visualSteps; }
    public void setVisualSteps(List<BruteForceAlgorithm.VisualStep> visualSteps) { this.visualSteps = visualSteps; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    public int getTextLength() { return textLength; }
    public void setTextLength(int textLength) { this.textLength = textLength; }
    public int getPatternLength() { return patternLength; }
    public void setPatternLength(int patternLength) { this.patternLength = patternLength; }
    public int getOccurrences() { return occurrences; }
    public void setOccurrences(int occurrences) { this.occurrences = occurrences; }
    public List<Integer> getPositions() { return positions; }
    public void setPositions(List<Integer> positions) { this.positions = positions; }
    public long getComparisons() { return comparisons; }
    public void setComparisons(long comparisons) { this.comparisons = comparisons; }
    public long getDurationNs() { return durationNs; }
    public void setDurationNs(long durationNs) { this.durationNs = durationNs; }
    public long getMinDurationNs() { return minDurationNs; }
    public void setMinDurationNs(long minDurationNs) { this.minDurationNs = minDurationNs; }
    public long getMaxDurationNs() { return maxDurationNs; }
    public void setMaxDurationNs(long maxDurationNs) { this.maxDurationNs = maxDurationNs; }
    public double getAvgDurationNs() { return avgDurationNs; }
    public void setAvgDurationNs(double avgDurationNs) { this.avgDurationNs = avgDurationNs; }
    public double getStdDevNs() { return stdDevNs; }
    public void setStdDevNs(double stdDevNs) { this.stdDevNs = stdDevNs; }
    public double getOpsPerNs() { return opsPerNs; }
    public void setOpsPerNs(double opsPerNs) { this.opsPerNs = opsPerNs; }
}
