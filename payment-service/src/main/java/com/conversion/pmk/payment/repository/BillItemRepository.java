package com.conversion.pmk.payment.repository;

import com.conversion.pmk.payment.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, UUID> {

    List<BillItem> findByPaymentPaymentId(UUID paymentId);
}
