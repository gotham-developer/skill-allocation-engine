package com.gothamdeveloper.skillallocation.repository;

import java.util.List;
import java.util.Optional;

import com.gothamdeveloper.skillallocation.domain.Project;

public interface ProjectRepository {

    void save(Project project);

    Optional<Project> findById(long id);

    List<Project> findAll();

    List<Project> findByManagerId(long managerId);

    boolean existsById(long id);

}