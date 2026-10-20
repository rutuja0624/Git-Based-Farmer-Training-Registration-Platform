# Task 4: Git and GitHub Repository Initialization
## Project: Git-Based Farmer Training Registration Platform

**Student Details:**
- **Name:** Rutuja Kamale
- **Student ID:** 23102A0028
- **Roll Number:** 27
- **Course:** DevOps Engineering Laboratory

---

### 1. Task Objective & Scope
The objective of Task 4 is to establish a centralized version control repository on GitHub for the **Farmer Training Registration Platform**. This includes setting up `.gitignore` hygiene rules, defining the standard enterprise directory structure, configuring GitHub Issue templates, establishing formal branch naming conventions, committing the initial application skeleton using Conventional Commits, and tracking initial issues.

---

### 2. Repository Metadata & Deliverables

| Item | Specification / Value |
| :--- | :--- |
| **Repository Name** | `Git-Based Farmer Training Registration Platform` (`Git-Based-Farmer-Training-Registration-Platform`) |
| **Remote Repository URL** | `https://github.com/rutuja0624/Git-Based-Farmer-Training-Registration-Platform` |
| **Default Branch** | `main` |
| **Integration Branch** | `develop` |
| **Visibility** | Public |
| **Owner** | Rutuja Kamale (`rutuja0624`) |
| **Branch Policy Document** | [`docs/branch-policy.md`](branch-policy.md) |
| **Issue Templates** | Feature Request, Bug Report, DevOps Task (`.github/ISSUE_TEMPLATE/`) |

---

### 3. Repository Folder Layout

```
DevOps/
├── .github/
│   └── ISSUE_TEMPLATE/
│       ├── bug_report.md
│       ├── devops_task.md
│       └── feature_request.md
├── docs/
│   ├── branch-policy.md
│   ├── task-1-problem-definition-and-scope.md
│   ├── task-2-agile-devops-workflow.md
│   ├── task-3-requirements-architecture-setup.md
│   └── task-4-git-github-initialization.md
├── src/
│   ├── main/
│   │   ├── java/com/farmer/devops/
│   │   │   ├── config/ (DataSeeder.java)
│   │   │   ├── controller/ (RegistrationController, TrainingController, WebViewController)
│   │   │   ├── dto/ (RegistrationRequest, StatusUpdateRequest, ApiResponse)
│   │   │   ├── exception/ (GlobalExceptionHandler, Custom Exceptions)
│   │   │   ├── model/ (Registration, TrainingProgram, RegistrationStatus)
│   │   │   ├── repository/ (RegistrationRepository, TrainingRepository)
│   │   │   ├── service/ (RegistrationService, TrainingService)
│   │   │   └── FarmerRegistrationApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/ (index.html, tracker.html, admin.html, css/style.css, js/app.js)
│   └── test/
│       └── java/com/farmer/devops/
│           ├── controller/RegistrationControllerTest.java
│           └── service/RegistrationServiceTest.java
├── .gitignore
├── pom.xml
└── README.md
```

---

### 4. Git Ignore Rules (`.gitignore`)
The repository excludes build artifacts, IDE configuration caches, and OS-specific files:
```gitignore
# Target build directories
target/
*.class
*.jar

# IDE and Editor directories
.idea/
*.iml
.vscode/
.project
.classpath
.settings/

# Logs and databases
*.log
*.db
h2-data/

# OS generated files
Thumbs.db
Desktop.ini
.DS_Store
```

---

### 5. Task-Wise Meaningful Commits Sequence

To fulfill the curriculum requirement of committing progress sequentially per task:

1. **Commit 1 (Task 1):** `docs(task-1): problem definition, target users, pain points, and MVP scope`
2. **Commit 2 (Task 2):** `docs(task-2): agile backlog, user stories, DoD, and devops lifecycle diagram`
3. **Commit 3 (Task 3):** `feat(task-3): requirements, architecture, REST APIs, and working Spring Boot local setup`
4. **Commit 4 (Task 4):** `ci(task-4): initialize GitHub repository, issue templates, branch policy, and README`

---

### 6. GitHub Initial Issues Seeded

The following GitHub issues are initialized to track subsequent curriculum tasks:
- **Issue #1:** `[DEVOPS] Task 5: Feature Development with Branching (First Core Workflow)`
- **Issue #2:** `[DEVOPS] Task 6: MVP Completion, Git Collaboration & Conflict Resolution`
- **Issue #3:** `[DEVOPS] Task 7: Jenkins Installation and Continuous Integration Job`

---
*Deliverable Status: Task 4 Initialized & Ready for Remote Push.*
