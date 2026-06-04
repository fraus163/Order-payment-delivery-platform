package com.fraus.paymentservice.domain;

import com.fraus.commonlibs.http.payment.CreatePaymentRequestDto;
import com.fraus.commonlibs.http.payment.CreatePaymentResponseDto;
import com.fraus.commonlibs.http.payment.PaymentMethod;
import com.fraus.commonlibs.http.payment.PaymentStatus;
import com.fraus.paymentservice.domain.db.PaymentEntityMapper;
import com.fraus.paymentservice.domain.db.PaymentEntityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentEntityMapper mapper;
    private final PaymentEntityRepository paymentEntityRepository;

    public CreatePaymentResponseDto makePayment(CreatePaymentRequestDto request) {
        var found = paymentEntityRepository.findByOrderId(request.orderId());
        if (found.isPresent()) {
            log.info("Payment already exists for orderId={}", request.orderId());
            return mapper.toResponseDto(found.get());
        }

        var entity = mapper.toEntity(request);

        var status = request.paymentMethod().equals(PaymentMethod.QR)
                ? PaymentStatus.PAYMENT_FAILED
                : PaymentStatus.PAYMENT_SUCCEEDED;

        entity.setPaymentStatus(status);

        var savedEntity = paymentEntityRepository.save(entity);
        return mapper.toResponseDto(savedEntity);
    }
}
