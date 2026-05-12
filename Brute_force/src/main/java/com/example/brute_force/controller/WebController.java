package com.example.brute_force.controller;

import com.example.brute_force.model.*;
import com.example.brute_force.service.BenchmarkService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
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
        model.addAttribute("env", getEnvironmentInfo());
        return "index";
    }

    @PostMapping("/run")
    public String runBenchmark(@ModelAttribute("config") TestConfig config, Model model) {
        TestResult result = benchmarkService.runBenchmark(config);
        model.addAttribute("result", result);
        model.addAttribute("stats", new BenchmarkStats());
        model.addAttribute("env", getEnvironmentInfo());
        return "index";
    }

    @GetMapping("/experiments")
    public String experiments(@ModelAttribute("experimentResults") List<TestResult> results, Model model) {
        model.addAttribute("env", getEnvironmentInfo());
        return "experiments";
    }

    private Map<String, String> getEnvironmentInfo() {
        Map<String, String> env = new HashMap<>();
        
        // System i Architektura
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVer = System.getProperty("os.version");
        env.put("os", osName + " " + osVer + " (" + osArch + ")");
        
        // Środowisko uruchomieniowe
        env.put("java", System.getProperty("java.vendor") + " JDK " + System.getProperty("java.version"));
        
        // Procesor
        String cpuName = getCpuName(osName, osArch);
        env.put("cpu", cpuName);
        env.put("cores", String.valueOf(Runtime.getRuntime().availableProcessors()));
        
        // Karta Graficzna (GPU)
        String gpuName = getGpuName(osName);
        env.put("gpu", gpuName);
        
        // Pamięć
        try {
            java.lang.management.OperatingSystemMXBean osBean = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunBean = (com.sun.management.OperatingSystemMXBean) osBean;
                long totalPhysical = sunBean.getTotalPhysicalMemorySize() / (1024 * 1024);
                env.put("ramPhysical", totalPhysical + " MB");
            } else {
                env.put("ramPhysical", "N/A");
            }
        } catch (Throwable e) {
            env.put("ramPhysical", "N/A");
        }
        
        env.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return env;
    }

    private String getCpuName(String osName, String osArch) {
        String cpuName = System.getenv("PROCESSOR_IDENTIFIER");
        if (osName.toLowerCase().contains("win")) {
            try {
                Process process = Runtime.getRuntime().exec("wmic cpu get name");
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
                reader.readLine(); // skip header
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        cpuName = line.trim();
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }
        return cpuName != null ? cpuName : "Generic " + osArch;
    }

    private String getGpuName(String osName) {
        if (osName.toLowerCase().contains("win")) {
            try {
                Process process = Runtime.getRuntime().exec("wmic path win32_VideoController get name");
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
                reader.readLine(); // skip header
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        return line.trim();
                    }
                }
            } catch (Exception ignored) {}
        }
        return "N/A";
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

    @GetMapping("/benchmark-compare")
    public String benchmarkCompare(Model model) {
        model.addAttribute("env", getEnvironmentInfo());
        return "benchmark-compare";
    }

    @GetMapping("/data/external/{filename}")
    @ResponseBody
    public org.springframework.core.io.Resource getExternalData(@PathVariable String filename) {
        return new org.springframework.core.io.ClassPathResource(filename + ".json");
    }

    @GetMapping("/export/full-json")
    public ResponseEntity<MachineBenchmarkResult> exportFullJson(@ModelAttribute("experimentResults") List<TestResult> results) {
        MachineBenchmarkResult fullResult = new MachineBenchmarkResult("Użytkownik", getEnvironmentInfo(), results);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=benchmark_results.json")
                .body(fullResult);
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
