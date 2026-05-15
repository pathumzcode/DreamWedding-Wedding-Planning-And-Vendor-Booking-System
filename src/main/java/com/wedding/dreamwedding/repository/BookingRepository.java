package com.wedding.dreamwedding.repository;

import com.wedding.dreamwedding.entity.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByVendorId(String vendorId);
    List<Booking> findByCustomerId(String customerId);
    List<Booking> findByCustomerIdAndVendorId(String customerId, String vendorId);
    List<Booking> findByCustomerIdAndVendorIdAndStatus(String customerId, String vendorId, String status);
}
