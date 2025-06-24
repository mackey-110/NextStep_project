package web.mvc.domain;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AI 이용 제한 추적 엔티티
 * 사용자별 일일 AI 사용량을 추적하여 제한 관리
 */
@Entity
@Table(name = "ai_usage_limits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiUsageLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long usageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "message_count")
    @PositiveOrZero
    @Builder.Default
    private Integer messageCount = 0;

    @Column(name = "tokens_used")
    @PositiveOrZero
    @Builder.Default
    private Integer tokensUsed = 0;

    // ===== 공통 필드 =====

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.usageDate == null) {
            this.usageDate = LocalDate.now();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== 비즈니스 메서드 =====

    /**
     * 메시지 사용량 추가
     */
    public void addMessageUsage(int tokenCount) {
        this.messageCount++;
        this.tokensUsed += tokenCount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 오늘 사용량인지 확인
     */
    public boolean isToday() {
        return LocalDate.now().equals(this.usageDate);
    }

    /**
     * 사용자 역할별 일일 메시지 제한 확인
     */
    public boolean isMessageLimitExceeded() {
        UserRole userRole = this.user.getUserRole();
        int dailyLimit = getDailyMessageLimit(userRole);
        return this.messageCount >= dailyLimit;
    }

    /**
     * 사용자 역할별 일일 토큰 제한 확인
     */
    public boolean isTokenLimitExceeded() {
        UserRole userRole = this.user.getUserRole();
        int dailyLimit = getDailyTokenLimit(userRole);
        return this.tokensUsed >= dailyLimit;
    }

    /**
     * 남은 메시지 횟수 계산
     */
    public int getRemainingMessages() {
        UserRole userRole = this.user.getUserRole();
        int dailyLimit = getDailyMessageLimit(userRole);
        return Math.max(0, dailyLimit - this.messageCount);
    }

    /**
     * 남은 토큰 수 계산
     */
    public int getRemainingTokens() {
        UserRole userRole = this.user.getUserRole();
        int dailyLimit = getDailyTokenLimit(userRole);
        return Math.max(0, dailyLimit - this.tokensUsed);
    }

    /**
     * 사용률 계산 (메시지 기준)
     */
    public double getMessageUsagePercentage() {
        UserRole userRole = this.user.getUserRole();
        int dailyLimit = getDailyMessageLimit(userRole);
        if (dailyLimit == 0)
            return 0.0;
        return Math.min(100.0, (double) this.messageCount / dailyLimit * 100);
    }

    /**
     * 사용률 계산 (토큰 기준)
     */
    public double getTokenUsagePercentage() {
        UserRole userRole = this.user.getUserRole();
        int dailyLimit = getDailyTokenLimit(userRole);
        if (dailyLimit == 0)
            return 0.0;
        return Math.min(100.0, (double) this.tokensUsed / dailyLimit * 100);
    }

    /**
     * 사용자 역할별 일일 메시지 제한 반환
     */
    private int getDailyMessageLimit(UserRole userRole) {
        switch (userRole) {
            case GUEST:
                return 0; // 게스트는 AI 사용 불가
            case FREE_MEMBER:
                return 10; // 무료 회원: 10개/일
            case PREMIUM_MEMBER:
                return 100; // 프리미엄 회원: 100개/일
            case MENTOR:
                return 200; // 멘토: 200개/일
            case ADMIN:
            case SUPER_ADMIN:
                return Integer.MAX_VALUE; // 관리자: 무제한
            default:
                return 0;
        }
    }

    /**
     * 사용자 역할별 일일 토큰 제한 반환
     */
    private int getDailyTokenLimit(UserRole userRole) {
        switch (userRole) {
            case GUEST:
                return 0; // 게스트는 AI 사용 불가
            case FREE_MEMBER:
                return 50000; // 무료 회원: 50K 토큰/일
            case PREMIUM_MEMBER:
                return 500000; // 프리미엄 회원: 500K 토큰/일
            case MENTOR:
                return 1000000; // 멘토: 1M 토큰/일
            case ADMIN:
            case SUPER_ADMIN:
                return Integer.MAX_VALUE; // 관리자: 무제한
            default:
                return 0;
        }
    }

    /**
     * 제한 정보를 문자열로 반환
     */
    public String getLimitInfo() {
        UserRole userRole = this.user.getUserRole();
        int messageLimit = getDailyMessageLimit(userRole);
        int tokenLimit = getDailyTokenLimit(userRole);

        if (messageLimit == Integer.MAX_VALUE) {
            return "무제한";
        }

        return String.format("메시지: %d/%d, 토큰: %d/%d",
                this.messageCount, messageLimit,
                this.tokensUsed, tokenLimit);
    }

    /**
     * 오늘 날짜 기준으로 새 사용량 레코드 생성
     */
    public static AiUsageLimit createTodayUsage(User user) {
        return AiUsageLimit.builder()
                .user(user)
                .usageDate(LocalDate.now())
                .messageCount(0)
                .tokensUsed(0)
                .build();
    }
}
