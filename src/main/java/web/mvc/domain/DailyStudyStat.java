package web.mvc.domain;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 일별 학습 통계 엔티티
 * 사용자의 일일 학습 활동을 집계하여 대시보드에 표시
 */
@Entity
@Table(name = "daily_study_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStudyStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Long statId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    // ===== 학습 통계 =====

    @Column(name = "total_study_minutes")
    @PositiveOrZero
    @Builder.Default
    private Integer totalStudyMinutes = 0;

    @Column(name = "completed_steps")
    @PositiveOrZero
    @Builder.Default
    private Integer completedSteps = 0;

    @Column(name = "ai_questions_asked")
    @PositiveOrZero
    @Builder.Default
    private Integer aiQuestionsAsked = 0;

    @Column(name = "searches_performed")
    @PositiveOrZero
    @Builder.Default
    private Integer searchesPerformed = 0;

    @Column(name = "streak_day_number")
    @PositiveOrZero
    @Builder.Default
    private Integer streakDayNumber = 0;

    // ===== 공통 필드 =====

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.studyDate == null) {
            this.studyDate = LocalDate.now();
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
     * 학습 시간 추가
     */
    public void addStudyMinutes(int minutes) {
        this.totalStudyMinutes += minutes;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 완료 단계 수 증가
     */
    public void incrementCompletedSteps() {
        this.completedSteps++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * AI 질문 수 증가
     */
    public void incrementAiQuestions() {
        this.aiQuestionsAsked++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 검색 횟수 증가
     */
    public void incrementSearches() {
        this.searchesPerformed++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 연속 학습 일수 설정
     */
    public void setStreakDayNumber(int streakDay) {
        this.streakDayNumber = streakDay;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 오늘 통계인지 확인
     */
    public boolean isToday() {
        return LocalDate.now().equals(this.studyDate);
    }

    /**
     * 어제 통계인지 확인
     */
    public boolean isYesterday() {
        return LocalDate.now().minusDays(1).equals(this.studyDate);
    }

    /**
     * 학습이 있었던 날인지 확인
     */
    public boolean hasStudyActivity() {
        return this.totalStudyMinutes > 0 ||
                this.completedSteps > 0 ||
                this.aiQuestionsAsked > 0 ||
                this.searchesPerformed > 0;
    }

    /**
     * 활발한 학습 일인지 확인 (30분 이상 또는 단계 완료)
     */
    public boolean isActiveStudyDay() {
        return this.totalStudyMinutes >= 30 || this.completedSteps > 0;
    }

    /**
     * 학습 시간을 시간:분 형식으로 반환
     */
    public String getFormattedStudyTime() {
        if (this.totalStudyMinutes == null || this.totalStudyMinutes == 0) {
            return "0분";
        }

        int hours = this.totalStudyMinutes / 60;
        int minutes = this.totalStudyMinutes % 60;

        if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes);
        } else {
            return String.format("%d분", minutes);
        }
    }

    /**
     * 학습 효율성 점수 계산 (0-100)
     */
    public int getEfficiencyScore() {
        int score = 0;

        // 학습 시간 (최대 40점, 2시간 이상 시 만점)
        if (this.totalStudyMinutes != null) {
            score += Math.min(40, this.totalStudyMinutes / 3); // 3분당 1점
        }

        // 완료 단계 (최대 30점, 단계당 15점)
        if (this.completedSteps != null) {
            score += Math.min(30, this.completedSteps * 15);
        }

        // AI 활용 (최대 15점, 질문 5개 이상 시 만점)
        if (this.aiQuestionsAsked != null) {
            score += Math.min(15, this.aiQuestionsAsked * 3);
        }

        // 능동적 학습 (최대 15점, 검색 10회 이상 시 만점)
        if (this.searchesPerformed != null) {
            score += Math.min(15, this.searchesPerformed * 1.5);
        }

        return Math.min(100, score);
    }

    /**
     * 오늘 날짜 기준으로 새 통계 레코드 생성
     */
    public static DailyStudyStat createTodayStats(User user) {
        return DailyStudyStat.builder()
                .user(user)
                .studyDate(LocalDate.now())
                .totalStudyMinutes(0)
                .completedSteps(0)
                .aiQuestionsAsked(0)
                .searchesPerformed(0)
                .streakDayNumber(0)
                .build();
    }

    /**
     * 특정 날짜의 통계 레코드 생성
     */
    public static DailyStudyStat createForDate(User user, LocalDate date) {
        return DailyStudyStat.builder()
                .user(user)
                .studyDate(date)
                .totalStudyMinutes(0)
                .completedSteps(0)
                .aiQuestionsAsked(0)
                .searchesPerformed(0)
                .streakDayNumber(0)
                .build();
    }

    /**
     * 학습 활동으로부터 통계 업데이트
     */
    public void updateFromActivity(LearningActivity activity) {
        switch (activity.getActivityType()) {
            case STEP_COMPLETE:
                incrementCompletedSteps();
                if (activity.getDurationMinutes() != null) {
                    addStudyMinutes(activity.getDurationMinutes());
                }
                break;
            case STUDY_SESSION:
                if (activity.getDurationMinutes() != null) {
                    addStudyMinutes(activity.getDurationMinutes());
                }
                break;
            case AI_QUESTION:
                incrementAiQuestions();
                break;
            case SEARCH:
                incrementSearches();
                break;
            default:
                // 기타 활동은 특별한 처리 없음
                break;
        }
    }

    /**
     * 통계 요약 정보 반환
     */
    public String getSummary() {
        return String.format("학습시간: %s, 완료단계: %d개, AI질문: %d개, 검색: %d회, 연속: %d일",
                getFormattedStudyTime(),
                this.completedSteps,
                this.aiQuestionsAsked,
                this.searchesPerformed,
                this.streakDayNumber);
    }
}
