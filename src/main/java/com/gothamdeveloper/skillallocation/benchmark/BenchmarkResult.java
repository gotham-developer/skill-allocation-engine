package com.gothamdeveloper.skillallocation.benchmark;

public record BenchmarkResult(String strategy,
                              int projects,
                              int trainees,
                              int allocated,
                              int unfilledOpenings,
                              long elapsedNanos) {

    public double elapsedMillis() {
        return elapsedNanos / 1_000_000.0;
    }

    public double allocationsPerSecond() {
        if (elapsedNanos == 0) {
            return 0;
        }

        return allocated / (elapsedNanos / 1_000_000_000.0);
    }

}