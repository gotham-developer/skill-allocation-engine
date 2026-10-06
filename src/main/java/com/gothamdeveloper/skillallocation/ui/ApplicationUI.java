package com.gothamdeveloper.skillallocation.ui;

import com.gothamdeveloper.skillallocation.application.AllocationResult;
import com.gothamdeveloper.skillallocation.application.SkillAllocationService;
import com.gothamdeveloper.skillallocation.domain.Skill;
import com.gothamdeveloper.skillallocation.exception.DuplicateEntityException;
import com.gothamdeveloper.skillallocation.exception.EntityNotFoundException;

public final class ApplicationUI {

    private static final int EXIT_OPTION = 0;

    private final SkillAllocationService service;
    private final ConsoleReader          reader;
    private final ConsoleWriter          writer;

    public ApplicationUI(SkillAllocationService service, ConsoleReader reader, ConsoleWriter writer) {
        this.service = service;
        this.reader = reader;
        this.writer = writer;
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
            case 4 -> allocateProjects();
            case 5 -> displayManagerProjects();
            case 6 -> displayUnallocatedTrainees();
            case 7 -> displayAllManagers();
            case 8 -> displayAllProjects();
            case 9 -> displayAllTrainees();
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

    private void allocateProjects() {
        AllocationResult result = service.allocateProjects();

        writer.printAllocationResult(result);
    }

    private void displayManagerProjects() {
        long managerId = reader.readLong("Manager ID: ");

        writer.printProjects(service.getProjectsByManager(managerId));
    }

    private void displayAllManagers() {
        writer.printManagers(service.getAllManagers());
    }

    private void displayAllProjects() {
        writer.printProjects(service.getAllProjects());
    }

    private void displayAllTrainees() {
        writer.printAllTrainees(service.getAllTrainees());
    }

    private void displayUnallocatedTrainees() {
        writer.printUnallocatedTrainees(service.getUnallocatedTrainees());
    }

}