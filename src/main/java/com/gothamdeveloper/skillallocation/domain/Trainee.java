package com.gothamdeveloper.skillallocation.domain;

import java.util.Objects;

public final class Trainee {

    private final long   id;
    private final String name;
    private final Skill  skill;

    private Project project;

    public Trainee(long id, String name, Skill skill) {
        if (id <= 0) {
            throw new IllegalArgumentException("Trainee ID must be greater than zero");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Trainee name must not be blank");
        }

        this.id = id;
        this.name = name.trim();
        this.skill = Objects.requireNonNull(skill, "Trainee skill must not be null.");
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Skill getSkill() {
        return skill;
    }

    public Project getProject() {
        return project;
    }

    public boolean isAllocated() {
        return project != null;
    }

    public boolean hasSkill(Skill requiredSkill) {
        Objects.requireNonNull(requiredSkill, "Required skill must not be null");

        return skill == requiredSkill;
    }

    void assignTo(Project project) {
        Objects.requireNonNull(project, "Project must not be null");

        if (isAllocated()) {
            throw new IllegalStateException("Trainee is already allocated to a project");
        }

        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trainee trainee)) {
            return false;
        }
        return id == trainee.id && Objects.equals(name, trainee.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Trainee{" +
               "id=" +
               id +
               ", name='" +
               name +
               '\'' +
               ", skill='" +
               skill +
               '\'' +
               ", allocated=" +
               isAllocated() +
               '}';
    }

}