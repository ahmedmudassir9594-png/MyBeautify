package com.cosmetics.ecommerce.payment;

import com.cosmetics.ecommerce.enums.PaymentMethod;
import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult charge(BigDecimal amount, PaymentMethod method);
    record PaymentResult(String transactionId, String gateway, boolean successful) {}
}