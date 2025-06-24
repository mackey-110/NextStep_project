package web.mvc.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 사용자별 개인 로드맵 엔티티
 */
@Entity
@Table(name = "user_roadmaps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserRoadMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_roadmap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private RoadMapTemplate template;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "custom_description", columnDefinition = "TEXT")
    private String customDescription;

    // === 진행 상태 ===

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RoadMapStatus status = RoadMapStatus.NOT_STARTED;

    @Builder.Default
    @Column(name = "progress_percentage", precision = 5, scale = 2)
    private BigDecimal progressPercentage = BigDecimal.ZERO;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "estimated_completion_date")
    private LocalDateTime estimatedCompletionDate;

    // === 개인화 설정 ===

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_steps", columnDefinition = "JSON")
    private List<Map<String, Object>> customSteps;

    @Builder.Default
    @Column(name = "daily_study_goal_hours")
    private Integer dailyStudyGoalHours = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // === 연관관계 ===

    @OneToMany(mappedBy = "userRoadMap", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserStepProgress> stepProgresses = new ArrayList<>();

    // === 비즈니스 메서드 ===

    /**
     * 로드맵 시작
     */
    public void start() {
        this.status = RoadMapStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        updateEstimatedCompletionDate();
    }

    /**
     * 로드맵 완료
     */
    public void complete() {
        this.status = RoadMapStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.progressPercentage = new BigDecimal("100.00");
    }

    /**
     * 로드맵 일시정지
     */
    public void pause() {
        this.status = RoadMapStatus.PAUSED;
    }

    /**
     * 로드맵 재개
     */
    public void resume() {
        this.status = RoadMapStatus.IN_PROGRESS;
    }

    /**
     * 진행률 업데이트
     */
    public void updateProgress() {
        if (stepProgresses.isEmpty()) {
            this.progressPercentage = BigDecimal.ZERO;
            return;
        }

        long completedSteps = stepProgresses.stream()
                .mapToLong(progress -> progress.getStatus() == StepProgressStatus.COMPLETED ? 1 : 0)
                .sum();

        BigDecimal progress = new BigDecimal(completedSteps)
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(stepProgresses.size()), 2, java.math.RoundingMode.HALF_UP);

        this.progressPercentage = progress;

        // 100% 완료 시 자동으로 완료 상태로 변경
        if (progress.compareTo(new BigDecimal("100.00")) == 0) {
            complete();
        }
    }

    /**
     * 일일 학습 목표 시간 설정
     */
    public void setDailyStudyGoal(Integer hours) {
        this.dailyStudyGoalHours = hours;
        updateEstimatedCompletionDate();
    }

    /**
     * 예상 완료일 업데이트
     */
    public void updateEstimatedCompletionDate() {
        if (template != null && dailyStudyGoalHours > 0) {
            int remainingHours = template.getEstimatedHours() - getCurrentStudyHours();
            int remainingDays = (remainingHours + dailyStudyGoalHours - 1) / dailyStudyGoalHours; // 올림
            this.estimatedCompletionDate = LocalDateTime.now().plusDays(remainingDays);
        }
    }

    /**
     * 현재까지 학습한 시간 계산
     */
    public int getCurrentStudyHours() {
        return stepProgresses.stream()
                .mapToInt(progress -> progress.getStudyHours().intValue())
                .sum();
    }

    /**
     * 커스텀 설정 업데이트
     */
    public void updateCustomSettings(String title, String customDescription,
            List<Map<String, Object>> customSteps) {
        this.title = title;
        this.customDescription = customDescription;
        this.customSteps = customSteps;
    }

    /**
     * 단계 진행 상황 추가
     */
    public void addStepProgress(UserStepProgress stepProgress) {
        this.stepProgresses.add(stepProgress);
        stepProgress.setUserRoadMap(this);
        updateProgress();
    }
}

/**
 * 로드맵 상태 열거형
 */
enum RoadMapStatus {
    NOT_STARTED("시작 전"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료"),
    PAUSED("일시정지");

    private final String description;

    RoadMapStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
