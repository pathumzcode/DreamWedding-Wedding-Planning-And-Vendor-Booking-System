/**
 * navbar-loader.js
 * Dynamically loads the shared navbar component into any page.
 *
 * Usage: add a <div id="navbar-placeholder"></div> where the navbar should appear,
 * then load this script BEFORE Bootstrap JS:
 *
 *   <script src="../js/navbar-loader.js"></script>  (from /pages/)
 *   <script src="js/navbar-loader.js"></script>       (from root)
 *
 * The script auto-detects path depth and applies correct relative URLs.
 */
(function () {
  'use strict';

  // ─── Path Detection ───────────────────────────────────────────────────────
  // Determine if we are one level deep inside /pages/ or at the root level.
  const isInPages = window.location.pathname.includes('/pages/');
  const ROOT   = isInPages ? '../'  : '';          // path to /static/
  const PAGES  = isInPages ? ''     : 'pages/';   // path prefix for page links

  // ─── Fetch & Inject Navbar ────────────────────────────────────────────────
  const navbarUrl = ROOT + 'components/navbar.html';

  fetch(navbarUrl)
    .then(res => {
      if (!res.ok) throw new Error('Navbar component not found: ' + navbarUrl);
      return res.text();
    })
    .then(html => {
      // Replace path placeholders
      html = html.replace(/__ROOT__/g, ROOT);
      html = html.replace(/__PAGES__/g, PAGES);

      // Inject into placeholder or prepend to body
      const placeholder = document.getElementById('navbar-placeholder');
      if (placeholder) {
        placeholder.outerHTML = html;
      } else {
        document.body.insertAdjacentHTML('afterbegin', html);
      }

      // After injecting, apply auth state & scroll behaviour
      _applyAuthState();
      _initScrollEffect();
    })
    .catch(err => console.error('[navbar-loader]', err));

  // ─── Auth State ───────────────────────────────────────────────────────────
  function _applyAuthState() {
    const userStr = sessionStorage.getItem('dreamWeddingUser');
    if (!userStr) return; // Guest: keep default navbar

    let user;
    try { user = JSON.parse(userStr); } catch (e) { return; }

    // Hide guest items
    _hide('loginNavItem');
    _hide('signupNavItem');

    // Show auth items
    _show('dashboardNavItem');
    _show('logoutNavItem');

    // Set user display name & Profile Pic/Initial
    const nameEl = document.getElementById('navUserName');
    const picContainer = document.getElementById('navProfilePicContainer');
    
    if (nameEl && user.firstName) {
      nameEl.textContent = user.firstName;
    }

    if (picContainer) {
      if (user.profilePicture) {
        // Show Image
        picContainer.innerHTML = `<img src="${user.profilePicture}" style="width:100%; height:100%; object-fit:cover;">`;
      } else {
        // Show Initial
        const name = user.firstName || user.hotelName || user.businessName || 'User';
        const initial = name.charAt(0).toUpperCase();
        picContainer.innerHTML = initial;
      }
    }

    // Build dashboard URL based on role
    const rolePaths = {
      ADMIN:    PAGES + 'admin-dashboard.html',
      VENDOR:   PAGES + 'vendor-dashboard.html',
      HOTEL:    PAGES + 'hotel-dashboard.html',
      CUSTOMER: PAGES + 'customer-dashboard.html',
    };
    const dashUrl = rolePaths[user.role] || (PAGES + 'customer-dashboard.html');

    const dashBtn = document.getElementById('dashboardNavBtn');
    if (dashBtn) dashBtn.href = dashUrl;
  }

  // ─── Scroll Behaviour (navbar shadow on scroll) ───────────────────────────
  function _initScrollEffect() {
    // Only applies when a hero section exists (index page)
    window.addEventListener('scroll', () => {
      const navbar = document.getElementById('mainNavbar');
      if (!navbar) return;

      if (window.scrollY > 60) {
        navbar.style.backgroundColor = 'rgba(250, 247, 241, 0.98)';
        navbar.style.boxShadow = '0 4px 24px rgba(26,23,20,0.12)';
      } else {
        navbar.style.backgroundColor = '';
        navbar.style.boxShadow = '';
      }
    }, { passive: true });
  }

  // ─── Helpers ──────────────────────────────────────────────────────────────
  function _hide(id) {
    const el = document.getElementById(id);
    if (el) el.classList.add('d-none');
  }
  function _show(id) {
    const el = document.getElementById(id);
    if (el) el.classList.remove('d-none');
  }

  // ─── Global Navbar Actions ────────────────────────────────────────────────
  /**
   * logoutUser - Clears session and redirects to index
   */
  window.logoutUser = async function() {
    const userStr = sessionStorage.getItem('dreamWeddingUser');
    if (userStr) {
      const user = JSON.parse(userStr);
      // Log logout activity
      await window.logActivity('LOGOUT', user.role, user);
    }
    
    sessionStorage.removeItem('dreamWeddingUser');
    window.location.href = ROOT + 'index.html';
  };

  /**
   * logActivity - Log user activity to database
   */
  window.logActivity = async function(action, role, manualUser = null) {
    const user = manualUser || JSON.parse(sessionStorage.getItem('dreamWeddingUser'));
    if (!user) return;

    try {
      await fetch(ROOT + 'api/actions/log', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: user.id,
          userEmail: user.email,
          action: action,
          role: role || user.role
        })
      });
    } catch (err) {
      console.error('Log Activity Error:', err);
    }
  };

})();
