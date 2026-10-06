# Skill Allocation Engine

> A Java-based project–trainee skill allocation engine with pluggable allocation strategies, deterministic sequential allocation, experimental concurrent allocation, and built-in performance benchmarking.

---

## 📌 Overview

**Skill Allocation Engine** is a lightweight Java application that solves a simple resource-allocation problem:

> Given a collection of projects with required skills and available openings, allocate eligible trainees to those projects based on their skills.

The project was designed as a **clean, framework-free Java application** to explore and demonstrate:

- Domain-driven object modeling
- Clean separation of concerns
- Repository abstraction
- Dependency injection through constructors
- Strategy Pattern
- Deterministic sequential processing
- Concurrent processing with `ExecutorService`
- Thread-safety and synchronization
- Performance benchmarking
- Algorithmic complexity
- Immutable domain identity
- In-memory persistence
- Java 21 language features and modern coding practices

The application deliberately avoids unnecessary infrastructure such as Spring, Quarkus, databases, ORM frameworks, or external services.

The focus is on **core Java engineering and allocation algorithms**.

---

## ✨ Features

### Core functionality

- Create managers
- Create trainees
- Create projects
- Associate projects with managers
- Define project skill requirements
- Define project openings
- Allocate trainees to projects
- Prevent a trainee from being allocated more than once
- Prevent allocation when a project has no available openings
- Validate skill compatibility
- Display managers, projects, and trainees
- Display projects belonging to a manager
- Display unallocated trainees

### Allocation strategies

The allocation engine supports interchangeable strategies:

- **Sequential Allocation**
- **Concurrent Allocation**

Both strategies implement the same `AllocationStrategy` contract.

This allows the allocation algorithm to evolve without coupling the application service or UI to a particular execution model.

### Benchmarking

The application also includes a built-in benchmark that compares:

- Sequential execution time
- Concurrent execution time
- Allocation throughput
- Number of projects processed
- Number of trainees allocated
- Number of unfilled openings
- Relative performance between strategies

The benchmark uses:

- Warm-up iterations
- Multiple measurement iterations
- Fresh benchmark data for every measurement
- `System.nanoTime()` for timing
- Deterministic random data generation

---

# 🏗️ Architecture

The application follows a layered architecture with explicit dependency boundaries.

```text
                         ┌───────────────────────┐
                         │      Console UI       │
                         │                       │
                         │  ApplicationUI        │
                         │  ConsoleReader        │
                         │  ConsoleWriter        │
                         └───────────┬───────────┘
                                     │
                                     ▼
                         ┌───────────────────────┐
                         │     Application       │
                         │                       │
                         │ SkillAllocationService│
                         │ AllocationResult      │
                         └───────────┬───────────┘
                                     │
                     ┌───────────────┴───────────────┐
                     │                               │
                     ▼                               ▼
          ┌─────────────────────┐        ┌─────────────────────┐
          │ Allocation Strategy │        │    Repositories     │
          │                     │        │                     │
          │ Sequential          │        │ ManagerRepository   │
          │ Concurrent          │        │ ProjectRepository   │
          │                     │        │ TraineeRepository   │
          └──────────┬──────────┘        └──────────┬──────────┘
                     │                              │
                     ▼                              ▼
          ┌─────────────────────┐        ┌─────────────────────┐
          │      Domain         │        │  In-Memory Storage  │
          │                     │        │                     │
          │ Manager             │        │ LinkedHashMap       │
          │ Project             │        │                     │
          │ Trainee             │        │                     │
          │ Skill               │        │                     │
          └─────────────────────┘        └─────────────────────┘
```

### Architectural principles

The project follows:

- Separation of concerns
- Dependency inversion
- Constructor injection
- Encapsulation
- Single responsibility
- Strategy-based polymorphism
- Domain-owned invariants
- Explicit repository abstractions
- Minimal infrastructure
- No unnecessary framework dependencies

---

# 🧩 Domain Model

The core domain contains four primary concepts.

```text
Manager
   │
   │ manages
   ▼
Project
   │
   │ requires
   ▼
Skill

Project
   │
   │ allocates
   ▼
Trainee
   │
   │ possesses
   ▼
Skill
```

