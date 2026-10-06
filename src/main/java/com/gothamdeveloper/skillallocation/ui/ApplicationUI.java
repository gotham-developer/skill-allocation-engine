package com.gothamdeveloper.skillallocation.ui;

import com.gothamdeveloper.skillallocation.application.AllocationResult;
import com.gothamdeveloper.skillallocation.application.SkillAllocationService;
import com.gothamdeveloper.skillallocation.application.allocation.AllocationStrategy;
import com.gothamdeveloper.skillallocation.benchmark.AllocationBenchmark;
import com.gothamdeveloper.skillallocation.benchmark.BenchmarkResult;
import com.gothamdeveloper.skillallocation.domain.Skill;
import com.gothamdeveloper.skillallocation.exception.DuplicateEntityException;
import com.gothamdeveloper.skillallocation.exception.EntityNotFoundException;

public final class ApplicationUI {

    private static final int EXIT_OPTION = 0;

    private final SkillAllocationService service;
    private final ConsoleReader          reader;
    private final ConsoleWriter          writer;

    private final AllocationStrategy sequentialStrategy;
    private final AllocationStrategy concurrentStrategy;

    private final AllocationBenchmark benchmark;

    public ApplicationUI(SkillAllocationService service,
                         ConsoleReader reader,
                         ConsoleWriter writer,
                         AllocationStrategy sequentialStrategy,
                         AllocationStrategy concurrentStrategy,
                         AllocationBenchmark benchmark) {
        this.service = service;
        this.reader = reader;
        this.writer = writer;
        this.sequentialStrategy = sequentialStrategy;
        this.concurrentStrategy = concurrentStrategy;
        this.benchmark = benchmark;
    }

    public void start() {
        boolean running = true;

        while (running) {
            writer.printMenu();

            int option = reader.readInt("Enter option: ");

            try {
                running = handleOption(option);
            } catch (DuplicateEntityException | EntityNotFoundException | IllegalArgumentException e) {
                writer.printError(e.getMessage());
            }
        }

        writer.printGoodbye();
    }

    private boolean handleOption(int option) {
        switch (option) {
            case 1 -> addManager();
            case 2 -> addTrainee();
            case 3 -> addProject();
            case 4 -> allocateSequentially();
            case 5 -> allocateConcurrently();
            case 6 -> displayManagerProjects();
            case 7 -> displayUnallocatedTrainees();
            case 8 -> displayAllManagers();
            case 9 -> displayAllProjects();
            case 10 -> displayAllTrainees();
            case 11 -> compareAllocationPerformance();
            case EXIT_OPTION -> {
                return false;
            }
            default -> writer.printError("Invalid option.");
        }

        return true;
    }

    private void addManager() {
        long   id   = reader.readLong("Manager ID: ");
        String name = reader.readString("Manager name: ");

        service.addManager(id, name);

        writer.printSuccess("Manager added successfully.");
    }

    private void addTrainee() {
        long   id         = reader.readLong("Trainee ID: ");
        String name       = reader.readString("Trainee name: ");
        String skillInput = reader.readString("Trainee skill: ");

        Skill skill = Skill.from(skillInput);

        service.addTrainee(id, name, skill);

        writer.printSuccess("Trainee added successfully.");
    }

    private void addProject() {
        long   id       = reader.readLong("Project ID: ");
        String name     = reader.readString("Project name: ");
        int    duration = reader.readInt("Project duration: ");

        String skillInput    = reader.readString("Required skill: ");
        Skill  requiredSkill = Skill.from(skillInput);

        int  openings  = reader.readInt("Number of openings: ");
        long managerId = reader.readLong("Manager ID: ");

        service.addProject(id, name, duration, requiredSkill, openings, managerId);

        writer.printSuccess("Project added successfully.");
    }

    private void allocateSequentially() {
        AllocationResult result = service.allocateProjects(sequentialStrategy);

        writer.printAllocationResult(result);
    }

    private void allocateConcurrently() {
        AllocationResult result = service.allocateProjects(concurrentStrategy);

        writer.printAllocationResult(result);
    }

    private void displayManagerProjects() {
        long managerId = reader.readLong("Manager ID: ");

        writer.printAllProjects(service.getProjectsByManager(managerId));
    }

    private void displayUnallocatedTrainees() {
        writer.printUnallocatedTrainees(service.getUnallocatedTrainees());
    }

    private void displayAllManagers() {
        writer.printAllManagers(service.getAllManagers());
    }

    private void displayAllProjects() {
        writer.printAllProjects(service.getAllProjects());
    }

    private void displayAllTrainees() {
        writer.printAllTrainees(service.getAllTrainees());
    }

    private void compareAllocationPerformance() {
        int projectCount = reader.readInt("Number of projects: ");
        int traineeCount = reader.readInt("Number of trainees: ");

        writer.printPrompt("Running allocation performance benchmark...");

        BenchmarkResult[] results = benchmark.run(projectCount, traineeCount);

        writer.printBenchmarkResults(results);
    }

}