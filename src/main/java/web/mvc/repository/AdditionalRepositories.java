package web.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.domain.AiUsageLimit;
import web.mvc.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * AI 사용량 제한 Repository
 */
@Repository
public interface AiUsageLimitRepository extends JpaRepository<AiUsageLimit, Long> {

    /**
     * 사용자별 특정 날짜 사용량 조회
     */
    Optional<AiUsageLimit> findByUserAndUsageDate(User user, LocalDate usageDate);

    /**
     * 사용자별 오늘 사용량 조회
     */
    @Query("SELECT a FROM AiUsageLimit a WHERE a.user = :user AND a.usageDate = CURRENT_DATE")
    Optional<AiUsageLimit> findTodayUsageByUser(@Param("user") User user);

    /**
     * 사용자별 사용량 이력 조회 (최신순)
     */
    List<AiUsageLimit> findByUserOrderByUsageDateDesc(User user);

    /**
     * 특정 기간 내 사용량 조회
     */
    @Query("SELECT a FROM AiUsageLimit a WHERE a.user = :user " +
           "AND a.usageDate >= :startDate AND a.usageDate <= :endDate " +
           "ORDER BY a.usageDate DESC")
    List<AiUsageLimit> findByUserAndUsageDateBetween(@Param("user") User user,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    /**
     * 사용자별 월별 사용량 통계
     */
    @Query("SELECT YEAR(a.usageDate), MONTH(a.usageDate), " +
           "SUM(a.messageCount), SUM(a.tokensUsed) " +
           "FROM AiUsageLimit a WHERE a.user = :user " +
           "GROUP BY YEAR(a.usageDate), MONTH(a.usageDate) " +
           "ORDER BY YEAR(a.usageDate) DESC, MONTH(a.usageDate) DESC")
    List<Object[]> getMonthlyUsageStatsByUser(@Param("user") User user);

    /**
     * 전체 일별 사용량 통계
     */
    @Query("SELECT a.usageDate, SUM(a.messageCount), SUM(a.tokensUsed) " +
           "FROM AiUsageLimit a " +
           "GROUP BY a.usageDate " +
           "ORDER BY a.usageDate DESC")
    List<Object[]> getDailyUsageStats();

    /**
     * 높은 사용량 사용자 조회 (특정 날짜)
     */
    @Query("SELECT a FROM AiUsageLimit a WHERE a.usageDate = :date " +
           "AND a.messageCount >= :minMessages " +
           "ORDER BY a.messageCount DESC")
    List<AiUsageLimit> findHighUsageUsers(@Param("date") LocalDate date, 
                                        @Param("minMessages") int minMessages);

    /**
     * 사용자별 총 사용량 조회
     */
    @Query("SELECT SUM(a.messageCount), SUM(a.tokensUsed) " +
           "FROM AiUsageLimit a WHERE a.user = :user")
    Object[] getTotalUsageByUser(@Param("user") User user);
}

/**
 * 학습 활동 Repository
 */
@Repository
interface LearningActivityRepository extends JpaRepository<web.mvc.domain.LearningActivity, Long> {

    /**
     * 사용자별 활동 조회 (최신순)
     */
    List<web.mvc.domain.LearningActivity> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 사용자별 특정 타입 활동 조회
     */
    List<web.mvc.domain.LearningActivity> findByUserAndActivityTypeOrderByCreatedAtDesc(
        User user, web.mvc.domain.LearningActivity.ActivityType activityType);

    /**
     * 사용자별 오늘 활동 조회
     */
    @Query("SELECT a FROM LearningActivity a WHERE a.user = :user AND DATE(a.createdAt) = CURRENT_DATE")
    List<web.mvc.domain.LearningActivity> findTodayActivitiesByUser(@Param("user") User user);

    /**
     * 사용자별 특정 기간 활동 조회
     */
    @Query("SELECT a FROM LearningActivity a WHERE a.user = :user " +
           "AND a.createdAt >= :startDate AND a.createdAt <= :endDate " +
           "ORDER BY a.createdAt DESC")
    List<web.mvc.domain.LearningActivity> findByUserAndCreatedAtBetween(@Param("user") User user,
                                                                       @Param("startDate") java.time.LocalDateTime startDate,
                                                                       @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * 사용자별 월별 활동 통계
     */
    @Query("SELECT YEAR(a.createdAt), MONTH(a.createdAt), a.activityType, COUNT(a) " +
           "FROM LearningActivity a WHERE a.user = :user " +
           "GROUP BY YEAR(a.createdAt), MONTH(a.createdAt), a.activityType " +
           "ORDER BY YEAR(a.createdAt) DESC, MONTH(a.createdAt) DESC")
    List<Object[]> getMonthlyActivityStatsByUser(@Param("user") User user);
}

/**
 * 일별 학습 통계 Repository
 */
@Repository
interface DailyStudyStatRepository extends JpaRepository<web.mvc.domain.DailyStudyStat, Long> {

    /**
     * 사용자별 특정 날짜 통계 조회
     */
    Optional<web.mvc.domain.DailyStudyStat> findByUserAndStudyDate(User user, LocalDate studyDate);

    /**
     * 사용자별 오늘 통계 조회
     */
    @Query("SELECT d FROM DailyStudyStat d WHERE d.user = :user AND d.studyDate = CURRENT_DATE")
    Optional<web.mvc.domain.DailyStudyStat> findTodayStatsByUser(@Param("user") User user);

    /**
     * 사용자별 최근 통계 조회
     */
    List<web.mvc.domain.DailyStudyStat> findByUserOrderByStudyDateDesc(User user);

    /**
     * 사용자별 특정 기간 통계 조회
     */
    @Query("SELECT d FROM DailyStudyStat d WHERE d.user = :user " +
           "AND d.studyDate >= :startDate AND d.studyDate <= :endDate " +
           "ORDER BY d.studyDate DESC")
    List<web.mvc.domain.DailyStudyStat> findByUserAndStudyDateBetween(@Param("user") User user,
                                                                     @Param("startDate") LocalDate startDate,
                                                                     @Param("endDate") LocalDate endDate);

    /**
     * 사용자별 연속 학습 일수 계산용 조회
     */
    @Query("SELECT d FROM DailyStudyStat d WHERE d.user = :user AND d.totalStudyMinutes > 0 " +
           "ORDER BY d.studyDate DESC")
    List<web.mvc.domain.DailyStudyStat> findStudyDaysForStreakCalculation(@Param("user") User user);
}

/**
 * 검색 로그 Repository
 */
@Repository
interface SearchLogRepository extends JpaRepository<web.mvc.domain.SearchLog, Long> {

    /**
     * 사용자별 검색 로그 조회
     */
    List<web.mvc.domain.SearchLog> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 사용자별 오늘 검색 수 조회
     */
    @Query("SELECT COUNT(s) FROM SearchLog s WHERE s.user = :user AND DATE(s.createdAt) = CURRENT_DATE")
    long countTodaySearchesByUser(@Param("user") User user);

    /**
     * 인기 검색어 조회
     */
    @Query("SELECT s.searchQuery, COUNT(s) as searchCount " +
           "FROM SearchLog s " +
           "WHERE s.createdAt >= :since " +
           "GROUP BY s.searchQuery " +
           "ORDER BY searchCount DESC")
    List<Object[]> getPopularSearchTerms(@Param("since") java.time.LocalDateTime since, 
                                       org.springframework.data.domain.Pageable pageable);
}
