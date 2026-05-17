/**
 * BUDGET TRACKER — Full Logic
 * Connects to /api/budget/{customerId} backend.
 */

const CATEGORIES = [
  { id: 'catering', name: 'Food and catering',        icon: 'fa-utensils', color: '#f5c518', colorBg: '#fffbea' },
  { id: 'photo',    name: 'Photography',               icon: 'fa-camera',   color: '#3b82f6', colorBg: '#eff6ff' },
  { id: 'attire',   name: 'Jewellery and Attire',      icon: 'fa-gem',      color: '#ef4444', colorBg: '#fef2f2' },
  { id: 'music',    name: 'Wedding music and dancing', icon: 'fa-music',    color: '#8b5cf6', colorBg: '#f5f3ff' },
  { id: 'florist',  name: 'Florist',                   icon: 'fa-spa',      color: '#ec4899', colorBg: '#fdf2f8' },
  { id: 'gifts',    name: 'Gifts',                     icon: 'fa-gift',     color: '#22c55e', colorBg: '#f0fdf4' },
];

let customerId = null;
let plan = null;
let donutChart = null;

// ─── INIT ────────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async () => {
  const userStr = sessionStorage.getItem('dreamWeddingUser');
  if (!userStr) {
    showToast('Please sign in first.', 'error');
    setTimeout(() => window.location.href = 'login.html', 1500);
    return;
  }
  const user = JSON.parse(userStr);
  if (user.role !== 'CUSTOMER') {
    showToast('Only customers can use the budget planner.', 'error');
    return;
  }
  customerId = user.id;
  await loadPlan();

  // Expense form submit
  document.getElementById('expenseForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    await saveExpense();
  });
});

// ─── LOAD PLAN ────────────────────────────────────────────────────────────────
async function loadPlan() {
  showLoading(true);
  try {
    const res = await fetch(`/api/budget/${customerId}`);
    plan = await res.json();
    if (!plan.totalBudget || plan.totalBudget === 0) {
      // No budget set yet — redirect to setup
      showToast('Set up your budget first!', 'error');
      setTimeout(() => window.location.href = 'budget-calculator.html', 1500);
      return;
    }
    renderAll();
  } catch (e) {
    console.error(e);
    showToast('Failed to load budget. Try again.', 'error');
  }
  showLoading(false);
}

// ─── RENDER ALL ──────────────────────────────────────────────────────────────
function renderAll() {
  renderStats();
  renderDonut();
  renderCategories();
  renderExpenses();
  renderEstimates();
  renderOverBudgetAlert();
}

function renderStats() {
  const totalBudget = plan.totalBudget || 0;
  const totalSpent  = plan.totalSpent  || 0;
  const totalRem    = plan.totalRemaining || 0;
  const totalEst    = (plan.estimates || []).reduce((s, e) => s + e.estimatedAmount, 0);
  const pct = totalBudget > 0 ? Math.round(totalSpent / totalBudget * 100) : 0;

  document.getElementById('sTotalBudget').textContent = `Rs. ${totalBudget.toLocaleString()}`;
  document.getElementById('sTotalSpent').textContent  = `Rs. ${totalSpent.toLocaleString()}`;
  document.getElementById('sSpentPct').textContent    = `${pct}% of budget`;
  const remEl = document.getElementById('sRemaining');
  remEl.textContent = `Rs. ${Math.abs(totalRem).toLocaleString()}${totalRem < 0 ? ' OVER' : ''}`;
  remEl.className = `s-value ${totalRem < 0 ? 'red' : 'green'}`;
  document.getElementById('sEstimates').textContent = `Rs. ${totalEst.toLocaleString()}`;
}

function renderOverBudgetAlert() {
  const alert = document.getElementById('overBudgetAlert');
  const overCats = (plan.categories || []).filter(c => c.isOverBudget);
  if (plan.isOverBudget || overCats.length > 0) {
    alert.style.display = 'block';
    const names = overCats.map(c => c.categoryName).join(', ');
    document.getElementById('overBudgetMsg').textContent = overCats.length
      ? `Over-budget categories: ${names}`
      : `Total spending exceeds your master budget.`;
  } else {
    alert.style.display = 'none';
  }
}

