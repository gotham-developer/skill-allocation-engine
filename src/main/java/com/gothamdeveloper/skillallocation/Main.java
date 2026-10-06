package com.gothamdeveloper.skillallocation;

import com.gothamdeveloper.skillallocation.application.SkillAllocationService;
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

        SkillAllocationService service = new SkillAllocationService(managerRepository, projectRepository,
                                                                    traineeRepository);
        ConsoleReader reader = new ConsoleReader();
        ConsoleWriter writer = new ConsoleWriter();

        ApplicationUI applicationUI = new ApplicationUI(service, reader, writer);

        applicationUI.start();

        reader.close();
    }

}