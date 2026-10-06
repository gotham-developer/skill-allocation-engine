package com.gothamdeveloper.skillallocation.repository.memory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gothamdeveloper.skillallocation.domain.Manager;
import com.gothamdeveloper.skillallocation.repository.ManagerRepository;

public class InMemoryManagerRepository implements ManagerRepository {

    private final Map<Long, Manager> managers = new LinkedHashMap<>();

    @Override
    public void save(Manager manager) {
        managers.put(manager.getId(), manager);
    }

    @Override
    public Optional<Manager> findById(long id) {
        return Optional.ofNullable(managers.get(id));
    }

    @Override
    public List<Manager> findAll() {
        return new ArrayList<>(managers.values());
    }

    @Override
    public boolean existsById(long id) {
        return managers.containsKey(id);
    }

}