package com.conversion.pmk.notification.repository;

import com.conversion.pmk.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByPaymentId(UUID paymentId);

    List<Notification> findBySendStatus(String status);
}
