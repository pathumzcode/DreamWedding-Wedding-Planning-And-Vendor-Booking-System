/**
 * scroll-reveal.js — DreamWedding Premium Animations
 *
 * Auto-applies IntersectionObserver scroll reveal to:
 *  - vendor cards      (.vendor-card)
 *  - how-it-works cards (.how-it-works-card)
 *  - category cards    (.category-card)
 *  - [data-reveal]     (any element with this attribute)
 *
 * Just include this script; no HTML changes required.
 */
(function () {
  'use strict';

  // Selectors that get the reveal treatment
  const REVEAL_SELECTORS = [
    '.vendor-card',
    '.category-card',
    '.how-it-works-card',
    '.testimonial-card',
    '.hotel-card',
    '[data-reveal]'
  ].join(', ');

  function initReveal() {
    const targets = document.querySelectorAll(REVEAL_SELECTORS);

    if (!targets.length) return;

    // Mark elements so the CSS transitions kick in
    targets.forEach((el, i) => {
      el.setAttribute('data-reveal', '');
      // Staggered delay based on position within its parent row
      const siblings = Array.from(el.parentElement.children);
      const idx = siblings.indexOf(el);
      el.style.transitionDelay = `${idx * 0.08}s`;
    });

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            entry.target.classList.add('revealed');
            observer.unobserve(entry.target); // fire once
          }
        });
      },
      {
        threshold: 0.12,
        rootMargin: '0px 0px -40px 0px'
      }
    );

    targets.forEach(el => observer.observe(el));
  }

  // Run after DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initReveal);
  } else {
    initReveal();
  }
})();
