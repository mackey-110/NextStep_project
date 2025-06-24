package web.mvc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.domain.AiChatMessage;
import web.mvc.domain.AiChatMessage.MessageType;
import web.mvc.domain.AiChatMessage.UserRating;
import web.mvc.domain.AiChatSession;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 채팅 메시지 Repository
 */
@Repository
public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, Long> {

    /**
     * 세션별 메시지 조회 (시간순)
     */
    List<AiChatMessage> findBySessionOrderByCreatedAt(AiChatSession session);

    /**
     * 세션별 메시지 조회 (페이징)
     */
    Page<AiChatMessage> findBySessionOrderByCreatedAt(AiChatSession session, Pageable pageable);

    /**
     * 세션별 특정 타입 메시지 조회
     */
    List<AiChatMessage> findBySessionAndMessageType(AiChatSession session, MessageType messageType);

    /**
     * 세션별 메시지 수 조회
     */
    @Query("SELECT COUNT(m) FROM AiChatMessage m WHERE m.session = :session")
    long countBySession(@Param("session") AiChatSession session);

    /**
     * 세션별 사용자 메시지 수 조회
     */
    @Query("SELECT COUNT(m) FROM AiChatMessage m WHERE m.session = :session AND m.messageType = 'USER'")
    long countUserMessagesBySession(@Param("session") AiChatSession session);

    /**
     * 세션별 AI 메시지 수 조회
     */
    @Query("SELECT COUNT(m) FROM AiChatMessage m WHERE m.session = :session AND m.messageType = 'AI'")
    long countAiMessagesBySession(@Param("session") AiChatSession session);

    /**
     * 특정 기간 내 메시지 조회
     */
    @Query("SELECT m FROM AiChatMessage m WHERE m.session.user.userId = :userId " +
            "AND m.createdAt >= :startDate AND m.createdAt <= :endDate")
    List<AiChatMessage> findByUserAndCreatedAtBetween(@Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 피드백이 있는 AI 메시지 조회
     */
    @Query("SELECT m FROM AiChatMessage m WHERE m.messageType = 'AI' AND m.userRating IS NOT NULL")
    List<AiChatMessage> findAiMessagesWithFeedback();

    /**
     * 특정 평가의 AI 메시지 조회
     */
    @Query("SELECT m FROM AiChatMessage m WHERE m.messageType = 'AI' AND m.userRating = :rating")
    List<AiChatMessage> findAiMessagesByRating(@Param("rating") UserRating rating);

    /**
     * 평가별 AI 메시지 통계
     */
    @Query("SELECT m.userRating, COUNT(m) FROM AiChatMessage m " +
            "WHERE m.messageType = 'AI' AND m.userRating IS NOT NULL " +
            "GROUP BY m.userRating")
    List<Object[]> getAiMessageFeedbackStats();

    /**
     * AI 모델별 메시지 통계
     */
    @Query("SELECT m.aiModel, COUNT(m) FROM AiChatMessage m " +
            "WHERE m.messageType = 'AI' AND m.aiModel IS NOT NULL " +
            "GROUP BY m.aiModel")
    List<Object[]> getAiModelUsageStats();

    /**
     * 총 토큰 사용량 조회 (사용자별)
     */
    @Query("SELECT SUM(m.tokensUsed) FROM AiChatMessage m " +
            "WHERE m.session.user.userId = :userId AND m.messageType = 'AI' AND m.tokensUsed IS NOT NULL")
    Long getTotalTokensUsedByUser(@Param("userId") Long userId);

    /**
     * 특정 날짜의 토큰 사용량 조회 (사용자별)
     */
    @Query("SELECT SUM(m.tokensUsed) FROM AiChatMessage m " +
            "WHERE m.session.user.userId = :userId AND m.messageType = 'AI' " +
            "AND DATE(m.createdAt) = :date AND m.tokensUsed IS NOT NULL")
    Long getTokensUsedByUserOnDate(@Param("userId") Long userId, @Param("date") java.time.LocalDate date);

    /**
     * 평균 응답 시간 조회 (AI 메시지)
     */
    @Query("SELECT AVG(m.responseTimeMs) FROM AiChatMessage m " +
            "WHERE m.messageType = 'AI' AND m.responseTimeMs IS NOT NULL")
    Double getAverageResponseTime();

    /**
     * 사용자별 평균 응답 시간 조회
     */
    @Query("SELECT AVG(m.responseTimeMs) FROM AiChatMessage m " +
            "WHERE m.session.user.userId = :userId AND m.messageType = 'AI' AND m.responseTimeMs IS NOT NULL")
    Double getAverageResponseTimeByUser(@Param("userId") Long userId);

    /**
     * 긴 메시지 조회 (특정 길이 이상)
     */
    @Query("SELECT m FROM AiChatMessage m WHERE LENGTH(m.content) >= :minLength")
    List<AiChatMessage> findLongMessages(@Param("minLength") int minLength);

    /**
     * 최근 AI 메시지 조회 (피드백 분석용)
     */
    @Query("SELECT m FROM AiChatMessage m WHERE m.messageType = 'AI' " +
            "ORDER BY m.createdAt DESC")
    List<AiChatMessage> findRecentAiMessages(Pageable pageable);

    /**
     * 세션의 첫 번째 사용자 메시지 조회
     */
    @Query("SELECT m FROM AiChatMessage m WHERE m.session = :session AND m.messageType = 'USER' " +
            "ORDER BY m.createdAt ASC LIMIT 1")
    AiChatMessage findFirstUserMessage(@Param("session") AiChatSession session);

    /**
     * 세션의 마지막 메시지 조회
     */
    @Query("SELECT m FROM AiChatMessage m WHERE m.session = :session " +
            "ORDER BY m.createdAt DESC LIMIT 1")
    AiChatMessage findLastMessage(@Param("session") AiChatSession session);

    /**
     * 특정 내용을 포함하는 메시지 검색
     */
    @Query("SELECT m FROM AiChatMessage m WHERE LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<AiChatMessage> searchByContent(@Param("keyword") String keyword);

    /**
     * 사용자별 일일 메시지 통계
     */
    @Query("SELECT DATE(m.createdAt), COUNT(m) FROM AiChatMessage m " +
            "WHERE m.session.user.userId = :userId " +
            "GROUP BY DATE(m.createdAt) " +
            "ORDER BY DATE(m.createdAt) DESC")
    List<Object[]> getDailyMessageStatsByUser(@Param("userId") Long userId);
}
