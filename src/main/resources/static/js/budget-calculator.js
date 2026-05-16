/**
 * BUDGET CALCULATOR LOGIC
 * Handles the AI-inspired smart calculation and transitions to the dashboard.
 */

document.addEventListener('DOMContentLoaded', () => {
    // SECURITY: Ensure only CUSTOMERS can access
    const userStr = sessionStorage.getItem('dreamWeddingUser');
    if (!userStr) {
        window.location.href = '../signin.html';
        return;
    }

    const budgetInput = document.getElementById('totalBudget');
    const guestInput = document.getElementById('guestCount');
    const priorityBtns = document.querySelectorAll('.priority-btn');
    const aiMessage = document.getElementById('aiMessage');
    const form = document.getElementById('calculatorForm');
    
    let selectedPriorities = [];

    // Base Allocations
    const baseAllocations = {
        "Venue & Catering": 40,
        "Photography & Video": 10,
        "Attire & Beauty": 10,
        "Music & Entertainment": 10,
        "Flowers & Decor": 10,
        "Logistics & Misc": 15,
        "Contingency Buffer": 5
    };

    // Handle Priority Selection
    priorityBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            // Prevent default checkbox behavior temporarily to handle logic
            e.preventDefault(); 
            const checkbox = this.querySelector('input');
            const val = checkbox.value;

            if (selectedPriorities.includes(val)) {
                // Deselect
                selectedPriorities = selectedPriorities.filter(p => p !== val);
                this.classList.remove('selected');
                checkbox.checked = false;
            } else {
                // Select (Max 3)
                if (selectedPriorities.length < 3) {
                    selectedPriorities.push(val);
                    this.classList.add('selected');
                    checkbox.checked = true;
                } else {
                    // Flash warning
                    const warn = document.getElementById('priorityWarning');
                    warn.classList.remove('d-none');
                    setTimeout(() => warn.classList.add('d-none'), 2000);
                }
            }

            document.getElementById('priorityCount').textContent = `${selectedPriorities.length}/3`;
            updateLivePreview();
        });
    });

    // Handle Live Typing for Preview
    budgetInput.addEventListener('input', updateLivePreview);
    guestInput.addEventListener('input', updateLivePreview);

    function updateLivePreview() {
        const budget = parseFloat(budgetInput.value) || 0;
        const guests = parseInt(guestInput.value) || 0;

        if (budget > 0) {
            // Calculate Dynamic Percentages
            let currentAllocations = { ...baseAllocations };
            
            // Shift +5% to priorities, subtract from Misc
            selectedPriorities.forEach(p => {
                currentAllocations[p] += 5;
                currentAllocations["Logistics & Misc"] -= 5; 
            });

            // Update Buffer display
            const bufferAmt = (budget * (currentAllocations["Contingency Buffer"] / 100)).toFixed(0);
            document.getElementById('previewBuffer').textContent = `$${parseInt(bufferAmt).toLocaleString()}`;

            // Update Plate Price display
            if (guests > 0) {
                const venueBudget = budget * (currentAllocations["Venue & Catering"] / 100);
                const platePrice = (venueBudget / guests).toFixed(0);
                document.getElementById('previewPlatePrice').textContent = `$${parseInt(platePrice).toLocaleString()} / guest`;

                // AI Message Trigger
                if (platePrice < 50) {
                    aiMessage.innerHTML = `
                        "I noticed your <strong>Price Per Guest</strong> is currently $${platePrice}. This might be difficult to achieve with traditional full-service catering.<br><br>
                        <strong>Suggestion:</strong> Consider a buffet-style dinner, a cocktail reception with heavy appetizers, or reducing the guest list slightly to give you more breathing room."
                    `;
                } else {
                    aiMessage.innerHTML = `
                        "Great! Your <strong>Price Per Guest</strong> is $${platePrice}, which gives you solid options for catering.<br><br>
                        I have shifted funds into your top priorities and reserved $${parseInt(bufferAmt).toLocaleString()} for unexpected costs. Click Generate to build your plan."
                    `;
                }
            }
        }
    }

    // Form Submission: Generate Plan
    form.addEventListener('submit', (e) => {
        e.preventDefault();
        
        const budget = parseFloat(budgetInput.value);
        const guests = parseInt(guestInput.value);

        if(budget && guests) {
            // 1. Calculate final allocations
            let finalAllocations = { ...baseAllocations };
            selectedPriorities.forEach(p => {
                finalAllocations[p] += 5;
                finalAllocations["Logistics & Misc"] -= 5; 
            });

            // 2. Build initial expense items for the budget tracker
            let initialExpenses = [];
            let timeId = Date.now();
            
            Object.keys(finalAllocations).forEach((category, index) => {
                // Don't add Contingency as an actual expense, just leave it as unspent budget
                if(category !== "Contingency Buffer") {
                    const amount = budget * (finalAllocations[category] / 100);
                    initialExpenses.push({
                        id: timeId + index,
                        category: category,
                        note: `AI Allocated (${finalAllocations[category]}%)`,
                        amount: amount,
                        date: new Date().toLocaleDateString()
                    });
                }
            });

            // 3. Save to LocalStorage to feed into the customer dashboard
            localStorage.setItem('dreamWedding_totalBudget', budget.toString());
            localStorage.setItem('dreamWedding_expenses', JSON.stringify(initialExpenses));

            // 4. Redirect
            // Redirecting to customer-dashboard (Budget tab) where they can see the Tracker
            window.location.href = 'customer-dashboard.html?tab=budget';
        }
    });

});
