package com.conversion.pmk.payment.mapper;

import com.conversion.pmk.payment.dto.request.BillItemRequest;
import com.conversion.pmk.payment.dto.response.BillItemResponse;
import com.conversion.pmk.payment.entity.BillItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BillItemMapper {

    BillItem toEntity(BillItemRequest request);

    BillItemResponse toResponse(BillItem billItem);
}