---

## 👨‍💼 Manager

A `Manager` represents a person responsible for one or more projects.

A manager contains:

- ID
- Name
- Projects

Example:

```java
Manager manager = new Manager(
        1,
        "John Doe"
);
```

### Design decisions

- Manager ID is immutable.
- Manager name is immutable.
- Projects are maintained internally.
- The collection exposed to callers is unmodifiable.
- A project can only be added when it belongs to that manager.
- Equality is based on manager ID.

---

# 📋 Project

A `Project` represents work requiring a specific skill.

A project contains:

- ID
- Name
- Duration
- Required skill
- Number of openings
- Manager
- Allocated trainees

Example:

```java
Project project = new Project(
        1,
        "Payment Service",
        12,
        Skill.JAVA,
        5,
        manager
);
```

### Project invariants

A project:

- Must have a positive ID.
- Must have a non-blank name.
- Must have a positive duration.
- Must have a valid required skill.
- Must have zero or more openings.
- Must have a manager.
- Cannot allocate a trainee without an available opening.
- Cannot allocate an already allocated trainee.
- Cannot allocate a trainee with an incompatible skill.

The `Project` entity owns the actual allocation operation so that allocation-related invariants cannot easily be bypassed.

---

# 👨‍🎓 Trainee

A `Trainee` represents a person available for project allocation.

A trainee contains:

- ID
- Name
- Skill
- Allocated project

Example:

```java
Trainee trainee = new Trainee(
        1,
        "Alice",
        Skill.JAVA
);
```

Allocation state is derived directly from the project reference:

```java
trainee.isAllocated()
```

There is deliberately **no separate `boolean projectAllocated` field**.

This avoids duplicated state such as:

```text
projectAllocated = true
project = null
```

The project reference is the single source of truth.

---

# 🛠️ Skill

Skills are represented using a Java `enum` rather than arbitrary strings.

Example:

```java
Skill.JAVA
Skill.PYTHON
Skill.CPP
Skill.SPRING_BOOT
Skill.KUBERNETES
```

This provides:

- Type safety
- Compile-time validation
- Efficient comparison
- No spelling inconsistencies inside the domain
- Cleaner domain logic

---

## 🔤 Skill aliases

User input supports aliases.

For example:

```text
c++
cpp
Cpp
C++
c plus plus
```

all resolve to:

```java
Skill.CPP
```

Similarly:

```text
js
javascript
```

resolve to:

```java
Skill.JAVASCRIPT
```

and:

```text
k8s
kubernetes
```

resolve to:

```java
Skill.KUBERNETES
```

The UI therefore accepts human-friendly input while the domain operates on strongly typed enums.

---

# 📦 Supported Skills

The current skill catalogue includes:

| Skill | Example aliases |
|---|---|
| Java | `java` |
| C | `c` |
| C++ | `c++`, `cpp`, `c plus plus` |
| C# | `c#`, `csharp`, `c sharp` |
| Python | `python` |
| JavaScript | `javascript`, `js` |
| TypeScript | `typescript`, `ts` |
| Go | `go`, `golang` |
| Rust | `rust` |
| Kotlin | `kotlin` |
| Swift | `swift` |
| PHP | `php` |
| Ruby | `ruby` |
| SQL | `sql` |
| HTML | `html` |
| CSS | `css` |
| React | `react` |
| Angular | `angular` |
| Vue | `vue` |
| Spring Boot | `spring boot`, `springboot` |
| .NET | `.net`, `dotnet`, `dot net` |
| Node.js | `node.js`, `nodejs`, `node js` |
| Docker | `docker` |
| Kubernetes | `kubernetes`, `k8s` |
| AWS | `aws`, `amazon web services` |

---

# 🗄️ Repository Layer

The application does not directly depend on `HashMap` or another storage implementation.

Instead, repository interfaces define the required operations.

```java
ManagerRepository
ProjectRepository
TraineeRepository
```

Example:

```java
public interface TraineeRepository {

    void save(Trainee trainee);

    Optional<Trainee> findById(long id);

    List<Trainee> findAll();

    List<Trainee> findUnallocated();

    boolean existsById(long id);
}
```

