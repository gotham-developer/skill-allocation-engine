package com.gothamdeveloper.skillallocation.ui;

import com.gothamdeveloper.skillallocation.application.AllocationResult;
import com.gothamdeveloper.skillallocation.benchmark.BenchmarkResult;
import com.gothamdeveloper.skillallocation.domain.Manager;
import com.gothamdeveloper.skillallocation.domain.Project;
import com.gothamdeveloper.skillallocation.domain.Trainee;

public final class ConsoleWriter {

    public void printMenu() {
        System.out.println();
        System.out.println("======================================");
        System.out.println("       SKILL ALLOCATION");
        System.out.println("======================================");
        System.out.println("1. Add Manager");
        System.out.println("2. Add Trainee");
        System.out.println("3. Add Project");
        System.out.println("4. Allocate Sequentially");
        System.out.println("5. Allocate Concurrently");
        System.out.println("6. Display Manager Projects");
        System.out.println("7. Display Unallocated Trainees");
        System.out.println("8. Display All Managers");
        System.out.println("9. Display All Projects");
        System.out.println("10. Display All Trainees");
        System.out.println("11. Compare Allocation Performance");
        System.out.println("0. Exit");
        System.out.println("======================================");
    }

    public void printPrompt(String message) {
        System.out.println(message);
    }

    public void printSuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }

    public void printError(String message) {
        System.out.println("ERROR: " + message);
    }

    public void printAllocationResult(AllocationResult result) {
        System.out.println();
        System.out.println("Allocation completed.");
        System.out.println("Projects processed: " + result.projectsProcessed());
        System.out.println("Trainees allocated: " + result.traineesAllocated());
        System.out.println("Unfilled openings: " + result.unfilledOpenings());
    }

    public void printAllProjects(Iterable<Project> projects) {
        System.out.println();
        System.out.println("Projects:");

        boolean found = false;

        for (Project project : projects) {
            found = true;

            System.out.println(project.toString());
            //            System.out.printf("[%d] %s | Skill: %s | Openings: %d | Duration: %d%n", project.getId(), project.getName(),
            //                              project.getRequiredSkill().getDisplayName(), project.getOpenings(),
            //                              project.getDuration());
        }

        if (!found) {
            System.out.println("No projects found.");
        }
    }

    public void printAllTrainees(Iterable<Trainee> trainees) {
        System.out.println();
        System.out.println("Trainees:");

        boolean found = false;

        for (Trainee trainee : trainees) {
            found = true;

            System.out.println(trainee.toString());
            //            System.out.printf("[%d] %s | Skill: %s%n", trainee.getId(), trainee.getName(),
            //                              trainee.getSkill().getDisplayName());
        }

        if (!found) {
            System.out.println("No trainees found.");
        }
    }

    public void printAllManagers(Iterable<Manager> managers) {
        System.out.println();
        System.out.println("Managers:");

        boolean found = false;

        for (Manager manager : managers) {
            found = true;

            System.out.println(manager.toString());
            //            System.out.printf("[%d] %s | Projects: %d%n", manager.getId(), manager.getName(),
            //                              manager.getProjects().size());
        }

        if (!found) {
            System.out.println("No managers found.");
        }
    }

    public void printUnallocatedTrainees(Iterable<Trainee> trainees) {
        System.out.println();
        System.out.println("Unallocated Trainees:");

        boolean found = false;

        for (Trainee trainee : trainees) {
            found = true;

            System.out.println(trainee.toString());
            //            System.out.printf("[%d] %s | Skill: %s%n", trainee.getId(), trainee.getName(),
            //                              trainee.getSkill().getDisplayName());
        }

        if (!found) {
            System.out.println("No unallocated trainees found.");
        }
    }

    public void printBenchmarkResults(BenchmarkResult[] results) {
        System.out.println();
        System.out.println("=================================");
        System.out.println("     ALLOCATION BENCHMARK");
        System.out.println("=================================");

        for (BenchmarkResult result : results) {
            System.out.println();
            System.out.println("Strategy: " + result.strategy());
            System.out.println("Projects: " + result.projects());
            System.out.println("Trainees: " + result.trainees());
            System.out.println("Allocated: " + result.allocated());
            System.out.println("Unfilled openings: " + result.unfilledOpenings());
            System.out.printf("Average time: %.3f ms%n", result.elapsedMillis());
            System.out.printf("Allocation throughput: %.2f allocations/sec%n", result.allocationsPerSecond());
        }

        if (results.length == 2) {
            double sequentialTime = results[0].elapsedMillis();
            double concurrentTime = results[1].elapsedMillis();
            double speedup        = sequentialTime / concurrentTime;

            System.out.printf("%nConcurrent speedup: %.2fx%n", speedup);
        }
    }

    public void printGoodbye() {
        System.out.println();
        System.out.println("Goodbye.");
    }

}