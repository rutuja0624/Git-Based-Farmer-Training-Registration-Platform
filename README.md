# 🌾 Git-Based Farmer Training Registration Platform

[![DevOps Build](https://img.shields.io/badge/Build-Maven%203.9-blue.svg)](https://maven.apache.org/)
[![Java Version](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Embedded Tomcat](https://img.shields.io/badge/Server-Embedded%20Tomcat%2010-yellow.svg)](https://tomcat.apache.org/)
[![Database](https://img.shields.io/badge/Database-H2%20In--Memory-lightblue.svg)](https://www.h2database.com/)

---

### 🎓 Academic & Student Information
- **Student Name:** Rutuja Kamale
- **Roll Number:** 23102A0028
- **Project Title:** Git-Based Farmer Training Registration Platform
- **Course:** DevOps 

---

## 📌 15-Task DevOps Curriculum Lifecycle Roadmap

Below is the approved 15-task sequence tracking our development, testing, CI/CD, containerization, and configuration management lifecycle:

| Task # | Task Title & Scope | Status | Deliverables & Artifacts |
| :---: | :--- | :---: | :--- |
| **Task 1** | **Problem Definition and Scope:** Study real-time need, target users, pain points, stakeholders, constraints, success criteria, and freeze 15-Task MVP scope. | ✅ Completed | [Task 1 Deliverable Doc](docs/task-1-problem-definition-and-scope.md) |
| **Task 2** | **Agile Planning and DevOps Workflow:** User stories, Gherkin acceptance criteria, product backlog, 15-task Kanban/Scrum plan, Definition of Done (DoD), and DevOps lifecycle diagram. | ✅ Completed | [Task 2 Deliverable Doc](docs/task-2-agile-devops-workflow.md) |
| **Task 3** | **Requirements, Architecture and Technology Setup:** SRS summary, use-case diagram, architecture diagram, data model/API list, and working local setup (Java 21, Spring Boot, Maven, H2 DB, Tomcat). | ✅ Completed | [Task 3 Deliverable Doc](docs/task-3-requirements-architecture-setup.md), Source Code |
| **Task 4** | **Git and GitHub Repository Initialization:** Create GitHub repository, add README, `.gitignore`, folder structure, issue templates, branch naming rules, meaningful initial commits, and branch policy. | ✅ Completed | [Task 4 Deliverable Doc](docs/task-4-git-github-initialization.md), [Branch Policy](docs/branch-policy.md) |
| **Task 5** | **Feature Development with Branching:** Implement the first core workflow on a feature branch (`feature/task-5-farmer-registration`). Use add, commit, push, pull, logs, pull request, review, and merge to develop. | ✅ Completed | [Task 5 Deliverable Doc](docs/task-5-feature-development-with-branching.md), PR & Merge |
| **Task 6** | **MVP Completion and Git Collaboration:** Complete functional MVP, district filtering, simulated conflict resolution, and release tagging (`v1.0.0-MVP`). | ✅ Completed | [Task 6 Deliverable Doc](docs/task-6-mvp-completion-git-collaboration.md), Tag `v1.0.0-MVP` |
| **Task 7** | **Jenkins Installation and Continuous Integration Job:** Configure Jenkins automation server on port 8080, connect GitHub repository, configure Maven CI job (`Farmer-Training-Registration-CI`) with SCM polling and artifact archiving. | ✅ Completed | [Task 7 Deliverable Doc](docs/task-7-jenkins-ci-job.md), [Job Config XML](ci/jenkins/job-config.xml) |
| **Task 8** | **Pipeline as Code and Server Deployment:** Create Jenkinsfile with checkout, build, package, and deploy stages. Deploy build to Tomcat/Nginx, parameterize environment setting. | ✅ Completed | [Task 8 Deliverable Doc](docs/task-8-pipeline-as-code-server-deployment.md), [Jenkinsfile](Jenkinsfile) |
| **Task 9** | **Selenium Test Design and Local Execution:** Identify 3-5 critical user journeys. Write Selenium WebDriver test cases with assertions, test data, and failure screenshots. Run suite locally via Maven. | ✅ Completed | [Task 9 Deliverable Doc](docs/task-9-selenium-test-design-local-execution.md), [Test Suite](src/test/java/com/farmer/devops/selenium/FarmerRegistrationUiTest.java), [Screenshots](docs/screenshots/) |
| **Task 10** | **Continuous Testing in Jenkins:** Integrate Selenium suite into Jenkins. Publish test reports, configure pipeline quality gate. Correct deliberately introduced defect and rerun. | ✅ Completed | [Task 10 Deliverable Doc](docs/task-10-continuous-testing-in-jenkins.md), [Pipeline Quality Gate](Jenkinsfile) |
| **Task 11** | **Docker Image and Container Lifecycle:** Create Dockerfile. Build and tag image, run container, map ports, inspect logs, stop/restart/remove containers, document container lifecycle. | ✅ Completed | [Task 11 Deliverable Doc](docs/task-11-docker-image-container-lifecycle.md), [Dockerfile](Dockerfile), [Compose](docker-compose.yml) |
| **Task 12** | **Jenkins-Docker Continuous Deployment:** Extend Jenkins pipeline to build versioned Docker image, publish to registry, deploy fresh container automatically after successful tests. | ⏳ Next | Versioned image, registry evidence, commit-to-container pipeline |
| **Task 13** | **Configuration Management Script:** Identify server prerequisites (packages, users, folders, files, ports, services). Create Ansible playbook or Puppet manifest to configure target node. | ⏳ Next | Config spec, Ansible playbook / Puppet manifest, execution log |
| **Task 14** | **Automated Provisioning and Reliability Validation:** Provision clean target environment using Ansible/Puppet. Deploy app, demonstrate idempotency, health check, test rollback/recovery. | ⏳ Next | Provisioned node, idempotency evidence, health-check, rollback demo |
| **Task 15** | **Final End-to-End Release, Documentation and Viva:** Complete DevOps workflow execution (Git ➔ Jenkins ➔ Selenium ➔ Docker ➔ Ansible). Technical docs, troubleshooting guide, viva presentation. | ⏳ Next | Final repository, live demo, complete report, presentation & viva |

---

## 🚀 Key Platform Features (Tasks 1 - 3 MVP Baseline)

1. **Farmer Self-Registration Form:**
   - Validates farmer name, 10-digit mobile number, village, district, land size, and selected training.
   - Prevents duplicate registrations for the same training module (`HTTP 409 Conflict`).
   - Generates a unique tracking registration ID (e.g., `REG-100201`).
2. **Instant Status Tracking:**
   - Allows farmers to track approval progress in real time using their Registration ID or Mobile Number.
   - Four-stage visual lifecycle: `Submitted` ➔ `Under Review` ➔ `Confirmed / Approved` ➔ `Training Completed`.
3. **Coordinator Approval & Attendance Console:**
   - Live KPI metric cards for pending, approved, and completed counts.
   - One-click **Approve**, **Reject**, or **Mark Attended** actions with immediate database state persistence.
4. **DevOps Actuator Healthcheck:**
   - Exposes `/actuator/health` and `/actuator/metrics` for container orchestration and uptime probes.

---

## 🛠️ Technology Stack

- **Runtime:** Java 21 LTS
- **Build Tool:** Apache Maven 3.9+
- **Application Framework:** Spring Boot 3.3.4
- **Web Server:** Embedded Apache Tomcat 10.1 (Configured port: `8085`)
- **Persistence:** Spring Data JPA with Hibernate ORM
- **Database:** Embedded H2 Database (In-Memory with web console at `/h2-console`)
- **Testing:** JUnit 5, Mockito, Spring Test MockMvc (10/10 automated tests passing)
- **Frontend:** Responsive HTML5, Modern CSS Variables, Vanilla ES6 JavaScript

---

## 💻 Local Quickstart Guide

### 1. Build & Run Tests
```bash
mvn clean test
```

### 2. Package Executable JAR
```bash
mvn clean package -DskipTests=false
```

### 3. Launch the Platform
```bash
# Option A: Run via Maven
mvn spring-boot:run

# Option B: Run packaged JAR
java -jar target/farmer-registration-platform-1.0.0.jar
```

### 4. Access URLs
- **Farmer Registration Portal:** `http://localhost:8085/`
- **Application Status Tracker:** `http://localhost:8085/tracker`
- **Coordinator Dashboard:** `http://localhost:8085/admin`
- **DevOps Health Check:** `http://localhost:8085/actuator/health`
- **H2 SQL Console:** `http://localhost:8085/h2-console` *(JDBC URL: `jdbc:h2:mem:farmerdb`, User: `sa`, Password: empty)*

---

## 📡 REST API Summary

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/trainings` | List all available training programs |
| `GET` | `/api/trainings/{id}` | Get training details & remaining capacity |
| `POST` | `/api/registrations` | Submit new farmer registration |
| `GET` | `/api/registrations` | List registrations (supports `?status=` and `?trainingId=`) |
| `GET` | `/api/registrations/track?query=...` | Track registration by ID or Mobile Number |
| `PATCH` | `/api/registrations/{id}/status` | Update application status (`APPROVED`, `REJECTED`, `COMPLETED`) |
| `GET` | `/api/registrations/metrics` | Retrieve dashboard KPI counts |
| `GET` | `/actuator/health` | DevOps system health probe |

---
*Developed by Rutuja Kamale (Roll Number: 23102A0028) for DevOps.*
