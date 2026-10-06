package com.gothamdeveloper.skillallocation.repository;

import java.util.List;
import java.util.Optional;

import com.gothamdeveloper.skillallocation.domain.Trainee;

public interface TraineeRepository {

    void save(Trainee trainee);

    Optional<Trainee> findById(long id);

    List<Trainee> findAll();

    List<Trainee> findUnallocated();

    boolean existsById(long id);

}