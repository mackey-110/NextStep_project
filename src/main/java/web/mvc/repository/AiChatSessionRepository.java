package web.mvc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.domain.AiChatSession;
import web.mvc.domain.AiChatSession.SessionStatus;
import web.mvc.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AI 채팅 세션 Repository
 */
@Repository
public interface AiChatSessionRepository extends JpaRepository<AiChatSession, Long> {

    /**
     * 세션 UUID로 조회
     */
    Optional<AiChatSession> findBySessionUuid(String sessionUuid);

    /**
     * 사용자별 세션 조회 (최신순)
     */
    List<AiChatSession> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 사용자별 세션 조회 (페이징)
     */
    Page<AiChatSession> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * 사용자별 활성 세션 조회
     */
    List<AiChatSession> findByUserAndStatus(User user, SessionStatus status);

    /**
     * 사용자의 최근 활성 세션 조회
     */
    @Query("SELECT s FROM AiChatSession s WHERE s.user = :user AND s.status = 'ACTIVE' ORDER BY s.updatedAt DESC LIMIT 1")
    Optional<AiChatSession> findLatestActiveSession(@Param("user") User user);

    /**
     * 컨텍스트가 있는 세션 조회
     */
    @Query("SELECT s FROM AiChatSession s WHERE s.user = :user AND (s.contextRoadmap IS NOT NULL OR s.contextStep IS NOT NULL)")
    List<AiChatSession> findSessionsWithContext(@Param("user") User user);

    /**
     * 로드맵 관련 세션 조회
     */
    @Query("SELECT s FROM AiChatSession s WHERE s.user = :user AND s.contextRoadmap.userRoadMapId = :roadmapId")
    List<AiChatSession> findByUserAndContextRoadmapId(@Param("user") User user, @Param("roadmapId") Long roadmapId);

    /**
     * 단계 관련 세션 조회
     */
    @Query("SELECT s FROM AiChatSession s WHERE s.user = :user AND s.contextStep.stepId = :stepId")
    List<AiChatSession> findByUserAndContextStepId(@Param("user") User user, @Param("stepId") Long stepId);

    /**
     * 특정 기간 내 세션 조회
     */
    @Query("SELECT s FROM AiChatSession s WHERE s.user = :user AND s.createdAt >= :startDate AND s.createdAt <= :endDate")
    List<AiChatSession> findByUserAndCreatedAtBetween(@Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 메시지 수 기준으로 세션 조회
     */
    @Query("SELECT s FROM AiChatSession s WHERE s.user = :user AND s.totalMessages >= :minMessages ORDER BY s.totalMessages DESC")
    List<AiChatSession> findByUserAndMinMessages(@Param("user") User user, @Param("minMessages") int minMessages);

    /**
     * 사용자별 총 세션 수 조회
     */
    @Query("SELECT COUNT(s) FROM AiChatSession s WHERE s.user = :user")
    long countByUser(@Param("user") User user);

    /**
     * 사용자별 활성 세션 수 조회
     */
    @Query("SELECT COUNT(s) FROM AiChatSession s WHERE s.user = :user AND s.status = 'ACTIVE'")
    long countActiveSessionsByUser(@Param("user") User user);

    /**
     * 오늘 생성된 세션 수 조회
     */
    @Query("SELECT COUNT(s) FROM AiChatSession s WHERE s.user = :user AND DATE(s.createdAt) = CURRENT_DATE")
    long countTodaySessionsByUser(@Param("user") User user);

    /**
     * 제목으로 세션 검색
     */
    @Query("SELECT s FROM AiChatSession s WHERE s.user = :user AND LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<AiChatSession> searchByTitle(@Param("user") User user, @Param("keyword") String keyword);

    /**
     * 비활성 세션 조회 (일정 시간 이상 업데이트되지 않은 세션)
     */
    @Query("SELECT s FROM AiChatSession s WHERE s.status = 'ACTIVE' AND s.updatedAt < :threshold")
    List<AiChatSession> findInactiveSessions(@Param("threshold") LocalDateTime threshold);

    /**
     * 사용자별 월별 세션 통계
     */
    @Query("SELECT YEAR(s.createdAt), MONTH(s.createdAt), COUNT(s) " +
            "FROM AiChatSession s WHERE s.user = :user " +
            "GROUP BY YEAR(s.createdAt), MONTH(s.createdAt) " +
            "ORDER BY YEAR(s.createdAt) DESC, MONTH(s.createdAt) DESC")
    List<Object[]> getMonthlySessionStats(@Param("user") User user);

    /**
     * 컨텍스트별 세션 통계
     */
    @Query("SELECT CASE " +
            "WHEN s.contextStep IS NOT NULL THEN 'step' " +
            "WHEN s.contextRoadmap IS NOT NULL THEN 'roadmap' " +
            "ELSE 'general' END as contextType, COUNT(s) " +
            "FROM AiChatSession s WHERE s.user = :user " +
            "GROUP BY contextType")
    List<Object[]> getContextTypeStats(@Param("user") User user);

    /**
     * 최근 활동한 세션 조회 (메시지가 있는 세션)
     */
    @Query("SELECT s FROM AiChatSession s WHERE s.user = :user AND s.totalMessages > 0 ORDER BY s.updatedAt DESC")
    List<AiChatSession> findRecentActiveSessions(@Param("user") User user, Pageable pageable);
}
