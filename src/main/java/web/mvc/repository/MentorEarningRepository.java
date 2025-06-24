package web.mvc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.domain.MentorEarning;
import web.mvc.domain.MentorEarning.EarningType;
import web.mvc.domain.MentorEarning.PayoutStatus;
import web.mvc.domain.MentorProfile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 멘토 수익 Repository
 */
@Repository
public interface MentorEarningRepository extends JpaRepository<MentorEarning, Long> {

    /**
     * 멘토별 수익 조회 (최신순)
     */
    List<MentorEarning> findByMentorOrderByCreatedAtDesc(MentorProfile mentor);

    /**
     * 멘토별 수익 조회 (페이징)
     */
    Page<MentorEarning> findByMentorOrderByCreatedAtDesc(MentorProfile mentor, Pageable pageable);

    /**
     * 멘토별 특정 타입 수익 조회
     */
    List<MentorEarning> findByMentorAndEarningTypeOrderByCreatedAtDesc(MentorProfile mentor, EarningType earningType);

    /**
     * 멘토별 지급 상태별 수익 조회
     */
    List<MentorEarning> findByMentorAndPayoutStatus(MentorProfile mentor, PayoutStatus payoutStatus);

    /**
     * 지급 대기 중인 수익 조회
     */
    @Query("SELECT e FROM MentorEarning e WHERE e.payoutStatus = 'PENDING' ORDER BY e.createdAt ASC")
    List<MentorEarning> findPendingPayouts();

    /**
     * 지급 대기 중인 수익 조회 (페이징)
     */
    Page<MentorEarning> findByPayoutStatusOrderByCreatedAtAsc(PayoutStatus payoutStatus, Pageable pageable);

    /**
     * 멘토별 지급 대기 중인 총 수익 조회
     */
    @Query("SELECT SUM(e.netAmount) FROM MentorEarning e WHERE e.mentor = :mentor AND e.payoutStatus = 'PENDING'")
    java.math.BigDecimal getTotalPendingEarningsByMentor(@Param("mentor") MentorProfile mentor);

    /**
     * 멘토별 총 수익 조회
     */
    @Query("SELECT SUM(e.netAmount) FROM MentorEarning e WHERE e.mentor = :mentor")
    java.math.BigDecimal getTotalEarningsByMentor(@Param("mentor") MentorProfile mentor);

    /**
     * 멘토별 이번 달 수익 조회
     */
    @Query("SELECT SUM(e.netAmount) FROM MentorEarning e WHERE e.mentor = :mentor " +
            "AND YEAR(e.createdAt) = YEAR(CURRENT_DATE) AND MONTH(e.createdAt) = MONTH(CURRENT_DATE)")
    java.math.BigDecimal getThisMonthEarningsByMentor(@Param("mentor") MentorProfile mentor);

    /**
     * 특정 기간 내 수익 조회
     */
    @Query("SELECT e FROM MentorEarning e WHERE e.mentor = :mentor " +
            "AND e.createdAt >= :startDate AND e.createdAt <= :endDate " +
            "ORDER BY e.createdAt DESC")
    List<MentorEarning> findByMentorAndCreatedAtBetween(@Param("mentor") MentorProfile mentor,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 멘토별 월별 수익 통계
     */
    @Query("SELECT YEAR(e.createdAt), MONTH(e.createdAt), " +
            "SUM(e.amount), SUM(e.netAmount) FROM MentorEarning e " +
            "WHERE e.mentor = :mentor " +
            "GROUP BY YEAR(e.createdAt), MONTH(e.createdAt) " +
            "ORDER BY YEAR(e.createdAt) DESC, MONTH(e.createdAt) DESC")
    List<Object[]> getMonthlyEarningStatsByMentor(@Param("mentor") MentorProfile mentor);

    /**
     * 멘토별 수익 타입별 통계
     */
    @Query("SELECT e.earningType, COUNT(e), SUM(e.amount), SUM(e.netAmount) FROM MentorEarning e " +
            "WHERE e.mentor = :mentor " +
            "GROUP BY e.earningType")
    List<Object[]> getEarningTypeStatsByMentor(@Param("mentor") MentorProfile mentor);

    /**
     * 전체 수익 타입별 통계
     */
    @Query("SELECT e.earningType, COUNT(e), SUM(e.amount), SUM(e.netAmount) FROM MentorEarning e " +
            "GROUP BY e.earningType")
    List<Object[]> getGlobalEarningTypeStats();

    /**
     * 월별 전체 수익 통계
     */
    @Query("SELECT YEAR(e.createdAt), MONTH(e.createdAt), " +
            "COUNT(e), SUM(e.amount), SUM(e.netAmount) FROM MentorEarning e " +
            "GROUP BY YEAR(e.createdAt), MONTH(e.createdAt) " +
            "ORDER BY YEAR(e.createdAt) DESC, MONTH(e.createdAt) DESC")
    List<Object[]> getMonthlyGlobalEarningStats();

    /**
     * 지급 상태별 통계
     */
    @Query("SELECT e.payoutStatus, COUNT(e), SUM(e.netAmount) FROM MentorEarning e " +
            "GROUP BY e.payoutStatus")
    List<Object[]> getPayoutStatusStats();

    /**
     * 상위 수익 멘토 조회
     */
    @Query("SELECT e.mentor, SUM(e.netAmount) as totalEarnings FROM MentorEarning e " +
            "GROUP BY e.mentor " +
            "ORDER BY totalEarnings DESC")
    List<Object[]> getTopEarningMentors(Pageable pageable);

    /**
     * 특정 기간 내 상위 수익 멘토 조회
     */
    @Query("SELECT e.mentor, SUM(e.netAmount) as totalEarnings FROM MentorEarning e " +
            "WHERE e.createdAt >= :startDate AND e.createdAt <= :endDate " +
            "GROUP BY e.mentor " +
            "ORDER BY totalEarnings DESC")
    List<Object[]> getTopEarningMentorsInPeriod(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 총 플랫폼 수수료 조회
     */
    @Query("SELECT SUM(e.amount - e.netAmount) FROM MentorEarning e")
    java.math.BigDecimal getTotalPlatformCommission();

    /**
     * 이번 달 플랫폼 수수료 조회
     */
    @Query("SELECT SUM(e.amount - e.netAmount) FROM MentorEarning e " +
            "WHERE YEAR(e.createdAt) = YEAR(CURRENT_DATE) AND MONTH(e.createdAt) = MONTH(CURRENT_DATE)")
    java.math.BigDecimal getThisMonthPlatformCommission();

    /**
     * 멘토별 수익 건수 조회
     */
    @Query("SELECT COUNT(e) FROM MentorEarning e WHERE e.mentor = :mentor")
    long countEarningsByMentor(@Param("mentor") MentorProfile mentor);

    /**
     * 멘토별 이번 달 수익 건수 조회
     */
    @Query("SELECT COUNT(e) FROM MentorEarning e WHERE e.mentor = :mentor " +
            "AND YEAR(e.createdAt) = YEAR(CURRENT_DATE) AND MONTH(e.createdAt) = MONTH(CURRENT_DATE)")
    long countThisMonthEarningsByMentor(@Param("mentor") MentorProfile mentor);
}
