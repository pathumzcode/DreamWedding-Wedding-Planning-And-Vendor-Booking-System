/**
 * wishlist-utils.js
 * Global utility for managing the user's wishlist across the platform.
 */
(function() {
    'use strict';

    window.Wishlist = {
        /**
         * toggle - Add or remove an item from the wishlist
         * @param {Object} item - { vendorId, vendorName, vendorCategory, vendorLocation, vendorImage }
         * @param {HTMLElement} btn - The button element to toggle class
         */
        async toggle(item, btn) {
            const userStr = sessionStorage.getItem('dreamWeddingUser');
            if (!userStr) {
                // Not logged in, redirect to login
                const currentUrl = encodeURIComponent(window.location.href);
                window.location.href = `login.html?redirect=${currentUrl}`;
                return;
            }

            const user = JSON.parse(userStr);
            if (user.role !== 'CUSTOMER') {
                alert('Only customers can add items to their wishlist.');
                return;
            }

            // 1. Check if already in wishlist
            try {
                const wishlist = await this.get(user.id);
                const existing = wishlist.find(wi => wi.vendorId === item.vendorId);

                if (existing) {
                    // Remove
                    await fetch(`/api/wishlist/${existing.id}`, { method: 'DELETE' });
                    if (btn) btn.classList.remove('active');
                } else {
                    // Add
                    const payload = {
                        customerId: user.id,
                        ...item
                    };
                    await fetch('/api/wishlist', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(payload)
                    });
                    if (btn) btn.classList.add('active');
                    
                    // Optional: Log Activity
                    if (window.logActivity) {
                        await window.logActivity('ADD_TO_WISHLIST', 'CUSTOMER', user);
                    }
                }
            } catch (err) {
                console.error('[Wishlist Error]', err);
            }
        },

        /**
         * get - Fetch user's wishlist
         */
        async get(customerId) {
            try {
                const res = await fetch(`/api/wishlist/${customerId}`);
                if (!res.ok) return [];
                return await res.json();
            } catch (e) {
                return [];
            }
        },

        /**
         * checkStatus - Check if specific items are in wishlist and update buttons
         * @param {Array} vendorIds - List of IDs to check
         * @param {String} customerId
         */
        async checkStatus(vendorIds, customerId) {
            if (!customerId) return;
            const wishlist = await this.get(customerId);
            const wishIds = wishlist.map(wi => wi.vendorId);
            
            vendorIds.forEach(id => {
                const btn = document.querySelector(`.wishlist-btn[data-vendor-id="${id}"]`);
                if (btn && wishIds.includes(id)) {
                    btn.classList.add('active');
                }
            });
        }
    };
})();
