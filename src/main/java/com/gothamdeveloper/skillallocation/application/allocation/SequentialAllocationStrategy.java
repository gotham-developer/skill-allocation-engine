package com.gothamdeveloper.skillallocation.application.allocation;

import java.util.List;

import com.gothamdeveloper.skillallocation.application.AllocationResult;
import com.gothamdeveloper.skillallocation.domain.Project;
import com.gothamdeveloper.skillallocation.domain.Trainee;

public final class SequentialAllocationStrategy implements AllocationStrategy {

    @Override
    public AllocationResult allocate(List<Project> projects, List<Trainee> trainees) {
        int traineesAllocated = 0;
        int unfilledOpenings  = 0;

        for (Project project : projects) {
            for (Trainee trainee : trainees) {
                if (!project.hasAvailableOpening()) {
                    break;
                }

                if (trainee.isAllocated()) {
                    continue;
                }

                if (!project.requiresSkill(trainee.getSkill())) {
                    continue;
                }

                project.allocate(trainee);
                traineesAllocated++;
            }

            unfilledOpenings += project.getOpenings();
        }

        return new AllocationResult(projects.size(), traineesAllocated, unfilledOpenings);
    }

    @Override
    public String getName() {
        return "Sequential";
    }

}