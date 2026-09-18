package com.cosmetics.ecommerce.entity;

import com.cosmetics.ecommerce.enums.PaymentMethod;
import com.cosmetics.ecommerce.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments", indexes = @Index(name = "idx_payment_transaction", columnList = "transactionId", unique = true))
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) private Order order;
    @Column(nullable = false, unique = true) private String transactionId;
    @Column(nullable = false) private String paymentGateway;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PaymentStatus paymentStatus;
    private Instant paidAt;
    @Column(nullable = false, updatable = false) private Instant createdAt = Instant.now();
    protected Payment() {}
    public Payment(Order order, String transactionId, String paymentGateway, BigDecimal amount, PaymentMethod paymentMethod, PaymentStatus paymentStatus) {
        this.order = order; this.transactionId = transactionId; this.paymentGateway = paymentGateway; this.amount = amount;
        this.paymentMethod = paymentMethod; this.paymentStatus = paymentStatus; this.paidAt = paymentStatus == PaymentStatus.SUCCESS ? Instant.now() : null;
    }
    public Long getId() { return id; } public String getTransactionId() { return transactionId; } public String getPaymentGateway() { return paymentGateway; }
    public BigDecimal getAmount() { return amount; } public PaymentMethod getPaymentMethod() { return paymentMethod; } public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public Instant getPaidAt() { return paidAt; } public Instant getCreatedAt() { return createdAt; }
}