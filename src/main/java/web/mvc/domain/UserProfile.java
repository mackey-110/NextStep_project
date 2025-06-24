package web.mvc.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 사용자 프로필 (온보딩 설문 결과) 엔티티
 */
@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // === 온보딩 설문 결과 ===

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "programming_languages", columnDefinition = "JSON")
    private List<String> programmingLanguages;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "interest_fields", columnDefinition = "JSON")
    private List<String> interestFields;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_level")
    private CurrentLevel currentLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "learning_goals", columnDefinition = "JSON")
    private List<String> learningGoals;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "learning_style", columnDefinition = "JSON")
    private Map<String, Object> learningStyle;

    // === 학습 통계 ===

    @Builder.Default
    @Column(name = "total_study_hours")
    private Integer totalStudyHours = 0;

    @Builder.Default
    @Column(name = "current_streak_days")
    private Integer currentStreakDays = 0;

    @Builder.Default
    @Column(name = "max_streak_days")
    private Integer maxStreakDays = 0;

    @Builder.Default
    @Column(name = "completed_roadmaps_count")
    private Integer completedRoadmapsCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // === 비즈니스 메서드 ===

    /**
     * 온보딩 설문 결과 업데이트
     */
    public void updateSurveyResults(List<String> programmingLanguages,
            List<String> interestFields,
            CurrentLevel currentLevel,
            List<String> learningGoals,
            Map<String, Object> learningStyle) {
        this.programmingLanguages = programmingLanguages;
        this.interestFields = interestFields;
        this.currentLevel = currentLevel;
        this.learningGoals = learningGoals;
        this.learningStyle = learningStyle;
    }

    /**
     * 학습 시간 추가
     */
    public void addStudyHours(int hours) {
        this.totalStudyHours += hours;
    }

    /**
     * 연속 학습일 업데이트
     */
    public void updateStreakDays(int streakDays) {
        this.currentStreakDays = streakDays;
        if (streakDays > this.maxStreakDays) {
            this.maxStreakDays = streakDays;
        }
    }

    /**
     * 연속 학습일 리셋
     */
    public void resetStreakDays() {
        this.currentStreakDays = 0;
    }

    /**
     * 완료한 로드맵 수 증가
     */
    public void incrementCompletedRoadmaps() {
        this.completedRoadmapsCount++;
    }

    /**
     * 학습 레벨 업데이트
     */
    public void updateCurrentLevel(CurrentLevel newLevel) {
        this.currentLevel = newLevel;
    }
}

/**
 * 학습자 현재 레벨 열거형
 */
enum CurrentLevel {
    COMPLETE_BEGINNER("완전 초보자"),
    BASIC_COMPLETED("기초 완료"),
    PROJECT_EXPERIENCE("프로젝트 경험"),
    WORK_EXPERIENCE("실무 경험");

    private final String description;

    CurrentLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