function renderDonut() {
  const cats = plan.categories || [];
  const labels = cats.map(c => c.categoryName);
  const data   = cats.map(c => c.spentAmount || 0);
  const colors = ['#f5c518','#3b82f6','#ef4444','#8b5cf6','#ec4899','#22c55e'];

  const totalSpent = plan.totalSpent || 0;
  const totalBudget = plan.totalBudget || 1;
  const pct = Math.min(100, Math.round(totalSpent / totalBudget * 100));

  document.getElementById('dcVal').textContent = `${pct}%`;

  const ctx = document.getElementById('donutChart').getContext('2d');
  if (donutChart) donutChart.destroy();
  donutChart = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels,
      datasets: [{
        data: data.some(d => d > 0) ? data : [1],
        backgroundColor: data.some(d => d > 0) ? colors : ['#f0ece4'],
        borderWidth: 0,
        cutout: '78%'
      }]
    },
    options: {
      responsive: true, maintainAspectRatio: true,
      plugins: { legend: { display: false }, tooltip: {
        callbacks: { label: ctx => ` Rs. ${ctx.raw.toLocaleString()}` }
      }}
    }
  });

  // Legend
  const legend = document.getElementById('donutLegend');
  legend.innerHTML = cats.map((c, i) => `
    <div style="display:flex; align-items:center; gap:8px; margin-bottom:10px; font-size:13px;">
      <div style="width:12px; height:12px; border-radius:3px; background:${colors[i % colors.length]}; flex-shrink:0;"></div>
      <span style="flex:1; color:#555;">${c.categoryName}</span>
      <span style="font-weight:600; color:#1a1a1a;">Rs. ${(c.spentAmount || 0).toLocaleString()}</span>
    </div>
  `).join('');
}

function renderCategories() {
  const container = document.getElementById('categoryCards');
  const cats = plan.categories || [];
  if (cats.length === 0) {
    container.innerHTML = '<p class="text-muted text-center small py-3">No categories set. <a href="budget-calculator.html">Edit setup</a></p>';
    return;
  }
  container.innerHTML = cats.map(cat => {
    const pct = cat.percentUsed || 0;
    const overClass = cat.isOverBudget ? 'over' : '';
    const catMeta = CATEGORIES.find(c => c.name === cat.categoryName);
    const icon = catMeta?.icon || 'fa-tag';
    const iconColor = catMeta?.color || '#888';
    const iconBg = catMeta?.colorBg || '#f5f5f5';
    const estimateForCat = (plan.estimates || []).filter(e => e.categoryName === cat.categoryName);
    const totalEst = estimateForCat.reduce((s, e) => s + e.estimatedAmount, 0);
    return `
      <div class="cat-card ${overClass}">
        <div class="cat-header">
          <div class="cat-name">
            <div class="cat-icon" style="background:${iconBg}; color:${iconColor};"><i class="fa-solid ${icon}"></i></div>
            ${cat.categoryName}
          </div>
          <div class="cat-amounts">
            <div class="cat-amt-block">
              <span>Allocated</span>
              <strong>Rs. ${(cat.allocatedAmount || 0).toLocaleString()}</strong>
            </div>
            <div class="cat-amt-block">
              <span>Spent</span>
              <strong class="${cat.isOverBudget ? 'red' : ''}">Rs. ${(cat.spentAmount || 0).toLocaleString()}</strong>
            </div>
            <div class="cat-amt-block">
              <span>Left</span>
              <strong>Rs. ${Math.max(0, cat.remainingAmount || 0).toLocaleString()}</strong>
            </div>
          </div>
        </div>
        <div class="progress-bar-bg">
          <div class="progress-bar-fill ${cat.isOverBudget ? 'over' : ''}" style="width:${Math.min(100, pct)}%"></div>
        </div>
        <div class="cat-footer">
          <span>${pct}% used</span>
          <div style="display:flex; gap:6px; flex-wrap:wrap;">
            ${cat.isOverBudget ? '<span class="over-tag"><i class="fa-solid fa-triangle-exclamation me-1"></i>Over budget</span>' : ''}
            ${totalEst > 0 ? `<span class="estimate-tag"><i class="fa-solid fa-file-invoice-dollar me-1"></i>Est: Rs. ${totalEst.toLocaleString()}</span>` : ''}
          </div>
        </div>
      </div>`;
  }).join('');
}

