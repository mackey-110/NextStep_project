package web.mvc.domain;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 실시간 알림 엔티티
 * 사용자에게 전송되는 각종 알림을 관리
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ===== 알림 내용 =====

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 200)
    @NotBlank
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String message;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    // ===== 읽음 상태 =====

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    // ===== 공통 필드 =====

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ===== 비즈니스 메서드 =====

    /**
     * 알림 읽음 처리
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * 알림 읽지 않음 처리
     */
    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
    }

    /**
     * 읽지 않은 알림인지 확인
     */
    public boolean isUnread() {
        return !Boolean.TRUE.equals(this.isRead);
    }

    /**
     * 오늘 생성된 알림인지 확인
     */
    public boolean isToday() {
        return this.createdAt.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    /**
     * 액션 URL이 있는지 확인
     */
    public boolean hasActionUrl() {
        return this.actionUrl != null && !this.actionUrl.trim().isEmpty();
    }

    /**
     * 최근 알림인지 확인 (24시간 이내)
     */
    public boolean isRecent() {
        return this.createdAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    /**
     * 중요한 알림인지 확인
     */
    public boolean isImportant() {
        return this.type == NotificationType.GOAL_ACHIEVED ||
                this.type == NotificationType.STREAK_MILESTONE ||
                this.type == NotificationType.MENTORING_MATCH;
    }

    /**
     * 알림 요약 정보 반환
     */
    public String getNotificationSummary() {
        String status = isUnread() ? "[NEW] " : "[READ] ";
        return status + this.type.getDescription() + ": " + this.title;
    }

    // ===== 정적 팩토리 메서드 =====

    /**
     * 단계 완료 알림 생성
     */
    public static Notification createStepCompleteNotification(User user, String stepTitle, String roadmapTitle) {
        return Notification.builder()
                .user(user)
                .type(NotificationType.STEP_COMPLETE)
                .title("단계 완료!")
                .message(String.format("'%s' 로드맵의 '%s' 단계를 완료했습니다! 🎉", roadmapTitle, stepTitle))
                .actionUrl("/dashboard")
                .build();
    }

    /**
     * 목표 달성 알림 생성
     */
    public static Notification createGoalAchievementNotification(User user, String goalDescription) {
        return Notification.builder()
                .user(user)
                .type(NotificationType.GOAL_ACHIEVED)
                .title("목표 달성!")
                .message(String.format("축하합니다! %s 목표를 달성했습니다! 🏆", goalDescription))
                .actionUrl("/profile/achievements")
                .build();
    }

    /**
     * 연속 학습 기록 알림 생성
     */
    public static Notification createStreakMilestoneNotification(User user, int streakDays) {
        return Notification.builder()
                .user(user)
                .type(NotificationType.STREAK_MILESTONE)
                .title("연속 학습 기록!")
                .message(String.format("와우! %d일 연속 학습을 달성했습니다! 🔥", streakDays))
                .actionUrl("/dashboard")
                .build();
    }

    /**
     * 새 콘텐츠 알림 생성
     */
    public static Notification createNewContentNotification(User user, String contentTitle, String contentType) {
        return Notification.builder()
                .user(user)
                .type(NotificationType.NEW_CONTENT)
                .title("새로운 콘텐츠!")
                .message(String.format("관심 분야에 새로운 %s '%s'이(가) 추가되었습니다!", contentType, contentTitle))
                .actionUrl("/contents")
                .build();
    }

    /**
     * 멘토링 매칭 알림 생성
     */
    public static Notification createMentoringMatchNotification(User user, String mentorName) {
        return Notification.builder()
                .user(user)
                .type(NotificationType.MENTORING_MATCH)
                .title("멘토 매칭 완료!")
                .message(String.format("%s 멘토와 매칭되었습니다. 멘토링을 시작해보세요!", mentorName))
                .actionUrl("/mentoring")
                .build();
    }

    /**
     * 사용자 맞춤 알림 생성
     */
    public static Notification createCustomNotification(User user, NotificationType type,
            String title, String message, String actionUrl) {
        return Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .actionUrl(actionUrl)
                .build();
    }

    /**
     * 시스템 공지 알림 생성 (모든 사용자에게)
     */
    public static Notification createSystemNotification(User user, String title, String message) {
        return Notification.builder()
                .user(user)
                .type(NotificationType.NEW_CONTENT) // 시스템 공지는 NEW_CONTENT 타입 사용
                .title("[공지] " + title)
                .message(message)
                .actionUrl("/announcements")
                .build();
    }

    /**
     * 알림 타입 열거형
     */
    public enum NotificationType {
        STEP_COMPLETE("단계 완료"),
        GOAL_ACHIEVED("목표 달성"),
        STREAK_MILESTONE("연속 학습 기록"),
        NEW_CONTENT("새 콘텐츠"),
        MENTORING_MATCH("멘토링 매칭");

        private final String description;

        NotificationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 알림 타입별 아이콘 반환
         */
        public String getIcon() {
            switch (this) {
                case STEP_COMPLETE:
                    return "✅";
                case GOAL_ACHIEVED:
                    return "🏆";
                case STREAK_MILESTONE:
                    return "🔥";
                case NEW_CONTENT:
                    return "📚";
                case MENTORING_MATCH:
                    return "🤝";
                default:
                    return "📢";
            }
        }

        /**
         * 알림 타입별 색상 클래스 반환
         */
        public String getColorClass() {
            switch (this) {
                case STEP_COMPLETE:
                    return "success";
                case GOAL_ACHIEVED:
                    return "warning";
                case STREAK_MILESTONE:
                    return "danger";
                case NEW_CONTENT:
                    return "info";
                case MENTORING_MATCH:
                    return "primary";
                default:
                    return "secondary";
            }
        }
    }
}
