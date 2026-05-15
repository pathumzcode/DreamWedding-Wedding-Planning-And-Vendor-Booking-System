/**
 * BUDGET TRACKER LOGIC
 */
let totalBudget = 0;
let expenses = [];
let budgetChart = null;

document.addEventListener('DOMContentLoaded', function() {
    const userStr = sessionStorage.getItem('dreamWeddingUser');
    if (!userStr) {
        alert('Please sign in to plan your budget.');
        window.location.href = '../signin.html';
        return;
    }

    loadLocalData();
    renderDashboard();

    // Add Expense Form Listener
    document.getElementById('expenseForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const category = document.getElementById('expenseCategory').value;
        const note = document.getElementById('expenseNote').value;
        const amount = parseFloat(document.getElementById('expenseAmount').value);

        if (amount > 0) {
            expenses.push({ id: Date.now(), category, note, amount, date: new Date().toLocaleDateString() });
            saveLocalData();
            
            const modalEl = document.getElementById('addExpenseModal');
            const modal = bootstrap.Modal.getInstance(modalEl);
            if(modal) modal.hide();
            
            document.getElementById('expenseForm').reset();
            renderDashboard();
        }
    });
});

function loadLocalData() {
    const storedBudget = localStorage.getItem('dreamWedding_totalBudget');
    if(storedBudget) totalBudget = parseFloat(storedBudget);
    const storedExpenses = localStorage.getItem('dreamWedding_expenses');
    if(storedExpenses) expenses = JSON.parse(storedExpenses);
}

function saveLocalData() {
    localStorage.setItem('dreamWedding_totalBudget', totalBudget.toString());
    localStorage.setItem('dreamWedding_expenses', JSON.stringify(expenses));
}

function renderDashboard() {
    const totalSpent = expenses.reduce((sum, exp) => sum + exp.amount, 0);
    const remaining = Math.max(0, totalBudget - totalSpent);
    const allocatedPercent = totalBudget > 0 ? Math.round((totalSpent / totalBudget) * 100) : 0;

    document.getElementById('totalSpentText').textContent = `Rs. ${totalSpent.toLocaleString()}`;
    document.getElementById('totalBudgetText').textContent = `of Rs. ${totalBudget.toLocaleString()} spent`;
    document.getElementById('totalAllocatedPercent').textContent = `${allocatedPercent}% allocated`;

    updateChart(totalSpent, remaining);
    renderExpenseHistory();
}

function updateChart(spent, remaining) {
    const ctx = document.getElementById('totalBudgetChart').getContext('2d');
    if (spent === 0 && remaining === 0) remaining = 1; 

    if (budgetChart) budgetChart.destroy();

    budgetChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            datasets: [{
                data: [spent, remaining],
                backgroundColor: ['#6b4c3b', '#f0f0f0'],
                borderWidth: 0,
                cutout: '85%'
            }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            plugins: { tooltip: { enabled: true }, legend: { display: false } },
            animation: { animateScale: true, animateRotate: true }
        }
    });
}

function renderExpenseHistory() {
    const tbody = document.getElementById('expenseHistoryTable');
    if (expenses.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-4"><i class="fa-solid fa-receipt me-2"></i>No expenses added yet.</td></tr>';
        return;
    }

    tbody.innerHTML = expenses.sort((a,b) => b.id - a.id).map(exp => `
        <tr>
            <td class="fw-semibold" style="color: var(--primary-gold);">${exp.category}</td>
            <td class="text-muted">${exp.note}</td>
            <td class="fw-bold text-dark">Rs. ${exp.amount.toLocaleString()}</td>
            <td class="text-end">
                <button class="btn btn-sm btn-outline-danger border-0 rounded-circle" onclick="deleteExpense(${exp.id})" title="Delete">
                    <i class="fa-solid fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

window.deleteExpense = function(id) {
    if (confirm('Are you sure you want to delete this expense?')) {
        expenses = expenses.filter(e => e.id !== id);
        saveLocalData();
        renderDashboard();
    }
};
