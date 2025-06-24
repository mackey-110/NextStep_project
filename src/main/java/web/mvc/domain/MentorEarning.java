package web.mvc.domain;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 멘토 수익 관리 엔티티
 * 멘토의 멘토링, 로드맵 판매 등으로 인한 수익을 관리
 */
@Entity
@Table(name = "mentor_earnings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorEarning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "earning_id")
    private Long earningId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private MentorProfile mentor;

    // ===== 수익 정보 =====

    @Enumerated(EnumType.STRING)
    @Column(name = "earning_type")
    private EarningType earningType;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    @DecimalMin("0.00")
    private BigDecimal amount;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private BigDecimal commissionRate;

    @Column(name = "net_amount", precision = 10, scale = 2)
    @DecimalMin("0.00")
    private BigDecimal netAmount;

    // ===== 수익 출처 =====

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "source_type", length = 50)
    private String sourceType;

    // ===== 지급 정보 =====

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status")
    @Builder.Default
    private PayoutStatus payoutStatus = PayoutStatus.PENDING;

    @Column(name = "payout_date")
    private LocalDate payoutDate;

    // ===== 공통 필드 =====

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        // 순수익 자동 계산
        calculateNetAmount();
    }

    // ===== 비즈니스 메서드 =====

    /**
     * 순수익 계산
     */
    public void calculateNetAmount() {
        if (this.amount != null && this.commissionRate != null) {
            BigDecimal commission = this.amount.multiply(this.commissionRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            this.netAmount = this.amount.subtract(commission);
        }
    }

    /**
     * 수수료 금액 계산
     */
    public BigDecimal getCommissionAmount() {
        if (this.amount == null || this.commissionRate == null) {
            return BigDecimal.ZERO;
        }
        return this.amount.multiply(this.commissionRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * 지급 완료 처리
     */
    public void completePayout() {
        this.payoutStatus = PayoutStatus.PAID;
        this.payoutDate = LocalDate.now();
    }

    /**
     * 지급 취소 처리
     */
    public void cancelPayout() {
        this.payoutStatus = PayoutStatus.CANCELLED;
        this.payoutDate = null;
    }

    /**
     * 지급 대기 중인지 확인
     */
    public boolean isPending() {
        return PayoutStatus.PENDING.equals(this.payoutStatus);
    }

    /**
     * 지급 완료되었는지 확인
     */
    public boolean isPaid() {
        return PayoutStatus.PAID.equals(this.payoutStatus);
    }

    /**
     * 지급 취소되었는지 확인
     */
    public boolean isCancelled() {
        return PayoutStatus.CANCELLED.equals(this.payoutStatus);
    }

    /**
     * 이번 달 수익인지 확인
     */
    public boolean isThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate createdDate = this.createdAt.toLocalDate();
        return createdDate.getYear() == now.getYear() &&
                createdDate.getMonthValue() == now.getMonthValue();
    }

    /**
     * 수익률 계산 (순수익/총액)
     */
    public double getNetMarginPercentage() {
        if (this.amount == null || this.amount.equals(BigDecimal.ZERO)) {
            return 0.0;
        }
        return this.netAmount.divide(this.amount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }

    /**
     * 수익 요약 정보 반환
     */
    public String getEarningSummary() {
        return String.format("%s 수익 %s원 (순수익: %s원, 수수료: %.1f%%)",
                this.earningType.getDescription(),
                this.amount,
                this.netAmount,
                this.commissionRate.doubleValue());
    }

    // ===== 정적 팩토리 메서드 =====

    /**
     * 멘토링 수익 생성
     */
    public static MentorEarning createMentoringEarning(MentorProfile mentor, BigDecimal amount,
            Long sessionId) {
        return MentorEarning.builder()
                .mentor(mentor)
                .earningType(EarningType.MENTORING)
                .amount(amount)
                .commissionRate(BigDecimal.valueOf(20)) // 멘토링 수수료 20%
                .sourceId(sessionId)
                .sourceType("mentoring_session")
                .build();
    }

    /**
     * 로드맵 판매 수익 생성
     */
    public static MentorEarning createRoadmapSaleEarning(MentorProfile mentor, BigDecimal amount,
            Long roadmapId) {
        return MentorEarning.builder()
                .mentor(mentor)
                .earningType(EarningType.ROADMAP_SALE)
                .amount(amount)
                .commissionRate(BigDecimal.valueOf(30)) // 로드맵 판매 수수료 30%
                .sourceId(roadmapId)
                .sourceType("roadmap_template")
                .build();
    }

    /**
     * 라이브 세션 수익 생성
     */
    public static MentorEarning createLiveSessionEarning(MentorProfile mentor, BigDecimal amount,
            Long sessionId) {
        return MentorEarning.builder()
                .mentor(mentor)
                .earningType(EarningType.LIVE_SESSION)
                .amount(amount)
                .commissionRate(BigDecimal.valueOf(15)) // 라이브 세션 수수료 15%
                .sourceId(sessionId)
                .sourceType("live_session")
                .build();
    }

    /**
     * 수익 유형별 기본 수수료율 반환
     */
    public static BigDecimal getDefaultCommissionRate(EarningType earningType) {
        switch (earningType) {
            case MENTORING:
                return BigDecimal.valueOf(20);
            case ROADMAP_SALE:
                return BigDecimal.valueOf(30);
            case LIVE_SESSION:
                return BigDecimal.valueOf(15);
            default:
                return BigDecimal.valueOf(25);
        }
    }

    /**
     * 수익 유형 열거형
     */
    public enum EarningType {
        MENTORING("멘토링"),
        ROADMAP_SALE("로드맵 판매"),
        LIVE_SESSION("라이브 세션");

        private final String description;

        EarningType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 지급 상태 열거형
     */
    public enum PayoutStatus {
        PENDING("지급 대기"),
        PAID("지급 완료"),
        CANCELLED("지급 취소");

        private final String description;

        PayoutStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
