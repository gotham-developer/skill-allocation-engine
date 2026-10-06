package com.gothamdeveloper.skillallocation.domain;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class Project {

    private final long    id;
    private final String  name;
    private final int     duration;
    private final Skill   requiredSkill;
    private final Manager manager;

    private int openings;

    private final Set<Trainee> trainees = new LinkedHashSet<>();

    public Project(long id, String name, int duration, Skill requiredSkill, int openings, Manager manager) {
        if (id <= 0) {
            throw new IllegalArgumentException("Project ID must be greater than zero");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Project name must not be blank");
        }

        if (duration <= 0) {
            throw new IllegalArgumentException("Project duration must be greater than zero");
        }

        if (openings < 0) {
            throw new IllegalArgumentException("Project openings cannot be negative");
        }

        this.manager = Objects.requireNonNull(manager, "Project manager must not be null");

        this.id = id;
        this.name = name.trim();
        this.duration = duration;
        this.requiredSkill = Objects.requireNonNull(requiredSkill, "Required skill must not be null.");
        this.openings = openings;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public Skill getRequiredSkill() {
        return requiredSkill;
    }

    public int getOpenings() {
        return openings;
    }

    public Manager getManager() {
        return manager;
    }

    public Set<Trainee> getTrainees() {
        return Collections.unmodifiableSet(trainees);
    }

    public boolean hasAvailableOpening() {
        return openings > 0;
    }

    public boolean requiresSkill(Skill skill) {
        Objects.requireNonNull(skill, "Skill must not be null");

        return requiredSkill == skill;
    }

    public void allocate(Trainee trainee) {
        Objects.requireNonNull(trainee, "Trainee must not be null");

        if (!hasAvailableOpening()) {
            throw new IllegalStateException("Project has no available openings");
        }

        if (trainee.isAllocated()) {
            throw new IllegalStateException("Trainee is already allocated to a project");
        }

        if (!requiresSkill(trainee.getSkill())) {
            throw new IllegalArgumentException("Trainee skill does not match the project required skill");
        }

        trainee.assignTo(this);
        trainees.add(trainee);
        openings--;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Project project)) {
            return false;
        }
        return id == project.id && Objects.equals(name, project.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Project{" +
               "id=" +
               id +
               ", name='" +
               name +
               '\'' +
               ", duration=" +
               duration +
               ", requiredSkill='" +
               requiredSkill +
               '\'' +
               ", openings=" +
               openings +
               ", manager=" +
               manager.getName() +
               ", allocatedTrainees=" +
               trainees.size() +
               '}';
    }

}