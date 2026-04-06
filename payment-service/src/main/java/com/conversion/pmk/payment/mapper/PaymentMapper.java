package com.conversion.pmk.payment.mapper;

import com.conversion.pmk.payment.dto.request.InitiatePaymentRequest;
import com.conversion.pmk.payment.dto.response.PaymentResponse;
import com.conversion.pmk.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BillItemMapper.class)
public interface PaymentMapper {

    // payMethod and payStatus are not set from request; caller sets them explicitly
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "payMethod", ignore = true)
    @Mapping(target = "payStatus", ignore = true)
    @Mapping(target = "paidAmt", ignore = true)
    @Mapping(target = "changeAmt", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "finishedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "billItems", ignore = true)
    @Mapping(target = "cashSession", ignore = true)
    @Mapping(target = "cardSession", ignore = true)
    @Mapping(target = "sessionRef", ignore = true)
    Payment toEntity(InitiatePaymentRequest request);

    @Mapping(target = "payMethod", expression = "java(payment.getPayMethod() != null ? payment.getPayMethod().name() : null)")
    @Mapping(target = "payStatus", expression = "java(payment.getPayStatus() != null ? payment.getPayStatus().name() : null)")
    @Mapping(target = "startedAt", expression = "java(payment.getStartedAt() != null ? payment.getStartedAt().toString() : null)")
    @Mapping(target = "finishedAt", expression = "java(payment.getFinishedAt() != null ? payment.getFinishedAt().toString() : null)")
    PaymentResponse toResponse(Payment payment);
}