The current implementation is:

```text
InMemoryManagerRepository
InMemoryProjectRepository
InMemoryTraineeRepository
```

Internally these repositories use:

```java
LinkedHashMap<Long, Entity>
```

---

## Why `LinkedHashMap`?

`LinkedHashMap` provides two useful properties:

1. Efficient lookup by ID.
2. Deterministic insertion-order iteration.

Deterministic ordering is particularly useful for this project because the sequential allocation strategy intentionally uses a predictable first-fit ordering.

A normal `HashMap` would not provide that ordering guarantee.

---

# 🧠 Application Service

`SkillAllocationService` coordinates application-level operations.

It is responsible for:

- Creating managers
- Creating trainees
- Creating projects
- Duplicate validation
- Repository interaction
- Query operations
- Invoking allocation strategies
- Logging application-level operations

The service does **not** implement the allocation algorithm itself.

Instead:

```java
public AllocationResult allocateProjects(
        AllocationStrategy strategy
)
```

delegates the operation to the selected strategy.

This keeps orchestration separate from allocation mechanics.

---

# 🔀 Strategy Pattern

The allocation engine uses the Strategy Pattern.

```java
public interface AllocationStrategy {

    AllocationResult allocate(
            List<Project> projects,
            List<Trainee> trainees
    );

    String getName();
}
```

Current implementations:

```text
AllocationStrategy
       │
       ├── SequentialAllocationStrategy
       │
       └── ConcurrentAllocationStrategy
```

This allows new allocation algorithms to be introduced without modifying the service.

For example, future strategies could include:

```text
SkillIndexedAllocationStrategy
PriorityBasedAllocationStrategy
BalancedAllocationStrategy
CapacityAwareAllocationStrategy
```

without changing the UI or service contract.

---

# 1️⃣ Sequential Allocation

The sequential strategy processes projects one at a time.

Conceptually:

```text
for each project
    for each trainee
        if project has no openings
            stop

        if trainee already allocated
            continue

        if trainee skill does not match
            continue

        allocate trainee
```

The strategy is intentionally deterministic.

Given the same ordered input:

```text
Projects
+
Trainees
+
Allocation rules
```

the result is predictable.

---

## Sequential allocation characteristics

### Advantages

- Simple
- Deterministic
- Easy to reason about
- Low synchronization overhead
- Low scheduling overhead
- Very efficient for small and medium workloads

### Disadvantages

- Single-threaded
- Potentially performs many trainee checks
- Cannot utilize multiple CPU cores

---

# 2️⃣ Concurrent Allocation

The concurrent strategy processes projects in parallel.

The high-level model is:

```text
                    Projects
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
     Worker 1       Worker 2       Worker 3
        │              │              │
     Project A      Project B      Project C
        │              │              │
        └──────────────┼──────────────┘
                       ▼
                Shared trainees
```

A fixed-size `ExecutorService` is used:

```java
Executors.newFixedThreadPool(threadCount)
```

The number of worker threads is bounded by:

```text
min(project count, available processors)
```

This prevents the application from blindly creating one thread per project.

---

# 🔒 Thread Safety

Concurrent allocation introduces shared mutable state.

Two resources require protection:

### Project state

A project contains mutable:

```text
openings
allocated trainees
```

Therefore project allocation is protected using:

```java
synchronized (project)
```

### Trainee state

A trainee can only belong to one project.

The check:

```java
if (!trainee.isAllocated())
```

and the subsequent allocation must behave atomically.

Therefore trainee state is protected using:

```java
synchronized (trainee)
```

The effective operation is:

```text
check trainee
      +
claim trainee
```

as one atomic critical section.

---

# 🔐 Lock Ordering

The concurrent strategy consistently follows:

```text
Project → Trainee
```

rather than sometimes acquiring:

```text
Trainee → Project
```

and elsewhere:

```text
Project → Trainee
```

Consistent lock ordering is important because inconsistent ordering can introduce deadlocks.

Example of the dangerous pattern:

```text
Thread A:
    locks Project
    waits for Trainee

Thread B:
    locks Trainee
    waits for Project
```

