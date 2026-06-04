package com.fraus.orderservice.api;

import com.fraus.commonlibs.http.payment.PaymentMethod;

public record OrderPaymentRequest(
        PaymentMethod paymentMethod
) {}
