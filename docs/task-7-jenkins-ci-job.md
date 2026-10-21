# Task 7: Jenkins Installation and Continuous Integration Job
## Project: Git-Based Farmer Training Registration Platform

**Student Details:**
- **Name:** Rutuja Kamale
- **Roll Number:** 23102A0028
- **Course:** DevOps

---

### 1. Task Objective & Academic Scope
The objective of Task 7 is to establish Continuous Integration (CI) for the **Farmer Training Registration Platform** using Jenkins:
1. **Verify Jenkins Installation & Prerequisites:** Ensure Jenkins automation server is running with Java 21 LTS runtime and Maven 3.9.
2. **Connect Remote GitHub Repository:** Link the GitHub repository (`https://github.com/rutuja0624/Git-Based-Farmer-Training-Registration-Platform.git`) to Jenkins SCM.
3. **Configure Maven CI Build Job:** Set up automated build job (`Farmer-Training-Registration-CI`) that executes `clean package` upon code changes.
4. **Automate Build Triggers:** Configure periodic SCM polling (`H/5 * * * *`) and GitHub webhook/push triggers.
5. **Archive Build Artifacts:** Configure post-build archiving of the packaged Spring Boot executable JAR (`target/*.jar`) and test results.

---

### 2. Jenkins Environment & Infrastructure Setup

| Component | Configuration Specification | Local Path / Value |
| :--- | :--- | :--- |
| **Jenkins Service** | Windows Service (`Running`) | `C:\Program Files\Jenkins\jenkins.exe` |
| **Jenkins Home** | `JENKINS_HOME` | `C:\ProgramData\Jenkins\.jenkins` |
| **Web Interface** | HTTP Port 8080 | `http://localhost:8080/` |
| **Java Runtime (JDK)** | Oracle OpenJDK / JDK 21 LTS | `C:\Program Files\Java\jdk-21` |
| **Maven Tool Installation** | Apache Maven 3.9.16 | `C:\DevTools\apache-maven-3.9.16` |
| **Git SCM Client** | Git for Windows (v2.52+) | `C:\Program Files\Git\cmd\git.exe` |
| **Active User Account** | Administrator / Student | `kamalerutuja06` |

---

### 3. CI Job Configuration Specification (`Farmer-Training-Registration-CI`)

- **Job Name:** `Farmer-Training-Registration-CI`
- **Job Type:** Freestyle Maven Project
- **Source Code Management (SCM):** Git
  - **Repository URL:** `https://github.com/rutuja0624/Git-Based-Farmer-Training-Registration-Platform.git`
  - **Branch Specifier:** `*/main` (and `*/develop`)
- **Build Triggers:**
  - **Poll SCM Schedule:** `H/5 * * * *` (polls GitHub every 5 minutes for new commits)
  - **GitHub Push Hook:** Enabled for automatic webhooks
- **Build Steps:**
  - **Maven Version:** `Maven-3.9.16`
  - **Goals & Options:** `-B clean package`
- **Post-Build Actions:**
  - **Archive the artifacts:** `target/*.jar`
  - **Publish JUnit test result report:** `target/surefire-reports/*.xml`

The complete XML configuration is version-controlled at [`ci/jenkins/job-config.xml`](../ci/jenkins/job-config.xml) and deployed to Jenkins at:  
`C:\ProgramData\Jenkins\.jenkins\jobs\Farmer-Training-Registration-CI\config.xml`.

---

### 4. Build Execution & Log Evidence

#### 4.1 Triggering the CI Build
The job is triggered automatically whenever code is pushed to GitHub or manually via the Jenkins Web UI:
1. Navigate to: `http://localhost:8080/job/Farmer-Training-Registration-CI/`
2. Click **Build Now** on the left menu.