Both threads could wait indefinitely.

The implementation avoids this by maintaining one lock acquisition order.

---

# 📊 Allocation Result

Both strategies return the same result model:

```java
public record AllocationResult(
        int projectsProcessed,
        int traineesAllocated,
        int unfilledOpenings
) {}
```

This provides a common result contract independent of the execution strategy.

Example:

```text
Projects processed : 100
Trainees allocated  : 552
Unfilled openings   : 0
```

This also allows the benchmark to compare strategies without knowing their internal implementation.

---

# ⚡ Benchmarking

The project includes a dedicated benchmark subsystem.

```text
benchmark/
├── AllocationBenchmark.java
├── BenchmarkData.java
├── BenchmarkDataFactory.java
└── BenchmarkResult.java
```

The benchmark compares:

```text
Sequential Allocation
        vs
Concurrent Allocation
```

---

## Benchmark methodology

Each strategy is tested using:

```text
Warm-up runs      : 2
Measurement runs  : 5
```

Each measurement creates fresh project and trainee objects.

This is important because allocation mutates the domain objects.

Reusing the same objects would produce invalid benchmark results because subsequent runs would operate on already allocated trainees and projects with reduced openings.

---

## Deterministic benchmark data

Benchmark data is generated using a fixed random seed:

```java
private static final long RANDOM_SEED = 42L;
```

This makes benchmark workloads reproducible.

The same input sizes produce the same generated dataset.

---

## What is measured?

Only the allocation operation is timed.

The benchmark deliberately excludes:

- Console I/O
- Object creation
- Benchmark data generation
- Application startup
- User input
- Result printing

The timing therefore focuses on:

```text
allocation algorithm execution
```

Timing uses:

```java
System.nanoTime()
```

which is appropriate for measuring short-duration JVM operations.

---

# 📈 Benchmark Metrics

Each benchmark reports:

### Average execution time

Average of the measurement runs.

```text
Average time: 482.964 ms
```

### Allocation throughput

Number of successful allocations per second.

```text
113,643.99 allocations/sec
```

### Allocation count

Number of trainees successfully allocated.

### Unfilled openings

Total remaining project capacity.

### Relative performance

Sequential and concurrent execution times are compared.

---

# 🧪 Observed Performance

Example benchmark results from the current implementation:

| Projects | Trainees | Strategy | Allocated | Avg. Time | Throughput |
|---:|---:|---|---:|---:|---:|
| 10 | 100 | Sequential | 33 | 0.103 ms | 319,767 alloc/s |
| 10 | 100 | Concurrent | 33 | 0.889 ms | 37,132 alloc/s |
| 100 | 10,000 | Sequential | 552 | 0.650 ms | 849,014 alloc/s |
| 100 | 10,000 | Concurrent | 552 | 2.858 ms | 193,147 alloc/s |
| 10,000 | 1,000,000 | Sequential | 54,886 | 482.964 ms | 113,644 alloc/s |
| 10,000 | 1,000,000 | Concurrent | 54,886 | 640.068 ms | 85,750 alloc/s |

### Current observation

The concurrent implementation is currently **slower** than the sequential implementation for these workloads.

That is expected and is an important result rather than a failure.

Concurrency introduces overhead from:

```text
Task creation
      ↓
Executor scheduling
      ↓
Thread coordination
      ↓
Synchronization
      ↓
Lock acquisition
      ↓
Future aggregation
      ↓
Executor shutdown
```

For smaller workloads, that overhead dominates the actual allocation work.

Even for larger workloads, synchronization around shared trainee state limits the benefit of parallel execution.

---

# 🧮 Algorithmic Complexity

The basic allocation approach can be viewed as approximately:

```text
O(P × T)
```

where:

```text
P = number of projects
T = number of trainees
```

The actual runtime can be significantly lower because each project stops searching once its openings are filled.

For example:

```text
Project requires 5 trainees
```

Once five compatible trainees are found, the remaining trainee list is not scanned for that project.

Therefore actual work depends heavily on:

- Number of projects
- Number of trainees
- Skill distribution
- Required skills
- Number of openings
- Position of matching trainees
- Number of already allocated trainees

