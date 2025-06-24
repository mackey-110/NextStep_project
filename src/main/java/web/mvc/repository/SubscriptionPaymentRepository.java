package web.mvc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.domain.SubscriptionPayment;
import web.mvc.domain.SubscriptionPayment.PaymentStatus;
import web.mvc.domain.SubscriptionPayment.PaymentSubscriptionType;
import web.mvc.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 구독 결제 Repository
 */
@Repository
public interface SubscriptionPaymentRepository extends JpaRepository<SubscriptionPayment, Long> {

    /**
     * 사용자별 결제 이력 조회 (최신순)
     */
    List<SubscriptionPayment> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 사용자별 결제 이력 조회 (페이징)
     */
    Page<SubscriptionPayment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * 사용자별 특정 상태 결제 조회
     */
    List<SubscriptionPayment> findByUserAndPaymentStatus(User user, PaymentStatus paymentStatus);

    /**
     * 사용자별 완료된 결제 조회
     */
    @Query("SELECT p FROM SubscriptionPayment p WHERE p.user = :user AND p.paymentStatus = 'COMPLETED' " +
            "ORDER BY p.createdAt DESC")
    List<SubscriptionPayment> findCompletedPaymentsByUser(@Param("user") User user);

    /**
     * 사용자의 최근 활성 구독 조회
     */
    @Query("SELECT p FROM SubscriptionPayment p WHERE p.user = :user AND p.paymentStatus = 'COMPLETED' " +
            "AND p.billingPeriodEnd >= CURRENT_DATE ORDER BY p.billingPeriodEnd DESC LIMIT 1")
    Optional<SubscriptionPayment> findActiveSubscriptionByUser(@Param("user") User user);

    /**
     * 외부 결제 ID로 조회
     */
    Optional<SubscriptionPayment> findByExternalPaymentId(String externalPaymentId);

    /**
     * 결제 상태별 조회
     */
    Page<SubscriptionPayment> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);

    /**
     * 구독 타입별 조회
     */
    Page<SubscriptionPayment> findBySubscriptionType(PaymentSubscriptionType subscriptionType, Pageable pageable);

    /**
     * 특정 기간 내 결제 조회
     */
    @Query("SELECT p FROM SubscriptionPayment p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate " +
            "ORDER BY p.createdAt DESC")
    List<SubscriptionPayment> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 만료 예정 구독 조회 (7일 이내)
     */
    @Query("SELECT p FROM SubscriptionPayment p WHERE p.paymentStatus = 'COMPLETED' " +
            "AND p.billingPeriodEnd BETWEEN CURRENT_DATE AND :expireDate")
    List<SubscriptionPayment> findExpiringSubscriptions(@Param("expireDate") LocalDate expireDate);

    /**
     * 만료된 구독 조회
     */
    @Query("SELECT p FROM SubscriptionPayment p WHERE p.paymentStatus = 'COMPLETED' " +
            "AND p.billingPeriodEnd < CURRENT_DATE")
    List<SubscriptionPayment> findExpiredSubscriptions();

    /**
     * 결제 게이트웨이별 통계
     */
    @Query("SELECT p.paymentGateway, COUNT(p), SUM(p.amount) FROM SubscriptionPayment p " +
            "WHERE p.paymentStatus = 'COMPLETED' " +
            "GROUP BY p.paymentGateway")
    List<Object[]> getPaymentGatewayStats();

    /**
     * 월별 매출 통계
     */
    @Query("SELECT YEAR(p.createdAt), MONTH(p.createdAt), " +
            "COUNT(p), SUM(p.amount) FROM SubscriptionPayment p " +
            "WHERE p.paymentStatus = 'COMPLETED' " +
            "GROUP BY YEAR(p.createdAt), MONTH(p.createdAt) " +
            "ORDER BY YEAR(p.createdAt) DESC, MONTH(p.createdAt) DESC")
    List<Object[]> getMonthlyRevenueStats();

    /**
     * 구독 타입별 매출 통계
     */
    @Query("SELECT p.subscriptionType, COUNT(p), SUM(p.amount) FROM SubscriptionPayment p " +
            "WHERE p.paymentStatus = 'COMPLETED' " +
            "GROUP BY p.subscriptionType")
    List<Object[]> getSubscriptionTypeRevenueStats();

    /**
     * 일별 결제 성공률 통계
     */
    @Query("SELECT DATE(p.createdAt), " +
            "COUNT(CASE WHEN p.paymentStatus = 'COMPLETED' THEN 1 END) as successCount, " +
            "COUNT(CASE WHEN p.paymentStatus = 'FAILED' THEN 1 END) as failedCount " +
            "FROM SubscriptionPayment p " +
            "WHERE p.createdAt >= :since " +
            "GROUP BY DATE(p.createdAt) " +
            "ORDER BY DATE(p.createdAt) DESC")
    List<Object[]> getDailyPaymentSuccessRateStats(@Param("since") LocalDateTime since);

    /**
     * 총 매출 조회
     */
    @Query("SELECT SUM(p.amount) FROM SubscriptionPayment p WHERE p.paymentStatus = 'COMPLETED'")
    java.math.BigDecimal getTotalRevenue();

    /**
     * 이번 달 매출 조회
     */
    @Query("SELECT SUM(p.amount) FROM SubscriptionPayment p WHERE p.paymentStatus = 'COMPLETED' " +
            "AND YEAR(p.createdAt) = YEAR(CURRENT_DATE) AND MONTH(p.createdAt) = MONTH(CURRENT_DATE)")
    java.math.BigDecimal getThisMonthRevenue();

    /**
     * 사용자별 총 결제 금액 조회
     */
    @Query("SELECT SUM(p.amount) FROM SubscriptionPayment p WHERE p.user = :user AND p.paymentStatus = 'COMPLETED'")
    java.math.BigDecimal getTotalPaymentAmountByUser(@Param("user") User user);
}
