package com.fraus.paymentservice.domain.db;

import com.fraus.commonlibs.http.payment.CreatePaymentRequestDto;
import com.fraus.commonlibs.http.payment.CreatePaymentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentEntityMapper {
    PaymentEntity toEntity(CreatePaymentRequestDto request);

    @Mapping(source = "id", target = "paymentId")
    CreatePaymentResponseDto toResponseDto(PaymentEntity entity);
}
