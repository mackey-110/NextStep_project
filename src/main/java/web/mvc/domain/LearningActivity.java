package web.mvc.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 학습 활동 로그 엔티티
 * 사용자의 모든 학습 활동을 추적하여 대시보드 및 통계에 활용
 */
@Entity
@Table(name = "learning_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ===== 활동 정보 =====

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type")
    private ActivityType activityType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "duration_minutes")
    @PositiveOrZero
    private Integer durationMinutes;

    /**
     * 추가 메타데이터 (JSON)
     * 활동 유형별 상세 정보 저장
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "JSON")
    private Map<String, Object> metadata;

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

    // ===== 정적 팩토리 메서드 =====

    /**
     * 로드맵 시작 활동 생성
     */
    public static LearningActivity createRoadmapStart(User user, Long roadmapId) {
        return LearningActivity.builder()
                .user(user)
                .activityType(ActivityType.ROADMAP_START)
                .targetId(roadmapId)
                .targetType("roadmap")
                .build();
    }

    /**
     * 단계 완료 활동 생성
     */
    public static LearningActivity createStepComplete(User user, Long stepId, int studyMinutes) {
        return LearningActivity.builder()
                .user(user)
                .activityType(ActivityType.STEP_COMPLETE)
                .targetId(stepId)
                .targetType("step")
                .durationMinutes(studyMinutes)
                .build();
    }

    /**
     * 학습 세션 활동 생성
     */
    public static LearningActivity createStudySession(User user, Long targetId, String targetType,
            int durationMinutes, Map<String, Object> metadata) {
        return LearningActivity.builder()
                .user(user)
                .activityType(ActivityType.STUDY_SESSION)
                .targetId(targetId)
                .targetType(targetType)
                .durationMinutes(durationMinutes)
                .metadata(metadata)
                .build();
    }

    /**
     * AI 질문 활동 생성
     */
    public static LearningActivity createAiQuestion(User user, Long sessionId, String question) {
        Map<String, Object> metadata = Map.of("question", question);
        return LearningActivity.builder()
                .user(user)
                .activityType(ActivityType.AI_QUESTION)
                .targetId(sessionId)
                .targetType("ai_session")
                .metadata(metadata)
                .build();
    }

    /**
     * 검색 활동 생성
     */
    public static LearningActivity createSearch(User user, String searchQuery, int resultCount) {
        Map<String, Object> metadata = Map.of(
                "query", searchQuery,
                "result_count", resultCount);
        return LearningActivity.builder()
                .user(user)
                .activityType(ActivityType.SEARCH)
                .targetType("search")
                .metadata(metadata)
                .build();
    }

    // ===== 비즈니스 메서드 =====

    /**
     * 활동 설명 반환
     */
    public String getActivityDescription() {
        String baseDescription = this.activityType.getDescription();

        if (this.metadata != null && this.metadata.containsKey("query")) {
            return baseDescription + ": " + this.metadata.get("query");
        }

        if (this.targetType != null) {
            return baseDescription + " (" + this.targetType + ")";
        }

        return baseDescription;
    }

    /**
     * 학습 시간이 포함된 활동인지 확인
     */
    public boolean hasStudyTime() {
        return this.durationMinutes != null && this.durationMinutes > 0;
    }

    /**
     * 특정 날짜의 활동인지 확인
     */
    public boolean isOnDate(LocalDateTime date) {
        return this.createdAt.toLocalDate().equals(date.toLocalDate());
    }

    /**
     * 메타데이터 JSON 변환 유틸리티
     */
    public void setMetadataFromJson(String json) {
        if (json != null && !json.trim().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.metadata = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON format for metadata", e);
            }
        }
    }

    public String getMetadataAsJson() {
        if (this.metadata == null)
            return null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this.metadata);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 메타데이터에서 특정 값 가져오기
     */
    public Object getMetadataValue(String key) {
        if (this.metadata == null)
            return null;
        return this.metadata.get(key);
    }

    /**
     * 메타데이터에 값 설정
     */
    public void setMetadataValue(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = Map.of(key, value);
        } else {
            this.metadata.put(key, value);
        }
    }

    /**
     * 학습 활동 유형 열거형
     */
    public enum ActivityType {
        ROADMAP_START("로드맵 시작"),
        STEP_COMPLETE("단계 완료"),
        STUDY_SESSION("학습 세션"),
        AI_QUESTION("AI 질문"),
        SEARCH("검색");

        private final String description;

        ActivityType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
