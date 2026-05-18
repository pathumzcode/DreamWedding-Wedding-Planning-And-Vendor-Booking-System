// Global Marquee Utility for high-end scrolling sections
const setupExploreStyleMarquee = (marqueeId, autoSpeed = 1) => {
  const slider = document.getElementById(marqueeId);
  if (!slider) return;
  const wrapper = slider.parentElement;

  // Triple content for seamless infinite looping
  const itemsHTML = slider.innerHTML;
  slider.innerHTML = itemsHTML + itemsHTML + itemsHTML;

  let targetScroll = 0;
  let currentScroll = 0;
  let isPaused = false;
  const lerp = 0.45;

  setTimeout(() => {
    const item = slider.firstElementChild;
    if (!item) return;
    const gap = parseFloat(window.getComputedStyle(slider).gap) || 0;
    const totalWidth = (slider.children.length / 3) * (item.offsetWidth + gap);
    currentScroll = targetScroll = totalWidth;
    wrapper.scrollLeft = totalWidth;
  }, 100);

  const animate = () => {
    if (isPaused) {
      requestAnimationFrame(animate);
      return;
    }

    const item = slider.firstElementChild;
    if (!item) return;
    const gap = parseFloat(window.getComputedStyle(slider).gap) || 0;
    const totalWidth = (slider.children.length / 3) * (item.offsetWidth + gap);

    targetScroll += autoSpeed;

    if (targetScroll >= totalWidth * 2) {
      targetScroll -= totalWidth;
      currentScroll -= totalWidth;
    } else if (targetScroll <= 0) {
      targetScroll += totalWidth;
      currentScroll += totalWidth;
    }

    currentScroll += (targetScroll - currentScroll) * lerp;
    wrapper.scrollLeft = currentScroll;
    
    requestAnimationFrame(animate);
  };

  wrapper.addEventListener('mouseenter', () => isPaused = true);
  wrapper.addEventListener('mouseleave', () => isPaused = false);

  wrapper.addEventListener('mousemove', (e) => {
    if (isPaused) return;
    const mouseX = e.pageX - wrapper.offsetLeft;
    const midX = wrapper.clientWidth / 2;
    const delta = (mouseX - midX) / midX; 
    targetScroll -= delta * 20; 
  });

  wrapper.addEventListener('touchmove', (e) => {
    const touchX = e.touches[0].pageX - wrapper.offsetLeft;
    const midX = wrapper.clientWidth / 2;
    const delta = (touchX - midX) / midX;
    targetScroll -= delta * 20;
  }, { passive: true });

  requestAnimationFrame(animate);
};

// FAQ Tab Switcher
function switchFaqTab(tab, btn) {
  // Close any open accordion items in ALL panels before switching
  document.querySelectorAll('.faq-panel .accordion-collapse.show').forEach(openItem => {
    const bsCollapse = bootstrap.Collapse.getInstance(openItem);
    if (bsCollapse) {
      bsCollapse.hide();
    } else {
      openItem.classList.remove('show');
    }
    // Also reset the button arrow state
    const toggleBtn = openItem.previousElementSibling?.querySelector('.accordion-button');
    if (toggleBtn) toggleBtn.classList.add('collapsed');
  });

  // Hide all panels
  document.querySelectorAll('.faq-panel').forEach(p => p.classList.add('d-none'));
  // Show selected panel
  const panel = document.getElementById('faq-' + tab);
  if (panel) panel.classList.remove('d-none');

  // Reset all tab buttons
  document.querySelectorAll('.faq-tab').forEach(b => {
    b.style.backgroundColor = 'transparent';
    b.style.color = '#f0ebe1';
    b.style.border = '2px solid rgba(255,255,255,0.3)';
  });
  // Highlight active tab
  btn.style.backgroundColor = 'var(--primary-color)';
  btn.style.color = 'white';
  btn.style.border = 'none';
}

