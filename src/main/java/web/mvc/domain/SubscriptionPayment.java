package web.mvc.domain;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 구독 결제 이력 엔티티
 * 사용자의 프리미엄 구독 결제 정보를 관리
 */
@Entity
@Table(name = "subscription_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ===== 구독 정보 =====

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type")
    private PaymentSubscriptionType subscriptionType;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    @DecimalMin("0.00")
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "KRW";

    // ===== 결제 정보 =====

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "billing_period_start")
    private LocalDate billingPeriodStart;

    @Column(name = "billing_period_end")
    private LocalDate billingPeriodEnd;

    // ===== 외부 결제 시스템 연동 =====

    @Column(name = "external_payment_id", length = 100)
    private String externalPaymentId;

    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway;

    // ===== 공통 필드 =====

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== 비즈니스 메서드 =====

    /**
     * 결제 완료 처리
     */
    public void completePayment(String externalId, String gateway) {
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.externalPaymentId = externalId;
        this.paymentGateway = gateway;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 결제 실패 처리
     */
    public void failPayment() {
        this.paymentStatus = PaymentStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 결제 환불 처리
     */
    public void refundPayment() {
        this.paymentStatus = PaymentStatus.REFUNDED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 결제 완료 여부 확인
     */
    public boolean isCompleted() {
        return PaymentStatus.COMPLETED.equals(this.paymentStatus);
    }

    /**
     * 결제 진행중 여부 확인
     */
    public boolean isPending() {
        return PaymentStatus.PENDING.equals(this.paymentStatus);
    }

    /**
     * 결제 실패 여부 확인
     */
    public boolean isFailed() {
        return PaymentStatus.FAILED.equals(this.paymentStatus);
    }

    /**
     * 환불된 결제 여부 확인
     */
    public boolean isRefunded() {
        return PaymentStatus.REFUNDED.equals(this.paymentStatus);
    }

    /**
     * 구독 기간 내 여부 확인
     */
    public boolean isValidPeriod() {
        LocalDate now = LocalDate.now();
        return this.billingPeriodStart != null &&
                this.billingPeriodEnd != null &&
                !now.isBefore(this.billingPeriodStart) &&
                !now.isAfter(this.billingPeriodEnd);
    }

    /**
     * 구독 만료 여부 확인
     */
    public boolean isExpired() {
        return this.billingPeriodEnd != null &&
                LocalDate.now().isAfter(this.billingPeriodEnd);
    }

    /**
     * 구독 갱신까지 남은 일수
     */
    public long getDaysUntilExpiry() {
        if (this.billingPeriodEnd == null)
            return 0;
        return LocalDate.now().until(this.billingPeriodEnd).getDays();
    }

    /**
     * 월 구독료인지 확인
     */
    public boolean isMonthlySubscription() {
        if (this.billingPeriodStart == null || this.billingPeriodEnd == null)
            return false;
        return this.billingPeriodStart.until(this.billingPeriodEnd).getMonths() == 1;
    }

    /**
     * 연 구독료인지 확인
     */
    public boolean isYearlySubscription() {
        if (this.billingPeriodStart == null || this.billingPeriodEnd == null)
            return false;
        return this.billingPeriodStart.until(this.billingPeriodEnd).getYears() == 1;
    }

    /**
     * 구독 기간 설정 (월 구독)
     */
    public void setMonthlyBillingPeriod(LocalDate startDate) {
        this.billingPeriodStart = startDate;
        this.billingPeriodEnd = startDate.plusMonths(1);
    }

    /**
     * 구독 기간 설정 (년 구독)
     */
    public void setYearlyBillingPeriod(LocalDate startDate) {
        this.billingPeriodStart = startDate;
        this.billingPeriodEnd = startDate.plusYears(1);
    }

    /**
     * 결제 정보 요약 반환
     */
    public String getPaymentSummary() {
        return String.format("%s 구독 %s %s (%s~%s)",
                this.subscriptionType.getDescription(),
                this.amount,
                this.currency,
                this.billingPeriodStart,
                this.billingPeriodEnd);
    }

    /**
     * 프리미엄 구독 결제 생성
     */
    public static SubscriptionPayment createPremiumPayment(User user, BigDecimal amount,
            String paymentMethod, LocalDate startDate) {
        SubscriptionPayment payment = SubscriptionPayment.builder()
                .user(user)
                .subscriptionType(PaymentSubscriptionType.PREMIUM)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .build();

        payment.setMonthlyBillingPeriod(startDate);
        return payment;
    }

    /**
     * PRO 구독 결제 생성
     */
    public static SubscriptionPayment createProPayment(User user, BigDecimal amount,
            String paymentMethod, LocalDate startDate) {
        SubscriptionPayment payment = SubscriptionPayment.builder()
                .user(user)
                .subscriptionType(PaymentSubscriptionType.PRO)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .build();

        payment.setMonthlyBillingPeriod(startDate);
        return payment;
    }

    /**
     * 결제용 구독 타입 열거형 (SubscriptionType과 구분)
     */
    public enum PaymentSubscriptionType {
        PREMIUM("프리미엄"),
        PRO("프로");

        private final String description;

        PaymentSubscriptionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 결제 상태 열거형
     */
    public enum PaymentStatus {
        PENDING("결제 대기"),
        COMPLETED("결제 완료"),
        FAILED("결제 실패"),
        REFUNDED("환불됨");

        private final String description;

        PaymentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
