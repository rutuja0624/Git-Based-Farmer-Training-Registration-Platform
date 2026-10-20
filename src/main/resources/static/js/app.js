// Farmer Training Registration Platform - Client Logic
const API_BASE = '/api';

// --- Toast Notifications ---
function showToast(message, type = 'success') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast ${type === 'error' ? 'toast-error' : ''}`;
  const icon = type === 'error' ? '⚠️' : '✅';
  toast.innerHTML = `<span>${icon}</span> <div>${message}</div>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// --- Home & Registration Page Logic ---
let availableTrainings = [];

async function loadTrainings() {
  const select = document.getElementById('trainingSelect');
  if (!select) return;

  try {
    const res = await fetch(`${API_BASE}/trainings`);
    const result = await res.json();
    if (result.success && result.data) {
      availableTrainings = result.data;
      select.innerHTML = '<option value="">-- Choose an Agricultural Workshop --</option>';
      availableTrainings.forEach(t => {
        const opt = document.createElement('option');
        opt.value = t.id;
        opt.textContent = `${t.title} (${t.category})`;
        select.appendChild(opt);
      });
    }
  } catch (err) {
    console.error('Error loading trainings:', err);
    showToast('Failed to load training programs. Please try again.', 'error');
  }
}

function handleTrainingSelection() {
  const select = document.getElementById('trainingSelect');
  const preview = document.getElementById('trainingPreview');
  if (!select || !preview) return;

  const trainingId = select.value;
  if (!trainingId) {
    preview.classList.remove('active');
    return;
  }

  const training = availableTrainings.find(t => t.id == trainingId);
  if (training) {
    document.getElementById('prevTitle').textContent = training.title;
    document.getElementById('prevInstructor').textContent = training.instructor;
    document.getElementById('prevLocation').textContent = training.location;
    document.getElementById('prevDate').textContent = training.trainingDate;
    document.getElementById('prevDesc').textContent = training.description;
    preview.classList.add('active');
  }
}

