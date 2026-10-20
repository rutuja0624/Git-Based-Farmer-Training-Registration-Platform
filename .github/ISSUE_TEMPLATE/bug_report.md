---
name: Farmer Platform Bug Report
about: Report a defect in registration, validation, status tracking, or coordinator console
title: '[BUG] '
labels: bug
assignees: ''

---

### 🐛 Bug Summary
A clear and specific description of the issue encountered on the Farmer Training Registration Platform.

---

### 📍 Affected Workflow
- [ ] Farmer Self-Registration Form (`POST /api/registrations`)
- [ ] Mobile Number (10 digits) or Land Size Format Validation
- [ ] Duplicate Registration Check (`HTTP 409 Conflict`)
- [ ] Real-Time Status Tracking by Registration ID / Mobile (`/tracker`)
- [ ] Coordinator Approval / Rejection Workflow (`/admin`, `PATCH /api/registrations/{id}/status`)
- [ ] Participant Table Filtering & Roster Pagination
- [ ] Spring Boot Actuator Health Probe (`/actuator/health`)

---

### 🔁 Steps to Reproduce
1. Navigate to: `http://localhost:8085/[page]`
2. Enter the following test inputs:
   - Farmer Name: `Ramesh Patil`
   - Mobile: `9876543210`
   - Training Program: `Organic Farming & Soil Health Management`
3. Click on: `[Submit Application / Check Status / Approve]`
4. Notice the error / unexpected behavior.

---

### 🎯 Expected vs. Actual Behavior
- **Expected:** [e.g., Submitting duplicate phone for the same training returns a user-friendly alert with HTTP 409 Conflict]
- **Actual:** [e.g., Error message was unformatted or submission hung]

---

### 💻 Environment & Diagnostic Logs
- **Runtime:** Java 21 LTS | Apache Maven 3.9 | Spring Boot 3.3.4
- **Browser:** [e.g., Chrome 129 on Windows / Android]
- **Console / Terminal Log Snippet:**
```
[Paste relevant server stack trace or browser console error here]
```