// Auto-close FAQ on Scroll Away
window.addEventListener('scroll', () => {
  const faqSection = document.querySelector('section[style*="background: linear-gradient"]'); // Finding FAQ by unique style
  if (!faqSection) return;

  const rect = faqSection.getBoundingClientRect();
  // If the FAQ section is far enough out of view, close open items
  if (rect.bottom < 0 || rect.top > window.innerHeight) {
    document.querySelectorAll('.faq-panel .accordion-collapse.show').forEach(openItem => {
      const bsCollapse = bootstrap.Collapse.getInstance(openItem);
      if (bsCollapse) bsCollapse.hide();
    });
  }
}, { passive: true });

document.addEventListener("DOMContentLoaded", () => {
  // Form Validation Logic
  const forms = document.querySelectorAll('.needs-validation')

  Array.from(forms).forEach(form => {
    form.addEventListener('submit', event => {
      if (!form.checkValidity()) {
        event.preventDefault()
        event.stopPropagation()
      }
      form.classList.add('was-validated')
    }, false)
  })

  // Smooth Scrolling for anchor links (if any)
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
      e.preventDefault();
      const targetElement = document.querySelector(this.getAttribute('href'));
      if (targetElement) {
        targetElement.scrollIntoView({
          behavior: 'smooth'
        });
      }
    });
  });

  // Example: simple password match validation on Registration form
  const registerForm = document.getElementById("registerForm");
  if (registerForm) {
    const pwd = document.getElementById("password");
    const confirmPwd = document.getElementById("confirmPassword");

    registerForm.addEventListener("submit", (e) => {
      if (pwd.value !== confirmPwd.value) {
        e.preventDefault();
        confirmPwd.setCustomValidity("Passwords do not match");
        confirmPwd.reportValidity();
      } else {
        confirmPwd.setCustomValidity("");
      }
    });

    confirmPwd.addEventListener('input', () => {
      if (pwd.value === confirmPwd.value) {
        confirmPwd.setCustomValidity("");
      } else {
        confirmPwd.setCustomValidity("Passwords do not match");
      }
    });
  }

  // Example: Display selected rating on Review pages
  const ratingStars = document.querySelectorAll('.star-rating i');
  let currentRating = 0;
  
  ratingStars.forEach((star, index) => {
    star.addEventListener('mouseover', () => {
      highlightStars(index + 1);
    });
    
    star.addEventListener('mouseout', () => {
      highlightStars(currentRating);
    });
    
    star.addEventListener('click', () => {
      currentRating = index + 1;
      const ratingInput = document.getElementById('ratingValue');
      if (ratingInput) ratingInput.value = currentRating;
    });
  });

  function highlightStars(count) {
    ratingStars.forEach((star, i) => {
      if (i < count) {
        star.classList.remove('far');
        star.classList.add('fas');
      } else {
        star.classList.remove('fas');
        star.classList.add('far');
      }
    });
  }

  // Horizontal Category Slider Logic
  const slider = document.getElementById('categorySlider');
  if (slider) {
    // Triple clones for infinite visual continuity
    const itemsHTML = slider.innerHTML;
    slider.innerHTML = itemsHTML + itemsHTML + itemsHTML;

    let targetScroll = 0;
    let currentScroll = 0;
    const scrollSpeed = 0.4; // Faster response (was 0.15)
    
    // Initial jump to center
    setTimeout(() => {
      const item = slider.firstElementChild;
      const gap = parseFloat(window.getComputedStyle(slider).gap) || 0;
      const totalWidth = (slider.children.length / 3) * (item.offsetWidth + gap);
      currentScroll = targetScroll = totalWidth;
      slider.scrollLeft = totalWidth;
    }, 100);

    const animate = () => {
      const item = slider.firstElementChild;
      if (!item) return;
      const gap = parseFloat(window.getComputedStyle(slider).gap) || 0;
      const totalWidth = (slider.children.length / 3) * (item.offsetWidth + gap);

      // 1. Auto-drift
      targetScroll += 1;

      // 2. Wrap boundaries
      if (targetScroll >= totalWidth * 2) {
        targetScroll -= totalWidth;
        currentScroll -= totalWidth;
      } else if (targetScroll <= 0) {
        targetScroll += totalWidth;
        currentScroll += totalWidth;
      }

      // 3. Smooth Lerp
      currentScroll += (targetScroll - currentScroll) * scrollSpeed;
      slider.scrollLeft = currentScroll;
      
      requestAnimationFrame(animate);
    };

    // Inverted: Mouse/Touch on Right (delta > 0) -> targetScroll decreases -> Cards move Right
    slider.addEventListener('mousemove', (e) => {
      const mouseX = e.pageX - slider.offsetLeft;
      const midX = slider.clientWidth / 2;
      const delta = (mouseX - midX) / midX; 
      targetScroll -= delta * 15; 
    });

    slider.addEventListener('touchmove', (e) => {
      const touchX = e.touches[0].pageX - slider.offsetLeft;
      const midX = slider.clientWidth / 2;
      const delta = (touchX - midX) / midX;
      targetScroll -= delta * 15;
    }, { passive: true });

    requestAnimationFrame(animate);

  }

  // ============================================================
  //  HERO SECTION — SCROLL ANIMATIONS
  // ============================================================
  const heroSection  = document.querySelector('.hero-section');
  const heroContent  = document.querySelector('.hero-content');
  // const navbar handled by navbar-loader.js
  const scrollArrow  = document.getElementById('heroScrollArrow');

  if (heroSection && heroContent) {
    window.addEventListener('scroll', () => {
      const scrollY      = window.scrollY;
      const heroHeight   = heroSection.offsetHeight;
      const progress     = Math.min(scrollY / heroHeight, 1); // 0 → 1 as hero scrolls out

      // 1. Parallax — move background upward at half scroll speed
      heroSection.style.backgroundPositionY = `calc(50% + ${scrollY * 0.35}px)`;

      // 2. Hero content — fade out + slide up as user scrolls
      const contentOpacity   = 1 - progress * 1.8;       // fully gone before 60% scroll
      const contentTranslate = scrollY * 0.25;            // moves up gently
      heroContent.style.opacity   = Math.max(0, contentOpacity);
      heroContent.style.transform = `translateY(-${contentTranslate}px)`;

      // 3. Scroll Arrow — fade out early
      if (scrollArrow) {
        scrollArrow.style.opacity = Math.max(0, 1 - progress * 4);
      }

      // 4. Navbar scroll effect is handled by navbar-loader.js
    }, { passive: true });
  }

  // Scroll-down arrow click — smooth scroll to next section
  if (scrollArrow) {
    scrollArrow.addEventListener('click', () => {
      const nextSection = heroSection.nextElementSibling;
      if (nextSection) {
        nextSection.scrollIntoView({ behavior: 'smooth' });
      }
    });
  }

  // ============================================================
  //  TESTIMONIALS — SEAMLESS INFINITE TRACKING
  // ============================================================
  
  // Star Rating Selection (Reusable for any container)
  const setupStarRating = (containerId, hiddenInputId = null) => {
    const container = document.getElementById(containerId);
    if (!container) return;

    const stars = container.querySelectorAll('i');
    stars.forEach(star => {
      star.addEventListener('click', function() {
        const rating = parseInt(this.getAttribute('data-rating'));
        
        // Remove active from all stars in this container
        stars.forEach(s => s.classList.remove('active'));
        
        // Add active to the clicked star specifically
        // CSS will handle highlighting this star and everything to its RIGHT (~ i)
        this.classList.add('active');

        // Update Hidden Input (if provided)
        if (hiddenInputId) {
          document.getElementById(hiddenInputId).value = rating;
        }
      });
    });
  };

  setupStarRating('quickStarRating');
});