function renderExpenses() {
  const tbody = document.getElementById('expenseTableBody');
  const exps = (plan.expenses || []).slice().sort((a, b) => (b.date || '').localeCompare(a.date || ''));
  if (exps.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-4"><i class="fa-solid fa-receipt me-2"></i>No expenses yet. Add your first one!</td></tr>';
    return;
  }
  tbody.innerHTML = exps.map(exp => `
    <tr>
      <td><span class="badge-cat">${exp.categoryName}</span></td>
      <td style="max-width:200px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap;" title="${exp.description}">${exp.description}</td>
      <td style="font-weight:700;">Rs. ${exp.amount.toLocaleString()}</td>
      <td style="white-space:nowrap;">
        <button class="btn-edit" onclick="openEditExpense('${exp.id}')" title="Edit"><i class="fa-solid fa-pen"></i></button>
        <button class="btn-del" onclick="deleteExpense('${exp.id}')" title="Delete"><i class="fa-solid fa-trash"></i></button>
      </td>
    </tr>`).join('');
}

function renderEstimates() {
  const container = document.getElementById('estimatesList');
  const ests = plan.estimates || [];
  if (ests.length === 0) {
    container.innerHTML = '<p class="text-muted text-center small py-2">No vendor estimates added yet.<br>Select a package from a vendor or hotel page.</p>';
    return;
  }
  container.innerHTML = ests.map(est => {
    const paidSoFar = (plan.expenses || [])
      .filter(e => e.estimateId === est.id)
      .reduce((s, e) => s + e.amount, 0);
    const leftOnEst = Math.max(0, est.estimatedAmount - paidSoFar);
    return `
      <div class="est-row">
        <div class="est-info">
          <div class="est-name">${est.vendorName}</div>
          <div class="est-meta">${est.packageName} • ${est.categoryName}</div>
          ${paidSoFar > 0 ? `<div style="font-size:11px; color:#27ae60; margin-top:3px;"><i class="fa-solid fa-circle-check me-1"></i>Paid: Rs. ${paidSoFar.toLocaleString()} / Left: Rs. ${leftOnEst.toLocaleString()}</div>` : ''}
        </div>
        <div style="display:flex; align-items:center; gap:10px;">
          <div class="est-amount">Rs. ${est.estimatedAmount.toLocaleString()}</div>
          <button class="btn-del" onclick="removeEstimate('${est.id}')" title="Remove estimate"><i class="fa-solid fa-xmark"></i></button>
        </div>
      </div>`;
  }).join('');
}

// ─── EXPENSE MODAL ───────────────────────────────────────────────────────────
function openAddExpense() {
  document.getElementById('expenseModalTitle').textContent = 'Add Expense';
  document.getElementById('editExpenseId').value = '';
  document.getElementById('expenseForm').reset();
  populateCategorySelect();
}

function openEditExpense(id) {
  const exp = (plan.expenses || []).find(e => e.id === id);
  if (!exp) return;
  document.getElementById('expenseModalTitle').textContent = 'Edit Expense';
  document.getElementById('editExpenseId').value = exp.id;
  populateCategorySelect(exp.categoryName);
  document.getElementById('expDesc').value = exp.description;
  document.getElementById('expAmt').value = exp.amount;
  const modal = new bootstrap.Modal(document.getElementById('expenseModal'));
  modal.show();
}

