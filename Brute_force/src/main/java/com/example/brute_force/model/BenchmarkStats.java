package com.example.brute_force.model;

import java.time.LocalDateTime;

public class BenchmarkStats {
    private int cpuCores;
    private String javaVersion;
    private String osName;
    private long totalMemory;
    private LocalDateTime timestamp;

    public BenchmarkStats() {
        this.cpuCores = Runtime.getRuntime().availableProcessors();
        this.javaVersion = System.getProperty("java.version");
        this.osName = System.getProperty("os.name");
        this.totalMemory = Runtime.getRuntime().totalMemory();
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public int getCpuCores() { return cpuCores; }
    public String getJavaVersion() { return javaVersion; }
    public String getOsName() { return osName; }
    public long getTotalMemory() { return totalMemory; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
