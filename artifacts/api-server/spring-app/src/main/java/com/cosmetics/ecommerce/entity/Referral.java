package com.cosmetics.ecommerce.entity;

import com.cosmetics.ecommerce.enums.ReferralStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "referrals", indexes = {
    @Index(name = "idx_referral_referrer", columnList = "referrer_id"),
    @Index(name = "idx_referral_referred", columnList = "referred_user_id", unique = true),
    @Index(name = "idx_referral_code", columnList = "referral_code")
})
public class Referral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "referrer_id", nullable = false)
    private User referrer;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "referred_user_id", nullable = false, unique = true)
    private User referredUser;

    @Column(name = "referral_code", nullable = false, length = 40)
    private String referralCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReferralStatus status = ReferralStatus.REGISTERED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_order_id")
    private Order firstOrder;

    @Column(precision = 12, scale = 2)
    private BigDecimal rewardAmount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant completedAt;

    protected Referral() {}

    public Referral(User referrer, User referredUser, String referralCode) {
        this.referrer = referrer;
        this.referredUser = referredUser;
        this.referralCode = referralCode;
        this.status = ReferralStatus.REGISTERED;
    }

    public Long getId() { return id; }
    public User getReferrer() { return referrer; }
    public User getReferredUser() { return referredUser; }
    public String getReferralCode() { return referralCode; }
    public ReferralStatus getStatus() { return status; }
    public Order getFirstOrder() { return firstOrder; }
    public BigDecimal getRewardAmount() { return rewardAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getCompletedAt() { return completedAt; }

    public void setStatus(ReferralStatus status) { this.status = status; }
    public void setFirstOrder(Order firstOrder) { this.firstOrder = firstOrder; }
    public void setRewardAmount(BigDecimal rewardAmount) { this.rewardAmount = rewardAmount; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
