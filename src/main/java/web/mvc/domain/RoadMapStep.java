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
 * 로드맵 단계 엔티티
 */
@Entity
@Table(name = "roadmap_steps")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RoadMapStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private RoadMapTemplate roadMapTemplate;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private StepDifficultyLevel difficultyLevel;

    // === JSON 필드들 ===

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "learning_resources", columnDefinition = "JSON")
    private List<Map<String, Object>> learningResources;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "practice_projects", columnDefinition = "JSON")
    private List<Map<String, Object>> practiceProjects;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "prerequisite_steps", columnDefinition = "JSON")
    private List<Long> prerequisiteSteps;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // === 비즈니스 메서드 ===

    /**
     * 단계 기본 정보 업데이트
     */
    public void updateBasicInfo(String title, String description, Integer estimatedHours,
            StepDifficultyLevel difficultyLevel) {
        this.title = title;
        this.description = description;
        this.estimatedHours = estimatedHours;
        this.difficultyLevel = difficultyLevel;
    }

    /**
     * 학습 자료 업데이트
     */
    public void updateLearningResources(List<Map<String, Object>> learningResources,
            List<Map<String, Object>> practiceProjects) {
        this.learningResources = learningResources;
        this.practiceProjects = practiceProjects;
    }

    /**
     * 전제조건 단계 설정
     */
    public void setPrerequisiteSteps(List<Long> prerequisiteSteps) {
        this.prerequisiteSteps = prerequisiteSteps;
    }

    /**
     * 단계 순서 변경
     */
    public void changeOrder(Integer newOrder) {
        this.stepOrder = newOrder;
    }
}

/**
 * 단계별 난이도 레벨
 */
enum StepDifficultyLevel {
    EASY("쉬움"),
    MEDIUM("보통"),
    HARD("어려움");

    private final String description;

    StepDifficultyLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
