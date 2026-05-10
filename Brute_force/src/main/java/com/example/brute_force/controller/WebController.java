package com.example.brute_force.controller;

import com.example.brute_force.model.BenchmarkStats;
import com.example.brute_force.model.MultiTestConfig;
import com.example.brute_force.model.TestConfig;
import com.example.brute_force.model.TestResult;
import com.example.brute_force.service.BenchmarkService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@SessionAttributes({"result", "experimentResults", "multiConfig"})
public class WebController {

    private final BenchmarkService benchmarkService;

    public WebController(BenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @ModelAttribute("result")
    public TestResult result() {
        return null;
    }

    @ModelAttribute("experimentResults")
    public List<TestResult> experimentResults() {
        return new ArrayList<>();
    }

    @ModelAttribute("config")
    public TestConfig config() {
        return new TestConfig();
    }

    @ModelAttribute("multiConfig")
    public MultiTestConfig multiConfig() {
        return new MultiTestConfig();
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("stats", new BenchmarkStats());
        return "index";
    }

    @PostMapping("/run")
    public String runBenchmark(@ModelAttribute("config") TestConfig config, Model model) {
        TestResult result = benchmarkService.runBenchmark(config);
        model.addAttribute("result", result);
        model.addAttribute("stats", new BenchmarkStats());
        return "index";
    }

    @GetMapping("/experiments")
    public String experiments(@ModelAttribute("experimentResults") List<TestResult> results, Model model) {
        return "experiments";
    }

    @PostMapping("/experiments/run")
    public String runExperiment(@ModelAttribute("multiConfig") MultiTestConfig multiConfig,
                              @ModelAttribute("experimentResults") List<TestResult> results,
                              Model model) {
        List<TestResult> newResults = benchmarkService.runMultiBenchmark(multiConfig);
        results.addAll(newResults);
        return "redirect:/experiments";
    }

    @GetMapping("/experiments/clear")
    public String clearExperiments(@ModelAttribute("experimentResults") List<TestResult> results) {
        results.clear();
        return "redirect:/experiments";
    }

    @GetMapping("/clear")
    public String clearResults(Model model) {
        model.addAttribute("result", null);
        return "redirect:/";
    }

    @GetMapping("/analysis")
    public String analysis() {
        return "analysis";
    }

    @GetMapping("/export/json")
    public ResponseEntity<byte[]> exportJson(@ModelAttribute("result") TestResult r) {
        if (r == null) return ResponseEntity.noContent().build();
        
        String json = String.format("{\"dataType\":\"%s\",\"textLength\":%d,\"patternLength\":%d,\"occurrences\":%d,\"comparisons\":%d,\"avgDurationNs\":%.2f}",
                r.getDataType(), r.getTextLength(), r.getPatternLength(), r.getOccurrences(), r.getComparisons(), r.getAvgDurationNs());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=result.json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json.getBytes());
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(@ModelAttribute("result") TestResult r) {
        if (r == null) return ResponseEntity.noContent().build();
        
        StringBuilder csv = new StringBuilder("Type,TextLength,PatternLength,Occurrences,Comparisons,AvgDurationNs,StdDevNs,OpsPerNs\n");
        csv.append(String.format("%s,%d,%d,%d,%d,%.2f,%.2f,%.4f\n",
                r.getDataType(), r.getTextLength(), r.getPatternLength(), r.getOccurrences(),
                r.getComparisons(), r.getAvgDurationNs(), r.getStdDevNs(), r.getOpsPerNs()));
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=result.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv.toString().getBytes());
    }
}
