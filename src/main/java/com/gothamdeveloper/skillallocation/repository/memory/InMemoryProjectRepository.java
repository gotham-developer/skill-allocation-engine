package com.gothamdeveloper.skillallocation.repository.memory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gothamdeveloper.skillallocation.domain.Project;
import com.gothamdeveloper.skillallocation.repository.ProjectRepository;

public class InMemoryProjectRepository implements ProjectRepository {

    private final Map<Long, Project> projects = new LinkedHashMap<>();

    @Override
    public void save(Project project) {
        projects.put(project.getId(), project);
    }

    @Override
    public Optional<Project> findById(long id) {
        return Optional.ofNullable(projects.get(id));
    }

    @Override
    public List<Project> findAll() {
        return new ArrayList<>(projects.values());
    }

    @Override
    public List<Project> findByManagerId(long managerId) {
        return projects.values().stream().filter(project -> project.getManager().getId() == managerId).toList();
    }

    @Override
    public boolean existsById(long id) {
        return projects.containsKey(id);
    }

}