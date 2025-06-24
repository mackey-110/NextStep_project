package web.mvc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.domain.Notification;
import web.mvc.domain.Notification.NotificationType;
import web.mvc.domain.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 알림 Repository
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 사용자별 알림 조회 (최신순)
     */
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 사용자별 알림 조회 (페이징)
     */
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * 사용자별 읽지 않은 알림 조회
     */
    List<Notification> findByUserAndIsReadOrderByCreatedAtDesc(User user, Boolean isRead);

    /**
     * 사용자별 읽지 않은 알림 조회 (페이징)
     */
    Page<Notification> findByUserAndIsReadOrderByCreatedAtDesc(User user, Boolean isRead, Pageable pageable);

    /**
     * 사용자별 알림 타입별 조회
     */
    List<Notification> findByUserAndTypeOrderByCreatedAtDesc(User user, NotificationType type);

    /**
     * 사용자별 읽지 않은 알림 수 조회
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.isRead = false")
    long countUnreadByUser(@Param("user") User user);

    /**
     * 사용자별 오늘 알림 수 조회
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND DATE(n.createdAt) = CURRENT_DATE")
    long countTodayNotificationsByUser(@Param("user") User user);

    /**
     * 특정 기간 내 알림 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user " +
            "AND n.createdAt >= :startDate AND n.createdAt <= :endDate " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findByUserAndCreatedAtBetween(@Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 최근 읽지 않은 알림 조회 (제한 개수)
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findRecentUnreadNotifications(@Param("user") User user, Pageable pageable);

    /**
     * 중요한 읽지 않은 알림 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false " +
            "AND n.type IN ('GOAL_ACHIEVED', 'STREAK_MILESTONE', 'MENTORING_MATCH') " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findImportantUnreadNotifications(@Param("user") User user);

    /**
     * 특정 타입의 읽지 않은 알림 수 조회
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.isRead = false AND n.type = :type")
    long countUnreadByUserAndType(@Param("user") User user, @Param("type") NotificationType type);

    /**
     * 사용자별 알림 타입별 통계
     */
    @Query("SELECT n.type, COUNT(n) FROM Notification n WHERE n.user = :user GROUP BY n.type")
    List<Object[]> getNotificationTypeStatsByUser(@Param("user") User user);

    /**
     * 사용자별 월별 알림 통계
     */
    @Query("SELECT YEAR(n.createdAt), MONTH(n.createdAt), COUNT(n) FROM Notification n " +
            "WHERE n.user = :user " +
            "GROUP BY YEAR(n.createdAt), MONTH(n.createdAt) " +
            "ORDER BY YEAR(n.createdAt) DESC, MONTH(n.createdAt) DESC")
    List<Object[]> getMonthlyNotificationStatsByUser(@Param("user") User user);

    /**
     * 오래된 읽은 알림 조회 (정리용)
     */
    @Query("SELECT n FROM Notification n WHERE n.isRead = true AND n.readAt < :threshold")
    List<Notification> findOldReadNotifications(@Param("threshold") LocalDateTime threshold);

    /**
     * 제목으로 알림 검색
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user " +
            "AND LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY n.createdAt DESC")
    List<Notification> searchByTitle(@Param("user") User user, @Param("keyword") String keyword);

    /**
     * 내용으로 알림 검색
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user " +
            "AND LOWER(n.message) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY n.createdAt DESC")
    List<Notification> searchByMessage(@Param("user") User user, @Param("keyword") String keyword);

    /**
     * 액션 URL이 있는 알림 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.actionUrl IS NOT NULL " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsWithAction(@Param("user") User user);

    /**
     * 읽지 않은 알림을 모두 읽음 처리하기 위한 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false")
    List<Notification> findUnreadNotificationsForUpdate(@Param("user") User user);

    /**
     * 특정 사용자의 알림 일괄 삭제를 위한 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user")
    List<Notification> findAllByUser(@Param("user") User user);

    /**
     * 알림 타입별 전체 통계
     */
    @Query("SELECT n.type, COUNT(n) FROM Notification n GROUP BY n.type")
    List<Object[]> getGlobalNotificationTypeStats();

    /**
     * 일별 알림 생성 통계
     */
    @Query("SELECT DATE(n.createdAt), COUNT(n) FROM Notification n " +
            "GROUP BY DATE(n.createdAt) " +
            "ORDER BY DATE(n.createdAt) DESC")
    List<Object[]> getDailyNotificationStats();

    /**
     * 읽음률 통계 (사용자별)
     */
    @Query("SELECT " +
            "COUNT(CASE WHEN n.isRead = true THEN 1 END) as readCount, " +
            "COUNT(CASE WHEN n.isRead = false THEN 1 END) as unreadCount " +
            "FROM Notification n WHERE n.user = :user")
    Object[] getReadRateStatsByUser(@Param("user") User user);
}
