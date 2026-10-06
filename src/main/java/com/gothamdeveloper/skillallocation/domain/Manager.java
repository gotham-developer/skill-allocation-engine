package com.gothamdeveloper.skillallocation.domain;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class Manager {

    private final long         id;
    private final String       name;
    private final Set<Project> projects = new LinkedHashSet<>();

    public Manager(long id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException("Manager ID must be greater than zero");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Manager name must not be blank");
        }

        this.id = id;
        this.name = name.trim();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Project> getProjects() {
        return Collections.unmodifiableSet(projects);
    }

    public void addProject(Project project) {
        Objects.requireNonNull(project, "Project must not be null");

        if (project.getManager() != this) {
            throw new IllegalArgumentException("Project is not assigned to this manager");
        }

        projects.add(project);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Manager manager)) {
            return false;
        }
        return id == manager.id && Objects.equals(name, manager.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Manager{"
               + "id=" + id
               + ", name='" + name + '\''
               + ", projectCount=" + projects.size()
               + '}';
    }

}