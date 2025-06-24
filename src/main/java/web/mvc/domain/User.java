package web.mvc.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자 기본 정보 엔티티
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false, length = 50)
    private String nickname;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    @Builder.Default
    private UserRole userRole = UserRole.FREE_MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type")
    @Builder.Default
    private SubscriptionType subscriptionType = SubscriptionType.FREE;

    @Column(name = "subscription_start_date")
    private LocalDateTime subscriptionStartDate;

    @Column(name = "subscription_end_date")
    private LocalDateTime subscriptionEndDate;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // === 비즈니스 메서드 ===

    /**
     * 프리미엄 구독 시작
     */
    public void startPremiumSubscription(SubscriptionType subscriptionType, LocalDateTime endDate) {
        this.subscriptionType = subscriptionType;
        this.subscriptionStartDate = LocalDateTime.now();
        this.subscriptionEndDate = endDate;
    }

    /**
     * 구독 종료
     */
    public void endSubscription() {
        this.subscriptionType = SubscriptionType.FREE;
        this.subscriptionStartDate = null;
        this.subscriptionEndDate = null;
    }

    /**
     * 마지막 로그인 시간 업데이트
     */
    public void updateLastLoginTime() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 이메일 인증 완료
     */
    public void verifyEmail() {
        this.emailVerified = true;
    }

    /**
     * 프로필 정보 업데이트
     */
    public void updateProfile(String nickname, String fullName, String profileImageUrl) {
        if (nickname != null)
            this.nickname = nickname;
        if (fullName != null)
            this.fullName = fullName;
        if (profileImageUrl != null)
            this.profileImageUrl = profileImageUrl;
    }

    /**
     * 계정 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 계정 활성화
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 프리미엄 구독 여부 확인
     */
    public boolean isPremiumUser() {
        return subscriptionType != SubscriptionType.FREE &&
                subscriptionEndDate != null &&
                subscriptionEndDate.isAfter(LocalDateTime.now());
    }

    /**
     * 특정 역할 이상의 권한을 가지는지 확인
     */
    public boolean hasAuthorityOf(UserRole requiredRole) {
        return this.userRole.hasAuthorityOf(requiredRole);
    }
}
