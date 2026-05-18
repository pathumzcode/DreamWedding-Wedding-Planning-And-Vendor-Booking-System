package com.wedding.dreamwedding.service;

import com.wedding.dreamwedding.dto.ReviewDTO;
import com.wedding.dreamwedding.entity.Booking;
import com.wedding.dreamwedding.entity.Customer;
import com.wedding.dreamwedding.entity.Review;
import com.wedding.dreamwedding.entity.Vendor;
import com.wedding.dreamwedding.repository.BookingRepository;
import com.wedding.dreamwedding.repository.CustomerRepository;
import com.wedding.dreamwedding.repository.ReviewRepository;
import com.wedding.dreamwedding.repository.VendorRepository;
import com.wedding.dreamwedding.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    // --- UNIVERSITY PROJECT THEORIES ---
    
    /**
     * DEPENDENCY: This service depends on these repositories 
     * to interact with the database.
     */
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;
    private final HotelRepository hotelRepository;

    public Review createVendorReview(ReviewDTO dto) {
        if (!"VENDOR".equals(dto.getType())) {
            throw new IllegalArgumentException("Type must be VENDOR");
        }
        
        // Verify customer has booked this vendor
        List<Booking> customerBookings = bookingRepository.findByCustomerIdAndVendorId(
                dto.getReviewerId(), dto.getVendorId());
                
        if (customerBookings == null || customerBookings.isEmpty()) {
            throw new IllegalStateException("Only customers who have booked this vendor can submit a review.");
        }

        Review review = new Review();
        review.setType("VENDOR");
        review.setVendorId(dto.getVendorId());
        
        // Fetch name for the review record
        vendorRepository.findById(dto.getVendorId()).ifPresent(v -> {
            review.setVendorName(v.getBusinessName() != null ? v.getBusinessName() : v.getFirstName());
        });
        if (review.getVendorName() == null) {
            hotelRepository.findById(dto.getVendorId()).ifPresent(h -> {
                review.setVendorName(h.getHotelName());
            });
        }
        
        review.setBookingId(dto.getBookingId());
        review.setRating(dto.getRating());
        review.setReviewTitle(dto.getReviewTitle());
        review.setReviewMessage(dto.getReviewMessage());
        review.setPhotos(dto.getPhotos());
        review.setEventDate(dto.getEventDate());
        review.setEventType(dto.getEventType());
        review.setBookedBy(dto.getBookedBy());
        review.setServiceRatings(dto.getServiceRatings());
        review.setReviewTags(dto.getReviewTags());
        
        Customer customer = customerRepository.findById(dto.getReviewerId())
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        
        review.setReviewerId(customer.getId());
        review.setReviewerName(customer.getFirstName() + " " + customer.getLastName());
        review.setReviewerProfilePic(customer.getProfilePicture());
        review.setReviewerRole("CUSTOMER");
        
        review.setVerified(true);
        review.setStatus("APPROVED");
        Review savedReview = reviewRepository.save(review);

        // Update Stats for either Vendor or Hotel
        updateTargetStats(dto.getVendorId());

        return savedReview;
    }

    private void updateTargetStats(String targetId) {
        List<Review> allReviews = reviewRepository.findByVendorIdAndStatus(targetId, "APPROVED");
        int count = allReviews.size();
        double avg = allReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        double roundedAvg = Math.round(avg * 10.0) / 10.0;

        vendorRepository.findById(targetId).ifPresent(v -> {
            v.setReviewCount(count);
            v.setAverageRating(roundedAvg);
            vendorRepository.save(v);
        });

        hotelRepository.findById(targetId).ifPresent(h -> {
            h.setReviewCount(count);
            h.setAverageRating(roundedAvg);
            hotelRepository.save(h);
        });
    }

    public Review createSiteReview(ReviewDTO dto, String role) {
        if (!"SITE".equals(dto.getType())) {
            throw new IllegalArgumentException("Type must be SITE");
        }

        Review review = new Review();
        review.setType("SITE");
        review.setExperienceCategory(dto.getExperienceCategory());
        review.setSuggestion(dto.getSuggestion());
        
        review.setRating(dto.getRating());
        review.setReviewMessage(dto.getReviewMessage());
        review.setReviewTags(dto.getReviewTags());
        
        review.setReviewerId(dto.getReviewerId());
        review.setReviewerRole(role);
        
        if ("CUSTOMER".equals(role)) {
            Customer customer = customerRepository.findById(dto.getReviewerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
            review.setReviewerName(customer.getFirstName() + " " + customer.getLastName());
            review.setReviewerProfilePic(customer.getProfilePicture());
        } else if ("VENDOR".equals(role)) {
            Vendor vendor = vendorRepository.findById(dto.getReviewerId())
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
            review.setReviewerName(vendor.getBusinessName() != null ? vendor.getBusinessName() : vendor.getFirstName());
            review.setReviewerProfilePic(vendor.getProfilePicture());
        } else {
            throw new IllegalArgumentException("Invalid role for site review");
        }
        
        review.setStatus("APPROVED");
        return reviewRepository.save(review);
    }

    public Review addVendorReply(String reviewId, String vendorId, String replyMessage) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
                
        if (!"VENDOR".equals(review.getType()) || !vendorId.equals(review.getVendorId())) {
            throw new IllegalStateException("Not authorized to reply to this review");
        }
        
        review.setVendorReply(replyMessage);
        review.setReplyDate(LocalDateTime.now());
        
        return reviewRepository.save(review);
    }
    
    public Review markHelpful(String reviewId, String userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
                
        if (!review.getHelpfulUserIds().contains(userId)) {
            review.getHelpfulUserIds().add(userId);
            review.setHelpfulCount(review.getHelpfulCount() + 1);
            return reviewRepository.save(review);
        }
        return review;
    }

    public Review updateStatus(String reviewId, String status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        review.setStatus(status);
        return reviewRepository.save(review);
    }

    /**
     * METHOD OVERLOADING - Version 1
     * Fetches all approved reviews for a vendor.
     */
    public List<Review> getVendorReviews(String vendorId) {
        return reviewRepository.findByVendorIdAndStatus(vendorId, "APPROVED");
    }

    /**
     * METHOD OVERLOADING - Version 2 (Different signature)
     * Fetches reviews for a vendor filtered by status.
     */
    public List<Review> getVendorReviews(String vendorId, String status) {
        return reviewRepository.findByVendorIdAndStatus(vendorId, status);
    }

    public List<Review> getSiteReviews() {
        return reviewRepository.findByTypeAndStatus("SITE", "APPROVED");
    }
    
    public List<Review> getMyReviews(String reviewerId) {
        return reviewRepository.findByReviewerId(reviewerId);
    }
    
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
    
    public void deleteReview(String id) {
        reviewRepository.deleteById(id);
    }
}
