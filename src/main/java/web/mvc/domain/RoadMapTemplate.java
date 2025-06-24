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

/**
 * 로드맵 템플릿 엔티티
 */
@Entity
@Table(name = "roadmap_templates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RoadMapTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "programming_language", length = 50)
    private String programmingLanguage;

    @Column(name = "field_category", length = 50)
    private String fieldCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficultyLevel;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    // === JSON 필드들 ===

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private List<String> tags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private List<String> prerequisites;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "learning_outcomes", columnDefinition = "JSON")
    private List<String> learningOutcomes;

    // === 통계 필드들 ===

    @Builder.Default
    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Builder.Default
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    // === 관리 필드들 ===

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Builder.Default
    @Column(name = "is_official")
    private Boolean isOfficial = false;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // === 연관관계 ===

    @OneToMany(mappedBy = "roadMapTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoadMapStep> steps = new ArrayList<>();

    // === 비즈니스 메서드 ===

    /**
     * 로드맵 기본 정보 업데이트
     */
    public void updateBasicInfo(String title, String description, String programmingLanguage,
            String fieldCategory, DifficultyLevel difficultyLevel,
            Integer estimatedHours, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.programmingLanguage = programmingLanguage;
        this.fieldCategory = fieldCategory;
        this.difficultyLevel = difficultyLevel;
        this.estimatedHours = estimatedHours;
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * 메타데이터 업데이트
     */
    public void updateMetadata(List<String> tags, List<String> prerequisites, List<String> learningOutcomes) {
        this.tags = tags;
        this.prerequisites = prerequisites;
        this.learningOutcomes = learningOutcomes;
    }

    /**
     * 사용 횟수 증가
     */
    public void incrementUsageCount() {
        this.usageCount++;
    }

    /**
     * 평점 업데이트
     */
    public void updateRating(BigDecimal newRating, int reviewCount) {
        this.averageRating = newRating;
        this.totalReviews = reviewCount;
    }

    /**
     * 공식 로드맵으로 승인
     */
    public void approveAsOfficial() {
        this.isOfficial = true;
    }

    /**
     * 로드맵 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 로드맵 활성화
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 단계 추가
     */
    public void addStep(RoadMapStep step) {
        this.steps.add(step);
        step.setRoadMapTemplate(this);
    }

    /**
     * 총 예상 시간 계산
     */
    public int calculateTotalEstimatedHours() {
        return steps.stream()
                .mapToInt(RoadMapStep::getEstimatedHours)
                .sum();
    }
}

/**
 * 난이도 레벨 열거형
 */
enum DifficultyLevel {
    BEGINNER("초급"),
    INTERMEDIATE("중급"),
    ADVANCED("고급");

    private final String description;

    DifficultyLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
