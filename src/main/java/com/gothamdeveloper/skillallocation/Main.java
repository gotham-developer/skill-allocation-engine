package com.gothamdeveloper.skillallocation;

import com.gothamdeveloper.skillallocation.application.SkillAllocationService;
import com.gothamdeveloper.skillallocation.application.allocation.AllocationStrategy;
import com.gothamdeveloper.skillallocation.application.allocation.ConcurrentAllocationStrategy;
import com.gothamdeveloper.skillallocation.application.allocation.SequentialAllocationStrategy;
import com.gothamdeveloper.skillallocation.benchmark.AllocationBenchmark;
import com.gothamdeveloper.skillallocation.repository.ManagerRepository;
import com.gothamdeveloper.skillallocation.repository.ProjectRepository;
import com.gothamdeveloper.skillallocation.repository.TraineeRepository;
import com.gothamdeveloper.skillallocation.repository.memory.InMemoryManagerRepository;
import com.gothamdeveloper.skillallocation.repository.memory.InMemoryProjectRepository;
import com.gothamdeveloper.skillallocation.repository.memory.InMemoryTraineeRepository;
import com.gothamdeveloper.skillallocation.ui.ApplicationUI;
import com.gothamdeveloper.skillallocation.ui.ConsoleReader;
import com.gothamdeveloper.skillallocation.ui.ConsoleWriter;

public final class Main {

    private Main() {
    }

    static void main() {
        ManagerRepository managerRepository = new InMemoryManagerRepository();
        ProjectRepository projectRepository = new InMemoryProjectRepository();
        TraineeRepository traineeRepository = new InMemoryTraineeRepository();

        AllocationStrategy sequentialStrategy = new SequentialAllocationStrategy();
        AllocationStrategy concurrentStrategy = new ConcurrentAllocationStrategy();

        SkillAllocationService service = new SkillAllocationService(managerRepository, projectRepository,
                                                                    traineeRepository);

        AllocationBenchmark benchmark = new AllocationBenchmark(sequentialStrategy, concurrentStrategy);

        ConsoleReader reader = new ConsoleReader();
        ConsoleWriter writer = new ConsoleWriter();

        ApplicationUI applicationUI = new ApplicationUI(service, reader, writer, sequentialStrategy, concurrentStrategy,
                                                        benchmark);

        applicationUI.start();

        reader.close();
    }

}