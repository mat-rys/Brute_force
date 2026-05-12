package com.example.brute_force.model;

import java.util.List;
import java.util.Map;

public class MachineBenchmarkResult {
    private String userName;
    private Map<String, String> environment;
    private List<TestResult> results;

    public MachineBenchmarkResult() {}

    public MachineBenchmarkResult(String userName, Map<String, String> environment, List<TestResult> results) {
        this.userName = userName;
        this.environment = environment;
        this.results = results;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Map<String, String> getEnvironment() { return environment; }
    public void setEnvironment(Map<String, String> environment) { this.environment = environment; }

    public List<TestResult> getResults() { return results; }
    public void setResults(List<TestResult> results) { this.results = results; }
}
