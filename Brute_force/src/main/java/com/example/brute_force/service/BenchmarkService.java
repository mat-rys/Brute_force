package com.example.brute_force.service;

import com.example.brute_force.algorithm.BruteForceAlgorithm;
import com.example.brute_force.model.MultiTestConfig;
import com.example.brute_force.model.TestConfig;
import com.example.brute_force.model.TestResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BenchmarkService {

    private final BruteForceAlgorithm algorithm = new BruteForceAlgorithm();

    public List<TestResult> runMultiBenchmark(MultiTestConfig multiConfig) {
        List<TestResult> results = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(1);

        for (int n : multiConfig.getTextLengthList()) {
            for (int m : multiConfig.getPatternLengthList()) {
                for (String type : multiConfig.getDataTypes()) {
                    for (int sigma : multiConfig.getAlphabetSizeList()) {
                        for (long seed : multiConfig.getSeedList()) {
                            // Dodajemy próbki dla każdego n
                            for (int s = 0; s < multiConfig.getSamplesPerN(); s++) {
                                for (String pos : multiConfig.getPatternPositions()) {
                                    for (int count : multiConfig.getMatchCountList()) {
                                        TestConfig config = new TestConfig();
                                        config.setTextLength(n);
                                        config.setPatternLength(m);
                                        config.setDataType(type);
                                        config.setAlphabetSize(sigma);
                                        config.setSeed(seed + s); // Unikalny seed dla każdej próbki
                                        config.setPatternPosition(pos);
                                        config.setMatchCount(count);
                                        config.setRepetitions(multiConfig.getRepetitions());
                                        config.setRepeatInsideBenchmark(multiConfig.getRepeatInsideBenchmark());
                                        config.setIgnoreCase(false);

                                        TestResult result = runBenchmark(config);
                                        result.setId("T" + counter.getAndIncrement());
                                        results.add(result);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    public TestResult runBenchmark(TestConfig config) {
        String text = generateText(config);
        String pattern = generatePattern(config);

        // Warmup JVM - stabilizacja JIT (20 razy)
        for (int i = 0; i < 20; i++) {
            algorithm.search(text, pattern, false, config.isIgnoreCase());
        }

        List<Long> durations = new ArrayList<>();
        BruteForceAlgorithm.Result lastResult = null;
        int repeats = Math.max(1, config.getRepeatInsideBenchmark());

        for (int i = 0; i < config.getRepetitions(); i++) {
            long start = System.nanoTime();
            for (int j = 0; j < repeats; j++) {
                lastResult = algorithm.search(text, pattern, false, config.isIgnoreCase());
            }
            long end = System.nanoTime();
            durations.add((end - start) / repeats);
        }

        // Odrzucanie skrajnych wyników (jeśli mamy wystarczająco dużo próbek)
        if (durations.size() >= 3) {
            Collections.sort(durations);
            durations.remove(0); // usuń min
            durations.remove(durations.size() - 1); // usuń max
        }

        // Liczba porównań jest stała dla tych samych danych w Brute Force
        long comparisons = (lastResult != null) ? lastResult.getComparisons() : 0;

        TestResult result = calculateStats(config, durations, comparisons, lastResult);
        result.setText(text);
        result.setPattern(pattern);

        // Dodanie opisów edukacyjnych
        fillEducationalData(result, config);
        
        // Jeśli tekst jest krótki, nagrywamy kroki dla wizualizacji (tylko jeśli potrzebne)
        if (text.length() <= 200 && config.getDataType().equalsIgnoreCase("CUSTOM")) {
            BruteForceAlgorithm.Result visualResult = algorithm.search(text, pattern, true, config.isIgnoreCase());
            result.setVisualSteps(visualResult.getSteps());
        }
        
        return result;
    }

    private void fillEducationalData(TestResult result, TestConfig config) {
        String type = config.getDataType().toUpperCase();
        int sigma = config.getAlphabetSize();
        String pos = config.getPatternPosition();

        switch (type) {
            case "NATURAL":
                result.setDataType("Tekst naturalny");
                result.setRepeatability("niska");
                result.setDataDescription("<b>Tekst naturalny:</b> Dane przypominające język naturalny (alfabet łaciński).");
                result.setAlgorithmImpact("Występują tu częste powtórzenia popularnych liter i krótkich prefiksów, co może zwiększyć liczbę porównań.");
                result.setDifficultyExplanation("average-case, mało częściowych dopasowań");
                break;
            case "UNIFORM":
                result.setDataType("Rozkład jednostajny");
                result.setRepeatability(sigma > 10 ? "niska" : "średnia");
                result.setDataDescription("<b>Rozkład jednostajny:</b> Każdy znak z alfabetu o rozmiarze σ=" + sigma + " ma równe prawdopodobieństwo.");
                result.setAlgorithmImpact("Przy większym σ (alfabet) algorytm szybciej wykrywa rozbieżności. Mniejsze σ zwiększa liczbę porównań.");
                result.setDifficultyExplanation(sigma > 10 ? "szybkie mismatch, zachowanie bliskie O(n)" : "więcej częściowych dopasowań");
                break;
            case "PATHOLOGICAL":
                result.setDataType("Dane patologiczne");
                result.setRepeatability("bardzo wysoka");
                result.setDataDescription("<b>Dane patologiczne:</b> Konstrukcja 'a...ab' wymuszająca maksymalną liczbę porównań.");
                result.setAlgorithmImpact("Niezależnie od innych parametrów, ten typ danych dąży do złożoności O(n * m).");
                result.setDifficultyExplanation(config.getTextLength() >= 10000 ? "degradacja wydajności, O(nm)" : "worst-case, mismatch na końcu wzorca");
                break;
            case "PERIODIC":
                result.setDataType("Dane periodyczne");
                result.setRepeatability("wysoka");
                result.setDataDescription("<b>Dane periodyczne:</b> Cykliczne powtarzanie tych samych sekwencji znaków.");
                result.setAlgorithmImpact("Regularność struktur powoduje powtarzalne wzorce pracy algorytmu na różnych przesunięciach.");
                result.setDifficultyExplanation("dużo częściowych dopasowań");
                break;
            case "DNA":
                result.setDataType("DNA / mały alfabet");
                result.setRepeatability("średnia");
                result.setDataDescription("<b>Dane DNA:</b> Alfabet 4-znakowy {A, C, G, T}.");
                result.setAlgorithmImpact("Bardzo mały alfabet (σ=4) wymusza częste porównywanie wielu znaków przed wykryciem błędu.");
                result.setDifficultyExplanation("większa liczba kolizji i partial match");
                break;
            case "CUSTOM":
                result.setDataType("Dane użytkownika");
                result.setRepeatability("zmienna");
                result.setDataDescription("<b>Dane użytkownika:</b> Tekst i wzorzec wprowadzone ręcznie.");
                result.setAlgorithmImpact("Wydajność zależy od charakterystyki wprowadzonych danych.");
                result.setDifficultyExplanation("zależy od użytkownika");
                break;
        }

        // Metoda generacji
        StringBuilder gen = new StringBuilder();
        gen.append("<b>Rozkład:</b> ").append(result.getDataType()).append(".<br>");
        gen.append("<b>Mechanizm RNG:</b> Deterministyczny Random(seed=").append(config.getSeed()).append(").<br>");
        gen.append("<b>Pozycja wzorca:</b> ").append(translatePosition(pos));
        if (pos.equals("MULTIPLE")) {
            gen.append(" (").append(config.getMatchCount()).append(" powtórzeń)");
        }
        gen.append(".<br>");
        if (type.equals("UNIFORM")) {
            gen.append("<b>Alfabet:</b> σ=").append(sigma).append(" (a-").append((char)('a' + sigma - 1)).append(").");
        } else if (type.equals("DNA")) {
            gen.append("<b>Alfabet:</b> DNA {A, C, G, T}.");
        } else if (type.equals("PATHOLOGICAL")) {
            gen.append("<b>Struktura:</b> Tekst samych 'a', wzorzec 'a...ab'.");
        } else if (type.equals("CUSTOM")) {
            gen.append("<b>Tryb:</b> Ręczne wprowadzanie danych.");
            if (config.isIgnoreCase()) {
                gen.append("<br><span class='badge bg-info text-white'>Ignorowanie wielkości liter: WŁĄCZONE</span>");
            }
        }
        result.setGenerationMethod(gen.toString());

        // Końcowe podsumowanie
        StringBuilder summary = new StringBuilder();
        summary.append("Wyszukiwanie zakończone. ");
        if (result.getOccurrences() > 0) {
            summary.append("Znaleziono ").append(result.getOccurrences()).append(" wystąpień wzorca. ");
        } else {
            summary.append("Nie znaleziono wzorca (prawdopodobnie został nadpisany przez strukturę danych). ");
        }
        summary.append("Średnia liczba porównań na jedno przesunięcie: ")
                .append(String.format("%.2f", (double) result.getComparisons() / Math.max(1, result.getShifts())))
                .append(". ");
        
        if (type.equals("PATHOLOGICAL")) {
            summary.append("Wynik potwierdza teoretyczną złożoność najgorszego przypadku O(n*m).");
        } else {
            summary.append("Wynik jest bliski złożoności średniej.");
        }
        result.setSearchSummary(summary.toString());
    }

    private String translatePosition(String pos) {
        switch (pos.toUpperCase()) {
            case "START": return "Początek";
            case "MIDDLE": return "Środek";
            case "END": return "Koniec";
            case "RANDOM": return "Losowo";
            case "MULTIPLE": return "Wiele wystąpień";
            default: return pos;
        }
    }

    private TestResult calculateStats(TestConfig config, List<Long> durations, long comparisons, BruteForceAlgorithm.Result lastResult) {
        TestResult result = new TestResult();
        result.setDataType(config.getDataType());
        result.setTextLength(config.getTextLength());
        result.setPatternLength(config.getPatternLength());
        result.setOccurrences(lastResult.getOccurrences());
        result.setPositions(lastResult.getPositions());
        result.setComparisons(comparisons);
        result.setCn((double) result.getComparisons() / config.getTextLength());
        result.setAlphabetSize(config.getAlphabetSize());
        result.setSeed(config.getSeed());
        result.setPatternPosition(translatePosition(config.getPatternPosition()));

        long min = durations.stream().min(Long::compare).orElse(0L);
        long max = durations.stream().max(Long::compare).orElse(0L);
        double avg = durations.stream().mapToLong(Long::longValue).average().orElse(0.0);

        double variance = durations.stream()
                .mapToDouble(d -> Math.pow(d - avg, 2))
                .average().orElse(0.0);
        double stdDev = Math.sqrt(variance);

        result.setMinDurationNs(min);
        result.setMaxDurationNs(max);
        result.setAvgDurationNs(avg);
        result.setStdDevNs(stdDev);
        result.setDurationNs((long) avg);
        result.setOpsPerNs(avg > 0 ? (double) result.getComparisons() / avg : 0);

        // Nowe metryki
        long shifts = Math.max(0, config.getTextLength() - config.getPatternLength() + 1);
        result.setShifts(shifts);
        result.setMismatches(shifts - lastResult.getOccurrences());
        result.setPartialMatches(result.getComparisons() - shifts);
        
        // Throughput: znaki na milisekundę (avg jest w ns)
        double durationMs = avg / 1_000_000.0;
        result.setThroughput(durationMs > 0 ? (double) config.getTextLength() / durationMs : 0);
        
        // Klasyfikacja scenariusza i złożoność
        if (config.getDataType().equalsIgnoreCase("PATHOLOGICAL")) {
            result.setScenarioCase("Worst-case");
            result.setTheoreticalComplexity("O(n \u00D7 m)");
        } else if (result.getCn() < 1.1) {
            result.setScenarioCase("Best-case");
            result.setTheoreticalComplexity("O(n)");
        } else {
            result.setScenarioCase("Average-case");
            result.setTheoreticalComplexity("~O(n)");
        }

        return result;
    }

    private String generateText(TestConfig config) {
        if (config.getDataType().equalsIgnoreCase("CUSTOM") && config.getCustomText() != null && !config.getCustomText().isEmpty()) {
            return config.getCustomText();
        }
        int n = config.getTextLength();
        Random random = new Random(config.getSeed());
        StringBuilder sb = new StringBuilder(n);
        String pattern = generatePattern(config);
        int m = pattern.length();
        int sigma = Math.max(1, Math.min(26, config.getAlphabetSize()));

        // 1. Generowanie tekstu bazowego
        switch (config.getDataType().toUpperCase()) {
            case "NATURAL":
                String lorem = "lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua ut enim ad minim veniam quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat ";
                while (sb.length() < n) sb.append(lorem);
                sb.setLength(n);
                break;
            case "UNIFORM":
                for (int i = 0; i < n; i++) sb.append((char) ('a' + random.nextInt(sigma)));
                break;
            case "PATHOLOGICAL":
                for (int i = 0; i < n; i++) sb.append('a');
                break;
            case "PERIODIC":
                String periodText = "abc";
                for (int i = 0; i < n; i++) sb.append(periodText.charAt(i % periodText.length()));
                break;
            case "DNA":
                char[] dnaChars = {'A', 'C', 'G', 'T'};
                for (int i = 0; i < n; i++) sb.append(dnaChars[random.nextInt(4)]);
                break;
            default:
                for (int i = 0; i < n; i++) sb.append((char) ('a' + random.nextInt(sigma)));
        }

        // 2. Wstrzykiwanie wzorca zgodnie z konfiguracją
        if (n >= m) {
            String pos = config.getPatternPosition().toUpperCase();
            switch (pos) {
                case "START":
                    sb.replace(0, m, pattern);
                    break;
                case "MIDDLE":
                    sb.replace(n / 2 - m / 2, n / 2 - m / 2 + m, pattern);
                    break;
                case "END":
                    sb.replace(n - m, n, pattern);
                    break;
                case "MULTIPLE":
                    int count = Math.max(1, config.getMatchCount());
                    for (int i = 0; i < count; i++) {
                        int interval = n / (count + 1);
                        int targetIdx = (i + 1) * interval;
                        if (targetIdx + m <= n) {
                            sb.replace(targetIdx, targetIdx + m, pattern);
                        }
                    }
                    break;
            }
        }

        return sb.toString();
    }

    private String generatePattern(TestConfig config) {
        if (config.getDataType().equalsIgnoreCase("CUSTOM") && config.getCustomPattern() != null && !config.getCustomPattern().isEmpty()) {
            return config.getCustomPattern();
        }
        int m = config.getPatternLength();
        Random random = new Random(config.getSeed() + 1);
        int sigma = Math.max(1, Math.min(26, config.getAlphabetSize()));

        switch (config.getDataType().toUpperCase()) {
            case "NATURAL":
                String lorem = "loremipsumdolorsitametconsecteturadipiscingelitseddoeiusmodtemporincididuntutlaboreetdoloremagnaaliqua";
                StringBuilder sbn = new StringBuilder();
                for (int i = 0; i < m; i++) sbn.append(lorem.charAt(random.nextInt(lorem.length())));
                return sbn.toString();
            case "UNIFORM":
                StringBuilder sbu = new StringBuilder(m);
                for (int i = 0; i < m; i++) {
                    sbu.append((char) ('a' + random.nextInt(sigma)));
                }
                return sbu.toString();
            case "PATHOLOGICAL":
                return "a".repeat(m - 1) + "b";
            case "PERIODIC":
                String p = "abcab";
                StringBuilder sbp = new StringBuilder();
                for (int i = 0; i < m; i++) sbp.append(p.charAt(i % p.length()));
                return sbp.toString();
            case "DNA":
                char[] dnaChars = {'A', 'C', 'G', 'T'};
                StringBuilder sbd = new StringBuilder(m);
                for (int i = 0; i < m; i++) {
                    sbd.append(dnaChars[random.nextInt(4)]);
                }
                return sbd.toString();
            default:
                StringBuilder sbr = new StringBuilder(m);
                for (int i = 0; i < m; i++) {
                    sbr.append((char) ('a' + random.nextInt(sigma)));
                }
                return sbr.toString();
        }
    }
}
