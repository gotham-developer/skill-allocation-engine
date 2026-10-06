package com.gothamdeveloper.skillallocation.benchmark;

import java.util.List;

import com.gothamdeveloper.skillallocation.domain.Project;
import com.gothamdeveloper.skillallocation.domain.Trainee;

public record BenchmarkData(List<Project> projects, List<Trainee> trainees) {

}