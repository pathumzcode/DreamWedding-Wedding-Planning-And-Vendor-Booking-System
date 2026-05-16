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

  // ─── Global Page Transition Loader ──────────────────────────────────────────
  function _initPageTransition() {
    // 1. Inject the transition CSS
    const style = document.createElement('style');
    style.innerHTML = `
      #page-transition-overlay {
        position: fixed;
        top: 0; left: 0;
        width: 100vw; height: 100vh;
        background-color: rgba(253, 251, 247, 0.95);
        backdrop-filter: blur(12px);
        -webkit-backdrop-filter: blur(12px);
        z-index: 99999;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        opacity: 0;
        pointer-events: none;
        transition: opacity 0.5s ease-in-out;
      }
      #page-transition-overlay.active {
        opacity: 1;
        pointer-events: all;
      }
      .transition-ring-container {
        position: relative;
        width: 85px; height: 85px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 24px;
      }
      .transition-ring {
        position: absolute;
        width: 100%; height: 100%;
        border-radius: 50%;
        border: 2px solid rgba(184, 150, 62, 0.15);
      }
      .transition-ring::after {
        content: '';
        position: absolute;
        top: -2px; left: -2px;
        width: calc(100% + 4px); height: calc(100% + 4px);
        border-radius: 50%;
        border: 2px solid transparent;
        border-top-color: #c9a227; /* Warm Gold */
        border-right-color: rgba(201, 162, 39, 0.6);
        animation: drawRing 2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
      }
      .transition-heart {
        color: #c9a227;
        font-size: 1.8rem;
        animation: pulseHeart 2s infinite ease-in-out;
        text-shadow: 0 0 15px rgba(201, 162, 39, 0.4);
      }
      .transition-text {
        font-family: 'Playfair Display', serif;
        font-size: 1.35rem;
        color: #7a6f65;
        letter-spacing: 0.5px;
        animation: textFade 2s infinite ease-in-out;
      }
      @keyframes drawRing {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
      }
      @keyframes pulseHeart {
        0% { transform: scale(0.9); opacity: 0.7; }
        50% { transform: scale(1.15); opacity: 1; }
        100% { transform: scale(0.9); opacity: 0.7; }
      }
      @keyframes textFade {
        0%, 100% { opacity: 0.6; }
        50% { opacity: 1; }
      }
      body.page-transitioning {
        opacity: 0;
        transition: opacity 0.4s ease-in-out;
      }
    `;
    document.head.appendChild(style);

    // 2. Inject the transition HTML
    const overlayHtml = `
      <div id="page-transition-overlay">
        <div class="transition-ring-container">
          <div class="transition-ring"></div>
          <i class="fa-solid fa-heart transition-heart"></i>
        </div>
        <div class="transition-text">Preparing your dream moment...</div>
      </div>
    `;
    document.body.insertAdjacentHTML('beforeend', overlayHtml);

    const overlay = document.getElementById('page-transition-overlay');

    // 3. Fade in page on load
    document.body.style.opacity = '0';
    setTimeout(() => {
      document.body.style.transition = 'opacity 0.6s ease-in-out';
      document.body.style.opacity = '1';
    }, 50);

    // 4. Intercept clicks on links for smooth exit
    document.addEventListener('click', (e) => {
      const link = e.target.closest('a');
      if (!link) return;
      
      const targetUrl = link.getAttribute('href');
      
      // Ignore empty links, hashes, new tabs, and js links
      if (!targetUrl || targetUrl.startsWith('#') || targetUrl.startsWith('javascript') || link.getAttribute('target') === '_blank') {
        return;
      }

      // Check if it's an internal link
      const isInternal = link.host === window.location.host;
      if (isInternal) {
        e.preventDefault();
        
        // Show loading animation
        overlay.classList.add('active');
        
        // Fade out body slightly
        setTimeout(() => {
          document.body.classList.add('page-transitioning');
        }, 100);

        // Redirect after animation (wait ~800ms for premium feel)
        setTimeout(() => {
          window.location.href = link.href;
        }, 800);
      }
    });
  }

  // Initialize transition loader
  document.addEventListener('DOMContentLoaded', _initPageTransition);

})();
