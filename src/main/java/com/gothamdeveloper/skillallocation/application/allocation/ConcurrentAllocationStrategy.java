package com.gothamdeveloper.skillallocation.application.allocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gothamdeveloper.skillallocation.application.AllocationResult;
import com.gothamdeveloper.skillallocation.domain.Project;
import com.gothamdeveloper.skillallocation.domain.Trainee;

public final class ConcurrentAllocationStrategy implements AllocationStrategy {

    @Override
    public AllocationResult allocate(List<Project> projects, List<Trainee> trainees) {
        if (projects.isEmpty()) {
            return new AllocationResult(0, 0, 0);
        }

        int threadCount = Math.min(projects.size(), Runtime.getRuntime().availableProcessors());

        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            List<Callable<Integer>> tasks = createTasks(projects, trainees);

            int traineesAllocated = 0;

            for (var future : executor.invokeAll(tasks)) {
                traineesAllocated += future.get();
            }

            int unfilledOpenings = projects.stream().mapToInt(Project::getOpenings).sum();

            return new AllocationResult(projects.size(), traineesAllocated, unfilledOpenings);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Concurrent allocation was interrupted.", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Concurrent allocation failed.", e.getCause());
        }
    }

    private List<Callable<Integer>> createTasks(List<Project> projects, List<Trainee> trainees) {
        List<Callable<Integer>> tasks = new ArrayList<>(projects.size());

        for (Project project : projects) {
            tasks.add(() -> allocateProject(project, trainees));
        }

        return tasks;
    }

    private int allocateProject(Project project, List<Trainee> trainees) {
        int allocated = 0;

        /*
         * The project lock protects its openings.
         *
         * The trainee lock protects the check-and-assign operation.
         *
         * Lock ordering is always:
         *
         *     Project -> Trainee
         *
         * We never acquire Trainee -> Project,
         * which avoids lock-order deadlocks.
         */
        synchronized (project) {
            for (Trainee trainee : trainees) {
                if (!project.hasAvailableOpening()) {
                    break;
                }

                synchronized (trainee) {
                    if (trainee.isAllocated()) {
                        continue;
                    }

                    if (!project.requiresSkill(trainee.getSkill())) {
                        continue;
                    }

                    project.allocate(trainee);
                    allocated++;
                }
            }
        }

        return allocated;
    }

    @Override
    public String getName() {
        return "Concurrent";
    }

}