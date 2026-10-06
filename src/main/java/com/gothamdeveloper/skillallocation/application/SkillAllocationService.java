package com.gothamdeveloper.skillallocation.application;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gothamdeveloper.skillallocation.domain.Manager;
import com.gothamdeveloper.skillallocation.domain.Project;
import com.gothamdeveloper.skillallocation.domain.Skill;
import com.gothamdeveloper.skillallocation.domain.Trainee;
import com.gothamdeveloper.skillallocation.exception.DuplicateEntityException;
import com.gothamdeveloper.skillallocation.exception.EntityNotFoundException;
import com.gothamdeveloper.skillallocation.repository.ManagerRepository;
import com.gothamdeveloper.skillallocation.repository.ProjectRepository;
import com.gothamdeveloper.skillallocation.repository.TraineeRepository;

public class SkillAllocationService {

    private static final Logger LOGGER = LogManager.getLogger(SkillAllocationService.class);

    private final ManagerRepository managerRepository;
    private final ProjectRepository projectRepository;
    private final TraineeRepository traineeRepository;

    public SkillAllocationService(ManagerRepository managerRepository,
                                  ProjectRepository projectRepository,
                                  TraineeRepository traineeRepository) {
        this.managerRepository = Objects.requireNonNull(managerRepository, "Manager repository must not be null");
        this.projectRepository = Objects.requireNonNull(projectRepository, "Project repository must not be null");
        this.traineeRepository = Objects.requireNonNull(traineeRepository, "Trainee repository must not be null");
    }

    public void addManager(long id, String name) {
        ensureIdDoesNotExist(managerRepository.existsById(id), "Manager", id);

        Manager manager = new Manager(id, name);

        managerRepository.save(manager);

        LOGGER.info("Manager added: id={}, name={}", manager.getId(), manager.getName());
    }

    public void addTrainee(long id, String name, Skill skill) {
        ensureIdDoesNotExist(traineeRepository.existsById(id), "Trainee", id);

        Trainee trainee = new Trainee(id, name, skill);

        traineeRepository.save(trainee);

        LOGGER.info("Trainee added: id={}, name={}, skill={}", trainee.getId(), trainee.getName(), trainee.getSkill());
    }

    public void addProject(long id, String name, int duration, Skill requiredSkill, int openings, long managerId) {
        ensureIdDoesNotExist(projectRepository.existsById(id), "Project", id);

        Manager manager = getManager(managerId);

        Project project = new Project(id, name, duration, requiredSkill, openings, manager);

        manager.addProject(project);
        projectRepository.save(project);

        LOGGER.info("Project added: id={}, name={}, managerId={}, requiredSkill={}, openings={}", project.getId(),
                    project.getName(), manager.getId(), project.getRequiredSkill(), project.getOpenings());
    }

    public AllocationResult allocateProjects() {
        int projectsProcessed = 0;
        int traineesAllocated = 0;
        int unfilledOpenings  = 0;

        List<Project> projects = projectRepository.findAll();
        List<Trainee> trainees = traineeRepository.findAll();

        LOGGER.info("Starting project allocation for {} projects and {} trainees", projects.size(), trainees.size());

        for (Project project : projects) {
            projectsProcessed++;

            for (Trainee trainee : trainees) {
                if (!project.hasAvailableOpening()) {
                    break;
                }

                if (trainee.isAllocated()) {
                    continue;
                }

                if (!project.requiresSkill(trainee.getSkill())) {
                    continue;
                }

                project.allocate(trainee);
                traineesAllocated++;

                LOGGER.debug("Trainee allocated: traineeId={}, projectId={}", trainee.getId(), project.getId());
            }

            unfilledOpenings += project.getOpenings();
        }

        AllocationResult result = new AllocationResult(projectsProcessed, traineesAllocated, unfilledOpenings);

        LOGGER.info("Project allocation completed: projectsProcessed={}, traineesAllocated={}, unfilledOpenings={}",
                    result.projectsProcessed(), result.traineesAllocated(), result.unfilledOpenings());

        return result;
    }

    public Manager getManager(long id) {
        return managerRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Manager not found: " + id));
    }

    public Project getProject(long id) {
        return projectRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + id));
    }

    public Trainee getTrainee(long id) {
        return traineeRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + id));
    }

    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Trainee> getAllTrainees() {
        return traineeRepository.findAll();
    }

    public List<Project> getProjectsByManager(long managerId) {
        getManager(managerId);

        return projectRepository.findByManagerId(managerId);
    }

    public List<Trainee> getUnallocatedTrainees() {
        return traineeRepository.findUnallocated();
    }

    private void ensureIdDoesNotExist(boolean exists, String entityName, long id) {
        if (exists) {
            throw new DuplicateEntityException(entityName + " already exists: " + id);
        }
    }

}