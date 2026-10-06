package com.gothamdeveloper.skillallocation.benchmark;

import com.gothamdeveloper.skillallocation.application.AllocationResult;
import com.gothamdeveloper.skillallocation.application.allocation.AllocationStrategy;

public final class AllocationBenchmark {

    private static final int WARMUP_RUNS      = 2;
    private static final int MEASUREMENT_RUNS = 3;

    private final AllocationStrategy sequentialStrategy;
    private final AllocationStrategy concurrentStrategy;

    public AllocationBenchmark(AllocationStrategy sequentialStrategy, AllocationStrategy concurrentStrategy) {
        this.sequentialStrategy = sequentialStrategy;
        this.concurrentStrategy = concurrentStrategy;
    }

    public BenchmarkResult[] run(int projectCount, int traineeCount) {
        validateCounts(projectCount, traineeCount);

        warmup(projectCount, traineeCount);

        BenchmarkResult sequential = measure(sequentialStrategy, projectCount, traineeCount);
        BenchmarkResult concurrent = measure(concurrentStrategy, projectCount, traineeCount);

        return new BenchmarkResult[]{ sequential, concurrent };
    }

    private void validateCounts(int projectCount, int traineeCount) {
        if (projectCount <= 0) {
            throw new IllegalArgumentException("Project count must be greater than zero.");
        }

        if (traineeCount <= 0) {
            throw new IllegalArgumentException("Trainee count must be greater than zero.");
        }
    }

    private void warmup(int projectCount, int traineeCount) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            runOnce(sequentialStrategy, projectCount, traineeCount);
            runOnce(concurrentStrategy, projectCount, traineeCount);
        }
    }

    private AllocationResult runOnce(AllocationStrategy strategy, int projectCount, int traineeCount) {
        BenchmarkData data = BenchmarkDataFactory.create(projectCount, traineeCount);

        return strategy.allocate(data.projects(), data.trainees());
    }

    private BenchmarkResult measure(AllocationStrategy strategy, int projectCount, int traineeCount) {
        long             totalNanos = 0;
        AllocationResult lastResult = null;

        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            BenchmarkData data = BenchmarkDataFactory.create(projectCount, traineeCount);

            long start = System.nanoTime();

            lastResult = strategy.allocate(data.projects(), data.trainees());

            totalNanos += System.nanoTime() - start;
        }

        long averageNanos = totalNanos / MEASUREMENT_RUNS;

        return new BenchmarkResult(strategy.getName(),
                                   projectCount,
                                   traineeCount,
                                   lastResult.traineesAllocated(),
                                   lastResult.unfilledOpenings(),
                                   averageNanos);
    }

}