package com.gothamdeveloper.skillallocation.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gothamdeveloper.skillallocation.domain.Manager;
import com.gothamdeveloper.skillallocation.domain.Project;
import com.gothamdeveloper.skillallocation.domain.Skill;
import com.gothamdeveloper.skillallocation.domain.Trainee;

public final class BenchmarkDataFactory {

    private static final long RANDOM_SEED = 42L;

    private BenchmarkDataFactory() {
    }

    public static BenchmarkData create(int projectCount, int traineeCount) {
        if (projectCount < 0) {
            throw new IllegalArgumentException("Project count must not be negative.");
        }

        if (traineeCount < 0) {
            throw new IllegalArgumentException("Trainee count must not be negative.");
        }

        Manager manager = new Manager(1, "Benchmark Manager");

        Random random = new Random(RANDOM_SEED);

        Skill[] skills = Skill.values();

        List<Trainee> trainees = new ArrayList<>(traineeCount);

        for (int i = 1; i <= traineeCount; i++) {
            Skill skill = skills[random.nextInt(skills.length)];
            trainees.add(new Trainee(i, "Trainee-" + i, skill));
        }

        List<Project> projects = new ArrayList<>(projectCount);

        for (int i = 1; i <= projectCount; i++) {
            Skill requiredSkill = skills[random.nextInt(skills.length)];

            int openings = 1 + random.nextInt(10);

            Project project = new Project(i, "Project-" + i, 1 + random.nextInt(12), requiredSkill, openings, manager);

            manager.addProject(project);
            projects.add(project);
        }

        return new BenchmarkData(projects, trainees);
    }

}