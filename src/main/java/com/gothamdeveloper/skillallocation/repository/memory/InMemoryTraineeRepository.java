package com.gothamdeveloper.skillallocation.repository.memory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gothamdeveloper.skillallocation.domain.Trainee;
import com.gothamdeveloper.skillallocation.repository.TraineeRepository;

public class InMemoryTraineeRepository implements TraineeRepository {

    private final Map<Long, Trainee> trainees = new LinkedHashMap<>();

    @Override
    public void save(Trainee trainee) {
        trainees.put(trainee.getId(), trainee);
    }

    @Override
    public Optional<Trainee> findById(long id) {
        return Optional.ofNullable(trainees.get(id));
    }

    @Override
    public List<Trainee> findAll() {
        return new ArrayList<>(trainees.values());
    }

    @Override
    public List<Trainee> findUnallocated() {
        return trainees.values().stream().filter(trainee -> !trainee.isAllocated()).toList();
    }

    @Override
    public boolean existsById(long id) {
        return trainees.containsKey(id);
    }

}