---

# 🧠 Concurrency vs Algorithm Optimization

One of the main engineering lessons demonstrated by this project is:

> Parallelizing an inefficient algorithm does not necessarily make it faster.

The current strategy searches trainees for every project.

A future skill-indexed approach could instead organize trainees like:

```text
JAVA
 ├── Trainee 1
 ├── Trainee 8
 └── Trainee 17

PYTHON
 ├── Trainee 3
 └── Trainee 11

C++
 ├── Trainee 4
 └── Trainee 19
```

Then a Java project would only inspect Java trainees.

This could dramatically reduce the amount of unnecessary searching.

In many workloads:

```text
Better algorithm + 1 thread
```

can outperform:

```text
Less efficient algorithm + multiple threads
```

This is why the concurrent strategy is intentionally treated as an experiment rather than automatically being considered the superior implementation.

---

# 🖥️ Console Application

The application provides an interactive console menu.

```text
========================================
       SKILL ALLOCATION ENGINE
========================================

1. Add Manager
2. Add Trainee
3. Add Project
4. Allocate Sequentially
5. Allocate Concurrently
6. Display Manager Projects
7. Display Unallocated Trainees
8. Display All Managers
9. Display All Projects
10. Display All Trainees
11. Compare Allocation Performance
0. Exit
```

---

# 📝 Example Workflow

### 1. Create a manager

```text
Enter manager ID: 1
Enter manager name: John
```

### 2. Create trainees

```text
Enter trainee ID: 1
Enter trainee name: Alice
Enter skill: Java
```

The input:

```text
Java
```

is converted into:

```java
Skill.JAVA
```

---

### 3. Create a project

```text
Enter project ID: 1
Enter project name: Payment Service
Enter duration: 12
Enter required skill: Java
Enter openings: 2
Enter manager ID: 1
```

---

### 4. Allocate

Choose:

```text
4. Allocate Sequentially
```

or:

```text
5. Allocate Concurrently
```

---

### 5. Compare performance

Choose:

```text
11. Compare Allocation Performance
```

Then provide benchmark sizes:

```text
Number of projects: 10000
Number of trainees: 1000000
```

The application runs both strategies against equivalent fresh datasets and reports their performance.

---

# 📁 Project Structure

```text
skill-allocation-engine/
│
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── nucleus/
│                   └── skillallocation/
│
│                       ├── Main.java
│                       │
│                       ├── application/
│                       │   ├── AllocationResult.java
│                       │   ├── SkillAllocationService.java
│                       │   │
│                       │   └── allocation/
│                       │       ├── AllocationStrategy.java
│                       │       ├── SequentialAllocationStrategy.java
│                       │       └── ConcurrentAllocationStrategy.java
│                       │
│                       ├── benchmark/
│                       │   ├── AllocationBenchmark.java
│                       │   ├── BenchmarkData.java
│                       │   ├── BenchmarkDataFactory.java
│                       │   └── BenchmarkResult.java
│                       │
│                       ├── domain/
│                       │   ├── Manager.java
│                       │   ├── Project.java
│                       │   ├── Skill.java
│                       │   └── Trainee.java
│                       │
│                       ├── exception/
│                       │   ├── DuplicateEntityException.java
│                       │   └── EntityNotFoundException.java
│                       │
│                       ├── repository/
│                       │   ├── ManagerRepository.java
│                       │   ├── ProjectRepository.java
│                       │   ├── TraineeRepository.java
│                       │   │
│                       │   └── memory/
│                       │       ├── InMemoryManagerRepository.java
│                       │       ├── InMemoryProjectRepository.java
│                       │       └── InMemoryTraineeRepository.java
│                       │
│                       └── ui/
│                           ├── ApplicationUI.java
│                           ├── ConsoleReader.java
│                           └── ConsoleWriter.java
│
├── src/
│   └── main/
│       └── resources/
│           └── log4j2.xml
│
├── pom.xml
└── README.md
```

---

# 🧱 Package Responsibilities

