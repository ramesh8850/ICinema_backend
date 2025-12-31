package com.infy.icinema.repository;

import com.infy.icinema.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByTransactionId(String transactionId);

    java.util.Optional<Payment> findByBooking_Id(Long bookingId);
}
