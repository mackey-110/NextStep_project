package web.mvc.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 사용자 학습 진도 엔티티
 */
@Entity
@Table(name = "user_step_progress")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserStepProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_roadmap_id", nullable = false)
    private UserRoadMap userRoadMap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private RoadMapStep step;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StepProgressStatus status = StepProgressStatus.NOT_STARTED;

    @Builder.Default
    @Column(name = "progress_percentage", precision = 5, scale = 2)
    private BigDecimal progressPercentage = BigDecimal.ZERO;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder.Default
    @Column(name = "study_hours", precision = 5, scale = 2)
    private BigDecimal studyHours = BigDecimal.ZERO;

    // === 사용자 노트 및 프로젝트 ===

    @Column(name = "user_notes", columnDefinition = "TEXT")
    private String userNotes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "completed_projects", columnDefinition = "JSON")
    private List<Map<String, Object>> completedProjects;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // === 비즈니스 메서드 ===

    /**
     * 단계 시작
     */
    public void start() {
        this.status = StepProgressStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    /**
     * 단계 완료
     */
    public void complete() {
        this.status = StepProgressStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.progressPercentage = new BigDecimal("100.00");
    }

    /**
     * 진행률 업데이트
     */
    public void updateProgress(BigDecimal percentage) {
        this.progressPercentage = percentage;

        if (percentage.compareTo(BigDecimal.ZERO) > 0 && this.status == StepProgressStatus.NOT_STARTED) {
            start();
        }

        if (percentage.compareTo(new BigDecimal("100.00")) == 0) {
            complete();
        }
    }

    /**
     * 학습 시간 추가
     */
    public void addStudyTime(BigDecimal hours) {
        this.studyHours = this.studyHours.add(hours);
    }

    /**
     * 사용자 노트 업데이트
     */
    public void updateUserNotes(String notes) {
        this.userNotes = notes;
    }

    /**
     * 완료한 프로젝트 추가
     */
    public void addCompletedProject(Map<String, Object> project) {
        if (this.completedProjects == null) {
            this.completedProjects = new java.util.ArrayList<>();
        }
        this.completedProjects.add(project);
    }

    /**
     * 단계를 완료 상태로 변경하고 학습 시간 기록
     */
    public void completeWithStudyTime(BigDecimal studyTime) {
        complete();
        addStudyTime(studyTime);
    }

    /**
     * 진행률 리셋 (다시 시작)
     */
    public void reset() {
        this.status = StepProgressStatus.NOT_STARTED;
        this.progressPercentage = BigDecimal.ZERO;
        this.startedAt = null;
        this.completedAt = null;
    }
}

/**
 * 단계 진행 상태 열거형
 */
enum StepProgressStatus {
    NOT_STARTED("시작 전"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료");

    private final String description;

    StepProgressStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