| Package | Responsibility |
|---|---|
| `domain` | Core business entities and domain rules |
| `application` | Application orchestration and result models |
| `application.allocation` | Allocation algorithms |
| `repository` | Persistence abstractions |
| `repository.memory` | In-memory repository implementations |
| `benchmark` | Performance testing and benchmark data |
| `ui` | Console interaction |
| `exception` | Application/domain exceptions |
| `Main` | Composition root and dependency wiring |

---

# 🔌 Dependency Flow

Dependencies intentionally point inward.

```text
UI
 ↓
Application
 ↓
Allocation Strategy
 ↓
Domain

Application
 ↓
Repository Interface
 ↓
In-Memory Repository
```

The application service depends on:

```java
ManagerRepository
ProjectRepository
TraineeRepository
```

rather than:

```java
InMemoryManagerRepository
InMemoryProjectRepository
InMemoryTraineeRepository
```

This is an example of dependency inversion.

---

# 💉 Dependency Injection

The project uses constructor injection.

For example:

```java
new SkillAllocationService(
        managerRepository,
        projectRepository,
        traineeRepository,
        sequentialStrategy
);
```

No dependency is created internally by the service.

This makes dependencies:

- Explicit
- Immutable
- Easy to reason about
- Replaceable
- Easy to benchmark with different strategies

---

# 🚫 Why There Is No Framework

This project intentionally does not use:

- Spring
- Spring Boot
- Quarkus
- Hibernate
- JPA
- JDBC
- Database servers
- REST APIs
- Dependency injection frameworks

The purpose is to demonstrate that clean architecture and good engineering practices do not require a framework.

The application is intentionally small enough that manual dependency composition is clearer than introducing a dependency injection container.

---

# 🗃️ Why There Is No Database

The current application uses in-memory repositories.

This keeps the scope focused on:

```text
Domain modeling
+
Allocation algorithms
+
Concurrency
+
Performance
```

There is no persistence requirement for the current problem.

A database can be introduced later without changing the application service contract because persistence is already abstracted behind repository interfaces.

For example:

```text
Current:

TraineeRepository
      ↓
InMemoryTraineeRepository


Future:

TraineeRepository
      ↓
PostgresTraineeRepository
```

The application layer does not need to know how the data is stored.

---

# 📜 Logging

The project uses **Log4j2** for application-level logging.

Logging is intentionally kept outside domain entities.

The domain should represent business state and behavior, not infrastructure concerns.

Typical application-level logs include:

```text
Starting Sequential project allocation...
Sequential allocation completed...
Starting Concurrent project allocation...
Concurrent allocation completed...
```

---

# 🧹 Validation Philosophy

Validation is separated according to responsibility.

### Console layer

Responsible for parsing input:

```text
String
int
long
```

It does not contain business rules.

### Application layer

Responsible for application-level rules such as:

```text
duplicate entity IDs
missing referenced manager
```

### Domain layer

Responsible for domain invariants such as:

```text
positive IDs
non-blank names
valid duration
valid skill
available project openings
compatible trainee skill
single project allocation
```

This prevents business rules from becoming scattered throughout the UI.

---

# 🔐 Domain Encapsulation

Mutable collections are not exposed directly.

Instead of:

```java
public Set<Trainee> getTrainees() {
    return trainees;
}
```

the domain exposes an unmodifiable view.

This prevents external callers from doing:

```java
project.getTrainees().clear();
```

and bypassing domain invariants.

The same principle is applied to manager projects.

---

# 🆔 Identity and Equality

Domain entities use their IDs for equality.

For example:

```text
Manager ID
Project ID
Trainee ID
```

are stable identities.

IDs are immutable.

There are no setters such as:

```java
setId(...)
```

because changing entity identity after creation would create difficult-to-reason-about behavior, especially when entities are stored in hash-based collections.

---

# 🧵 Why No Shared Allocation Counter?

The concurrent strategy does not use a shared counter such as:

```java
AtomicInteger
```

for every successful allocation.

Instead, each worker maintains a local count:

```text
Worker 1 → 12 allocations
Worker 2 → 18 allocations
Worker 3 → 21 allocations
```

The coordinator aggregates the results:

```text
12 + 18 + 21 = 51
```