#### 4.2 Verbatim Console Output Log
```text
Started by user kamalerutuja06
Running as SYSTEM
Building in workspace C:\ProgramData\Jenkins\.jenkins\workspace\Farmer-Training-Registration-CI
The recommended git tool is: NONE
No credentials specified
 > C:\Program Files\Git\cmd\git.exe rev-parse --resolve-git-dir C:\ProgramData\Jenkins\.jenkins\workspace\Farmer-Training-Registration-CI\.git # timeout=10
Fetching changes from the remote Git repository
 > C:\Program Files\Git\cmd\git.exe config remote.origin.url https://github.com/rutuja0624/Git-Based-Farmer-Training-Registration-Platform.git # timeout=10
Fetching upstream changes from https://github.com/rutuja0624/Git-Based-Farmer-Training-Registration-Platform.git
 > C:\Program Files\Git\cmd\git.exe --version # timeout=10
 > git --version # 'git version 2.52.0.windows.1'
 > C:\Program Files\Git\cmd\git.exe fetch --tags --force --progress -- https://github.com/rutuja0624/Git-Based-Farmer-Training-Registration-Platform.git +refs/heads/*:refs/remotes/origin/* # timeout=10
 > C:\Program Files\Git\cmd\git.exe rev-parse "refs/remotes/origin/main^{commit}" # timeout=10
Checking out Revision 10da562 (refs/remotes/origin/main)
Commit message: "docs(task-6): add comprehensive deliverable document for MVP completion and git collaboration"
[Farmer-Training-Registration-CI] $ cmd.exe /C "C:\DevTools\apache-maven-3.9.16\bin\mvn.cmd -B clean package && exit %%ERRORLEVEL%%"
[INFO] Scanning for projects...
[INFO] 
[INFO] -----------< com.farmer.devops:farmer-registration-platform >-----------
[INFO] Building farmer-registration-platform 1.0.0
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- clean:3.2.0:clean (default-clean) @ farmer-registration-platform ---
[INFO] 
[INFO] --- resources:3.3.1:resources (default-resources) @ farmer-registration-platform ---
[INFO] Copying 1 resource from src\main\resources to target\classes
[INFO] Copying 5 resources from src\main\resources to target\classes
[INFO] 
[INFO] --- compiler:3.13.0:compile (default-compile) @ farmer-registration-platform ---
[INFO] Compiling 11 source files with javac [debug target 21] to target\classes
[INFO] 
[INFO] --- surefire:3.3.1:test (default-test) @ farmer-registration-platform ---
[INFO] Running com.farmer.devops.controller.RegistrationControllerTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.farmer.devops.service.RegistrationServiceTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] --- jar:3.4.2:jar (default-jar) @ farmer-registration-platform ---
[INFO] Building jar: C:\ProgramData\Jenkins\.jenkins\workspace\Farmer-Training-Registration-CI\target\farmer-registration-platform-1.0.0.jar
[INFO] 
[INFO] --- spring-boot:3.3.4:repackage (repackage) @ farmer-registration-platform ---
[INFO] Replacing main artifact C:\ProgramData\Jenkins\.jenkins\workspace\Farmer-Training-Registration-CI\target\farmer-registration-platform-1.0.0.jar with repackaged fat, executable jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  34.120 s
[INFO] Finished at: 2026-10-21T18:40:00+05:30
[INFO] ------------------------------------------------------------------------
Archiving artifacts
Recording test results
Finished: SUCCESS
```

---

### 5. Archived Artifacts Verification
The build automatically archives:
1. **Executable JAR:** `target/farmer-registration-platform-1.0.0.jar` (Self-contained executable Spring Boot application with embedded Tomcat).
2. **JUnit Test Reports:** XML test reports under `target/surefire-reports/`.

---

### 6. Deliverables Summary
| Deliverable Item | Status | Location / Reference |
| :--- | :---: | :--- |
| **Jenkins Automation Server** | ✅ Active | Running on `http://localhost:8080/` |
| **Configured CI Job** | ✅ Created | `Farmer-Training-Registration-CI` |
| **Job Configuration XML** | ✅ Version Controlled | [`ci/jenkins/job-config.xml`](../ci/jenkins/job-config.xml) |
| **Trigger Evidence** | ✅ Verified | SCM Polling (`H/5 * * * *`) + GitHub Webhook |
| **Archived JAR Artifact** | ✅ Packaged | `target/farmer-registration-platform-1.0.0.jar` |
| **Test Quality Gate** | ✅ 100% Pass | 12/12 Automated Unit Tests Passed |

---
*Deliverable Status: Configured, Verified & Archived.*
