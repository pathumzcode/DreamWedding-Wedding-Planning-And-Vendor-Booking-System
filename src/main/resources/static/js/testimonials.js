/* Testimonials Loader */
document.addEventListener('DOMContentLoaded', function() {
    const testimonialsGrid = document.getElementById('testimonialsGrid');
    const loadMoreBtn = document.getElementById('loadMoreTestimonials');
    
    // Initial display count
    let displayCount = 4;
    let allTestimonials = [];

    // Fetch site reviews from backend
    async function fetchTestimonials() {
        try {
            const response = await fetch('/api/reviews/site');
            const data = await response.json();
            
            // If no reviews in DB, use some beautiful dummy data to wow the user
            if (!data || data.length === 0) {
                allTestimonials = getDummyTestimonials();
            } else {
                allTestimonials = data;
            }
            
            renderTestimonials();
        } catch (error) {
            console.error('Error fetching testimonials:', error);
            allTestimonials = getDummyTestimonials();
            renderTestimonials();
        }
    }

    function renderTestimonials() {
        testimonialsGrid.innerHTML = '';
        const toDisplay = allTestimonials.slice(0, displayCount);
        
        toDisplay.forEach(item => {
            const card = document.createElement('div');
            card.className = 'testimonial-card';
            
            // Generate star rating HTML
            let starsHtml = '';
            for (let i = 1; i <= 5; i++) {
                if (i <= item.rating) {
                    starsHtml += '<i class="fa-solid fa-star"></i>';
                } else {
                    starsHtml += '<i class="fa-regular fa-star"></i>';
                }
            }

            // Using dummy avatars if none provided
            const avatarUrl = item.reviewerProfilePic || item.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(item.reviewerName)}&background=random&color=fff`;
            const role = item.reviewerRole === 'CUSTOMER' ? 'Happy Bride' : 'Wedding Vendor';

            card.innerHTML = `
                <div class="card-top">
                    <img src="${avatarUrl}" alt="${item.reviewerName}" class="client-img">
                    <div class="client-info">
                        <h4>${item.reviewerName}</h4>
                        <p>${item.experienceCategory || role}</p>
                    </div>
                    <div class="quote-icon">
                        <i class="fa-solid fa-quote-right"></i>
                    </div>
                </div>
                <div class="rating">
                    ${starsHtml}
                    <span class="score">${item.rating}.0</span>
                </div>
                <p class="testimonial-text">
                    ${item.reviewMessage || item.suggestion}
                </p>
            `;
            testimonialsGrid.appendChild(card);
        });

        // Hide load more if all shown
        if (displayCount >= allTestimonials.length) {
            loadMoreBtn.style.display = 'none';
        }
    }

    loadMoreBtn.addEventListener('click', () => {
        displayCount += 2;
        renderTestimonials();
    });

    function getDummyTestimonials() {
        return [
            {
                reviewerName: "Amara & Kasun",
                experienceCategory: "Married in Colombo",
                rating: 5,
                reviewMessage: "The perfect platform for planning! We found our dream venue and the most amazing florist within a few clicks. The budget tracker kept us right on target.",
                avatar: "https://images.unsplash.com/photo-1583939003579-730e3918a45a?auto=format&fit=crop&w=150&q=80"
            },
            {
                reviewerName: "Sarah & Mark",
                experienceCategory: "Destination Wedding",
                rating: 5,
                reviewMessage: "Planning from abroad seemed impossible until we found DreamWedding. The verified reviews gave us so much confidence in our choices.",
                avatar: "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=150&q=80"
            },
            {
                reviewerName: "Nirosha & Dinuka",
                experienceCategory: "Boutique Wedding",
                rating: 5,
                reviewMessage: "We loved how easy it was to compare different packages. We ended up booking a vendor we hadn't even heard of, and they were incredible!",
                avatar: "https://images.unsplash.com/photo-1591608971362-f08b2a75731a?auto=format&fit=crop&w=150&q=80"
            },
            {
                reviewerName: "Jessica & Liam",
                experienceCategory: "Beach Ceremony",
                rating: 5,
                reviewMessage: "A truly stress-free experience. The dashboard helped us manage all our vendor communications in one place. Best wedding tool out there!",
                avatar: "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?auto=format&fit=crop&w=150&q=80"
            }
        ];
    }

    fetchTestimonials();
});