This reduces unnecessary contention on shared state.

---

# 🧪 Testing Policy

The project intentionally does **not currently contain automated tests**.

This is a deliberate scope decision for the current iteration.

The application is being used primarily as an exercise in:

- Java design
- architecture
- concurrency
- allocation algorithms
- benchmarking

A future iteration could introduce:

```text
Unit tests
Integration tests
Concurrency tests
Benchmark regression tests
```

without changing the core architecture.

---

# ⚠️ Current Limitations

The current implementation is intentionally simple.

### 1. In-memory only

All data is lost when the application exits.

### 2. First-fit allocation

The sequential algorithm uses the order of projects and trainees rather than an optimization algorithm.

### 3. Concurrent allocation is not deterministic

The concurrent strategy may produce different trainee-to-project assignments because worker scheduling can differ between executions.

The important invariant is that a trainee is never allocated to multiple projects.

### 4. Concurrent overhead

For many workloads, the concurrent strategy is slower because of:

- Executor overhead
- Task scheduling
- Lock acquisition
- Shared trainee state
- Future coordination

### 5. No advanced allocation optimization

The current implementation does not optimize trainee lookup by skill.

### 6. No persistence

There is currently no database or external storage.

---

# 🚀 Future Improvements

Potential future improvements include:

## Algorithmic improvements

- Skill-indexed trainee lookup
- Priority-based project allocation
- Trainee ranking
- Project priority
- Capacity-aware allocation
- Fair allocation
- Skill proficiency levels
- Multi-skill projects
- Multiple skills per trainee

## Concurrency improvements

- Reduce synchronization scope
- Use skill-specific queues
- Partition trainees by skill
- Reduce shared mutable state
- Compare alternative concurrency models
- Evaluate virtual threads where appropriate
- Investigate lock-free structures where beneficial

## Benchmark improvements

- Multiple workload profiles
- Sparse-match workload
- Dense-match workload
- High-contention workload
- Large-opening workload
- Skill-imbalanced workload
- Benchmark result export
- JVM/CPU metadata
- Statistical variance
- Percentiles instead of only averages

## Persistence

Possible future repository implementations:

```text
PostgreSQL
SQLite
File-based storage
```

without changing the repository abstraction.

## User interface

Potential future interfaces:

```text
REST API
Web UI
Desktop UI
```

The application/domain layers could remain largely unchanged.

---

# 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Application language/runtime |
| Maven | Build and dependency management |
| Log4j2 | Application logging |
| JUnit | Not currently used |
| Database | None |
| ORM | None |
| Framework | None |
| Storage | In-memory |

---

# 📋 Requirements

Before running the application, install:

- Java 21+
- Maven 3.9+

Verify:

```bash
java -version
mvn -version
```

---

# ▶️ Running the Application

Clone the repository:

```bash
git clone <repository-url>
cd skill-allocation-engine
```

Run with Maven:

```bash
mvn clean compile exec:java
```

If the project uses the Maven Wrapper:

### Windows

```powershell
.\mvnw.cmd clean compile exec:java
```

### Linux/macOS

```bash
./mvnw clean compile exec:java
```

---

# 🔨 Build

Compile the project:

```bash
mvn clean compile
```

Package the project:

```bash
mvn clean package
```

---

# 🧭 Typical Usage

A typical session looks like:

```text
1. Add Manager
2. Add Trainee
3. Add Project

        ↓

4. Allocate Sequentially

        OR

5. Allocate Concurrently

        ↓

6–10. Inspect allocation state

        ↓

11. Compare Allocation Performance
```

---

# 🧠 Engineering Decisions

This project intentionally favors **clarity over unnecessary abstraction**.

### Why `enum Skill`?

Because skills are a closed, known set and the domain benefits from type safety.

### Why repositories?

To separate application logic from storage.

### Why Strategy Pattern?

Because allocation execution is expected to evolve.

### Why constructor injection?

Because dependencies should be explicit and immutable.

### Why `LinkedHashMap`?

Because the application benefits from deterministic iteration order while retaining efficient ID lookup.

### Why `ExecutorService`?

Because it provides controlled thread management without manually managing worker threads.

