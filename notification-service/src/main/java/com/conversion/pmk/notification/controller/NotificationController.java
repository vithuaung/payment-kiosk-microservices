package com.conversion.pmk.notification.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.notification.dto.response.NotificationResponse;
import com.conversion.pmk.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notifications", description = "Query notification records")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Get all notifications for a payment")
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByPaymentId(
            @PathVariable("paymentId") UUID paymentId) {
        List<NotificationResponse> notifications = notificationService.getByPaymentId(paymentId);
        return ResponseEntity.ok(ApiResponse.ok(notifications));
    }
}
