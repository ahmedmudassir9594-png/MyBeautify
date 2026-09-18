package com.cosmetics.ecommerce.payment;

import com.cosmetics.ecommerce.enums.PaymentMethod;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {
    @Override
    public PaymentResult charge(BigDecimal amount, PaymentMethod method) {
        return new PaymentResult("MOCK-" + UUID.randomUUID(), "mock", true);
    }
}