### Why project-level parallelism?

Projects are largely independent allocation units, making them a natural boundary for parallel execution.

### Why synchronize trainees?

Because trainee allocation is shared mutable state and must be claimed atomically.

### Why no `parallelStream()`?

Explicit task execution provides clearer control over:

- Thread count
- Task boundaries
- Synchronization
- Exception handling
- Benchmark behavior

### Why no virtual threads?

The current workload is CPU-bound and in-memory rather than I/O-bound. Virtual threads would not inherently make this workload faster.

---

# 🔬 Key Engineering Lessons

This project demonstrates several practical engineering principles.

### 1. Correctness comes before concurrency

Adding threads does not automatically make an application faster.

### 2. Shared mutable state is expensive

Parallel workers need coordination when they operate on common objects.

### 3. Locking has a cost

Synchronization protects correctness but introduces overhead.

### 4. Benchmark results matter more than assumptions

The concurrent implementation was expected to potentially improve throughput at scale, but actual measurements currently show that the sequential implementation remains faster for the tested workloads.

### 5. Algorithmic optimization can beat parallelism

Reducing unnecessary work is often more valuable than executing the same work across more CPU cores.

### 6. Abstraction enables experimentation

Because allocation is represented by:

```java
AllocationStrategy
```

different algorithms can be tested without rewriting the application.

### 7. Determinism is valuable

Deterministic data and ordering make debugging and benchmarking substantially easier.

---

# 📐 Design Summary

```text
                    ┌─────────────────┐
                    │   Application   │
                    │      Entry      │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │  ApplicationUI  │
                    └────────┬────────┘
                             │
                             ▼
                 ┌─────────────────────────┐
                 │ SkillAllocationService  │
                 └───────┬─────────┬───────┘
                         │         │
              ┌──────────┘         └──────────┐
              ▼                               ▼
    ┌─────────────────────┐         ┌──────────────────┐
    │ AllocationStrategy  │         │   Repositories   │
    └──────────┬──────────┘         └────────┬─────────┘
               │                             │
       ┌───────┴────────┐          ┌─────────┴─────────┐
       ▼                ▼          ▼         ▼         ▼
 Sequential        Concurrent   Manager   Project   Trainee
 Allocation        Allocation   Memory    Memory    Memory
       │                │
       └────────┬───────┘
                ▼
        ┌───────────────┐
        │    Domain     │
        │               │
        │ Manager       │
        │ Project       │
        │ Trainee       │
        │ Skill         │
        └───────────────┘
```

---

# 📊 Project Status

| Area | Status |
|---|---|
| Domain model | ✅ Complete |
| Manager management | ✅ Complete |
| Trainee management | ✅ Complete |
| Project management | ✅ Complete |
| Skill enum & aliases | ✅ Complete |
| Repository abstraction | ✅ Complete |
| In-memory repositories | ✅ Complete |
| Sequential allocation | ✅ Complete |
| Concurrent allocation | ✅ Complete |
| Thread-safety | ✅ Implemented |
| Benchmarking | ✅ Implemented |
| Console UI | ✅ Complete |
| Persistence | ⏳ Not implemented |
| Automated tests | ⏳ Not implemented |
| Advanced allocation algorithm | 🔮 Future |
| REST API | 🔮 Future |

---

# 📄 License

This project is currently intended as a personal engineering/learning project.

Add a license here if the repository is intended for public redistribution.

---

## ⭐ Final Note

**Skill Allocation Engine** is intentionally more than a CRUD console application.

The project explores the progression from:

```text
Simple domain model
        ↓
Clean architecture
        ↓
Repository abstraction
        ↓
Strategy Pattern
        ↓
Deterministic allocation
        ↓
Concurrent allocation
        ↓
Thread-safety
        ↓
Benchmarking
        ↓
Performance analysis
        ↓
Algorithmic optimization
```

The current benchmark results demonstrate an important engineering reality:

> **Concurrency is a tool, not an optimization by itself.**

The goal of this project is therefore not simply to make the concurrent implementation "win", but to understand **when concurrency helps, when it hurts, why it behaves that way, and how the underlying algorithm affects the result.**