// Global function for quick rating button
function submitQuickRating() {
  const starsContainer = document.getElementById('quickStarRating');
  const activeStars = starsContainer.querySelectorAll('i.active').length;
  
  if (activeStars === 0) {
    alert('Please pick a star rating first! ⭐');
    return;
  }

  alert(`Thank you for your ${activeStars}-star quick rating! We appreciate it. 🌟`);
  
  // Reset quick stars
  starsContainer.querySelectorAll('i').forEach(s => {
    s.classList.remove('active');
    s.style.color = '#ddd';
  });
}

document.addEventListener('DOMContentLoaded', () => {
  const budgetRoot = document.getElementById('totalBudgetForm');
  if (!budgetRoot) return;

  const BUDGET_STORAGE_KEY = 'dreamWeddingBudgetPlanner';

  const categories = [
    { key: 'venue', label: 'Venue', percent: 35 },
    { key: 'catering', label: 'Catering', percent: 25 },
    { key: 'photography', label: 'Photography', percent: 10 },
    { key: 'decor', label: 'Decor', percent: 10 },
    { key: 'entertainment', label: 'Entertainment', percent: 8 },
    { key: 'attire', label: 'Attire & Beauty', percent: 7 },
    { key: 'other', label: 'Other', percent: 5 }
  ];

  const defaultState = {
    totalBudget: 25000,
    categories: categories.reduce((acc, item) => {
      acc[item.key] = item.percent;
      return acc;
    }, {}),
    expenses: [],
    vendors: [
      { id: 'v1', name: 'Sunset Garden Estates', category: 'venue', estimate: 8500, included: true },
      { id: 'v2', name: 'Delightful Fine Dining', category: 'catering', estimate: 5200, included: true },
      { id: 'v3', name: 'Lens & Light Studios', category: 'photography', estimate: 1800, included: true },
      { id: 'v4', name: 'Soundscape DJ Services', category: 'entertainment', estimate: 1200, included: false }
    ]
  };

  const loadState = () => {
    try {
      const saved = localStorage.getItem(BUDGET_STORAGE_KEY);
      if (!saved) return JSON.parse(JSON.stringify(defaultState));
      const parsed = JSON.parse(saved);
      return {
        totalBudget: Number(parsed.totalBudget) || defaultState.totalBudget,
        categories: { ...defaultState.categories, ...(parsed.categories || {}) },
        expenses: Array.isArray(parsed.expenses) ? parsed.expenses : [],
        vendors: Array.isArray(parsed.vendors) && parsed.vendors.length ? parsed.vendors : defaultState.vendors
      };
    } catch (error) {
      return JSON.parse(JSON.stringify(defaultState));
    }
  };

  let state = loadState();

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(amount || 0);
  };

  const saveState = () => {
    localStorage.setItem(BUDGET_STORAGE_KEY, JSON.stringify(state));
  };

  const totalBudgetInput = document.getElementById('totalBudgetInput');
  const plannedBudgetValue = document.getElementById('plannedBudgetValue');
  const actualSpentValue = document.getElementById('actualSpentValue');
  const remainingValue = document.getElementById('remainingValue');
  const budgetHealthBadge = document.getElementById('budgetHealthBadge');
  const budgetCategoriesWrap = document.getElementById('budgetCategoriesWrap');
  const allocationTotalText = document.getElementById('allocationTotalText');
  const expenseCategory = document.getElementById('expenseCategory');
  const expenseForm = document.getElementById('expenseForm');
  const expenseNote = document.getElementById('expenseNote');
  const expenseAmount = document.getElementById('expenseAmount');
  const categoryProgressTable = document.getElementById('categoryProgressTable');
  const vendorEstimateTable = document.getElementById('vendorEstimateTable');
  const includedVendorEstimateValue = document.getElementById('includedVendorEstimateValue');
  const expenseHistoryTable = document.getElementById('expenseHistoryTable');

  const categoryByKey = categories.reduce((acc, item) => {
    acc[item.key] = item.label;
    return acc;
  }, {});

  const getVendorEstimatedTotal = () => state.vendors.filter(v => v.included).reduce((sum, v) => sum + Number(v.estimate || 0), 0);
  const getManualExpenseTotal = () => state.expenses.reduce((sum, item) => sum + Number(item.amount || 0), 0);
  const getActualTotal = () => getVendorEstimatedTotal() + getManualExpenseTotal();
  const getAllocationFor = (categoryKey) => (state.totalBudget * Number(state.categories[categoryKey] || 0)) / 100;

  const getSpentByCategory = (categoryKey) => {
    const manual = state.expenses
      .filter(item => item.category === categoryKey)
      .reduce((sum, item) => sum + Number(item.amount || 0), 0);
    const vendor = state.vendors
      .filter(item => item.category === categoryKey && item.included)
      .reduce((sum, item) => sum + Number(item.estimate || 0), 0);
    return manual + vendor;
  };

  const renderCategoryInputs = () => {
    budgetCategoriesWrap.innerHTML = '';
    categories.forEach((item) => {
      const currentPercent = Number(state.categories[item.key] || 0);
      const col = document.createElement('div');
      col.className = 'col-md-6';
      col.innerHTML = `
        <div class="budget-category-card">
          <div class="d-flex justify-content-between align-items-center">
            <strong>${item.label}</strong>
            <span>${formatCurrency(getAllocationFor(item.key))}</span>
          </div>
          <label class="form-label mt-2 mb-1 small text-muted">Allocation %</label>
          <input type="number" class="form-control form-control-sm budget-percent-input" data-category="${item.key}" min="0" max="100" step="1" value="${currentPercent}">
        </div>
      `;
      budgetCategoriesWrap.appendChild(col);
    });
  };

  const renderExpenseCategoryOptions = () => {
    expenseCategory.innerHTML = categories
      .map(item => `<option value="${item.key}">${item.label}</option>`)
      .join('');
  };

  const renderVendorTable = () => {
    vendorEstimateTable.innerHTML = '';
    state.vendors.forEach((vendor) => {
      const row = document.createElement('tr');
      row.innerHTML = `
        <td>
          <input class="form-check-input vendor-include-toggle" type="checkbox" data-vendor-id="${vendor.id}" ${vendor.included ? 'checked' : ''}>
        </td>
        <td>${vendor.name}</td>
        <td>${categoryByKey[vendor.category] || vendor.category}</td>
        <td>${formatCurrency(vendor.estimate)}</td>
      `;
      vendorEstimateTable.appendChild(row);
    });
    includedVendorEstimateValue.textContent = formatCurrency(getVendorEstimatedTotal());
  };

  const renderProgressTable = () => {
    categoryProgressTable.innerHTML = '';
    categories.forEach((item) => {
      const allocated = getAllocationFor(item.key);
      const spent = getSpentByCategory(item.key);
      const usagePercent = allocated > 0 ? Math.round((spent / allocated) * 100) : 0;
      const progressClass = usagePercent > 100 ? 'bg-danger' : usagePercent > 85 ? 'bg-warning' : 'bg-success';

      const row = document.createElement('tr');
      row.innerHTML = `
        <td>${item.label}</td>
        <td>${formatCurrency(allocated)}</td>
        <td>${formatCurrency(spent)}</td>
        <td style="min-width: 180px;">
          <div class="progress budget-progress">
            <div class="progress-bar ${progressClass}" role="progressbar" style="width: ${Math.min(usagePercent, 100)}%"></div>
          </div>
          <small class="text-muted">${usagePercent}% used</small>
        </td>
      `;
      categoryProgressTable.appendChild(row);
    });
  };

  const renderExpenseHistory = () => {
    expenseHistoryTable.innerHTML = '';
    if (!state.expenses.length) {
      expenseHistoryTable.innerHTML = '<tr><td colspan="4" class="text-muted">No manual expenses added yet.</td></tr>';
      return;
    }

    [...state.expenses].reverse().forEach((item) => {
      const row = document.createElement('tr');
      row.innerHTML = `
        <td>${categoryByKey[item.category] || item.category}</td>
        <td>${item.note}</td>
        <td>${formatCurrency(item.amount)}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-danger delete-expense-btn" data-expense-id="${item.id}">Delete</button>
        </td>
      `;
      expenseHistoryTable.appendChild(row);
    });
  };

  const renderSummary = () => {
    const planned = Number(state.totalBudget || 0);
    const actual = getActualTotal();
    const remaining = planned - actual;
    const allocationTotal = categories.reduce((sum, item) => sum + Number(state.categories[item.key] || 0), 0);

    totalBudgetInput.value = planned;
    plannedBudgetValue.textContent = formatCurrency(planned);
    actualSpentValue.textContent = formatCurrency(actual);
    remainingValue.textContent = formatCurrency(remaining);
    allocationTotalText.textContent = `${allocationTotal}%`;

    const spentRatio = planned > 0 ? (actual / planned) : 0;
    if (spentRatio > 1) {
      budgetHealthBadge.textContent = 'Over Budget';
      budgetHealthBadge.className = 'badge rounded-pill bg-danger budget-status-badge';
    } else if (spentRatio > 0.85) {
      budgetHealthBadge.textContent = 'Near Limit';
      budgetHealthBadge.className = 'badge rounded-pill bg-warning text-dark budget-status-badge';
    } else {
      budgetHealthBadge.textContent = 'On Track';
      budgetHealthBadge.className = 'badge rounded-pill bg-success budget-status-badge';
    }
  };

  const refreshAll = () => {
    renderCategoryInputs();
    renderExpenseCategoryOptions();
    renderVendorTable();
    renderProgressTable();
    renderExpenseHistory();
    renderSummary();
  };

  budgetRoot.addEventListener('submit', (event) => {
    event.preventDefault();
    state.totalBudget = Number(totalBudgetInput.value || 0);
    saveState();
    refreshAll();
  });

  budgetCategoriesWrap.addEventListener('input', (event) => {
    const input = event.target.closest('.budget-percent-input');
    if (!input) return;
    const percent = Number(input.value || 0);
    state.categories[input.dataset.category] = Math.max(0, Math.min(100, percent));
    saveState();
    renderCategoryInputs();
    renderProgressTable();
    renderSummary();
  });

  expenseForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const amount = Number(expenseAmount.value || 0);
    if (!amount) return;
    state.expenses.push({
      id: `${Date.now()}-${Math.random().toString(16).slice(2)}`,
      category: expenseCategory.value,
      note: expenseNote.value.trim(),
      amount
    });
    expenseForm.reset();
    saveState();
    refreshAll();
  });

  vendorEstimateTable.addEventListener('change', (event) => {
    const toggle = event.target.closest('.vendor-include-toggle');
    if (!toggle) return;
    const vendor = state.vendors.find((item) => item.id === toggle.dataset.vendorId);
    if (!vendor) return;
    vendor.included = toggle.checked;
    saveState();
    renderVendorTable();
    renderProgressTable();
    renderExpenseHistory();
    renderSummary();
  });
  expenseHistoryTable.addEventListener('click', (event) => {
    const deleteBtn = event.target.closest('.delete-expense-btn');
    if (!deleteBtn) return;
    state.expenses = state.expenses.filter((item) => item.id !== deleteBtn.dataset.expenseId);
    saveState();
    refreshAll();
  });

  refreshAll();
});

