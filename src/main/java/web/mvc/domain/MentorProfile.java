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
 * 멘토 프로필 엔티티
 * 사용자가 멘토로 활동할 때의 상세 정보
 */
@Entity
@Table(name = "mentor_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentor_id")
    private Long mentorId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ===== 멘토 자격 정보 =====

    /**
     * 전문 분야 (JSON)
     * ["backend", "frontend", "devops"]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "expertise_areas", columnDefinition = "JSON")
    private List<String> expertiseAreas;

    @Column(name = "years_of_experience")
    @PositiveOrZero
    private Integer yearsOfExperience;

    @Column(name = "company", length = 100)
    private String company;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "portfolio_url", length = 500)
    private String portfolioUrl;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    // ===== 멘토링 정보 =====

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal hourlyRate;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "mentor_rating", precision = 3, scale = 2)
    @DecimalMin("0.00")
    @DecimalMax("5.00")
    @Builder.Default
    private BigDecimal mentorRating = BigDecimal.ZERO;

    @Column(name = "total_mentoring_hours")
    @PositiveOrZero
    @Builder.Default
    private Integer totalMentoringHours = 0;

    @Column(name = "total_reviews")
    @PositiveOrZero
    @Builder.Default
    private Integer totalReviews = 0;

    // ===== 승인 정보 =====

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    @Builder.Default
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

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
     * 멘토 승인 처리
     */
    public void approve(User approver) {
        this.approvalStatus = ApprovalStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.approvedBy = approver;
    }

    /**
     * 멘토 승인 거부
     */
    public void reject(User approver) {
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.approvedAt = LocalDateTime.now();
        this.approvedBy = approver;
        this.isAvailable = false;
    }

    /**
     * 멘토링 완료 후 통계 업데이트
     */
    public void completeMentoring(int hours, BigDecimal rating) {
        this.totalMentoringHours += hours;
        this.totalReviews++;

        // 평균 평점 계산
        BigDecimal totalRating = this.mentorRating.multiply(BigDecimal.valueOf(this.totalReviews - 1));
        totalRating = totalRating.add(rating);
        this.mentorRating = totalRating.divide(BigDecimal.valueOf(this.totalReviews), 2, RoundingMode.HALF_UP);
    }

    /**
     * 멘토 이용 가능 여부 확인
     */
    public boolean isActiveAndAvailable() {
        return ApprovalStatus.APPROVED.equals(this.approvalStatus) &&
                Boolean.TRUE.equals(this.isAvailable);
    }

    /**
     * 전문 분야 JSON 변환 유틸리티
     */
    public void setExpertiseAreasFromJson(String json) {
        if (json != null && !json.trim().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.expertiseAreas = mapper.readValue(json, new TypeReference<List<String>>() {
                });
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON format for expertise areas", e);
            }
        }
    }

    public String getExpertiseAreasAsJson() {
        if (this.expertiseAreas == null)
            return null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this.expertiseAreas);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 승인 상태 열거형
     */
    public enum ApprovalStatus {
        PENDING("승인 대기"),
        APPROVED("승인됨"),
        REJECTED("승인 거부");

        private final String description;

        ApprovalStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
