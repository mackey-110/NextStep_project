package web.mvc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.domain.MentorProfile;
import web.mvc.domain.MentorProfile.ApprovalStatus;
import web.mvc.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * 멘토 프로필 Repository
 */
@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {

    /**
     * 사용자 ID로 멘토 프로필 조회
     */
    Optional<MentorProfile> findByUser(User user);

    /**
     * 사용자 ID로 멘토 프로필 조회
     */
    Optional<MentorProfile> findByUserId(Long userId);

    /**
     * 승인 상태별 멘토 조회
     */
    List<MentorProfile> findByApprovalStatus(ApprovalStatus approvalStatus);

    /**
     * 승인 상태별 멘토 조회 (페이징)
     */
    Page<MentorProfile> findByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);

    /**
     * 활성화된 승인 멘토 조회
     */
    @Query("SELECT m FROM MentorProfile m WHERE m.approvalStatus = 'APPROVED' AND m.isAvailable = true")
    List<MentorProfile> findActiveApprovedMentors();

    /**
     * 활성화된 승인 멘토 조회 (페이징)
     */
    @Query("SELECT m FROM MentorProfile m WHERE m.approvalStatus = 'APPROVED' AND m.isAvailable = true")
    Page<MentorProfile> findActiveApprovedMentors(Pageable pageable);

    /**
     * 전문 분야별 멘토 조회
     */
    @Query("SELECT m FROM MentorProfile m WHERE m.approvalStatus = 'APPROVED' AND m.isAvailable = true " +
            "AND JSON_CONTAINS(m.expertiseAreas, :expertiseArea)")
    List<MentorProfile> findByExpertiseArea(@Param("expertiseArea") String expertiseArea);

    /**
     * 경력 년수 범위로 멘토 조회
     */
    @Query("SELECT m FROM MentorProfile m WHERE m.approvalStatus = 'APPROVED' AND m.isAvailable = true " +
            "AND m.yearsOfExperience >= :minYears AND m.yearsOfExperience <= :maxYears")
    List<MentorProfile> findByExperienceRange(@Param("minYears") int minYears, @Param("maxYears") int maxYears);

    /**
     * 시간당 요금 범위로 멘토 조회
     */
    @Query("SELECT m FROM MentorProfile m WHERE m.approvalStatus = 'APPROVED' AND m.isAvailable = true " +
            "AND m.hourlyRate >= :minRate AND m.hourlyRate <= :maxRate")
    List<MentorProfile> findByHourlyRateRange(@Param("minRate") java.math.BigDecimal minRate,
            @Param("maxRate") java.math.BigDecimal maxRate);

    /**
     * 평점 기준으로 상위 멘토 조회
     */
    @Query("SELECT m FROM MentorProfile m WHERE m.approvalStatus = 'APPROVED' AND m.isAvailable = true " +
            "AND m.mentorRating >= :minRating ORDER BY m.mentorRating DESC")
    List<MentorProfile> findTopRatedMentors(@Param("minRating") java.math.BigDecimal minRating, Pageable pageable);

    /**
     * 회사명으로 멘토 조회
     */
    @Query("SELECT m FROM MentorProfile m WHERE m.approvalStatus = 'APPROVED' AND m.isAvailable = true " +
            "AND LOWER(m.company) LIKE LOWER(CONCAT('%', :company, '%'))")
    List<MentorProfile> findByCompany(@Param("company") String company);

    /**
     * 승인 대기 중인 멘토 수 조회
     */
    @Query("SELECT COUNT(m) FROM MentorProfile m WHERE m.approvalStatus = 'PENDING'")
    long countPendingMentors();

    /**
     * 활성 멘토 수 조회
     */
    @Query("SELECT COUNT(m) FROM MentorProfile m WHERE m.approvalStatus = 'APPROVED' AND m.isAvailable = true")
    long countActiveMentors();

    /**
     * 멘토 통계 조회
     */
    @Query("SELECT m.approvalStatus, COUNT(m) FROM MentorProfile m GROUP BY m.approvalStatus")
    List<Object[]> getMentorStatistics();

    /**
     * 특정 기간 내 등록된 멘토 조회
     */
    @Query("SELECT m FROM MentorProfile m WHERE m.createdAt >= :startDate AND m.createdAt <= :endDate")
    List<MentorProfile> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * 검색 조건으로 멘토 조회 (복합 검색)
     */
    @Query("SELECT m FROM MentorProfile m WHERE m.approvalStatus = 'APPROVED' AND m.isAvailable = true " +
            "AND (:expertise IS NULL OR JSON_CONTAINS(m.expertiseAreas, :expertise)) " +
            "AND (:minYears IS NULL OR m.yearsOfExperience >= :minYears) " +
            "AND (:maxYears IS NULL OR m.yearsOfExperience <= :maxYears) " +
            "AND (:minRate IS NULL OR m.hourlyRate >= :minRate) " +
            "AND (:maxRate IS NULL OR m.hourlyRate <= :maxRate) " +
            "ORDER BY m.mentorRating DESC")
    Page<MentorProfile> searchMentors(@Param("expertise") String expertise,
            @Param("minYears") Integer minYears,
            @Param("maxYears") Integer maxYears,
            @Param("minRate") java.math.BigDecimal minRate,
            @Param("maxRate") java.math.BigDecimal maxRate,
            Pageable pageable);
}