function populateCategorySelect(selected) {
  const sel = document.getElementById('expCat');
  const cats = (plan.categories && plan.categories.length > 0)
    ? plan.categories.map(c => c.categoryName)
    : CATEGORIES.map(c => c.name);
  sel.innerHTML = cats.map(c => `<option value="${c}" ${c === selected ? 'selected' : ''}>${c}</option>`).join('');
}

function populateEstimateSelect(selectedId) {
  const sel = document.getElementById('expEstimate');
  const ests = plan.estimates || [];
  sel.innerHTML = '<option value="">None</option>' +
    ests.map(e => `<option value="${e.id}" ${e.id === selectedId ? 'selected' : ''}>${e.vendorName} — ${e.packageName}</option>`).join('');
}

async function saveExpense() {
  const id       = document.getElementById('editExpenseId').value;
  const category = document.getElementById('expCat').value;
  const desc     = document.getElementById('expDesc').value.trim();
  const amount   = parseFloat(document.getElementById('expAmt').value);
  const date     = document.getElementById('expDate').value;
  const estId    = document.getElementById('expEstimate').value || null;

  if (!desc || amount <= 0) { showToast('Fill in all required fields.', 'error'); return; }

  const payload = { categoryName: category, description: desc, amount, date, estimateId: estId };
  showLoading(true);
  try {
    const url = id
      ? `/api/budget/${customerId}/expenses/${id}`
      : `/api/budget/${customerId}/expenses`;
    const method = id ? 'PUT' : 'POST';
    const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
    const data = await res.json();
    plan = data.plan;
    bootstrap.Modal.getInstance(document.getElementById('expenseModal')).hide();
    renderAll();
    showToast(id ? 'Expense updated!' : 'Expense added!', 'success');
  } catch (e) { showToast('Error saving expense.', 'error'); }
  showLoading(false);
}

async function deleteExpense(id) {
  if (!confirm('Delete this expense?')) return;
  showLoading(true);
  try {
    const res = await fetch(`/api/budget/${customerId}/expenses/${id}`, { method: 'DELETE' });
    const data = await res.json();
    plan = data.plan;
    renderAll();
    showToast('Expense deleted.', 'success');
  } catch(e) { showToast('Error deleting expense.', 'error'); }
  showLoading(false);
}

// ─── ESTIMATES ────────────────────────────────────────────────────────────────
async function removeEstimate(id) {
  if (!confirm('Remove this vendor estimate from your budget view?')) return;
  showLoading(true);
  try {
    const res = await fetch(`/api/budget/${customerId}/estimates/${id}`, { method: 'DELETE' });
    const data = await res.json();
    plan = data.plan;
    renderAll();
    showToast('Estimate removed.', 'success');
  } catch(e) { showToast('Error removing estimate.', 'error'); }
  showLoading(false);
}

