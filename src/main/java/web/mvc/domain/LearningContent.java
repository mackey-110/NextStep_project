package web.mvc.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 학습 콘텐츠 엔티티
 * 강의, 아티클, 도구, 프로젝트, 책 등의 학습 자료 정보
 */
@Entity
@Table(name = "learning_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "title", nullable = false, length = 300)
    @NotBlank
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentType contentType;

    @Column(name = "content_url", length = 1000)
    private String contentUrl;

    // ===== 분류 정보 =====

    @Column(name = "programming_language", length = 50)
    private String programmingLanguage;

    @Column(name = "category", length = 100)
    private String category;

    /**
     * 태그 목록 (JSON)
     * ["python", "django", "api"]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "JSON")
    private List<String> tags;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficultyLevel;

    // ===== 메타데이터 =====

    @Column(name = "author", length = 100)
    private String author;

    @Column(name = "duration_minutes")
    @PositiveOrZero
    private Integer durationMinutes;

    @Column(name = "price", precision = 10, scale = 2)
    @DecimalMin("0.00")
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "is_free")
    @Builder.Default
    private Boolean isFree = true;

    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "ko"; // 'ko', 'en'

    // ===== 통계 정보 =====

    @Column(name = "view_count")
    @PositiveOrZero
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "like_count")
    @PositiveOrZero
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "bookmark_count")
    @PositiveOrZero
    @Builder.Default
    private Integer bookmarkCount = 0;

    @Column(name = "average_rating", precision = 3, scale = 2)
    @DecimalMin("0.00")
    @DecimalMax("5.00")
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "review_count")
    @PositiveOrZero
    @Builder.Default
    private Integer reviewCount = 0;

    // ===== 관리 정보 =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "is_approved")
    @Builder.Default
    private Boolean isApproved = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // ===== 공통 필드 =====

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== 비즈니스 메서드 =====

    /**
     * 조회수 증가
     */
    public void incrementViewCount() {
        this.viewCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 좋아요 증가
     */
    public void incrementLikeCount() {
        this.likeCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 좋아요 감소
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 북마크 증가
     */
    public void incrementBookmarkCount() {
        this.bookmarkCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 북마크 감소
     */
    public void decrementBookmarkCount() {
        if (this.bookmarkCount > 0) {
            this.bookmarkCount--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 콘텐츠 승인
     */
    public void approve(User approver) {
        this.isApproved = true;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 콘텐츠 비활성화
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 콘텐츠 활성화
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 리뷰 추가 후 평균 평점 업데이트
     */
    public void addReview(BigDecimal rating) {
        BigDecimal totalRating = this.averageRating.multiply(BigDecimal.valueOf(this.reviewCount));
        totalRating = totalRating.add(rating);
        this.reviewCount++;
        this.averageRating = totalRating.divide(BigDecimal.valueOf(this.reviewCount), 2, RoundingMode.HALF_UP);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 무료 콘텐츠 여부 확인
     */
    public boolean isFreeContent() {
        return Boolean.TRUE.equals(this.isFree) || BigDecimal.ZERO.equals(this.price);
    }

    /**
     * 승인된 콘텐츠 여부 확인
     */
    public boolean isApprovedContent() {
        return Boolean.TRUE.equals(this.isApproved);
    }

    /**
     * 활성 콘텐츠 여부 확인
     */
    public boolean isActiveContent() {
        return Boolean.TRUE.equals(this.isActive);
    }

    /**
     * 공개 가능한 콘텐츠 여부 확인
     */
    public boolean isPubliclyAvailable() {
        return isApprovedContent() && isActiveContent();
    }

    /**
     * 태그 JSON 변환 유틸리티
     */
    public void setTagsFromJson(String json) {
        if (json != null && !json.trim().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.tags = mapper.readValue(json, new TypeReference<List<String>>() {
                });
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON format for tags", e);
            }
        }
    }

    public String getTagsAsJson() {
        if (this.tags == null)
            return null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this.tags);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 콘텐츠 유형 열거형
     */
    public enum ContentType {
        COURSE("강의"),
        ARTICLE("아티클"),
        TOOL("도구"),
        PROJECT("프로젝트"),
        BOOK("도서");

        private final String description;

        ContentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 난이도 레벨 열거형
     */
    public enum DifficultyLevel {
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
}
