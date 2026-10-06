package com.gothamdeveloper.skillallocation.repository;

import java.util.List;
import java.util.Optional;

import com.gothamdeveloper.skillallocation.domain.Manager;

public interface ManagerRepository {

    void save(Manager manager);

    Optional<Manager> findById(long id);

    List<Manager> findAll();

    boolean existsById(long id);

}