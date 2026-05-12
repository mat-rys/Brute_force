package com.example.brute_force.algorithm;

import java.util.ArrayList;
import java.util.List;

public class BruteForceAlgorithm {

    public static class VisualStep {
        private final int textIndex;
        private final int patternIndex;
        private final int shift;
        private final boolean match;
        private final String description;
        private final String type; // "COMPARE", "MATCH", "MISMATCH", "SHIFT", "FOUND"
        private final int pseudoCodeLine;

        public VisualStep(int textIndex, int patternIndex, int shift, boolean match, String description, String type, int pseudoCodeLine) {
            this.textIndex = textIndex;
            this.patternIndex = patternIndex;
            this.shift = shift;
            this.match = match;
            this.description = description;
            this.type = type;
            this.pseudoCodeLine = pseudoCodeLine;
        }

        public int getTextIndex() { return textIndex; }
        public int getPatternIndex() { return patternIndex; }
        public int getShift() { return shift; }
        public boolean isMatch() { return match; }
        public String getDescription() { return description; }
        public String getType() { return type; }
        public int getPseudoCodeLine() { return pseudoCodeLine; }
    }

    public static class Result {
        private final int occurrences;
        private final List<Integer> positions;
        private final long comparisons;
        private final long durationNs;
        private final List<VisualStep> steps;

        public Result(int occurrences, List<Integer> positions, long comparisons, long durationNs, List<VisualStep> steps) {
            this.occurrences = occurrences;
            this.positions = positions;
            this.comparisons = comparisons;
            this.durationNs = durationNs;
            this.steps = steps;
        }

        public int getOccurrences() { return occurrences; }
        public List<Integer> getPositions() { return positions; }
        public long getComparisons() { return comparisons; }
        public long getDurationNs() { return durationNs; }
        public List<VisualStep> getSteps() { return steps; }
    }

    public Result search(String text, String pattern) {
        return search(text, pattern, false);
    }

    public Result search(String text, String pattern, boolean recordSteps) {
        return search(text, pattern, recordSteps, false);
    }

    public Result search(String text, String pattern, boolean recordSteps, boolean ignoreCase) {
        int n = text.length();
        int m = pattern.length();
        long comparisons = 0;
        List<Integer> positions = new ArrayList<>();
        List<VisualStep> steps = recordSteps ? new ArrayList<>() : null;
        
        long startTime = System.nanoTime();

        if (m > 0 && n >= m) {
            for (int i = 0; i <= n - m; i++) {
                if (recordSteps && steps.size() < 2000) {
                    steps.add(new VisualStep(-1, -1, i, false, 
                        String.format("Sprawdzanie pozycji i=%d. Przesunięcie wzorca...", i), "SHIFT", 2));
                }
                int j = 0;
                while (j < m) {
                    comparisons++;
                    char tChar = text.charAt(i + j);
                    char pChar = pattern.charAt(j);
                    
                    boolean isMatch;
                    if (ignoreCase) {
                        isMatch = Character.toLowerCase(tChar) == Character.toLowerCase(pChar);
                    } else {
                        isMatch = tChar == pChar;
                    }
                    
                    if (recordSteps && steps.size() < 2000) {
                        String status = isMatch ? "ZGODNOŚĆ (MATCH)" : "ROZBIEŻNOŚĆ (MISMATCH)";
                        String desc = String.format("Krok: i=%d, j=%d. Porównanie: tekst[%d]='%c' z wzorzec[%d]='%c' -> %s", 
                            i, j, i + j, tChar, j, pChar, status);
                        steps.add(new VisualStep(i + j, j, i, isMatch, desc, isMatch ? "MATCH" : "MISMATCH", 4));
                    }

                    if (!isMatch) {
                        if (recordSteps && steps.size() < 2000) {
                            steps.add(new VisualStep(i + j, j, i, false, 
                                String.format("MISMATCH na pozycji j=%d (tekst[%d] != wzorzec[%d]). Przesunięcie i na %d.", j, i + j, j, i + 1), "MISMATCH", 5));
                        }
                        break;
                    }
                    j++;
                }
                if (j == m) {
                    positions.add(i);
                    if (recordSteps && steps.size() < 2000) {
                        steps.add(new VisualStep(-1, -1, i, true, 
                            String.format("!!! FULL MATCH !!! -> Znaleziono wzorzec na pozycji i=%d. Dodano do listy wyników.", i), "FOUND", 7));
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        return new Result(positions.size(), positions, comparisons, endTime - startTime, steps);
    }
}