async function handleRegistrationSubmit(event) {
  event.preventDefault();

  const fullName = document.getElementById('fullName').value.trim();
  const mobileNumber = document.getElementById('mobileNumber').value.trim();
  const email = document.getElementById('email').value.trim();
  const village = document.getElementById('village').value.trim();
  const district = document.getElementById('district').value.trim();
  const landSizeAcres = parseFloat(document.getElementById('landSize').value);
  const trainingId = document.getElementById('trainingSelect').value;

  // Client Validation
  if (!fullName || !mobileNumber || !village || !district || !trainingId || isNaN(landSizeAcres)) {
    showToast('Please fill all mandatory fields.', 'error');
    return;
  }

  if (!/^[0-9]{10}$/.test(mobileNumber)) {
    showToast('Mobile number must be exactly 10 digits.', 'error');
    return;
  }

  if (landSizeAcres <= 0) {
    showToast('Land size must be a positive number of acres.', 'error');
    return;
  }

  const payload = {
    fullName,
    mobileNumber,
    email: email || null,
    village,
    district,
    landSizeAcres,
    trainingId: parseInt(trainingId)
  };

  const submitBtn = document.getElementById('submitBtn');
  submitBtn.disabled = true;
  submitBtn.textContent = 'Submitting Application...';

  try {
    const res = await fetch(`${API_BASE}/registrations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const result = await res.json();
    if (res.ok && result.success) {
      document.getElementById('regForm').reset();
      document.getElementById('trainingPreview').classList.remove('active');
      openSuccessModal(result.data.registrationCode, result.data.fullName);
    } else {
      showToast(result.message || 'Registration failed.', 'error');
    }
  } catch (err) {
    console.error('Registration error:', err);
    showToast('Connection error. Could not reach server.', 'error');
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = 'Submit Registration';
  }
}

function openSuccessModal(regCode, name) {
  const modal = document.getElementById('successModal');
  if (!modal) return;
  document.getElementById('modalCode').textContent = regCode;
  document.getElementById('modalFarmerName').textContent = name;
  modal.classList.add('active');
}

function closeSuccessModal() {
  const modal = document.getElementById('successModal');
  if (modal) modal.classList.remove('active');
}

function copyRegistrationCode() {
  const code = document.getElementById('modalCode').textContent;
  navigator.clipboard.writeText(code).then(() => {
    showToast('Registration Code copied to clipboard!');
  });
}

// --- Status Tracker Page Logic ---
async function trackRegistrationStatus(queryInput) {
  const query = queryInput || document.getElementById('trackQuery').value.trim();
  const resultsContainer = document.getElementById('trackerResults');
  if (!resultsContainer) return;

  if (!query) {
    showToast('Please enter your Registration ID or Mobile Number.', 'error');
    return;
  }

  resultsContainer.innerHTML = '<div style="text-align:center; padding:2rem;">Searching for registration record...</div>';

  try {
    const res = await fetch(`${API_BASE}/registrations/track?query=${encodeURIComponent(query)}`);
    const result = await res.json();

    if (res.ok && result.success && result.data.length > 0) {
      renderTrackerResults(result.data);
    } else {
      resultsContainer.innerHTML = `
        <div class="card" style="text-align:center; padding:3rem 1.5rem;">
          <div style="font-size:3rem; margin-bottom:1rem;">🔍</div>
          <h3>No Registration Record Found</h3>
          <p style="color:var(--text-muted); margin-top:0.5rem;">No application was found matching "${query}". Please verify your Registration ID or 10-digit mobile number.</p>
        </div>
      `;
    }
  } catch (err) {
    console.error('Track error:', err);
    resultsContainer.innerHTML = '<div class="card" style="text-align:center; color:var(--danger); padding:2rem;">Error connecting to server. Please try again.</div>';
  }
}

function renderTrackerResults(records) {
  const resultsContainer = document.getElementById('trackerResults');
  resultsContainer.innerHTML = '';

  records.forEach(reg => {
    const badgeClass = `badge-${reg.status.toLowerCase()}`;
    const formattedDate = new Date(reg.registeredAt).toLocaleString('en-US', {
      dateStyle: 'medium',
      timeStyle: 'short'
    });

    const isPending = reg.status === 'PENDING';
    const isApproved = reg.status === 'APPROVED';
    const isCompleted = reg.status === 'COMPLETED';
    const isRejected = reg.status === 'REJECTED';

    let timelineHtml = '';
    if (isRejected) {
      timelineHtml = `
        <div style="background:var(--danger-light); color:var(--danger); padding:1rem; border-radius:var(--radius-md); margin: 1.5rem 0; font-weight:600;">
          ❌ Application Status: REJECTED ${reg.remarks ? `(${reg.remarks})` : ''}
        </div>
      `;
    } else {
      timelineHtml = `
        <div class="timeline">
          <div class="timeline-step completed">
            <div class="timeline-circle">✓</div>
            <div class="timeline-label">Submitted</div>
          </div>
          <div class="timeline-step ${isPending ? 'active' : 'completed'}">
            <div class="timeline-circle">${isPending ? '⏳' : '✓'}</div>
            <div class="timeline-label">Under Review</div>
          </div>
          <div class="timeline-step ${isApproved ? 'completed' : isCompleted ? 'completed' : ''}">
            <div class="timeline-circle">${isApproved || isCompleted ? '✓' : '3'}</div>
            <div class="timeline-label">Confirmed</div>
          </div>
          <div class="timeline-step ${isCompleted ? 'completed' : ''}">
            <div class="timeline-circle">${isCompleted ? '🎓' : '4'}</div>
            <div class="timeline-label">Completed</div>
          </div>
        </div>
      `;
    }

    const card = document.createElement('div');
    card.className = 'card';
    card.style.marginBottom = '1.5rem';
    card.innerHTML = `
      <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:0.5rem; margin-bottom:1rem; border-bottom:1px solid var(--border); padding-bottom:1rem;">
        <div>
          <span style="font-size:0.85rem; color:var(--text-muted); font-weight:600;">REGISTRATION ID</span>
          <h2 style="color:var(--primary); font-family:monospace; font-size:1.5rem;">${reg.registrationCode}</h2>
        </div>
        <span class="badge ${badgeClass}">${reg.status}</span>
      </div>

      ${timelineHtml}

      <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap:1.25rem; margin-top:1.5rem;">
        <div>
          <div style="font-size:0.8rem; color:var(--text-muted);">Farmer Name</div>
          <div style="font-weight:700; font-size:1.05rem;">${reg.fullName}</div>
        </div>
        <div>
          <div style="font-size:0.8rem; color:var(--text-muted);">Mobile Number</div>
          <div style="font-weight:600;">${reg.mobileNumber}</div>
        </div>
        <div>
          <div style="font-size:0.8rem; color:var(--text-muted);">Village & District</div>
          <div style="font-weight:600;">${reg.village}, ${reg.district}</div>
        </div>
        <div>
          <div style="font-size:0.8rem; color:var(--text-muted);">Registered Landholding</div>
          <div style="font-weight:600;">${reg.landSizeAcres} Acres <span class="badge" style="background:#e0f2fe; color:#0369a1; font-size:0.72rem; margin-left:0.3rem;">${reg.farmerCategory || 'SMALLHOLDER'}</span></div>
        </div>
        <div>
          <div style="font-size:0.8rem; color:var(--text-muted);">Applied Workshop</div>
          <div style="font-weight:700; color:var(--primary);">${reg.trainingProgram.title}</div>
        </div>
        <div>
          <div style="font-size:0.8rem; color:var(--text-muted);">Date & Time</div>
          <div style="font-weight:600;">${reg.trainingProgram.trainingDate}</div>
        </div>
        <div>
          <div style="font-size:0.8rem; color:var(--text-muted);">Venue Location</div>
          <div style="font-weight:600;">${reg.trainingProgram.location}</div>
        </div>
        <div>
          <div style="font-size:0.8rem; color:var(--text-muted);">Application Timestamp</div>
          <div style="font-weight:600;">${formattedDate}</div>
        </div>
      </div>
      ${reg.remarks ? `<div style="margin-top:1.25rem; font-size:0.88rem; background:var(--surface-alt); padding:0.6rem 0.9rem; border-radius:var(--radius-sm);"><strong>Coordinator Note:</strong> ${reg.remarks}</div>` : ''}
    `;
    resultsContainer.appendChild(card);
  });
}

// --- Coordinator Admin Dashboard Logic ---
async function loadAdminDashboard() {
  await Promise.all([loadAdminMetrics(), loadAdminRegistrations()]);
}

async function loadAdminMetrics() {
  try {
    const res = await fetch(`${API_BASE}/registrations/metrics`);
    const result = await res.json();
    if (result.success && result.data) {
      document.getElementById('metricTotal').textContent = result.data.total;
      document.getElementById('metricPending').textContent = result.data.pending;
      document.getElementById('metricApproved').textContent = result.data.approved;
      document.getElementById('metricCompleted').textContent = result.data.completed;
    }
  } catch (err) {
    console.error('Error fetching metrics:', err);
  }
}

async function loadAdminRegistrations() {
  const tableBody = document.getElementById('adminTableBody');
  const statusFilter = document.getElementById('filterStatus') ? document.getElementById('filterStatus').value : '';
  if (!tableBody) return;

  tableBody.innerHTML = '<tr><td colspan="7" style="text-align:center; padding:2rem;">Loading participant roster...</td></tr>';

  try {
    let url = `${API_BASE}/registrations`;
    if (statusFilter) {
      url += `?status=${statusFilter}`;
    }

    const res = await fetch(url);
    const result = await res.json();

    if (result.success && result.data) {
      const records = result.data;
      if (records.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="7" style="text-align:center; padding:2rem; color:var(--text-muted);">No registration records found for this filter.</td></tr>';
        return;
      }

      tableBody.innerHTML = '';
      records.forEach(reg => {
        const tr = document.createElement('tr');
        const badgeClass = `badge-${reg.status.toLowerCase()}`;
        const canApprove = reg.status === 'PENDING';
        const canComplete = reg.status === 'APPROVED';

        tr.innerHTML = `
          <td><strong>${reg.registrationCode}</strong></td>
          <td>
            <strong>${reg.fullName}</strong><br>
            <small style="color:var(--text-muted);">${reg.mobileNumber}</small>
          </td>
          <td>${reg.village}, ${reg.district}<br><small>${reg.landSizeAcres} Acres <span class="badge" style="background:#e0f2fe; color:#0369a1; font-size:0.7rem; padding:0.12rem 0.45rem;">${reg.farmerCategory || 'SMALLHOLDER'}</span></small></td>
          <td>
            <span style="font-weight:600; color:var(--text-main);">${reg.trainingProgram.title}</span><br>
            <small style="color:var(--text-muted);">${reg.trainingProgram.trainingDate}</small>
          </td>
          <td><span class="badge ${badgeClass}">${reg.status}</span></td>
          <td style="font-size:0.85rem;">${new Date(reg.registeredAt).toLocaleDateString()}</td>
          <td>
            <div style="display:flex; gap:0.4rem; flex-wrap:wrap;">
              ${canApprove ? `
                <button class="btn btn-sm btn-success" onclick="updateRegistrationStatus(${reg.id}, 'APPROVED', 'Application confirmed by coordinator')">Approve</button>
                <button class="btn btn-sm btn-danger" onclick="updateRegistrationStatus(${reg.id}, 'REJECTED', 'Batch capacity reached or criteria unmet')">Reject</button>
              ` : ''}
              ${canComplete ? `
                <button class="btn btn-sm btn-primary" onclick="updateRegistrationStatus(${reg.id}, 'COMPLETED', 'Attended training session')">Mark Attended</button>
              ` : ''}
              ${!canApprove && !canComplete ? `<span style="color:var(--text-muted); font-size:0.8rem;">No actions</span>` : ''}
            </div>
          </td>
        `;
        tableBody.appendChild(tr);
      });
    }
  } catch (err) {
    console.error('Error loading admin registrations:', err);
    tableBody.innerHTML = '<tr><td colspan="7" style="text-align:center; color:var(--danger); padding:2rem;">Failed to load registrations.</td></tr>';
  }
}

async function updateRegistrationStatus(id, newStatus, remarks) {
  try {
    const res = await fetch(`${API_BASE}/registrations/${id}/status`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status: newStatus, remarks })
    });

    const result = await res.json();
    if (res.ok && result.success) {
      showToast(`Registration marked as ${newStatus}!`);
      loadAdminDashboard();
    } else {
      showToast(result.message || 'Status update failed.', 'error');
    }
  } catch (err) {
    console.error('Update status error:', err);
    showToast('Failed to update status.', 'error');
  }
}

// Global initialization
document.addEventListener('DOMContentLoaded', () => {
  // If on index page
  if (document.getElementById('regForm')) {
    loadTrainings();
    document.getElementById('trainingSelect').addEventListener('change', handleTrainingSelection);
    document.getElementById('regForm').addEventListener('submit', handleRegistrationSubmit);
  }

  // If on tracker page
  if (document.getElementById('trackBtn')) {
    document.getElementById('trackBtn').addEventListener('click', () => trackRegistrationStatus());
    document.getElementById('trackQuery').addEventListener('keypress', (e) => {
      if (e.key === 'Enter') trackRegistrationStatus();
    });

    // Check if query param exists in URL
    const urlParams = new URLSearchParams(window.location.search);
    const q = urlParams.get('query');
    if (q) {
      document.getElementById('trackQuery').value = q;
      trackRegistrationStatus(q);
    }
  }

  // If on admin page
  if (document.getElementById('adminTableBody')) {
    loadAdminDashboard();
    document.getElementById('filterStatus').addEventListener('change', loadAdminRegistrations);
  }
});