// Quick Site Rating Logic for Home Page
document.addEventListener('DOMContentLoaded', () => {
    const quickStars = document.querySelectorAll('#quickStarRating i');
    let selectedRating = 0;

    quickStars.forEach(star => {
        star.addEventListener('click', () => {
            selectedRating = parseInt(star.dataset.rating);
            updateQuickStars(selectedRating);
        });

        star.addEventListener('mouseenter', () => {
            updateQuickStars(parseInt(star.dataset.rating));
        });

        star.addEventListener('mouseleave', () => {
            updateQuickStars(selectedRating);
        });
    });

    function updateQuickStars(rating) {
        quickStars.forEach(s => {
            const r = parseInt(s.dataset.rating);
            if (r <= rating) {
                s.style.color = '#b8963e';
            } else {
                s.style.color = '#ddd';
            }
        });
    }

    window.submitQuickRating = async function() {
        if (selectedRating === 0) {
            alert('Please select a star rating first.');
            return;
        }

        const userStr = sessionStorage.getItem('dreamWeddingUser');
        if (!userStr) {
            window.location.href = `pages/login.html?redirect=index.html&rating=${selectedRating}`;
            return;
        }

        const user = JSON.parse(userStr);
        // If they just want to give a rating, we can redirect them to the full review page with the rating pre-selected
        window.location.href = `pages/add-site-review.html?rating=${selectedRating}`;
    };
});