// ─── FULL SUMMARY MODAL ───────────────────────────────────────────────────────
function showSummaryModal() {
  const body = document.getElementById('summaryBody');
  const totalBudget = plan.totalBudget || 0;
  const totalSpent  = plan.totalSpent  || 0;
  const totalEst    = (plan.estimates || []).reduce((s, e) => s + e.estimatedAmount, 0);

  let html = `
    <div style="background:linear-gradient(135deg,#1a1a1a,#2d2d2d); color:white; border-radius:16px; padding:20px 24px; margin-bottom:20px;">
      <div class="row text-center">
        <div class="col-4">
          <div style="font-size:11px; color:#aaa; letter-spacing:1px;">TOTAL BUDGET</div>
          <div style="font-family:'Playfair Display',serif; font-size:22px; margin-top:4px;">Rs. ${totalBudget.toLocaleString()}</div>
        </div>
        <div class="col-4">
          <div style="font-size:11px; color:#aaa; letter-spacing:1px;">TOTAL SPENT</div>
          <div style="font-family:'Playfair Display',serif; font-size:22px; margin-top:4px; color:${plan.isOverBudget ? '#ff6b6b' : '#69e09a'};">Rs. ${totalSpent.toLocaleString()}</div>
        </div>
        <div class="col-4">
          <div style="font-size:11px; color:#aaa; letter-spacing:1px;">REMAINING</div>
          <div style="font-family:'Playfair Display',serif; font-size:22px; margin-top:4px;">Rs. ${(plan.totalRemaining || 0).toLocaleString()}</div>
        </div>
      </div>
    </div>

    <h6 style="font-weight:700; font-size:14px; color:var(--muted); letter-spacing:1px; text-transform:uppercase; margin-bottom:14px;">Category Breakdown</h6>`;

  (plan.categories || []).forEach(cat => {
    const icon = CATEGORIES.find(c => c.name === cat.categoryName)?.icon || 'fa-tag';
    html += `
      <div class="summary-item">
        <div class="si-icon"><i class="fa-solid ${icon}"></i></div>
        <div class="si-info">
          <div class="si-name">${cat.categoryName}</div>
          <div class="si-meta">Allocated: Rs. ${(cat.allocatedAmount || 0).toLocaleString()} • ${cat.percentUsed || 0}% used</div>
        </div>
        <div>
          <div class="si-price ${cat.isOverBudget ? 'text-danger' : ''}">Rs. ${(cat.spentAmount || 0).toLocaleString()}</div>
          ${cat.isOverBudget ? '<div style="font-size:10px; color:#e53935; text-align:right;">OVER</div>' : ''}
        </div>
      </div>`;
  });

  if ((plan.estimates || []).length > 0) {
    html += `<h6 style="font-weight:700; font-size:14px; color:var(--muted); letter-spacing:1px; text-transform:uppercase; margin:20px 0 14px;">Vendor Estimates (Committed)</h6>`;
    (plan.estimates || []).forEach(est => {
      const paidSoFar = (plan.expenses || []).filter(e => e.estimateId === est.id).reduce((s, e) => s + e.amount, 0);
      html += `
        <div class="summary-item">
          <div class="si-icon"><i class="fa-solid fa-handshake"></i></div>
          <div class="si-info">
            <div class="si-name">${est.vendorName}</div>
            <div class="si-meta">${est.packageName} • ${est.categoryName} • Paid so far: Rs. ${paidSoFar.toLocaleString()}</div>
          </div>
          <div class="si-price">Rs. ${est.estimatedAmount.toLocaleString()}</div>
        </div>`;
    });
    html += `<div style="text-align:right; margin-top:12px; font-weight:700; color:var(--gold);">Total Committed: Rs. ${totalEst.toLocaleString()}</div>`;
  }

  body.innerHTML = html;
  new bootstrap.Modal(document.getElementById('summaryModal')).show();
}

// ─── GLOBAL: Add estimate from vendor/hotel page ──────────────────────────────
window.BudgetPlanner = {
  addEstimate: async function(vendorId, vendorName, packageName, categoryName, estimatedAmount, type) {
    const userStr = sessionStorage.getItem('dreamWeddingUser');
    if (!userStr) return;
    const user = JSON.parse(userStr);
    if (user.role !== 'CUSTOMER') return;

    try {
      const res = await fetch(`/api/budget/${user.id}/estimates`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ vendorId, vendorName, packageName, categoryName, estimatedAmount, type })
      });
      if (res.ok) return true;
    } catch(e) { console.error(e); }
    return false;
  }
};

// ─── HELPERS ─────────────────────────────────────────────────────────────────
function showToast(msg, type = 'success') {
  const el = document.getElementById('toastMsg');
  el.textContent = msg;
  el.className = `toast-msg ${type} show`;
  setTimeout(() => el.classList.remove('show'), 3000);
}
function showLoading(on) {
  document.getElementById('loadingOverlay').style.display = on ? 'flex' : 'none';
}
