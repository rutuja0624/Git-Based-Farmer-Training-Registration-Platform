# Branch Naming Rules & Git Branch Policy
## Project: Git-Based Farmer Training Registration Platform
**Author:** Rutuja Kamale (Student ID: 23102A0028, Roll: 27)

---

### 1. Purpose
This policy sets the branch naming standards, lifecycle rules, pull request requirements, and merge procedures for the **Git-Based Farmer Training Registration Platform** repository.

---

### 2. Core Branches

| Branch | Purpose | Protection Rules |
| :--- | :--- | :--- |
| **`main`** | **Production Baseline:** Holds the release-ready, fully tested code. | Protected. Direct pushes prohibited in team workflows. Requires pull request review. |
| **`develop`** | **Integration Baseline:** Active integration branch where features are consolidated before release. | Protected. All automated tests must pass before merging. |

---

### 3. Supporting Branch Naming Conventions

All temporary branches must follow the strict prefix naming format: `<category>/<short-description>`:

#### 3.1 Feature Branches (`feature/`)
Used for developing new user stories and curriculum tasks.
- Format: `feature/<task-number>-<short-description>` or `feature/<feature-name>`
- Base branch: `develop`
- Merge target: `develop`
- **Examples:**
  - `feature/task-5-farmer-registration-form`
  - `feature/task-6-approval-and-tracking`
  - `feature/mobile-validation`

#### 3.2 Bugfix Branches (`bugfix/`)
Used to fix non-critical bugs found during integration testing.
- Format: `bugfix/<issue-number>-<short-description>`
- Base branch: `develop`
- Merge target: `develop`
- **Example:** `bugfix/fix-mobile-regex-validation`

#### 3.3 Release Branches (`release/`)
Used to prepare and harden a production release (version bumping, final docs).
- Format: `release/v<major>.<minor>.<patch>`
- Base branch: `develop`
- Merge targets: `main` (with release tag) AND `develop`
- **Example:** `release/v1.0.0`

#### 3.4 Hotfix Branches (`hotfix/`)
Used for urgent production fixes.
- Format: `hotfix/<short-description>`
- Base branch: `main`
- Merge targets: `main` (with patch tag) AND `develop`
- **Example:** `hotfix/h2-connection-leak`

---

### 4. Commit Message Standard (Conventional Commits)
Commits must be meaningful and adhere to the format:
`<type>(<scope>): <imperative description>`

**Allowed types:**
- `feat`: New feature or user story
- `fix`: Bug fix
- `docs`: Documentation updates
- `test`: Automated unit, integration, or Selenium test cases
- `ci`: CI/CD pipelines (Jenkins, GitHub Actions)
- `build`: Maven dependencies or build config changes
- `refactor`: Code refactoring without changing functionality
- `chore`: Maintenance tasks

---

### 5. Pull Request (PR) & Merge Rules
1. **Title:** Must mirror the Conventional Commit format (e.g., `feat(task-5): implement farmer registration workflow`).
2. **Review:** At least 1 peer or agent approval required.
3. **CI Status:** Build, compile, and automated tests must exit with code 0 before merge.
4. **Merge Method:**
   - Squash and Merge for small feature branches.
   - Standard Merge commit for release branches to preserve release history.
