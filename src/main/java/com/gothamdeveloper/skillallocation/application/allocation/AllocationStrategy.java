package com.gothamdeveloper.skillallocation.application.allocation;

import java.util.List;

import com.gothamdeveloper.skillallocation.application.AllocationResult;
import com.gothamdeveloper.skillallocation.domain.Project;
import com.gothamdeveloper.skillallocation.domain.Trainee;

public interface AllocationStrategy {

    AllocationResult allocate(List<Project> projects, List<Trainee> trainees);

    String getName();

}