// Initialize Testimonial Marquees
const initSiteReviews = async () => {
  const ltrMarquee = document.getElementById('feedbackMarqueeLTR');
  const rtlMarquee = document.getElementById('feedbackMarqueeRTL');

  if (!ltrMarquee || !rtlMarquee) {
    console.warn('Marquee elements not found. Skipping site reviews load.');
    return;
  }

  try {
    console.log('Fetching site reviews from /api/reviews/site ...');
    const response = await fetch('/api/reviews/site');
    if (!response.ok) throw new Error('Failed to fetch reviews: ' + response.status);
    
    const reviews = await response.json();
    console.log('Site reviews loaded:', reviews.length);

    if (reviews.length === 0) {
      ltrMarquee.innerHTML = '<div class="text-center py-4 w-100"><p class="text-muted">No stories shared yet. Be the first!</p></div>';
      rtlMarquee.innerHTML = '';
      return;
    }

    const html = reviews.map(r => {
      let stars = '';
      for (let i = 1; i <= 5; i++) {
        stars += `<i class="${i <= r.rating ? 'fa-solid' : 'fa-regular'} fa-star"></i>`;
      }
      
      const avatar = r.reviewerProfilePic || `https://ui-avatars.com/api/?name=${encodeURIComponent(r.reviewerName)}&background=random&color=fff`;
      
      let photosHtml = '';
      if (r.photos && r.photos.length > 0) {
          photosHtml = `<div class="d-flex gap-1 mt-2 mb-3">
              ${r.photos.slice(0, 3).map(p => `<img src="${p}" style="width:50px; height:50px; object-fit:cover; border-radius:6px;">`).join('')}
              ${r.photos.length > 3 ? `<div style="width:50px; height:50px; background:rgba(0,0,0,0.05); border-radius:6px; display:flex; align-items:center; justify-content:center; font-size:0.7rem; color: #666;">+${r.photos.length - 3}</div>` : ''}
          </div>`;
      }

      return `
        <div class="testimonial-card p-4">
          <div class="d-flex mb-2 text-warning">${stars}</div>
          ${r.reviewTitle ? `<h6 class="fw-bold mb-2">${r.reviewTitle}</h6>` : ''}
          <p class="testimonial-text mb-3">"${r.reviewMessage}"</p>
          ${photosHtml}
          <div class="d-flex align-items-center mt-auto">
            <img src="${avatar}" class="rounded-circle me-2" width="40" height="40" alt="${r.reviewerName}">
            <div>
              <h6 class="mb-0 fw-bold">${r.reviewerName}</h6>
              <small class="text-muted">${r.experienceCategory || 'Customer'}</small>
            </div>
          </div>
        </div>
      `;
    });

    const half = Math.ceil(html.length / 2);
    const row1 = html.slice(0, half).join('');
    const row2 = html.slice(half).join('') || row1;

    ltrMarquee.innerHTML = row1;
    rtlMarquee.innerHTML = row2;

    setupExploreStyleMarquee('feedbackMarqueeLTR', 0.5);
    setupExploreStyleMarquee('feedbackMarqueeRTL', -0.5);
    
  } catch (err) {
    console.error('Error loading site reviews:', err);
    ltrMarquee.innerHTML = '<div class="text-center py-4 w-100 text-danger">Failed to load stories. Please try again later.</div>';
  }
};

// Run on load
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initSiteReviews);
} else {
  initSiteReviews();
}

// NOTE: Navbar auth-state injection is handled by navbar-loader.js
